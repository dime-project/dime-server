package eu.dime.ps.datamining.account;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.NAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.ResourceMatchingService;
import eu.dime.ps.semantic.service.impl.ResourceMatchingServiceImpl;

public abstract class AccountUpdaterBase {

	protected ResourceStore resourceStore;
	protected TripleStore tripleStore;
	protected ResourceMatchingService resourceMatchingService;
	
	public AccountUpdaterBase(ResourceStore resourceStore) {
		this.resourceStore = resourceStore;
		this.tripleStore = resourceStore.getTripleStore();
		this.resourceMatchingService = new ResourceMatchingServiceImpl(tripleStore);
	}
	
	protected Resource match(Resource resource) {
		// matches the new results with the current resources in the store
		Map<String, String> mappings = resourceMatchingService.match(resource);

		// replaces instance identifiers using the mappings, and store all data in an RDF model
		Model tempModel = RDF2Go.getModelFactory().createModel().open();
		tempModel.addAll(resource.getModel().iterator());

		// keeps track of all blank node ids replaced by URIs
		Map<String, URI> bnUris = new HashMap<String, URI>();
		
		// replaces the resource identifier with its mapping resource (if found)
		// needs to check for all mappings (and not just the resource id),
		// because the resource model may contain nested resources (also may be mapped)
		for (String key : mappings.keySet()) {
			String identifier = mappings.get(key);
			org.ontoware.rdf2go.model.node.Resource keyResource = key.charAt(0) == '_' ?
					tempModel.createBlankNode(key.substring(2)) : tempModel.createURI(key);
			org.ontoware.rdf2go.model.node.Resource mappedResource = identifier.charAt(0) == '_' ?
					tempModel.createBlankNode(identifier.substring(2)) : tempModel.createURI(identifier);
			
			bnUris.put(keyResource.toString(), mappedResource.asURI());
			ModelUtils.replaceIdentifier(tempModel, keyResource, mappedResource);
		}

		// replaces all blank nodes ids for unique URIs
		Model skolemizedModel = RDF2Go.getModelFactory().createModel().open();
		bnUris.putAll(ModelUtils.skolemize(tempModel, skolemizedModel));

		return new Resource(skolemizedModel, bnUris.get(resource.asResource().toString()), false);
	}
	
	protected Collection<? extends Resource> match(Collection<? extends Resource> resources) {
		Collection<Resource> results = new ArrayList<Resource>(resources.size());
		for (Resource resource : resources) {
			results.add(match(resource));
		}
		return results;
	}

	protected void removeResources(URI accountUri) throws AccountIntegrationException {
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

	protected void removeResources(URI accountUri, String path) throws AccountIntegrationException {
		if (!tripleStore.isTypedAs(accountUri, DAO.Account)) {
			throw new AccountIntegrationException(accountUri + " is not a dao:Account.");
		}

		URI accountPathGraph = new URIImpl(accountUri + path);
		// removes the triples in the account + path graph (inferred triples are also deleted)
		tripleStore.removeStatements(accountPathGraph, Variable.ANY, Variable.ANY, Variable.ANY);
		tripleStore.touchGraph(accountPathGraph);
	}

	// each account + path have their own graph
	protected URI getGraph(URI accountUri, String path) {
		return new URIImpl(accountUri.toString().concat(path));
	}

	protected String getProfilePrefLabel(PersonContact profile, URI accountUri) {
		String name = null;
		if (profile.getAllPersonName().hasNext()) {
			PersonName personName = profile.getAllPersonName().next();
			name = personName.getFullname();
			if (name == null) {
				name = personName.getNameGiven();
			}
		}
		Node accountType = ModelUtils.findObject(tripleStore, accountUri, RDFS.label);
		if (name == null) {
			return "Undefined";
		} else {
			return name+"@"+(accountType == null ? "Unknown" : accountType.asLiteral().getValue());
		}
	}
	
	protected String getPersonGroupPrefLabel(URI accountUri) {
		Node prefLabel = ModelUtils.findObject(tripleStore, accountUri, NAO.prefLabel);
		if (prefLabel == null) {
			return "Undefined";
		} else {
			return "People@"+prefLabel.asLiteral().getValue();
		}
	}
	
}
