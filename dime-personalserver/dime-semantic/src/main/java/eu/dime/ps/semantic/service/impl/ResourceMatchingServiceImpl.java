package eu.dime.ps.semantic.service.impl;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.service.ResourceMatchingService;
import eu.dime.ps.semantic.util.StringUtils;

public class ResourceMatchingServiceImpl implements ResourceMatchingService {

	private static final Logger logger = LoggerFactory.getLogger(ResourceMatchingServiceImpl.class);
	
	private TripleStore tripleStore;

	public ResourceMatchingServiceImpl() {}
	
	public ResourceMatchingServiceImpl(TripleStore tripleStore) {
		this.tripleStore = tripleStore;
	}
	
	public void setTripleStore(TripleStore tripleStore) {
		this.tripleStore = tripleStore;
	}
	
	public Resource getResourceByIdentifier(String externalId) {
		logger.debug("Looking for resources which identifier (nao:externalIdentifier or nco:contactUID) is "+externalId+"...");
		String queryString = StringUtils.strjoinNL(
				"PREFIX nao: "+NAO.NS_NAO.toSPARQL(),
				"PREFIX nco: "+NCO.NS_NCO.toSPARQL(),
				"SELECT ?result WHERE {",
				"  { ?result nao:externalIdentifier ?id . }",
				"  UNION { ?result nco:contactUID ?id . }",
				"  FILTER (str(?id) = \""+externalId+"\")",
				"}");
		Resource result = null;
		ClosableIterator<QueryRow> selectIt = tripleStore.sparqlSelect(queryString).iterator();
		if (selectIt.hasNext()) {
			result = selectIt.next().getValue("result").asResource();
		}
		logger.debug(result == null ? "No resource found" : "Found "+result);
		return result;
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Map<String, String> match(T resource) {
		return findMatches(resource);
	}
	
	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Map<String, String> match(Collection<T> resources) {
		Map<String, String> mappings = new HashMap<String, String>();
		for (T resource : resources) {
			mappings.putAll(findMatches(resource));
		}
		return mappings;
	}
	
	private Map<String, String> findMatches(org.ontoware.rdfreactor.schema.rdfs.Resource resource) {
		Model resourceModel = resource.getModel();
		Map<Resource, Resource> mappings = new HashMap<Resource, Resource>();
	
		try {
			// resources which have a unique identifier (nao:externalIdentifier, nco:contactUID, etc.) would match
			// to the resource URI which have the identifier
			String queryString = StringUtils.strjoinNL(
					"PREFIX nao: "+NAO.NS_NAO.toSPARQL(),
					"PREFIX nco: "+NCO.NS_NCO.toSPARQL(),
					"SELECT ?r ?id WHERE {",
					"  { ?r nao:externalIdentifier ?id . }",
					"  UNION { ?r nco:contactUID ?id . }",
					"}");
			ClosableIterator<QueryRow> idRows = resourceModel.sparqlSelect(queryString).iterator();
			Resource newUri, oldUri;
			while (idRows.hasNext()) {
				QueryRow row = idRows.next();
				newUri = row.getValue("r").asResource();
				oldUri = getResourceByIdentifier(row.getLiteralValue("id"));
				if (oldUri != null) {
					mappings.put(newUri, oldUri);
					logger.debug("Mapping added: "+newUri+" => "+oldUri);
				}
			}
			idRows.close();
	
			// FIXME this is buggy and incomplete. it's not really needed for PoC, so it will be fixed and improved
			// later on.
//			// nested resources of mapped resources via its nao:externalId are also mapped if the
//			// values of all their properties are the same as the known ones, but first it's checked
//			// if the nested resource was already mapped if it also has an externalIdentifier
//			for (Resource r : mappings.keySet()) {
//				ClosableIterator<? extends Statement> rIt = resourceModel.findStatements(r.asResource(), Variable.ANY, Variable.ANY);
//				while (rIt.hasNext()) {
//					Node object = rIt.next().getObject();
//					if (object instanceof BlankNode) {
//						Resource nested = (Resource) object;
//						
//						// nested resource already mapped, skipping...
//						if (!mappings.containsKey(nested.toString())) {
//							ClosableIterator<? extends Statement> nIt = resourceModel.findStatements(nested, Variable.ANY, Variable.ANY);
//							StringBuilder queryBuilder = new StringBuilder("SELECT ?match WHERE {\n");
//							while (nIt.hasNext()) {
//								Statement statement = nIt.next();
//								Node o = statement.getObject();
//								String queryObject = o instanceof BlankNode ? blankNodeToVariable(o.toString()) : o.toSPARQL(); 
//								queryBuilder.append("?match "+statement.getPredicate().toSPARQL()+" "+queryObject+" .\n");
//							}
//							queryBuilder.append("}");
//							System.out.println(queryBuilder.toString());
//							ClosableIterator<QueryRow> rowIt = tripleStore.sparqlSelect(queryBuilder.toString()).iterator();
//							Resource candidate = null;
//							if (rowIt.hasNext()) {
//								Node match = rowIt.next().getValue("match");
//								if (match != null) {
//									candidate = match.asResource();
//									mappings.put(nested, candidate);
//								}
//							}
//							if (rowIt.hasNext()) {
//								logger.warn("There are more than one resource matching to "+nested+". One is randomnly picked.");
//							}
//							rowIt.close();
//						}
//					}
//				}
//				rIt.close();
//			}	
		} catch (Exception e) {
			logger.error("Error occurred, resource matching could not been completed.", e);
		}
		
		// transforms result to Map<String, String>
		Map<String, String> results = new HashMap<String, String>();
		for (Resource key : mappings.keySet()) {
			results.put(key.toString(), mappings.get(key).toString());
		}
		logger.debug(results.size()+" resource matching found.");

		return results;
	}

	// this is not really used, but it might be useful in future development for matching
	public boolean match(Model aModel, Model otherModel) {
		if (aModel.isEmpty() && otherModel.isEmpty()) {
			return true;
		}

		// compares aModel against otherModel
		ClosableIterator<? extends Statement> it1 = aModel.iterator();
		while (it1.hasNext()) {
			Statement statement = it1.next();
			if (!otherModel.contains(Variable.ANY, statement.getPredicate(), statement.getObject())) {
				return false;
			}
		}
		it1.close();
		
		// compares otherModel against aModel
		ClosableIterator<? extends Statement> it2 = otherModel.iterator();
		while (it2.hasNext()) {
			Statement statement = it2.next();
			if (!aModel.contains(Variable.ANY, statement.getPredicate(), statement.getObject())) {
				return false;
			}
		}
		it2.close();
		
		return true;
	}
	
	private String blankNodeToVariable(String blankNode) {
		return blankNode.replaceFirst("_:", "?");
	}
	
}
