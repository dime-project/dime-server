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