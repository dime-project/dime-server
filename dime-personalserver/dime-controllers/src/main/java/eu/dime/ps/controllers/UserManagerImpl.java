package eu.dime.ps.controllers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.NoResultException;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import eu.dime.commons.dto.AccountEntry;
import eu.dime.commons.dto.UserRegister;
import eu.dime.commons.exception.DimeException;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.ps.gateway.service.internal.DimeDNSRegisterFailedException;
import eu.dime.ps.controllers.account.register.DimeDNSRegisterService;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.exception.UserNotFoundException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.security.utils.PasswordGenerator;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeDNSCannotConnectException;
import eu.dime.ps.gateway.service.internal.DimeDNSCannotResolveException;
import eu.dime.ps.gateway.service.internal.DimeDNSException;
import eu.dime.ps.gateway.service.internal.DimeIPResolver;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.exception.ReadOnlyValueChangedOnUpdate;
import eu.dime.ps.storage.manager.EntityFactory;
import eu.dime.ps.storage.util.QueryUtil;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import javax.naming.NamingException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author mheupel
 * @author Ismael Rivera
 * @author mplanaguma
 * @author Simon Thiel
 */
public class UserManagerImpl implements UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManagerImpl.class);
    
    private static Properties properties = null;

    private static Properties getProps() throws IOException {
        if (properties == null) {
            properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
        }
        return properties;
    }
    
    private EntityFactory entityFactory;
    private final ModelFactory modelFactory;

    private final BroadcastManager broadcastManager;
    
    static final Lock lock = new ReentrantLock();


    private TenantManager tenantManager;
    private AccountManager accountManager;
    private PersonManager personManager;
    private ProfileManager profileManager;
    private ProfileCardManager profileCardManager;
    private ServiceGateway serviceGateway;
    private DimeDNSRegisterService dimeDNSRegisterService;
    private ShaPasswordEncoder dimePasswordEncoder;
    
    @Autowired
    private NotifierManager notifierManager;


    public void setDimeDNSRegisterService(DimeDNSRegisterService dimeDNSRegisterService) {
        this.dimeDNSRegisterService = dimeDNSRegisterService;
    }

    public void setTenantManager(TenantManager tenantManager) {
        this.tenantManager = tenantManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    public void setProfileManager(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public void setProfileCardManager(ProfileCardManager profileCardManager) {
        this.profileCardManager = profileCardManager;
    }

    @Autowired
    public void setShaPasswordEncoder(ShaPasswordEncoder encoder) {
        this.dimePasswordEncoder = encoder;
    }

    @Autowired
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Autowired
    public void setServiceGateway(ServiceGateway serviceGateway) {
        this.serviceGateway = serviceGateway;
    }

    public UserManagerImpl() {
        this.broadcastManager = BroadcastManager.getInstance();
        this.modelFactory = new ModelFactory();
    }

    @Override
    public List<User> getAll() {
        return User.findAll();
    }

    @Override
    public User getByUsername(String username) {
        Long tenantId = TenantContextHolder.getTenant();
        List<User> userList = User.findAllByUsername(username);
        for (User user : userList) {
            if (user.getTenant().getId().equals(tenantId)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        if ((password == null) || (password.trim().isEmpty())) {
            return null;
        }
        return QueryUtil.getSingleResultOrNull(User.findByUsernameAndPassword(username,
                dimePasswordEncoder.encodePassword(password, username)));
    }

    private void validateUser(String username, String password) {
        if (existsOwnerByUsername(username)) {
            throw new IllegalArgumentException("username: " + username + " already exists");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password field empty");
        }
    }

    private User createUser(UserRegister userRegister) {
        // creating OWNER of the tenant
        User user = entityFactory.buildUser();
        user.setUsername(userRegister.getUsername());
        user.setPassword(dimePasswordEncoder.encodePassword(userRegister.getPassword(), userRegister.getUsername()));
        user.setEnabled(true);
        user.setRole(Role.OWNER);
        user.setEmailAddress(userRegister.getEmailAddress());
        user.setEvaluationDataCapturingEnabled(userRegister.getCheckbox_agree());

        return user;
    }

    private Tenant createTenant(String username, User user) {
        // creating tenant for the user
        Tenant tenant = tenantManager.create(username, user);
        logger.debug("Created new tenant [id=" + tenant.getId() + ", name=" + tenant.getName() + "] for user " + user.getUsername());
        return tenant;
    }

    private PersonContact buildProfile(UserRegister userRegister){
        // create RDF profile object and attributes

        PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
        
        PersonName name = modelFactory.getNCOFactory().createPersonName();
        name.setNameGiven(userRegister.getFirstname());
        name.setNameFamily(userRegister.getLastname());
        name.setFullname(userRegister.getFirstname() + " " + userRegister.getLastname());
        name.setNickname(userRegister.getNickname());
        profile.setPersonName(name);
        profile.getModel().addAll(name.getModel().iterator());

        if (userRegister.getEmailAddress() != null) {
	        EmailAddress email = modelFactory.getNCOFactory().createEmailAddress();
	        email.setEmailAddress(userRegister.getEmailAddress());
                email.setPrefLabel("Email");
	        profile.setEmailAddress(email);
	        profile.getModel().addAll(email.getModel().iterator());
        }

        // set profile's label
        String username = userRegister.getUsername();
        profile.setPrefLabel(username + "@di.me");
        
        return profile;
    }
    
    private PersonContact initModelForUser(PersonContact profile, Tenant tenant) throws DimeException {

        // create the di.me account
        Account account = modelFactory.getDAOFactory().createAccount();
        account.setAccountType(DimeServiceAdapter.NAME);
        account.setPrefLabel(profile.getPrefLabel());

        // create the profile card for the di.me account
        PrivacyPreference profileCard = modelFactory.getPPOFactory().createPrivacyPreference();
        profileCard.setLabel(PrivacyPreferenceType.PROFILECARD.name());
        profileCard.setPrefLabel(DefaultDataSetup.DEFAULT_PUBLIC_PROFILE_CARD_NAME);

        // set di.me account as sharedThrough in the profile card's access space
        AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
        accessSpace.setSharedThrough(account);
        profileCard.getModel().addAll(accessSpace.getModel().iterator());
        profileCard.setAccessSpace(accessSpace);

        // add profile attributes to profile card
        ClosableIterator<PersonName> names = profile.getAllPersonName();
        while (names.hasNext()) {
            profileCard.addAppliesToResource(names.next());
        }
        names.close();
        ClosableIterator<EmailAddress> emails = profile.getAllEmailAddress();
        while (emails.hasNext()) {
            profileCard.addAppliesToResource(emails.next());
        }
        emails.close();

        // AccountManager reads the tenant from the TenantContextHolder
        TenantContextHolder.setTenant(tenant.getId());

        try {
            // create a di.me account and a profile card by default with the information provided to register

        	Person me = profileManager.getMe(); // Retrieve the owner person (me)

            accountManager.add(account);
            profileManager.add(me, profile, true);

            profileCard.setCreator(me);
            profileCardManager.add(profileCard);

            broadcastManager.sendBroadcast(new Event(Long.toString(tenant.getId()), UserManager.ACTION_USER_REGISTERED, me.asURI()));

        } catch (InfosphereException e) {
            throw new DimeException(e.getMessage(), e);
        }
        
        return profile;
    }

    private void registerToUserResolverService(User user, PersonContact profile) throws DimeException {
        ServiceProvider serviceProvider = ServiceProvider.findByName(DimeServiceAdapter.NAME);
        List<ServiceAccount> accounts = ServiceAccount.findAllByTenantAndServiceProvider(user.getTenant(), serviceProvider);
        DimeUserResolverServiceAdapter userResolver = null;
        try {
            userResolver = serviceGateway.getDimeUserResolverServiceAdapter();

            // said name from dime account for this user
            if (accounts.size() > 0) {
                ServiceAccount account = accounts.get(0);
                userResolver.setIdentifer(account.getName());

                userResolver.set(AttributeMap.PROFILE_ME, profile);
            } else {
                throw new DimeException("User's profile couldn't be published on the di.me User Directory: "
                        + "no account has been found for " + user.getTenant());
            }
        } catch (ServiceNotAvailableException e) {
            throw new DimeException("User's profile couldn't be published on the di.me User Directory: " + e.getMessage(), e);
        } catch (AttributeNotSupportedException e) {
            throw new DimeException("User's profile couldn't be published on the di.me User Directory: " + e.getMessage(), e);
        } catch (InvalidDataException e) {
            throw new DimeException("User's profile couldn't be published on the di.me User Directory: " + e.getMessage(), e);
        }
    }

    @Override 
    public User register(UserRegister userRegister) throws IllegalArgumentException, DimeDNSRegisterFailedException, DimeException {
        
    	boolean unlocked;
    	try {
                unlocked = lock.tryLock(5L, TimeUnit.SECONDS);// only one register at a time
        } catch (InterruptedException e) {
                throw new DimeDNSRegisterFailedException("Register failed", e);
        }
    	if (!unlocked) {
    		logger.error("Could not aquire lock within 5 seconds. Returning without registering.");
    		throw new DimeException("Could not register because the system is busy.");
    	}
    	User user=null;
        Tenant tenant=null;
        try {
            validateUser(userRegister.getUsername(), userRegister.getPassword());

            // TODO improve performance: register it asynchronously
            // TODO improve robustness: what happens if this fails? rollback mechanism! Transaction.
            //
            //EDIT by Simon: moved to the beginning of the registration process:
            // we try first to register and hope that no one will try to connect before the rest is established
            // so, we run into an exception in case the dns is not available
            // better would be to somehow reserve a registration and enable it after the user was created
            dimeDNSRegisterService.registerSaid(userRegister.getUsername());
            user = createUser(userRegister);
            user.persist();
            user.flush();
            logger.debug("Creating new user [id=" + user.getId() + ", username="
                + user.getUsername() + ", role=" + user.getRole() + "]");
            tenant = createTenant(userRegister.getUsername(), user);
            PersonContact profile = null;
            try {
            } catch (Exception e) {
                throw new DimeException("Failed to create profile for user. Registration could not be completed.\n"
                    +e.getClass().getName()+": "+e.getMessage(), e);
            }
            try {
                profile = buildProfile(userRegister);
            } catch (Exception e) {
                throw new DimeException("Failed to publish public profile when registering. Aborting registration.\n"
                    +e.getClass().getName()+": "+e.getMessage(), e);

            }
            try {
                initModelForUser(profile, tenant);             
            } catch (Exception e) {
                throw new DimeException("Failed to store semantic model when registering. Aborting registration.\n"
                    +e.getClass().getName()+": "+e.getMessage(), e);
            }
            try {
                registerToUserResolverService(user, profile); //FIXME:<--- unfortunately, this requires an Account to be created,
                                                            //to rdf register needs to be done before (to be fixed)
            } catch (Exception e) {
                throw new DimeException("Failed to register at dime user register service. Aborting registration.\n"
                    +e.getClass().getName()+": "+e.getMessage(), e);
            }
        } catch (Exception e) {
                //FIXME: unregister at DNS
                if (user!=null){
                    user.remove();
                }
                if (tenant!=null){
                    tenant.remove();
                }
                //FIXME: remove profile from URS
                //FIXME remove profile from rdf

                logger.error(e.getClass().getName()+": "+e.getMessage(), e);
                throw new DimeException(e.getClass().getName()+": "+e.getMessage(),e);
        } finally {
                lock.unlock();
        }
        return user;
    }

    private boolean existsOwnerByUsername(String username) {
        return getOwnerByUsername(username) != null;
    }

    /**
     * Adds a profile for an existing User; creates an account and person in the
     * infosphere
     *
     * @param accountUri
     * @param contact
     * @return
     * @throws InfosphereException
     */
    @Override
    public Account addProfile(URI accountUri, PersonContact contact) throws InfosphereException {
        Long tenantId = TenantContextHolder.getTenant();
        Tenant tenant = Tenant.find(tenantId);
        if (tenant == null) {
            throw new InfosphereException("Cannot add profile/account'" + accountUri.toString() + "': tenant '" + tenantId + "' not found.");
        }
        User user = User.findByAccountUri(accountUri.toString());
        if (user == null) {
            throw new InfosphereException("Could not add profile/account. User with uri: " + accountUri + " does not exist.");
        }

        String name = contact.getPrefLabel();

        Account account = modelFactory.getDAOFactory().createAccount(accountUri);
        account.setAccountType(DimeServiceAdapter.NAME);
        account.setPrefLabel(name + "@" + DimeServiceAdapter.NAME);


        contact.getModel().addStatement(contact, NIE.dataSource, account);
        contact.setPrefLabel(name + "@" + DimeServiceAdapter.NAME);

        Person person = personManager.create(contact);

        account.setCreator(person);
        accountManager.add(account);

        return account;
    }

    @Override
    public User add(String said, URI accountUri) throws InfosphereException {
        Long tenantId = TenantContextHolder.getTenant();
        Tenant tenant = Tenant.find(tenantId);
        if (tenant == null) {
            throw new InfosphereException("Cannot add contact with said '" + said + "': tenant '" + tenantId + "' not found.");
        }

        User user = User.findByTenantAndByUsername(tenant, said);
        if (user != null) {
            throw new InfosphereException("Cannot add contact with said '" + said + "' [tenant=" + tenantId + "]:"
                    + " there's already another User with id " + user.getId());
        }

        try {
            // creating GUEST of the tenant
            // password is set to NULL, and the user is disabled until an item is shared with them
            user = entityFactory.buildUser();
            user.setUsername(said);
            user.setPassword(null);
            user.setEnabled(false);
            user.setRole(Role.GUEST);
            user.setAccountUri(accountUri.toString());
            user.setTenant(tenant);
            user.merge();
            user.flush();
            logger.debug("Created new user [id=" + user.getId() + ", username=" + user.getUsername() + ", role=" + user.getRole() + "]");
        } catch (IllegalArgumentException e) {
            // FIXME what to do if the User fails to be created in the db? should we remove the
            // resources from the database?
        }

        return user;
    }

    @Override
    public void remove(String userId) {
        User.find(Long.parseLong(userId)).remove();
    }

    @Override
    public void removeByUsername(String userId) {
        this.getByUsername(userId).remove();
    }

    @Override
    public boolean exists(String userId) {
        return User.find(Long.parseLong(userId)) != null;
    }

    @Override
    public boolean existsByUsername(String username) {
        Long tenantId = TenantContextHolder.getTenant();
        if (tenantId != null) {
            return this.getByUsername(username) != null;
        } else {
            return getOwnerByUsername(username) != null;
        }
    }

    private User getOwnerByUsername(String username) {
        List<User> userList = User.findAllByUsername(username);
        for (User user : userList) {
            if (user.getRole().equals(Role.OWNER)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) {
        try {
            return User.findByUsernameAndPassword(username,
                    dimePasswordEncoder.encodePassword(password, username)).getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean changePassword(String username, String password) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username field empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password field empty");
        }

        try {
            User user = this.getByUsername(username);
            user.setPassword(dimePasswordEncoder.encodePassword(password, username));
            user.merge();
            user.flush();
        } catch (IllegalArgumentException e) {
            logger.error("Could not change password for user with username '" + username + "': " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean disable(String username) {
        try {
            User user = this.getByUsername(username);
            // return false if was already disabled
            if (!user.isEnabled()) {
                return false;
            }
            user.setEnabled(false);
            user.merge();
            user.flush();
        } catch (IllegalArgumentException e) {
            logger.error("Could not disable user with username '" + username + "': " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public User enable(Long id) {
        try {
            User user = User.find(id);
            // return false if was already enabled
            if (user.isEnabled()) {
                return null;
            }
            user.setEnabled(true);
            user.merge();
            user.flush();
            return user;
        } catch (IllegalArgumentException e) {
            logger.error("Could not enable user with id '" + id + "': " + e.getMessage(), e);
            return null;
        }
    }

    /*
     * will generate a new password for not enabled accounts
     */
    @Override
    public User generatePassword(Long id) {
        try {
            User user = User.find(id);
            // return false if was already enabled
//			if (user.isEnabled()){
//				return null;
//			}			
            String password = PasswordGenerator.getRandomPassword();

            user.setPassword(dimePasswordEncoder.encodePassword(password, user.getUsername()));
            user.merge();
            user.flush();
            User tmp = new User(); //tmp user to return to contact (pw cannot be hashed there)
            tmp.setPassword(password);
            tmp.setUsername(user.getUsername());
            tmp.setEnabled(user.isEnabled());
            tmp.setRole(user.getRole());
            return tmp;
        } catch (IllegalArgumentException e) {
            logger.error("Could not generate password for user with id '" + id + "': " + e.getMessage(), e);
            return null;
        }
    }

    private User updateUserFromAccountEntry(User oldUser, AccountEntry accountEntry)
            throws ReadOnlyValueChangedOnUpdate {

        if (!accountEntry.getUsername().equals(oldUser.getUsername())) {
            throw new ReadOnlyValueChangedOnUpdate("When trying to update user (old, new): "
                    + accountEntry.toString() + ", " + oldUser.toString());
            //TODO handle role - read only for user as well
        }

        oldUser.setEnabled(accountEntry.getEnabled());
        oldUser.setEvaluationDataCapturingEnabled(accountEntry.getEvaluationDataCapturingEnabled());
        if ((accountEntry.getPassword() != null)
                && (accountEntry.getPassword().length() > 0)) { //only update password if given
            logger.info("updating password for user: "+accountEntry.getUsername());
            String encodedPWD = dimePasswordEncoder.encodePassword(accountEntry.getPassword(), oldUser.getUsername());
            oldUser.setPassword(encodedPWD);
        }
        oldUser.setUiLanguage(accountEntry.getUiLanguage());
        oldUser.setUserStatusFlag(accountEntry.getUserStatusFlag());

        sendNotification(oldUser);

        return oldUser;
    }

    private AccountEntry populateAccountEntry(User user) {
        AccountEntry result = new AccountEntry();

        result.setEnabled(user.isEnabled());
        result.setEvaluationDataCapturingEnabled(user.getEvaluationDataCapturingEnabled());
        result.setEvaluationId(user.getEvaluationId());
        result.setPassword(user.getPassword());
        result.setRole(user.getRole().ordinal());
        result.setSaid(user.getTenant().getName());
        result.setUiLanguage(user.getUiLanguage());
        result.setUsername(user.getUsername());
        result.setType("auth");
        result.setUserStatusFlag(user.getUserStatusFlag());
        return result;
    }

    @Override
    public User getUserForAccountAndTenant(String saidSender, String saidTenant) {
        return getUserForUsernameAndSaidTenant(saidSender, saidTenant);
    }

    @Override
    public User getUserForUsernameAndSaidTenant(String username, String tenantName) {
        Tenant tenant = tenantManager.getByAccountName(tenantName);
        return User.findByTenantAndByUsername(tenant, username);
    }

    @Override
    public AccountEntry updateUserByAccount(AccountEntry accountUpdate)
            throws ReadOnlyValueChangedOnUpdate, UserNotFoundException {

        User oldUser = getUserForUsernameAndSaidTenant(accountUpdate.getUsername(), accountUpdate.getSaid());
        if (oldUser == null) {
            throw new UserNotFoundException("Unable to find user with tenantSaid:"
                    + accountUpdate.getSaid());
        }


        oldUser = updateUserFromAccountEntry(oldUser, accountUpdate);
        oldUser.merge();
        oldUser.flush();

        return populateAccountEntry(oldUser);
    }

    @Override
    public AccountEntry getUserAccount(String userName) {
        User user = getByUsername(userName);
        return populateAccountEntry(user);
    }

    private void sendNotification(User oldUser) {
        Long t = TenantContextHolder.getTenant();
        SystemNotification notification =
            new SystemNotification(t, DimeInternalNotification.OP_UPDATE,
                "@me", DimeInternalNotification.ITEM_TYPE_USER, "@me");
        try {
                notifierManager.pushInternalNotification(notification);
        } catch (NotifierException e) {
                logger.error(e.getMessage(),e);
        }
    }

    @Override
    public boolean validateUserCanLogEvaluationData(User user) {

        if (user==null){
            logger.error("user==null", new RuntimeException());
            return false;
        }

        if (!user.getEvaluationDataCapturingEnabled()){
            return false;
        }
        String filterPrefix=null;
        try {
            filterPrefix = getProps().getProperty("EVALUATION_FILTER_PREFIX", null);
            if (filterPrefix!=null){
                filterPrefix = filterPrefix.trim();
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if ((filterPrefix!=null)
                  && (filterPrefix.length()>0)
                  && (user.getUsername().startsWith(filterPrefix))){
            return false;
        }

        return true;

    }

     public User getCurrentUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName().toString();
        String pw = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        User user = getByUsernameAndPassword(username, pw);

        return user;
    }

    @Override
    public boolean saidIsRegisteredAtDNS(String said) throws DimeDNSException{
        try {
            String  serverIP = new DimeIPResolver().resolveSaid(said);
            if (!serverIP.isEmpty()){
                return true;
            }

        } catch (DimeDNSCannotConnectException ex) {
            throw ex;
        } catch (DimeDNSCannotResolveException ex) {
            return false;
         } catch (DimeDNSException ex) {
            throw ex;
        }
        return false;

    }
}
