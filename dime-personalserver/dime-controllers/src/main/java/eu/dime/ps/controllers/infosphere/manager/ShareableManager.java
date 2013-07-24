package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.ForbiddenException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;

/**
 * Allows to:
 * <ul>
 *   <li>retrieve user's data respecting the privacy preference settings
 *   <li>add/update/remove resources to a specific account (other users' shared resources)
 * </ul>
 * 
 * @author Ismael Rivera
 */
public interface ShareableManager<T extends Resource> {

	/**
	 * Checks if the resource already exists.
	 * 
	 * @param resourceId identifier of the resource
	 * @return true if the resource exists; false otherwise
	 * @throws InfosphereException if the data store cannot be reached, or some other
	 *         major problem occurs.
	 */
	public boolean exist(String resourceId) throws InfosphereException;

	/**
	 * Retrieves a resource for the given resource identifier. The resource will only
	 * contain the metadata that is possible/meant to be shared with the requester.
	 *  
	 * @param resourceId
	 * @param requesterId account identifier representing the requester
	 * @return the resource object requested
	 * @throws InfosphereException
	 */
	public T get(String resourceId, String requesterId) throws NotFoundException, ForbiddenException, InfosphereException;

	/**
	 * Retrieves all resources shared through a specific user account. As in {@link get(String, String)},
	 * the resources being returned will only contain metadata shared with the requester.
	 * 
	 * @param sharedBy sender's user account identifier of the resources
	 * @param requesterId account identifier representing the requester
	 * @return a list of all resources accessible by the given user
	 * @throws InfosphereException if the user's account or the requester's account don't exist,
	 *         or the data store is not available
	 */
	public Collection<T> getAll(String sharedBy, String requesterId) throws InfosphereException;
	
	/**
	 * Adds a resource which was retrieved/gathered from another user's account, or from
	 * an online account of the owner of the PIM.
	 * 
	 * @param resource resource object with the known metadata for the resource
	 * @param sharedBy sender's user account identifier 
	 * @param sharedWith recipient's user account identifier
	 * @throws InfosphereException if the user's account doesn't exist, the data store is
	 *         not available, or a resource with the same identifier already exists
	 */
	public void add(T resource, String sharedBy, String sharedWith) throws InfosphereException;

	/**
	 * Updates the resource metadata of a given resource, in a specific user's account.
	 * 
	 * @param resource resource object with the known metadata for the resource
	 * @param sharedBy sender's user account identifier 
	 * @param sharedWith recipient's user account identifier
	 * @throws InfosphereException if the user's account doesn't exist, the data store is
	 *         not available, or the resource is unknown
	 */
	public void update(T resource, String sharedBy, String sharedWith) throws InfosphereException;
	
	/**
	 * Removes a resource and all its metadata from a given user's account.
	 * 
	 * @param resource resource object, only the identifier is important, its metadata
	 *        won't be used or taken into account
	 * @param sharedBy sender's user account identifier 
	 * @throws InfosphereException if the user's account doesn't exist, the data store is
	 *         not available, or the resource is unknown
	 */
	public void remove(T resource, String sharedBy) throws InfosphereException;

}
