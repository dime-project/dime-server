/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.gateway.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.jsonldjava.core.JSONLD;
import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.core.Options;
import com.github.jsonldjava.impl.RDF2GoRDFParser;
import com.github.jsonldjava.impl.RDF2GoTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;

import eu.dime.ps.semantic.model.Class4Type;
import eu.dime.ps.semantic.vocabulary.DefaultOntologies;
import eu.dime.ps.semantic.vocabulary.DefaultOntologies.Ontology;

/**
 * Deals with JSON-LD serialization and deserialization for RDF.
 * 
 * @author Ismael Rivera
 */
public class JSONLDUtils {

	private static final Logger logger = LoggerFactory.getLogger(JSONLDUtils.class);
	
	private static final RDF2GoRDFParser RDF_PARSER = new RDF2GoRDFParser();
	private static final Options OPTIONS = new Options();
	static {
		OPTIONS.outputForm = "compacted";
	}

	private static final Map<String, String> NS_PREFIX_MAP = new HashMap<String, String>();
	static {
		for (Ontology ontology : DefaultOntologies.getDefaults()) {
			Model model = RDF2Go.getModelFactory().createModel().open();
			try {
				model.readFrom(ontology.getInputStream(), ontology.getSyntax());
			} catch (Exception e) {
				logger.warn("Cannot read namespaces prefixes from ontology " + ontology.getUri() + ": " + e.getMessage());
			}
			final Map<String, String> namespaces = model.getNamespaces();
	        for (final String prefix : namespaces.keySet()) {
	        	NS_PREFIX_MAP.put(prefix, namespaces.get(prefix));
	        }
		}
	}
	
	private static final boolean INCLUDE_DEFAULT_PREFIXES = false;
	
	/**
	 * Deserializes a JSON-LD document to RDF.
	 * 
	 * @param object key-value map representing the JSON-LD document
	 * @param returnType the class representing the RDFS resource
	 * @return a RDFReactor resource containing the RDF data
	 */
	public static <T extends Resource> T deserialize(Map<String, Object> object, Class<T> returnType)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
		ModelSet deserialized = (ModelSet) JSONLD.toRDF(object, callback);
		
		ClosableIterator<Statement> statements = deserialized.findStatements(Variable.ANY, Variable.ANY, RDF.type, Variable.ANY);
		if (!statements.hasNext()) {
			return null; // there must be at least one triple <?, a, ?>
		}
		
		Model resourceModel = RDF2Go.getModelFactory().createModel().open();
		resourceModel.addAll(deserialized.iterator());
		Statement statement = statements.next();
		Resource resource = new Resource(resourceModel, statement.getSubject(), false);
		
		deserialized.close();
		
