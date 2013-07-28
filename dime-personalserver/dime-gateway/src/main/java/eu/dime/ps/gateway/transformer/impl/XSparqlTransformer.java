/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.gateway.transformer.impl;

import ie.deri.smile.rdf.util.ModelUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.IOUtils;
import org.deri.xsparql.evaluator.XSPARQLEvaluator;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.commons.util.FileUtils;
import eu.dime.ps.gateway.service.ResourceAttributes;
import eu.dime.ps.gateway.transformer.FormatUtils;
import eu.dime.ps.gateway.transformer.Transformer;
import eu.dime.ps.gateway.transformer.TransformerException;

/**
 * Implementation of Transformer based on XSPARQL, which is responsible for serializing / 
 * deserializing data to / from XML to digital me types. The real implementation
 * of this will use the XSparql API which will result in a very neat and 
 * extensible mapping process.
 * 
 * Specific mapper configuration will be resolved by both the service identifier
 * and the Personal Server API Path in use. 
 * 
 * Note that this class is thread safe.
 * 
 * @author Will Fleury
 * @author Ismael Rivera
 */
public class XSparqlTransformer implements Transformer {

	private static final Logger logger = LoggerFactory.getLogger(XSparqlTransformer.class);
		
	/**
	 * This is the directory in the resources where the xsparql files 
	 * are located.
	 */
	protected final String resourcesPath = "transformer";
		
	protected final String config = "transformer.properties";
		
	/**
	 * This properties instance contains the filename of the xsparql which
	 * can process the given serviceIdentifier & path. 
	 * NOTE: If the serviceIdentifier_path combination is not found in this
	 * file then it defaults back to 
	 * resourcesPath/serviceIdentifier/resourceType.xsparql
	 */
	protected Properties xsparqlResourceProperties;
	
	/**
	 * This is the identifier used as a sub path when loading xsparql 
	 * resources for any service which uses the Open Social API.
	 */
	protected final String opensocialIdentifier = "opensocial";
	
	/**
	 * This is a list of all services which use the Open Social API so that
	 * the we can replace their identifiers with the default "opensocial"
	 * when loading the xsparql files..
	 */
	protected final List<String> opensocialIdentifiers = 
			Arrays.asList("hi5", "99factors", "myspace", "orkut", "netlog", "sonico", "friendster", "ning");
	
	/**
	 * This is used to cache any loaded xsparql queries in memory so that
	 * a file read is not required every time they are used..
	 * Key is the xsparql resource name which is loaded in the value!
	 */
	protected ConcurrentMap<String, String> queryCache; 
	
	/**
	 * Contains the hash/checksum of the XML returned for every request,
	 * grouped by service/account and path.
	 * It's used to avoid repeated transformations for the same XML data.
	 */
	private ConcurrentMap<String, String> hashes;
	
	public XSparqlTransformer() {
		//create thread safe cache
		queryCache = new ConcurrentHashMap<String, String>();
		
		//thread safe set of responses hashes
		hashes = new ConcurrentHashMap<String, String>();
		
		//load some extra settings
		loadProperties();
	}

