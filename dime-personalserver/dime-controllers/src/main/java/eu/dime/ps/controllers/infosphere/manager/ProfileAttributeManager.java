package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;

/**
 * Management of profile attributes.
 * 
 * @author Ismael Rivera
 */
public interface ProfileAttributeManager extends InfoSphereManager<Resource> {
	
	/**
	 * Retrieves all the instances containing the individual pieces of
	 * profile information for a specific profile or profile card.
	 * 
	 * @param containerId is the identifier of profile or profile card which contains
	 *        a set of profile attributes
	 * @return
	 */
	Collection<Resource> getAllByContainer(String containerId) throws InfosphereException;
	
}
