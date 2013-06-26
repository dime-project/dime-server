package eu.dime.ps.semantic.service;

import java.util.List;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.dcon.Aspect;

/**
 * Provides a way to interact with the live context (and previous context)
 * of a device. It allows retrieving and updating the different aspects of
 * the context, and metadata/resources related to the aspects.
 * 
 * @author Ismael Rivera
 */
public interface LiveContextService {
	
	public Model getLiveContext();
	public Model getPreviousContext();

	/**
	 * Retrieves the first instance found of a specific type which
	 * is part of the live context.
	 * 
	 * @param returnType class (type) of the resource to be returned
	 * @return the first instance for a type
	 */
	<T extends Aspect> T get(Class<T> returnType);

	/**
	 * Retrieves the URIs of all aspects used in the live context.
	 * 
	 * @return a list with URIs of all aspects used in the live context
	 */
	List<URI> getAspects();
	
	/**
	 * Retrieves an instance of Element as an object of the returnType
	 * specified. It only returns basic (not context) metadata about
	 * the element.
	 * 
	 * To also fetch context metadata about the element refer to 
	 * {@link #get(URI, Class, URI)}, specifying the data source of
	 * the data. 
	 * 
	 * @param element URI identifying the element
	 * @param returnType class (type) of the resource to be returned
	 * @return the resource instance
	 * @throws NotFoundException if element cannot be found
	 */
	<T extends Resource> T get(URI element, Class<T> returnType)
			throws NotFoundException;

	/**
	 * Same as {@link #get(URI, Class)}, but it also returns context data
	 * captured by the specified data source.
	 * 
	 * @param element URI identifying the element
	 * @param returnType class (type) of the resource to be returned
	 * @param datasource URI identifying the data source
	 * @return the resource instance
	 * @throws NotFoundException if element cannot be found
	 */
	<T extends Resource> T get(URI element, Class<T> returnType, URI datasource)
			throws NotFoundException;

	/**
	 * Retrieves the URIs of all elements used in the live context.
	 * 
	 * @return a list with URIs of all elements used in the live context
	 */
	List<URI> getElements();
	
	/**
	 * Retrieves a session for updating the live context for a particular data source.
	 * All changes done within this session will be attached to the data source, and
	 * context data from others data sources won't be affected.
	 * 
	 * @param dataSource URI of the data source (account, device, etc.)
	 * @return a session object
	 */
	LiveContextSession getSession(URI dataSource);
	
}