	/**
	 * @throws TransformerException {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 */
	@Override
	public <T extends Resource> Collection<T> deserialize(String xml, String serviceIdentifier,
			String path, Class<T> returnType) {
		String hash = null;
		String previousHash = hashes.get(serviceIdentifier + path);
		try {
			hash = FileUtils.doSHA1Hash(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			if (hash.equals(previousHash)) {
				logger.info("Same XML already transformed for service "+serviceIdentifier+" at "+path+": returning empty collection!");
				return new ArrayList<T>(0);
			}
			hashes.putIfAbsent(serviceIdentifier + path, hash);
		} catch (IOException e) {}
		
		return performDeserialization(xml, serviceIdentifier, path, returnType);
	}

	/**
	 * @throws TransformerException {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 */
	@Override
	public <T extends Resource> Collection<Collection<T>> deserializeCollection(Collection<String> xmls, 
			String serviceIdentifier, String path, Class<T> returnType) {
		
		Collection<Collection<T>> results = new ArrayList<Collection<T>>(xmls.size());
		
		for (String xml : xmls) {
			results.add(deserialize(xml, serviceIdentifier, path, returnType));
			// do not perform optimization for collections
			hashes.remove(serviceIdentifier + path);
		}
		
		return results;
	}
	
	/**
	 * @throws TransformerException {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 */
	@Override
	public String serialize(Collection<? extends Resource> resources, 
			String serviceIdentifier, String path) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * @throws TransformerException {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 */
	@Override
	public Collection<String> serializeCollection(
			Collection<Collection<? extends Resource>> resources,
			String serviceIdentifier, String path) {
		
		Collection<String> results = new ArrayList<String>(resources.size());
		
		for (Collection<? extends Resource> resource : resources) {
			results.add(serialize(resource, serviceIdentifier, path));
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param xml the service data to process.
	 * @param serviceIdentifier the service identifier
	 * @param path the Personal server api path identifier
	 * @return a collection of resources 
	 * 
	 * @throws TransformerException if there is a problem during the deserialization
	 * @throws UnsupportedOperationException if there is no mapper available for 
	 * the particular serviceIdentifier/path combination
	 */
	private <T extends Resource> Collection<T> performDeserialization(String xml, String serviceIdentifier,
			String path, Class<T> returnType) {
		logger.debug(serviceIdentifier+" "+path+":\n"+xml);
		File xmlDoc = null;
		InputStream queryStream = null;
		Collection<T> resources = new ArrayList<T>();
		
		try {
			//ensure its always right case - better to just have lower case anyway..
			serviceIdentifier = serviceIdentifier.toLowerCase();
	
			//some external services return their data using a XSPARQL reserved keyword,
			//or the format of values such as date is not ISO 8601, etc. and all that
			//is normalised here before executing the XSPARQL query
			if ((new ResourceAttributes(path).getResourceType().equals(ResourceAttributes.ATTR_LIVEPOST)) && 
				(serviceIdentifier.equals("linkedin"))) {
					xml = FormatUtils.fixLivePostXMLNodes(xml);
			}  else if ((new ResourceAttributes(path).getResourceType().equals(ResourceAttributes.ATTR_LIVEPOST)) && 
					(serviceIdentifier.equals("facebook")))	{
				xml = FormatUtils.changeXMLStringNodeName(xml, "from", "user");
			} else if ((new ResourceAttributes(path).getResourceType().equals(ResourceAttributes.ATTR_LIVEPOST)) && 
					(serviceIdentifier.equals("twitter"))) {
				xml = FormatUtils.fixTwitterDateFormats(xml);
		    } else if ((new ResourceAttributes(path).getResourceType().equals(ResourceAttributes.ATTR_PROFILE)) && 
					(serviceIdentifier.equals("googleplus"))) {
		    	String[][][] xmlElements = {{{"organizations","e","organization"},{"image","url","thumbnailUrl"},{"","url","profileUrl"},{"urls","e","url"}}};
				xml = FormatUtils.changeGooglePlusXMLNodeNames(xml, xmlElements);
			}
			
			xmlDoc = File.createTempFile("doc", ".xml");
						
			//be careful with the god damn utf-8
			IOUtils.copy(new ByteArrayInputStream(xml.getBytes("UTF-8")), new FileOutputStream(xmlDoc));

			//lets see if we have already loaded this query..?						
			//get resource name for this service & path combo. This can
			//then be used as the cache key..
			String xqueryResource = getXQueryResourceName(serviceIdentifier, path);
						
			String cachedQuery = queryCache.get(xqueryResource);
			
			//ok lets get it 
			if (cachedQuery == null) {
				// loads the XSPARQL query
				// we get this as a InputStream or String, and need to write it to a temporary
				// file in order to set it in the XSPARQL query
				queryStream = this.getClass().getClassLoader().getResourceAsStream(xqueryResource);
				if (queryStream == null)
						throw new UnsupportedOperationException(
										"No transformer available for "+serviceIdentifier+
										" "+path);

				logger.debug("XSparql resource loaded for {}, path: {}  was: {}", 
						new Object[]{serviceIdentifier, path, xqueryResource});
				
				//lets not forget about our UTF-8 friend here either..
				//shouldn't ever be any non utf-8 chars in the query but..
				cachedQuery = IOUtils.toString(queryStream, "UTF-8");
				
				queryCache.putIfAbsent(xqueryResource, cachedQuery);
			}
						
			// sets the temporary xml file in the query
			String query = cachedQuery.replace("%doc%", xmlDoc.toURI().toString());
			
			// creates evaluator
			XSPARQLEvaluator evaluator = new XSPARQLEvaluator();
			
			// executes the query, and write the results in a string
			StringWriter writer = new StringWriter();
			
			evaluator.evaluate(new StringReader(query), writer);
			String content = writer.toString();
			
			// writes the results of the query in a model
			ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes("UTF-8"));
			ModelSet outputModel = null;
			ClosableIterator<Statement> resourceIt = null;
   
			try {
				outputModel = RDF2Go.getModelFactory().createModelSet();
				outputModel.open();
				ModelUtils.loadFromInputStream(bais, Syntax.Turtle, outputModel);

				URI typeUri = new URIImpl(returnType.getDeclaredField("RDFS_CLASS").get("").toString());
				resourceIt = outputModel.findStatements(Variable.ANY, Variable.ANY, RDF.type, typeUri);
				while (resourceIt.hasNext()) {
					org.ontoware.rdf2go.model.node.Resource instanceId = resourceIt.next().getSubject();
					Model resourceModel = RDF2Go.getModelFactory().createModel().open();
					ModelUtils.fetch(outputModel, resourceModel, instanceId, false, true, new URI[]{typeUri});
					Resource resource = new Resource(resourceModel, instanceId, false);
					resources.add((T) resource.castTo(returnType));
				}
				resourceIt.close();
			} finally {
				//cleanup resources.
				if (outputModel != null) outputModel.close();
				if (resourceIt != null) resourceIt.close();
			}
			
			return resources;
		} catch (UnsupportedOperationException e) {
			throw e;
		} catch (Exception e) {
			String msg = "Service Identifier: " + serviceIdentifier + "path: "+path + 
					"\nxml: "+xml;
			throw new TransformerException(msg, e);
		} finally {
			//remove the temporary file if it exists.
			if (xmlDoc != null)
				xmlDoc.delete();
			
			if (queryStream != null) {
				try { queryStream.close(); } catch (IOException e) { }
			}
		}
	}
	
	private String getMappedIdentifier(String serviceIdentifier) {
		//Get the identifier to use in the path. If the service is part
		//of the open social services, then use default open social path
		return opensocialIdentifiers.contains(serviceIdentifier) ? opensocialIdentifier : serviceIdentifier;
	}
	
	private String getXQueryResourceName(String serviceIdentifier, String path) {	
		//get the type e.g. the "persons" from "/persons/@me/all"
		ResourceAttributes resource = new ResourceAttributes(path);
		String type = resource.getResourceType();
		
		//Get the identifier to use in the path. If the service is part
		//of the open social services, then use default open social path
		String identifier = getMappedIdentifier(serviceIdentifier);						
		
		String xsparqlResourceName = identifier + "/" + type + ".xsparql";
		
		//this is the key used to find a specific xsparql resource
		//for this identifier & path.
		//NO SUPPORT for query parameters in the path/URL
		String key = identifier + "_" + path;
		
		//lets check to see if there is a particular xsparql file for the key
		xsparqlResourceName = xsparqlResourceProperties.getProperty(key, xsparqlResourceName);
		
		//Now combine the config path, identifier and resource type to get filename.
		return resourcesPath + "/" + xsparqlResourceName;
	}
	
	private void loadProperties() {
		Properties properties = new Properties();
		try {
			properties = PropertiesLoaderUtils.loadAllProperties(resourcesPath + "/" + config);
		} catch (Exception e) {
			logger.warn("Could not find transformer properties. "
					+ "Defaulting to serviceIdentifier/resourceType.xsparql ", e);
		}
		this.xsparqlResourceProperties = properties;
	}

}
