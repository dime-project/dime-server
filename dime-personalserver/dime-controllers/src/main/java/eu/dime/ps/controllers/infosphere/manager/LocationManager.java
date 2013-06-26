package eu.dime.ps.controllers.infosphere.manager;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.pimo.Location;

/**
 * Manager for locations management.
 * 
 * @author Ismael Rivera
 */
public interface LocationManager extends InfoSphereManager<Location> {

	Location getByPlacemarkId(String placemarkId) throws InfosphereException;
	
}