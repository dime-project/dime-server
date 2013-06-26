package eu.dime.ps.controllers.infosphere.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Implements {@link PlacemarkManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 *
 * @author Ismael Rivera
 */
public class PlacemarkManagerImpl extends InfoSphereManagerBase<Placemark> implements PlacemarkManager {

	public PlacemarkManagerImpl() {}
	
	@Override
	public Collection<Placemark> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<Placemark> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(Placemark.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}
	
	@Override
	public Placemark get(String placemarkId) throws InfosphereException {
		return get(placemarkId, new ArrayList<URI>(0));
	}
	
	@Override
	public Placemark get(String placemarkId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(placemarkId), Placemark.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot retrieve placemark "+placemarkId, e);
		}
	}

}