package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link EventManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class EventManagerImpl extends InfoSphereManagerBase<SocialEvent> implements EventManager {

	@Override
	public Collection<SocialEvent> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<SocialEvent> getAll(List<URI> properties) throws InfosphereException {
		PimoService pimoService = getPimoService();
		return pimoService.find(SocialEvent.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}


	@Override
	public Collection<SocialEvent> getAll(Person person)
			throws InfosphereException {
		return getAll(person, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<SocialEvent> getAll(Person person, List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		return pimoService.find(SocialEvent.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(PIMO.attendee).is(person)
				.results();
	}

	@Override
	public SocialEvent get(String eventId) throws InfosphereException {
		return get(eventId, new ArrayList<URI>(0));
	}

	@Override
	public SocialEvent get(String eventId, List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.get(new URIImpl(eventId), SocialEvent.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException(e.getMessage(), e);
		}
	}
	
	/**
	 * Adds an event, and adds the PIM user (owner) as an attendee by default.
	 * 
	 * @param socialEvent
	 * @throws InfosphereException
	 */
	@Override
	public void add(SocialEvent socialEvent) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			socialEvent.setAttendee(pimoService.getUserUri());
			pimoService.create(socialEvent);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Cannot create event: "+e, e);
		}
	}

	@Override
	public void update(SocialEvent socialEvent) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			pimoService.update(socialEvent, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot update event [id="+socialEvent.asResource()+"]: "+e, e);
		}
	}

	@Override
	public void remove(String eventId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			pimoService.remove(new URIImpl(eventId));
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot remove event [id="+eventId+"]: "+e, e);
		}
	}

	@Override
	public void addAttendee(Person person, String socialEventId)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			SocialEvent event = resourceStore.get(new URIImpl(socialEventId), SocialEvent.class);
			resourceStore.addValue(pimoService.getPimoUri(), event.asResource(), PIMO.attendee, person.asResource());
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot add attendee to event [id="+socialEventId+"]: "+e, e);
		}
	}

	@Override
	public void removeAttendee(Person person, String socialEventId)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			SocialEvent event = resourceStore.get(new URIImpl(socialEventId), SocialEvent.class);
			resourceStore.removeValue(event.asResource(), PIMO.attendee, person.asResource());
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot remove attendee of event [id="+socialEventId+"]: "+e, e);
		}
	}
	
}