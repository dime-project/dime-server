package eu.dime.ps.semantic.query;

import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.exception.NotFoundException;

/**
 * Data container (model, RDF store) which can be query using SPARQL,
 * or programmatically using {@link Query}.
 * 
 * @author Ismael Rivera
 */
public interface Queryable extends Sparqlable {
	
	/**
	 * Creates a new {@link Query} object, and adds a first triple pattern <?, rdf:type, returnType>
	 * to return all resources of a specific type.
	 * 
	 * @param returnType the class of the resources to query for
	 * @return the Query object
	 */
	<T extends org.ontoware.rdfreactor.schema.rdfs.Resource> Query<T> find(Class<T> returnType);
		
	/**
	 * Returns the resource instance with its associated metadata.
	 * 
	 * @param instanceIdentifier URI or blank node identifying the resource
	 * @return the resource instance
	 * @throws NotFoundException
	 */
	Resource get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier)
			throws NotFoundException;

	/**
	 * Same as {@link #get(org.ontoware.rdf2go.model.node.Resource)}, except
	 * only a set of properties for the resource are retrieved.
	 * 
	 * @param instanceIdentifier
	 * @param properties set of properties to fill with values in the resource
	 * @return an object of T, representing the resource
	 * @throws NotFoundException
	 */
	Resource get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			URI... properties) throws NotFoundException;

	/**
	 * Same as {@link #get(org.ontoware.rdf2go.model.node.Resource)}, except that
	 * the resource is cast to a <em>T</em>.
	 * 
	 * @param instanceIdentifier
	 * @param returnType class of the object for casting the resource instance
	 * @return an object of T, representing the resource
	 * @throws NotFoundException
	 */
	<T extends Resource> T get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException;

	/**
	 * Same as {@link #get(org.ontoware.rdf2go.model.node.Resource, Class)}, except
	 * only a set of properties for the resource are retrieved.
	 * 
	 * @param instanceIdentifier
	 * @param returnType
	 * @param properties set of properties to fill with values in the resource
	 * @return an object of T, representing the resource
	 * @throws NotFoundException
	 */
	<T extends Resource> T get(org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException;

	/**
	 * Same as {@link #get(org.ontoware.rdf2go.model.node.Resource, Class)}, except
	 * the resource must be contained in the graph URI provided.
	 * 
	 * @param graphUri
	 * @param instanceIdentifier
	 * @param returnType
	 * @return an object of T, representing the resource
	 * @throws NotFoundException
	 */
	<T extends Resource> T get(URI graphUri, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType) throws NotFoundException;

	/**
	 * Same as {@link #get(org.ontoware.rdf2go.model.node.Resource, Class, URI...)}, except
	 * the resource must be contained in the graph URI provided.
	 * 
	 * @param graphUri
	 * @param instanceIdentifier
	 * @param returnType
	 * @param properties set of properties to fill with values in the resource
	 * @return an object of T, representing the resource
	 * @throws NotFoundException
	 */
	<T extends Resource> T get(URI graphUri, org.ontoware.rdf2go.model.node.Resource instanceIdentifier,
			Class<T> returnType, URI... properties) throws NotFoundException;

}
