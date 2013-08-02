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

package eu.dime.ps.controllers.accesscontrol;

import eu.dime.ps.storage.entities.User;

/**
 * 
 * @author Marcel Heupel
 */
public interface AccessControlManager {

	/**
	 * <p>Checks if a given user can access a specific said:
	 * <ul>
	 * <li>A user 'owner' can only access a said (tenant) which he owns.</li>
	 * <li>A user 'guest' can only access service accounts if the service accounts
	 * exists in the tenant which this user belongs to.</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>The user must be logged in</p>
	 * 
	 * @param said can be a tenant name, or a service account name
	 * @param username user the user who's logged in
	 * @return true if the user operation can continue; false otherwise
	 */
	boolean canAccess(String said, User user);
	
}
