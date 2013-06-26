package eu.dime.ps.controllers.infosphere.manager;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dcon.Situation;

/**
 * Manager for situation management.
 * 
 * @author Ismael Rivera
 */
public interface SituationManager extends InfoSphereManager<Situation> {

	Situation getByName(String name) throws InfosphereException;
	
	/**
	 * Activates the situation for the user.
	 * It indicates that the user is at a specific situation at the moment.
	 * 
	 * @param situationId identifier of the situation
	 * @throws InfosphereException if the situation doesn't not exist or cannot be activated
	 */
	void activate(String situationId) throws InfosphereException;

	/**
	 * Deactivates the situation for the user.
	 * It indicates that the situation is no longer valid for the user.
	 * 
	 * @param situationId identifier of the situation
	 * @throws InfosphereException if the situation doesn't not exist or cannot be deactivated
	 */
	void deactivate(String situationId) throws InfosphereException;

}