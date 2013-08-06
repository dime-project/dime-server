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

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nco.OrganizationContact;

/**
 * Management of profile attributes.
 * 
 * @author Ismael Rivera
 */
public interface ProfileAttributeManager extends InfoSphereManager<Resource> {
	
	/**
	 * Retrieves all the instances containing the individual pieces of
	 * profile information for a specific profile or profile card.
	 * 
	 * @param containerId is the identifier of profile or profile card which contains
	 *        a set of profile attributes
	 * @return
	 */
	Collection<Resource> getAllByContainer(String containerId) throws InfosphereException;

	OrganizationContact getOrganization(String orgId)
			throws InfosphereException;
	
}
