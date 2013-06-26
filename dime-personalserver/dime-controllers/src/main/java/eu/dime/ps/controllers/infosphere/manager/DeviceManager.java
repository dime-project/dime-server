package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.ddo.Device;

/**
 * Manager for device management.
 * 
 * @author Ismael Rivera
 */
public interface DeviceManager extends InfoSphereManager<Device> {

	Collection<Device> getAllOwnedBy(String personId)
			throws InfosphereException;

	Collection<Device> getAllOwnedBy(String personId, List<URI> properties)
			throws InfosphereException;

}