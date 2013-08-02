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

package eu.dime.ps.semantic.model;

import java.util.UUID;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract factory which provides some convenient methods for managing 'models',
 * resources and URIs.
 * 
 * This factory must be extended by factories for the different vocabularies.
 *  
 * @author Ismael Rivera
 */
public abstract class ResourceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	/**
	 * Creates an empty RDF2Go model, and opens the connection to it
	 * @return the RDF2Go model
	 */
	protected final Model createModel() {
		return RDF2Go.getModelFactory().createModel().open();
	}
	
	/**
	 * Creates an empty RDF2Go model for a given context, and opens the
	 * connection to it
	 * @param contextUri context URI for the model
	 * @return the RDF2Go model
	 */
	protected final Model createModel(URI contextUri) {
		return RDF2Go.getModelFactory().createModel(contextUri).open();
	}
	
	protected final URI generateUniqueURI() {
		return new URIImpl("urn:uuid:"+UUID.randomUUID(), true);	
	}
	
	public Resource createResource(URI type) {
		return createResource(type, generateUniqueURI());
	}
	
	public Resource createResource(URI type, URI resourceUri) {
		Resource resource = new Resource(createModel(), resourceUri, true);
		resource.getModel().addStatement(resource, RDF.type, type);
		return resource;
	}
	
	public <T extends Resource> T createResource(Class<T> returnType) {
		return createResource(returnType, generateUniqueURI());
	}

	@SuppressWarnings("unchecked")
	public <T extends Resource> T createResource(Class<T> returnType, URI resourceUri) {
		Resource resource = new Resource(createModel(), resourceUri, true);
		resource.getModel().addStatement(resourceUri, RDF.type, getUriOfClass(returnType));
		return (T) resource.castTo(returnType);
	}
	
	/**
	 * Returns the RDFS Class URI for a model POJO class. The POJO class must contain a field
	 * named 'RDFS_CLASS', and its value should be the class URI.
	 * 
	 * @param clazz the Java class to extract the URI
	 * @return the RDFS Class URI
	 */
	public static <T extends Resource> URI getUriOfClass(Class<T> clazz) {
		URI typeUri = null;
		try {
			typeUri = (URI) clazz.getDeclaredField("RDFS_CLASS").get("");
			logger.debug("RDFS_CLASS is "+typeUri+" for class "+clazz.toString());
		} catch (Exception e) {
			logger.error("RDFS_CLASS attribute is not defined for the class " + clazz.toString());
		}
		return typeUri;
	}

}
