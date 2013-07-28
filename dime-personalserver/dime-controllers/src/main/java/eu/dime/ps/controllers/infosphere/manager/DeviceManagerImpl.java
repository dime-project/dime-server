/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.DDO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Implements {@link DeviceManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class DeviceManagerImpl extends InfoSphereManagerBase<Device> implements DeviceManager {

	@Override
	public Device get(String deviceId) throws InfosphereException {
		return get(deviceId, new ArrayList<URI>(0));
	}
	
	@Override
	public Device get(String deviceId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(deviceId), Device.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot find device by id "+deviceId, e);
		}
	}

	@Override
	public Collection<Device> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<Device> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(Device.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}
	
	@Override
	public Collection<Device> getAllOwnedBy(String personId) throws InfosphereException {
		return getAllOwnedBy(personId, new ArrayList<URI>(0));
	}

	@Override
	public Collection<Device> getAllOwnedBy(String personId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		Collection<Device> devices = 
				resourceStore.find(Device.class)
					.distinct()
					.select(properties.toArray(new URI[properties.size()]))
					.where(new URIImpl(personId), DDO.owns, Query.THIS)
					.results();
			return devices;
	}

}