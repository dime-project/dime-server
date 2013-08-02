/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.model.pimo.Thing;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link LocationManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 *
 * @author Ismael Rivera
 */
public class LocationManagerImpl extends InfoSphereManagerBase<Location> implements LocationManager {

	public LocationManagerImpl() {}
	
	@Override
	public Collection<Location> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<Location> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(Location.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}
	
	@Override
	public Location get(String locationId) throws InfosphereException {
		return get(locationId, new ArrayList<URI>(0));
	}

	@Override
	public Location get(String locationId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(locationId), Location.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot retrieve location "+locationId, e);
		}
	}

	@Override
	public Location getByPlacemarkId(String placemarkId) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		URI placemark = new URIImpl(placemarkId);
		try {
			if (resourceStore.isTypedAs(placemark, NFO.Placemark)) {
				Thing thing = pimoService.getOrCreateThingForOccurrence(placemark);
				if (thing instanceof Location) {
					return (Location) thing;
				} else {
					// it should have been a PIMO Location, so we type it as such for the next time
					resourceStore.addValue(pimoService.getPimoUri(), thing, RDF.type, PIMO.Location);
					return (Location) thing.castTo(Location.class);
				}
			} else {
				throw new InfosphereException(placemarkId + " is not a Placemark.");
			}
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot retrieve location for placemark "+placemarkId, e);
		}
	}

}