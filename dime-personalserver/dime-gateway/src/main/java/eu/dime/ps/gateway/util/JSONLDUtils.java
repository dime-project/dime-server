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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.jsonldjava.core.JSONLD;
import com.github.jsonldjava.core.JSONLDProcessingError;
import com.github.jsonldjava.core.JSONLDTripleCallback;
import com.github.jsonldjava.core.Options;
import com.github.jsonldjava.impl.RDF2GoRDFParser;
import com.github.jsonldjava.impl.RDF2GoTripleCallback;
import com.github.jsonldjava.utils.JSONUtils;

import eu.dime.ps.semantic.model.Class4Type;
import eu.dime.ps.semantic.vocabulary.DefaultOntologies;
import eu.dime.ps.semantic.vocabulary.DefaultOntologies.Ontology;

/**
 * Deals with JSONLD serialization and deserialization for RDF.
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
	 * Deserializes a JSONLD document to RDF. The JSONLD document may contain several
	 * (related) resources, but only one resource of the requested <i>returnType</i>.
	 * 
	 * @param object the JSONLD document
	 * @param returnType the class representing the RDFS resource
	 * @return a RDFReactor resource containing the RDF data
	 */
	public static <T extends Resource> T deserialize(Object object, Class<T> returnType)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
		ModelSet modelSet = toRDF(object, callback);

		// find the resource URI corresponding to the returnType
		URI subject = null;
		ClosableIterator<Statement> statements = modelSet.findStatements(Variable.ANY, Variable.ANY, RDF.type, Variable.ANY);
		while (statements.hasNext()) {
			Statement statement = statements.next();
			URI type = statement.getObject().asURI();
			if (returnType.equals(guessClass(type))) {
				if (subject == null) {
					subject = statement.getSubject().asURI();
				}
				// ensure the RDF metadata only contains ONE resource of the requested type
				else if (!subject.equals(statement.getSubject())) {
					throw new IllegalArgumentException("The following JSON contains more than one resource of type '"
							+ returnType.getName() + "', please consider calling instead deserializeCollection(Object):\n"
							+ JSONUtils.toString(object));
				}
			}
		}
		statements.close();

		if (subject == null) {
			throw new IllegalArgumentException("The following JSON does not contain any resource of type '"
					+ returnType.getName() + "':\n" + JSONUtils.toString(object));
		}
		
		Model resourceModel = RDF2Go.getModelFactory().createModel().open();
		resourceModel.addAll(modelSet.iterator());
		Resource resource = new Resource(resourceModel, subject, false);
		
		modelSet.close();
		
		return (T) resource.castTo(returnType);
	}

	/**
	 * Same as {@link #deserialize(Object, Class)}, instead the JSONLD parameter is a String.
	 */
	public static <T extends Resource> T deserialize(String jsonObject, Class<T> returnType)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		return deserialize(JSONUtils.fromString(jsonObject), returnType);
	}

	/**
	 * Deserializes a collection of JSONLD documents into RDFReactor objects.
	 * 
	 * @param collection a collection of JSONLD documents
	 * @return a list of RDFReactor resources, one for each JSON object
	 */
	@Deprecated
	public static List<? extends Resource> deserializeCollection(List<Object> collection)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		List<Resource> results = new ArrayList<Resource>();
		
		for (Object object : collection) {
			RDF2GoTripleCallback callback = new RDF2GoTripleCallback();
			ModelSet deserialized = toRDF(object, callback);
			
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

	/**
	 * Same as {@link #deserializeCollection(List)}, instead the JSONLD parameter is a String.
	 */
	@Deprecated
	public static List<? extends Resource> deserializeCollection(String jsonArray)
			throws JsonParseException, JsonMappingException, JSONLDProcessingError {
		return deserializeCollection((List<Object>) JSONUtils.fromString(jsonArray));
	}

	/**
	 * Serializes an RDF resource into JSONLD. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a key-value map with representing the JSONLD document
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	public static <T extends Resource> Object serialize(T resource) throws JSONLDProcessingError {
		return serialize(null, resource);
	}

	/**
	 * Serializes an RDF resource into JSONLD. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the RDFReactor resource to serialize
	 * @return a key-value map with representing the JSONLD document
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	public static <T extends Resource> Object serialize(Map<String, String> prefixes, T resource)
			throws JSONLDProcessingError {
		if (INCLUDE_DEFAULT_PREFIXES) addDefaultPrefixes(resource.getModel());
		if (prefixes != null) addPrefixes(prefixes, resource.getModel());
		return JSONLD.fromRDF(resource.getModel(), OPTIONS, RDF_PARSER);
	}

	/**
	 * Serializes an RDF resource into JSONLD into a string. 
	 * 
	 * @param resource the RDFReactor resource to serialize
	 * @return a string representation of the JSONLD document
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	public static <T extends Resource> String serializeAsString(T resource) throws JSONLDProcessingError {
		return serializeAsString(null, resource);
	}

	/**
	 * Serializes an RDF resource into JSONLD into a string. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the RDFReactor resource to serialize
	 * @return a string representation of the JSONLD document
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	public static <T extends Resource> String serializeAsString(Map<String, String> prefixes, T resource)
			throws JSONLDProcessingError {
		if (INCLUDE_DEFAULT_PREFIXES) addDefaultPrefixes(resource.getModel());
		if (prefixes != null) addPrefixes(prefixes, resource.getModel());
		return JSONUtils.toString(JSONLD.fromRDF(resource.getModel(), OPTIONS, RDF_PARSER));
	}
	
	/**
	 * Serializes an collection of RDF resources into JSONLD. 
	 * 
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a list of key-value maps with representing the JSONLD documents
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	@Deprecated
	public static <T extends Resource> List<Object> serializeCollection(T... collection)
			throws JSONLDProcessingError {
		return serializeCollection(null, collection);
	}

	/**
	 * Serializes an collection of RDF resources into JSONLD. 
	 * 
	 * @param prefixes a key-value Map with namespace prefixes and fully-qualified URIs
	 * @param resource the array of RDFReactor resources to serialize
	 * @return a list of key-value maps with representing the JSONLD documents
	 * @throws JSONLDProcessingError if serialization to JSONLD fails
	 */
	@Deprecated
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
	
	private static ModelSet toRDF(Object object, JSONLDTripleCallback callback) throws JSONLDProcessingError {
		ModelSet modelSet = (ModelSet) JSONLD.toRDF(object, callback);
		ModelSet result = RDF2Go.getModelFactory().createModelSet();
		result.open();
		
		// JSONLD will serialize Double/Float objects using the canonical representation
		// in scientific notation, using the default Locale of the JVM.
		// we always want to use the Java's default standard form (see Double.parseDouble & Double.toString).
		final DecimalFormat defaultFormat = new DecimalFormat("0.0###############E0");
		final ClosableIterator<Statement> statements = modelSet.findStatements(Variable.ANY, Variable.ANY, Variable.ANY, Variable.ANY);
		while (statements.hasNext()) {
			final Statement statement = statements.next();
			final Node node = statement.getObject();
			if (node instanceof DatatypeLiteral) {
				final DatatypeLiteral literal = (DatatypeLiteral) node;
				final URI datatype = literal.getDatatype();
				if (datatype.equals(XSD._double)
						|| datatype.equals(XSD._float)
						|| datatype.equals(XSD._decimal)) {
					final String value = literal.getValue();
					try {
						final double number = defaultFormat.parse(value).doubleValue();
						final DatatypeLiteral formattedLiteral = new DatatypeLiteralImpl(Double.toString(number), datatype);
						result.addStatement(statement.getContext(), statement.getSubject(), statement.getPredicate(), formattedLiteral);
					} catch (ParseException e) {
						throw new JSONLDProcessingError("Couldn't parse '" + value + "' using " + Locale.getDefault() + "' locale: " + e.getMessage());
					}
				} else {
					result.addStatement(statement);
				}
			} else {
				result.addStatement(statement);
			}
		}
		statements.close();
		
		return result;
	}
	
}
