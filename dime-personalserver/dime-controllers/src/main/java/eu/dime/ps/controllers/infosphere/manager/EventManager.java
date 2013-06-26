package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.SocialEvent;

/**
 * Manager for event management.
 * 
 * @author Ismael Rivera
 */
public interface EventManager extends InfoSphereManager<SocialEvent> {

	/**
	 * Retrieves all events, which have been attended by an
	 * specific person.
	 * 
	 * @param person person who attended the event
	 * @return a collection of all events attended by person
	 * @throws InfosphereException
	 */
	Collection<SocialEvent> getAll(Person person)
			throws InfosphereException;

	Collection<SocialEvent> getAll(Person person, List<URI> properties)
			throws InfosphereException;

	/**
	 * Adds a person as attendee to an event.
	 * Both person and event must exists beforehand.
	 * 
	 * @param person
	 * @param socialEvent
	 * @throws InfosphereException
	 */
	void addAttendee(Person person, String socialEventId)
			throws InfosphereException;

	/**
	 * Removes a person from the attendee list of an event.
	 * 
	 * @param person
	 * @param socialEvent
	 * @throws InfosphereException
	 */
	void removeAttendee(Person person, String socialEventId)
			throws InfosphereException;

}