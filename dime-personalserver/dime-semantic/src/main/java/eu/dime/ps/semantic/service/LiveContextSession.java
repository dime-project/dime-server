package eu.dime.ps.semantic.service;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.model.dcon.Aspect;
import eu.dime.ps.semantic.service.exception.LiveContextException;

/**
 * LiveContextSession provides a way to update information in the live context. 
 * 
 * @author Ismael Rivera
 */
public interface LiveContextSession {

	/**
	 * Makes all changes made since the previous commit/rollback permanent.
	 * This method should be used only when auto-commit mode has been disabled.
	 * 
	 * @throws LiveContextException if an RDF repository access error occurs, if this method is
	 *         called on a closed session or the session is in auto-commit mode
	 */
	void commit() throws LiveContextException;

	/**
	 * Undoes all changes made in the current session.
	 * This method should be used only when auto-commit mode has been disabled.
	 * 
	 * @throws LiveContextException if an RDF repository access error occurs, this method is
	 *         called on a closed session or session is in auto-commit mode
	 */
	void rollback() throws LiveContextException;

	/**
	 * Retrieves the current auto-commit mode for this session.
	 * 
	 * @return the current state of this session's auto-commit mode
	 * @throws LiveContextException SQLException - if an RDF repository access error occurs or this method is called on a closed session
	 */
	boolean getAutoCommit() throws LiveContextException;

	/**
	 * Sets this connection's auto-commit mode to the given state. If a connection is in auto-commit mode, then all its SQL statements will be executed and committed as individual sessions. Otherwise, its SQL statements are grouped into sessions that are terminated by a call to either the method commit or the method rollback. By default, new connections are in auto-commit mode.
	 * 
	 * @param autoCommit true to enable auto-commit mode; false to disable it
	 * @throws LiveContextException if an RDF repository access error occurs, or this method is called on a closed session
	 */
	void setAutoCommit(boolean autoCommit) throws LiveContextException;

	/**
	 * Releases this session's RDF repository connection.<br />
	 * Calling the method close on a session that is already closed is a no-op.<br />
	 * <br />
	 * It is strongly recommended that an application explicitly commits or rolls back an active session
	 * prior to calling the close method. If the close method is called and there is an active session,
	 * the results are implementation-defined.
	 *  
	 * @throws LiveContextException if an RDF repository access error occurs
	 */
	void close() throws LiveContextException;

	/**
	 * Attach a set of the elements to an aspect.
	 * This action removes all previous information for a given relation (property), and adds the new elements.
	 * 
	 * @param aspect subclass of Aspect representing the aspect to be updated
	 * @param property URI of the property relating the aspect with its elements
	 * @param elementUris set of element instances to be updated.
	 * @throws LiveContextException if the operation cannot be performed
	 */
	void set(Class<? extends Aspect> aspect, URI property, URI... elementUris) throws LiveContextException;
	
	/**
	 * Same as {@link #set(Class, URI, URI...)}, except the elements are RDFReactor objects. 
	 */
	void set(Class<? extends Aspect> aspect, URI property, Resource... elements) throws LiveContextException;

	/**
	 * Sets literal values to a particular {@link eu.dime.ps.semantic.model.dcon.Element} identified by its URI.
	 * This action removes all previous values for a given relation (property), and adds the new ones.
	 * 
	 * @param resource URI of the element to updated
	 * @param property URI of the property relating the element with its values
	 * @param values set of values to be attached to the element.
	 * @throws LiveContextException if the operation cannot be performed
	 */
	void set(URI resource, URI property, Object... values) throws LiveContextException;

	/**
	 * Attach a set of the elements to an aspect.
	 * 
	 * @param aspect subclass of Aspect representing the aspect to be updated
	 * @param property URI of the property relating the aspect with its elements
	 * @param elementUris set of element instances to be updated.
	 * @throws LiveContextException if the operation cannot be performed
     */
	void add(Class<? extends Aspect> aspect, URI property, URI... elementUris) throws LiveContextException;
    
	/**
	 * Same as {@link #add(Class, URI, URI...)}, except the elements are RDFReactor objects. 
	 */
	void add(Class<? extends Aspect> aspect, URI property, Resource... elements) throws LiveContextException;

	/**
	 * Adds literal values to a particular {@link eu.dime.ps.semantic.model.dcon.Element} identified by its URI.
	 * 
	 * @param resource URI of the element to updated
	 * @param property URI of the property relating the element with its values
	 * @param values set of values to be attached to the element.
	 * @throws LiveContextException if the operation cannot be performed
	 */
	void add(URI resource, URI property, Object... values) throws LiveContextException;
	
    /**
     * Removes all relations to elements from a given Aspect. The relations are determined by a property.
     * 
     * @param aspect subclass of Aspect representing the aspect to be updated
	 * @param property URI of the property relating the aspect with its elements
	 * @throws LiveContextException if the operation cannot be performed
     */
	void remove(Class<? extends Aspect> aspect, URI property) throws LiveContextException;
	
	/**
     * Removes all relations to values from a given {@link eu.dime.ps.semantic.model.dcon.Element}.
     * The relations are determined by a property.
	 * 
	 * @param resource URI of the element to updated
	 * @param property URI of the property relating the element with its values
	 * @throws LiveContextException if the operation cannot be performed
	 */
	void remove(URI resource, URI property) throws LiveContextException;
	
	/**
	 * Same as {@link #remove(Class, URI)}, except only relations to specific elements are removed. 
	 */
	void remove(Class<? extends Aspect> aspect, URI property, URI... elementUris) throws LiveContextException;
	
	/**
	 * Same as {@link #remove(URI, URI)}, except only relations to specific values are removed. 
	 */
	void remove(URI resource, URI property, Object... values) throws LiveContextException;

}
