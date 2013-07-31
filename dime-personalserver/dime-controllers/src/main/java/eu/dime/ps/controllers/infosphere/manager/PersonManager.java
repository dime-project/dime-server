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
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Manager for people management.
 * 
 * @author Ismael Rivera
 */
public interface PersonManager extends InfoSphereManager<Person> {

	boolean isPerson(String resourceId) throws InfosphereException;
	
	Collection<Person> getAllByGroup(PersonGroup group) throws InfosphereException;

	Collection<Person> getAllByGroup(PersonGroup group, List<URI> properties)
			throws InfosphereException;

	Collection<Person> find(String searchTerm) throws InfosphereException;
	
	/**
	 * Same as {@link #getAllByAccount(String, List)}, except all metadata
	 * will be returned.
	 * @param accountId
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Person> getAllByAccount(String accountId)
			throws InfosphereException;
	
	/**
	 * Retrieves all pimo:Person instances which were gathered from an Account.
	 * <ul>
	 * <li>In the case of online accounts (e.g. facebook, twitter, etc.), a collection
	 * of people representing the contacts/friends in the account will be returned.</li>
	 * <li>In case of di.me accounts, only one person will be returned, the system does
	 * not allow sharing information of someone's contacts within di.me</li>
	 * </ul>
	 * @param accountId the account identifier (URI)
	 * @param properties the list of properties to retrieve for the person
	 * @return a list of people obtained from an account
	 * @throws InfosphereException If the accountId is not found
	 */
	Collection<Person> getAllByAccount(String accountId, List<URI> properties)
			throws InfosphereException;
	
	Person create(PersonContact contact) throws InfosphereException;

	/**
	 * Merges a list of people into one person.
	 * @param master is the person which will aggregate all information of all merged people
	 * @param targets array of people to be merged with the master person 
	 * @return the person result of the merging process, which is the master person + all
	 *         metadata of all people in targets
	 */
	Person merge(URI master, URI... targets) throws InfosphereException;

	Collection<Person> getAllByProfile(PersonContact profile)
			throws InfosphereException;

	Collection<Person> getAllByProfile(PersonContact profile,
			List<URI> properties) throws InfosphereException;

}