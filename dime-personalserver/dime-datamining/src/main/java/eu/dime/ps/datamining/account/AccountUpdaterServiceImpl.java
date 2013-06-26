package eu.dime.ps.datamining.account;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NIE;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.service.ResourceMatchingService;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Deals with updates for accounts on the semantic repository coming from
 * social services such as LinkedIn, Twitter, etc.
 * 
 * NOTE: also sends notifications, though this should be extracted from here,
 * some event listener, or aspect, etc.
 * 
 * @author Ismael Rivera
 */
public class AccountUpdaterServiceImpl implements AccountUpdaterService {

	private static final Logger logger = LoggerFactory.getLogger(AccountUpdaterServiceImpl.class);
	
	private TripleStore tripleStore;
	private PimoService pimoService;
	private ResourceMatchingService resourceMatchingService;
	
	public AccountUpdaterServiceImpl(PimoService pimoService, ResourceMatchingService resourceMatchingService) {
		this.pimoService = pimoService;
		this.tripleStore = pimoService.getTripleStore();
		this.resourceMatchingService = resourceMatchingService;
	}

	public void setPimoService(PimoService pimoService) {
		this.pimoService = pimoService;
		this.tripleStore = pimoService.getTripleStore();
	}

	public void setResourceMatchingService(ResourceMatchingService resourceMatchingService) {
		this.resourceMatchingService = resourceMatchingService;
	}

