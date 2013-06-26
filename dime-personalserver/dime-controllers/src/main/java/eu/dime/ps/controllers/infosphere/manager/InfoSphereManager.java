package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * 
 * @author Ismael Rivera
 */
public interface InfoSphereManager<T extends Resource> {
	
	/**
	 * Retrieves the Person instance of the owner/main user of the infosphere.
	 * 
	 * @return a person object owner of the infosphere
	 * @throws InfosphereException
	 */
	public Person getMe() throws InfosphereException;

	/**
	 * 
	 * @param resourceId
	 * @return
	 * @throws InfosphereException
	 */
	public boolean exist(String resourceId) throws InfosphereException;

	/**
	 * Retrieves a resource of any arbitrary type.
	 *  
	 * @param resourceId the resource identifier
	 * @param returnType the class of the resource to retrieve
	 * @return an instance of the 'returnType' class with the data of the resource
	 */
	public <R extends Resource> R get(String resourceId, Class<R> returnType)
			throws InfosphereException;

	public <R extends Resource> R get(String resourceId, Class<R> returnType, List<URI> properties)
			throws InfosphereException;

	public T get(String resourceId) throws InfosphereException;

	public Collection<T> getAll() throws InfosphereException;

	public Collection<T> getAll(List<URI> properties) throws InfosphereException;

	public T get(String entityId, List<URI> properties) throws InfosphereException;

	public void add(T entity) throws InfosphereException;

	public void update(T entity) throws InfosphereException;

	public void update(T entity, boolean override) throws InfosphereException;

	public void remove(String entityId) throws InfosphereException;

}
