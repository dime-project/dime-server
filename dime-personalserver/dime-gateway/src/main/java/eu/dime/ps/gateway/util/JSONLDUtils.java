package eu.dime.ps.gateway.util;

import ie.deri.smile.jsonld.rdf2go.RDF2GoJSONLDSerializer;
import ie.deri.smile.jsonld.rdf2go.RDF2GoTripleCallback;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCAL;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.NEXIF;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NID3;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

import de.dfki.km.json.jsonld.JSONLDProcessor;
import de.dfki.km.json.jsonld.JSONLDSerializer;
import eu.dime.ps.semantic.model.Class4Type;

/**
 * Deals with JSON-LD serialization and deserialization for RDF.
 *  
 * @author Ismael Rivera
 */
public class JSONLDUtils {

	private static final RDF2GoJSONLDSerializer jsonldSerializer = new RDF2GoJSONLDSerializer();
	private static final JSONLDProcessor jsonldProcessor = new JSONLDProcessor();

	/**
	 * Deserializes a JSON-LD document to RDF.
	 * 
	 * @param object key-value map representing the JSON-LD document
	 * @param returnType the class representing the RDFS resource
	 * @return a RDFReactor resource containing the RDF data
	 */
	public static <T extends Resource> T deserialize(Map<String, Object> object, Class<T> returnType)
			throws JsonParseException, JsonMappingException {
		RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
		jsonldProcessor.triples(object, callback);
		Model model = callback.getModel();
		ClosableIterator<Statement> statements = model.findStatements(Variable.ANY, RDF.type, Variable.ANY);
		if (!statements.hasNext())
			return null;
		
		Statement statement = statements.next();
		Resource resource = new Resource(model, statement.getSubject(), false);
		return (T) resource.castTo(returnType);
	}

	public static <T extends Resource> T deserialize(String jsonObject, Class<T> returnType)
			throws JsonParseException, JsonMappingException {
		Object deserialized = de.dfki.km.json.JSONUtils.fromString(jsonObject);
		if (deserialized instanceof Map) {
			return deserialize((Map<String, Object>) deserialized, returnType);
		} else if (deserialized instanceof List) {
			T result = null;
			Model relatedMetadata = RDF2Go.getModelFactory().createModel().open();
			
			List<? extends Resource> resources = deserializeCollection((List<Object>) deserialized);
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
		} else {
			throw new IllegalArgumentException("The following JSON could not be deserialized into" +
					" an RDF resource instance: "+jsonObject);
		}
	}

	public static List<? extends Resource> deserializeCollection(List<Object> collection)
			throws JsonParseException, JsonMappingException {
		RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
		List<Resource> results = new ArrayList<Resource>();
		
		Model callbackModel = callback.getModel();
		for (Object object : collection) {
			callbackModel.removeAll();
			jsonldProcessor.triples(object, callback);
			ClosableIterator<Statement> statements = callbackModel.findStatements(Variable.ANY, RDF.type, Variable.ANY);
			if (!statements.hasNext())
				break;
			
			Statement statement = statements.next();
			Class<?> clazz = guessClass(statement.getObject().asURI());

			Model resourceModel = RDF2Go.getModelFactory().createModel().open();
			resourceModel.addAll(callbackModel.iterator());
			Resource resource = new Resource(resourceModel, statement.getSubject(), false);
			results.add((Resource) resource.castTo(clazz));
		}
		if (callbackModel != null) {
			callbackModel.close();
		}
		
		return results;
	}

	public static List<? extends Resource> deserializeCollection(String jsonArray)
			throws JsonParseException, JsonMappingException {
		return deserializeCollection((List<Object>) de.dfki.km.json.JSONUtils.fromString(jsonArray));
	}

	/**
	 * Serializes an RDF resource into JSON-LD. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a key-value map with representing the JSON-LD document
	 */
	public static <T extends Resource> Object serialize(T resource) {
    	jsonldSerializer.reset();
    	addDefaultPrefixes(jsonldSerializer);
		jsonldSerializer.importModel(resource.getModel());
    	return jsonldSerializer.asObject();
	}
	
	/**
	 * Serializes an RDF resource into JSON-LD into a string. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a string representation of the JSON-LD document
	 */
	public static <T extends Resource> String serializeAsString(T resource) {
    	jsonldSerializer.reset();
    	addDefaultPrefixes(jsonldSerializer);
		jsonldSerializer.importModel(resource.getModel());
    	return jsonldSerializer.asString();
	}
	
	/**
	 * Serializes an collection of RDF resources into JSON-LD. 
	 * 
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a list of key-value maps with representing the JSON-LD documents
	 */
	public static <T extends Resource> List<Object> serializeCollection(T... collection) {
    	jsonldSerializer.reset();
    	addDefaultPrefixes(jsonldSerializer);
    	for (T resource : collection) {
    		jsonldSerializer.importModel(resource.getModel());
    	}
    	return (List<Object>) jsonldSerializer.asObject();
	}

	/**
	 * Serializes an collection of RDF resources into JSON-LD into a string. 
	 * 
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a string representation of the JSON-LD documents
	 */
	public static <T extends Resource> String serializeCollectionAsString(T... collection) {
    	jsonldSerializer.reset();
    	addDefaultPrefixes(jsonldSerializer);
    	for (T resource : collection) {
    		jsonldSerializer.importModel(resource.getModel());
    	}
    	return jsonldSerializer.asString();
	}

	private static Class<? extends Resource> guessClass(URI type) {
		Class<? extends Resource> clazz = null;
		if (type != null)	clazz = Class4Type.getClassForType(type);
		if (clazz == null)	clazz = (Class<? extends Resource>) Resource.class; // by default everything is a Resource
		return clazz;
	}
	
	private static void addDefaultPrefixes(JSONLDSerializer jsonldSerializer) {
    	jsonldSerializer.setPrefix(RDF.RDF_NS, "rdf");
    	jsonldSerializer.setPrefix(RDFS.NAMESPACE, "rdfs");
    	jsonldSerializer.setPrefix(XMLSchema.NAMESPACE, "xsd");

    	jsonldSerializer.setPrefix(NAO.NS_NAO.toString(), "nao");
    	jsonldSerializer.setPrefix(NCAL.NS_NCAL.toString(), "ncal");
    	jsonldSerializer.setPrefix(NCO.NS_NCO.toString(), "nco");
    	jsonldSerializer.setPrefix(NEXIF.NS_NEXIF.toString(), "nexif");
    	jsonldSerializer.setPrefix(NFO.NS_NFO.toString(), "nfo");
    	jsonldSerializer.setPrefix(NID3.NS_NID3.toString(), "nid3");
    	jsonldSerializer.setPrefix(NIE.NS_NIE.toString(), "nie");
    	jsonldSerializer.setPrefix(PIMO.NS_PIMO.toString(), "pimo");

    	jsonldSerializer.setPrefix(DLPO.NS_DLPO.toString(), "dlpo");
    	jsonldSerializer.setPrefix(PPO.NS_PPO.toString(), "ppo");
	}
	
}
