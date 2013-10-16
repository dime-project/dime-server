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

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Manager for person groups management.
 * 
 * @author Ismael Rivera
 */
public interface PersonGroupManager extends InfoSphereManager<PersonGroup> {

	boolean isPersonGroup(String resourceId) throws InfosphereException;

	Collection<PersonGroup> getAdhocGroups() throws InfosphereException;

	Collection<PersonGroup> getAdhocGroups(List<URI> properties) throws InfosphereException;
	
	Collection<PersonGroup> getAll(Person person) throws InfosphereException;
	
	Collection<PersonGroup> getAll(Person person, List<URI> properties) throws InfosphereException;
	
	void addAdhocGroup(PersonGroup personGroup) throws InfosphereException;

	Collection<PersonGroup> getAllByAccount(Account account,
			List<URI> properties) throws InfosphereException;

	Collection<PersonGroup> getAllByAccount(Account account)
			throws InfosphereException;
	
}