package eu.dime.ps.controllers;

import eu.dime.commons.dto.AccountEntry;
import eu.dime.commons.dto.UserRegister;
import eu.dime.commons.exception.DimeException;
import eu.dime.ps.controllers.account.register.DNSRegisterFailedException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.exception.UserNotFoundException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.exception.ReadOnlyValueChangedOnUpdate;
import java.util.List;
import org.ontoware.rdf2go.model.node.URI;

/**
 *
 * @author mheupel
 * @author Ismael Rivera
 */
public interface UserManager {

	public static final String ACTION_USER_REGISTERED = "eu.dime.ps.controllers.action.USER_REGISTERED";
	
    public List<User> getAll();

    public User getByUsername(String username);

    public User getByUsernameAndPassword(String username, String password);

    /**
     * Registers a new user and creates a tenant for it. The user credentials
     * are required, and are assigned by the user creating the account.
     *    
     * @param userRegister
     * @return User entity created in the registering process
     * @throws DimeException
     * @throws IllegalArgumentException
     */
    public User register(UserRegister userRegister) throws IllegalArgumentException, DimeException, DNSRegisterFailedException;

    /**
     * Adds a contact to the user's infosphere. It also sets a new guest user
     * with no credentials.
     *
     * @param said account id known of this person
     * @param accountUri URI for the account said of the person
     * @param profile profile information known when adding the new contact
     * @return newly created user object
     */
    public User add(String said, URI accountUri) throws InfosphereException;

    public void remove(String userId);

    public void removeByUsername(String userId);

    public boolean exists(String userId);

    public boolean existsByUsername(String username);

    public boolean existsByUsernameAndPassword(String username, String password);

    public boolean changePassword(String username, String password);

    public boolean disable(String username);

    /**
     * Enable/activate a user. If the user has no password assigned, a new
     * random password will be generated.
     *
     * @param username username of the user to look for
     * @return true if able to enable/activate the user; false if an error
     * occurs
     */
    public User enable(Long id);

    /**
     * retrieves Credentials to log in to other PS (as guest)
     *
     * @param saidSender
     * @return
     */
    public User getUserForAccountAndTenant(String saidSender, String saidTenant);

    public User getUserForUsernameAndSaidTenant(String username, String tenantName);

    public User generatePassword(Long id);

    Account addProfile(URI accountUri, PersonContact contact) throws InfosphereException;

    public AccountEntry updateUserByAccount(AccountEntry accountUpdate)
            throws ReadOnlyValueChangedOnUpdate, UserNotFoundException;

    public AccountEntry getUserAccount(String userName);
}
