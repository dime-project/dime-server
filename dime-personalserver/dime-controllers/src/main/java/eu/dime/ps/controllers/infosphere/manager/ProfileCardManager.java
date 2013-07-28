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
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

/**
 * Manager for profile cards management.
 * 
 * @author Ismael Rivera
 */
public interface ProfileCardManager extends InfoSphereManager<PrivacyPreference> {

	PrivacyPreference getByLabel(String label) throws InfosphereException;

	Collection<PrivacyPreference> getAllByPerson(Person person)
			throws InfosphereException;

	Collection<PrivacyPreference> getAllByPerson(Person person, List<URI> properties)
			throws InfosphereException;

	/**
	 * Compose and return a profile (nco:PersonContact instance) based on
	 * profile information granted through a given profile card.
	 * 
	 * @param profileId
	 * @param properties
	 * @return
	 * @throws InfosphereException
	 */
	PersonContact getProfile(String profileId, List<URI> properties)
			throws InfosphereException;

	/**
	 * Returns a collection of profiles (nco:PersonContact instances) based on
	 * profile information granted through profile cards. Each profile corresponds
	 * to a profile card.
	 * 
	 * @param accountId
	 * @param properties
	 * @return
	 * @throws InfosphereException
	 */
	Collection<PersonContact> getAllProfile(String accountId, List<URI> properties)
			throws InfosphereException;
	
}