		return (T) resource.castTo(returnType);
	}

	public static <T extends Resource> T deserialize(String jsonObject, Class<T> returnType)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		Object deserialized = JSONUtils.fromString(jsonObject);
		List<Object> collection = null;
		
		if (deserialized instanceof Map) {
			Map<String, Object> deserializedMap = (Map<String, Object>) deserialized;
			if (deserializedMap.containsKey("@graph")) {
				collection = (List<Object>) deserializedMap.get("@graph");
			} else {
				return deserialize(deserializedMap, returnType);
			}
		} else if (deserialized instanceof List) {
			collection = (List<Object>) deserialized;
		} else {
			throw new JSONLDProcessingError("Expected Map or List, got " + deserialized.getClass() + ". The input JSON" +
					" could not be deserialized into an RDF resource instance: " + jsonObject);
		}
		
		T result = null;
		Model relatedMetadata = RDF2Go.getModelFactory().createModel().open();
		
		List<? extends Resource> resources = deserializeCollection(collection);
		for (Resource resource : resources) {
			if (resource.getClass().equals(returnType)) {
				if (result == null) {
					result = (T) resource.castTo(returnType);
				} else {
					throw new IllegalArgumentException("The JSON parameter contains more than one resource of type '" + returnType.getName() +
							"', please consider calling instead deserializeCollection(String)");
				}
			} else {
				// store metadata of other resources of a different type, which are
				// somehow related to the main resource (composition, etc.), to include
				// it with the main resource metadata
				relatedMetadata.addAll(resource.getModel().iterator());
			}
		}
		
		if (result == null) {
			throw new IllegalArgumentException("The JSON parameter does not contain any metadata for a resource" +
					" of type '" + returnType.getName() + "'.");
		} else {
			result.getModel().addAll(relatedMetadata.iterator());
		}
		
		return result;
	}

	public static List<? extends Resource> deserializeCollection(List<Object> collection)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		List<Resource> results = new ArrayList<Resource>();
		
		for (Object object : collection) {
			RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
			ModelSet deserialized = (ModelSet) JSONLD.toRDF(object, callback);
			
			ClosableIterator<Statement> statements = deserialized.findStatements(Variable.ANY, Variable.ANY, RDF.type, Variable.ANY);
			if (!statements.hasNext()) {
				logger.warn(object + " won't be deserialized since no rdf:type was found");
				break;
			}
			
			Statement statement = statements.next();
			Class<?> clazz = guessClass(statement.getObject().asURI());

			Model resourceModel = RDF2Go.getModelFactory().createModel().open();
			resourceModel.addAll(deserialized.iterator());
			Resource resource = new Resource(resourceModel, statement.getSubject(), false);
			results.add((Resource) resource.castTo(clazz));
			
			deserialized.close();
		}
		
		return results;
	}

	public static List<? extends Resource> deserializeCollection(String jsonArray)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		return deserializeCollection((List<Object>) JSONUtils.fromString(jsonArray));
	}

	/**
	 * Serializes an RDF resource into JSON-LD. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a key-value map with representing the JSON-LD document
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> Object serialize(T resource) throws JSONLDProcessingError {
		return serialize(null, resource);
	}

	/**
	 * Serializes an RDF resource into JSON-LD. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the RDFReactor resource to serialize
	 * @return a key-value map with representing the JSON-LD document
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> Object serialize(Map<String, String> prefixes, T resource)
			throws JSONLDProcessingError {
		if (INCLUDE_DEFAULT_PREFIXES) addDefaultPrefixes(resource.getModel());
		if (prefixes != null) addPrefixes(prefixes, resource.getModel());
		return JSONLD.fromRDF(resource.getModel(), OPTIONS, RDF_PARSER);
	}

	/**
	 * Serializes an RDF resource into JSON-LD into a string. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a string representation of the JSON-LD document
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> String serializeAsString(T resource) throws JSONLDProcessingError {
		return serializeAsString(null, resource);
	}

	/**
	 * Serializes an RDF resource into JSON-LD into a string. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the RDFReactor resource to serialize
	 * @return a string representation of the JSON-LD document
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> String serializeAsString(Map<String, String> prefixes, T resource)
			throws JSONLDProcessingError {
		if (INCLUDE_DEFAULT_PREFIXES) addDefaultPrefixes(resource.getModel());
		if (prefixes != null) addPrefixes(prefixes, resource.getModel());
		return JSONUtils.toString(JSONLD.fromRDF(resource.getModel(), OPTIONS, RDF_PARSER));
	}
	
	/**
	 * Serializes an collection of RDF resources into JSON-LD. 
	 * 
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a list of key-value maps with representing the JSON-LD documents
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> List<Object> serializeCollection(T... collection)
			throws JSONLDProcessingError {
		return serializeCollection(null, collection);
	}

	/**
	 * Serializes an collection of RDF resources into JSON-LD. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a list of key-value maps with representing the JSON-LD documents
	 * @throws JSONLDProcessingError if serialization to JSON-LD fails
	 */
	public static <T extends Resource> List<Object> serializeCollection(Map<String, String> prefixes,
			T... collection) throws JSONLDProcessingError {
		List<Object> jsonList = new ArrayList<Object>(collection.length);
		for (T resource : collection) {
			if (INCLUDE_DEFAULT_PREFIXES) addDefaultPrefixes(resource.getModel());
			if (prefixes != null) addPrefixes(prefixes, resource.getModel());
			jsonList.add(JSONLD.fromRDF(resource.getModel(), OPTIONS, RDF_PARSER));
		}
		return jsonList;
	}

	private static Class<? extends Resource> guessClass(URI type) {
		Class<? extends Resource> clazz = null;
		if (type != null)	clazz = Class4Type.getClassForType(type);
		if (clazz == null)	clazz = (Class<? extends Resource>) Resource.class; // by default everything is a Resource
		return clazz;
	}
	
	private static void addPrefixes(Map<String, String> prefixes, Model model) {
		for (String prefix : prefixes.keySet()) {
			model.setNamespace(prefix, prefixes.get(prefix));
		}
	}
	
	private static void addDefaultPrefixes(Model model) {
		addPrefixes(NS_PREFIX_MAP, model);
	}
	
}
