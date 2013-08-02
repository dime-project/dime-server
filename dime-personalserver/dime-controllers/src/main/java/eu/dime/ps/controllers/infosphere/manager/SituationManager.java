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

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dcon.Situation;

/**
 * Manager for situation management.
 * 
 * @author Ismael Rivera
 */
public interface SituationManager extends InfoSphereManager<Situation> {

	Situation getByName(String name) throws InfosphereException;
	
	/**
	 * Activates the situation for the user.
	 * It indicates that the user is at a specific situation at the moment.
	 * 
	 * @param situationId identifier of the situation
	 * @throws InfosphereException if the situation doesn't not exist or cannot be activated
	 */
	void activate(String situationId) throws InfosphereException;

	/**
	 * Deactivates the situation for the user.
	 * It indicates that the situation is no longer valid for the user.
	 * 
	 * @param situationId identifier of the situation
	 * @throws InfosphereException if the situation doesn't not exist or cannot be deactivated
	 */
	void deactivate(String situationId) throws InfosphereException;

}