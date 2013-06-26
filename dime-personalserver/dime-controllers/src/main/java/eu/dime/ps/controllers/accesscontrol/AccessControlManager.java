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