	@Override
	public synchronized <T extends Resource> void updateResources(URI accountUri,
			String path, Collection<T> resources) throws AccountIntegrationException {
		logger.info("Updating "+path+" with "+resources.size()+" resources.");
		
		if (!tripleStore.isTypedAs(accountUri, DAO.Account)) {
			throw new AccountIntegrationException(accountUri + " is not a dao:Account.");
		}
		
		// graph where to store these resources
		URI accountPathGraph = new URIImpl(accountUri + path);
		Node accountPrefLabel = ModelUtils.findObject(tripleStore, accountUri, NAO.prefLabel);
		String accountLabel = accountPrefLabel != null && accountPrefLabel instanceof Literal ?
				accountPrefLabel.asLiteral().getValue() : accountUri.toString();
		
		// matches the new results with the current resources in the store
		Map<String, String> mappings = resourceMatchingService.match(resources);
		
		// keeps track of all replaced blank nodes ids by URIs
		Map<String, URI> bnUris = new HashMap<String, URI>();
		
		// replaces instance identifiers using the mappings, and store all data in an RDF model
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		Model tempModel = RDF2Go.getModelFactory().createModel().open();
		for (T resource : resources) {
			tempModel.addAll(resource.getModel().iterator());
			
			// replaces the resource identifier with its mapping resource (if found)
			// needs to check for all mappings (and not just the resource id),
			// because the resource model may contain nested resources (also may be mapped)
			for (String key : mappings.keySet()) {
				String identifier = mappings.get(key);
				org.ontoware.rdf2go.model.node.Resource keyResource = key.charAt(0) == '_' ?
						tempModel.createBlankNode(key.substring(2)) : tempModel.createURI(key);
				org.ontoware.rdf2go.model.node.Resource mappedResource = identifier.charAt(0) == '_' ?
						tempModel.createBlankNode(identifier.substring(2)) : tempModel.createURI(identifier);
				ModelUtils.replaceIdentifier(tempModel, keyResource, mappedResource);
				bnUris.put(keyResource.toString(), mappedResource.asURI());
			}

			// adds all triples to the sink model, and clears out the temporary one
			sinkModel.addAll(tempModel.iterator());
			tempModel.removeAll();
		}
		
		// skolemizes the RDF data (replaces blank nodes for URIs)
		Model skolemizedModel = RDF2Go.getModelFactory().createModel().open();
		bnUris.putAll(ModelUtils.skolemize(sinkModel, skolemizedModel));
		
		// links all resources to the account using nie:dataSource
		for (T resource : resources) {
			URI resourceUri = bnUris.get(resource.asResource().toString());
			skolemizedModel.addStatement(resourceUri, NIE.dataSource, accountUri);
		}
//		// link all nco:PersonContact & dlpo:LivePost to the account (dao:source)
//		String queryString = StringUtils.strjoinNL(
//				PimoService.SPARQL_PREAMBLE,
//				"SELECT DISTINCT ?resource WHERE {",
//				"  { ?resource a nco:OrganizationContact }",
//				"  UNION { ?resource a nco:PersonContact }",
//				"  UNION { ?resource a dlpo:ActivityPost }",
//				"  UNION { ?resource a dlpo:AvailabilityPost }",
//				"  UNION { ?resource a dlpo:AudioPost }",
//				"  UNION { ?resource a dlpo:BlogPost }",
//				"  UNION { ?resource a dlpo:Checkin }",
//				"  UNION { ?resource a dlpo:Comment }",
//				"  UNION { ?resource a dlpo:EventPost }",
//				"  UNION { ?resource a dlpo:ImagePost }",
//				"  UNION { ?resource a dlpo:LivePost }",
//				"  UNION { ?resource a dlpo:Message }",
//				"  UNION { ?resource a dlpo:MultimediaPost }",
//				"  UNION { ?resource a dlpo:PresencePost }",
//				"  UNION { ?resource a dlpo:Status }",
//				"  UNION { ?resource a dlpo:VideoPost }",
//				"  UNION { ?resource a dlpo:WebDocumentPost }",
//				"}");
//		ClosableIterator<QueryRow> queryIt = skolemizedModel.sparqlSelect(queryString).iterator();
//		while (queryIt.hasNext()) {
//			skolemizedModel.addStatement(queryIt.next().getValue("resource").asResource(), NIE.dataSource, accountUri);
//		}
//		queryIt.close();

		// FIXME on parallel updates, sometimes a new resource was created, even though it had been
		// created previously, but it's external identifier was deleted before finding the matching,
		// thus creating more resources than expected in some cases (ametic events/users)
		// storing the external ids in a graph, which is not deleted, solves this problem for now
		// but should be fixed in a better way after PoC
		ClosableIterator<Statement> externalIdentifiers = skolemizedModel.findStatements(Variable.ANY, NAO.externalIdentifier, Variable.ANY);
		tripleStore.addAll(new URIImpl("urn:accounts:external-ids"), externalIdentifiers);
		externalIdentifiers.close();
		ClosableIterator<Statement> contactUIDs = skolemizedModel.findStatements(Variable.ANY, NCO.contactUID, Variable.ANY);
		tripleStore.addAll(new URIImpl("urn:accounts:external-ids"), contactUIDs);
		contactUIDs.close();
		
		// removes previous data of the service account
		removeResources(accountUri, path);

		// adds the new data to the RDF store
		tripleStore.addAll(accountPathGraph, skolemizedModel.iterator());
		tripleStore.touchGraph(accountPathGraph);
		
		// lifting resources to PIMO, and performs matching against existing PIMO instances
		for (T resource : resources) {
//			org.ontoware.rdf2go.model.node.Resource identifier = resource.asResource();
			URI resourceUri = bnUris.get(resource.asResource().toString());
			pimoService.getOrCreateThingForOccurrence(resourceUri);
			
			Model metadata = RDF2Go.getModelFactory().createModel().open();
			ModelUtils.fetch(tripleStore.getModel(accountPathGraph), metadata, resourceUri);
			Resource resourceMetadata = new Resource(metadata, resourceUri, false); 
			if (mappings.containsKey(resourceUri.toString())) {
				// it was already in the store, was just updated its metadata
				BroadcastManager.getInstance().sendBroadcast(new Event(pimoService.getName(), AccountUpdaterService.ACTION_RESOURCE_MODIFY, resourceMetadata));
			} else {
////				// it's already an URI, or when 'skolemizing' it was assigned one
////				URI resourceUri = identifier instanceof URI ? 
////							identifier.asURI() : bnUris.get(identifier.toString());
////
//				// new item found, broadcasting event
				BroadcastManager.getInstance().sendBroadcast(new Event(pimoService.getName(), AccountUpdaterService.ACTION_RESOURCE_NEW, resourceMetadata));
			}
		}
		
		// closing all models not needed anymore
		tempModel.close();
		sinkModel.close();
		skolemizedModel.close();
	}

	@Override
	public void removeResources(URI accountUri) throws AccountIntegrationException {
		if (!tripleStore.isTypedAs(accountUri, DAO.Account)) {
			throw new AccountIntegrationException(accountUri + " is not a dao:Account.");
		}

		// removes the triples in all graphs for the account (inferred triples are also deleted)
		Collection<URI> graphs = tripleStore.listContext(accountUri.toString());
		for (URI graph : graphs) {
			tripleStore.removeStatements(graph, Variable.ANY, Variable.ANY, Variable.ANY);
			tripleStore.touchGraph(graph);
		}
	}

	@Override
	public void removeResources(URI accountUri, String path) throws AccountIntegrationException {
		if (!tripleStore.isTypedAs(accountUri, DAO.Account)) {
			throw new AccountIntegrationException(accountUri + " is not a dao:Account.");
		}

		URI accountPathGraph = new URIImpl(accountUri + path);
		// removes the triples in the account + path graph (inferred triples are also deleted)
		tripleStore.removeStatements(accountPathGraph, Variable.ANY, Variable.ANY, Variable.ANY);

		tripleStore.touchGraph(accountPathGraph);
	}
	
}
