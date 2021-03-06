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

package eu.dime.ps.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.dime.commons.dto.AccountEntry;
import eu.dime.commons.dto.UserRegister;
import eu.dime.commons.exception.DimeException;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.exception.UserNotFoundException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.security.utils.PasswordGenerator;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.controllers.util.TenantNotFoundException;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.dns.DNSAccountRegistrar;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.gateway.service.internal.AccountCannotResolveException;
import eu.dime.ps.gateway.service.internal.AccountRegistrar;
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
import eu.dime.ps.storage.datastore.impl.DataStoreProvider;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.AttributeMatch;
import eu.dime.ps.storage.entities.CrawlerHandler;
import eu.dime.ps.storage.entities.CrawlerJob;
import eu.dime.ps.storage.entities.HistoryCache;
import eu.dime.ps.storage.entities.Notification;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.ProfileMatch;
import eu.dime.ps.storage.exception.ReadOnlyValueChangedOnUpdate;
import eu.dime.ps.storage.manager.EntityFactory;

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
    private AccountRegistrar accountRegistrar;
    private ShaPasswordEncoder dimePasswordEncoder;
    
    @Autowired
    private NotifierManager notifierManager;    
   
	private DataStoreProvider dataStoreProvider;


    public void setAccountRegistrar(AccountRegistrar accountRegistrar) {
        this.accountRegistrar = accountRegistrar;
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
    
    @Autowired
    public void setDataStoreProvider(DataStoreProvider dataStoreProvider) {
		this.dataStoreProvider = dataStoreProvider;
	}


    public UserManagerImpl() {
        this.broadcastManager = BroadcastManager.getInstance();
        this.modelFactory = new ModelFactory();
    }


    private void validate(UserRegister userRegister) {
    	// username validations
    	String username = userRegister.getUsername();
    	validatePresence(username, "username");
    	validateAlphanumeric(username, "username");
        if (existsOwnerByUsername(username)) {
            throw new IllegalArgumentException("username: " + username + " already exists");
        }
        
        // checks if username doesn't exist globally in the DNS (in case of using DNS)
        // TODO it would be better to ask the AccountRegistrar for this, and let the
        // implementation decide...
        if (accountRegistrar instanceof DNSAccountRegistrar) {
        	DNSAccountRegistrar dns = (DNSAccountRegistrar) accountRegistrar;
        	try {
        		URL target = dns.resolve(username);
        		if (target != null) {
        			throw new IllegalArgumentException("Username was already registered at the di.me DNS");
        		}
        	} catch (AccountCannotResolveException e) {}
        }

        // email validations
        String email = userRegister.getEmailAddress();
        validatePresence(email, "email address");
        validateEmail(email, "email address");
        
        // password validations
        String password = userRegister.getPassword();
        validatePresence(password, "password");
    }

    private void validatePresence(String value, String field) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(field + " is empty");
        }
    }
    
    private void validateAlphanumeric(String value, String field) {
        if (!StringUtils.isAlphanumeric(value)) {
            throw new IllegalArgumentException(field + " contains non alphanumeric characters");
        }
    }

    private void validateEmail(String value, String field) {
    	// TODO add a real implementation of email validation
    	if (!value.contains("@")) {
            throw new IllegalArgumentException(field + " is an invalid email address");
        }
    }

    @Override
    public List<User> getAll() {
        return User.findAll();
    }

    @Override
    public User getByUsername(String username) {
        Long tenantId = TenantHelper.getCurrentTenantId();
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
        
        // password may be passed encoded as well
        User user = User.findByUsernameAndPassword(username, dimePasswordEncoder.encodePassword(password, username));
        if (user == null) {
        	user = User.findByUsernameAndPassword(username, password);
        }
        if (user == null){
        	throw new NoResultException("Result null");
        }
        return user;
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
	        profile.setEmailAddress(email);
	        profile.getModel().addAll(email.getModel().iterator());
        }

        // set profile's label
        profile.setPrefLabel(userRegister.getFirstname() + " " + userRegister.getLastname());
        
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

    private void registerToUserResolverService(User user, PersonContact profile, Tenant tenant) throws DimeException {
        ServiceProvider serviceProvider = ServiceProvider.findByName(DimeServiceAdapter.NAME);
        List<ServiceAccount> accounts = ServiceAccount.findAllByTenantAndServiceProvider(user.getTenant(), serviceProvider);
        DimeUserResolverServiceAdapter userResolver = null;
        try {
            userResolver = serviceGateway.getDimeUserResolverServiceAdapter();
            userResolver.setTenant(tenant);

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
    public User register(UserRegister userRegister) throws IllegalArgumentException, DimeException {
        
    	boolean unlocked;
    	try {
                unlocked = lock.tryLock(5L, TimeUnit.SECONDS);// only one register at a time
        } catch (InterruptedException e) {
                throw new DimeException("Register failed", e);
        }
    	if (!unlocked) {
    		logger.error("Could not aquire lock within 5 seconds. Returning without registering.");
    		throw new DimeException("Could not register because the system is busy.");
    	}
    	
    	User user = null;
        Tenant tenant = null;
        try {
            validate(userRegister);

            // TODO improve performance: register it asynchronously
            // TODO improve robustness: what happens if this fails? rollback mechanism! Transaction.
            //
            //EDIT by Simon: moved to the beginning of the registration process:
            // we try first to register and hope that no one will try to connect before the rest is established
            // so, we run into an exception in case the dns is not available
            // better would be to somehow reserve a registration and enable it after the user was created
            accountRegistrar.register(userRegister.getUsername());
            
            user = createUser(userRegister);
            user.persist();
            user.flush();
            logger.debug("Creating new user [id=" + user.getId() + ", username="
                + user.getUsername() + ", role=" + user.getRole() + "]");
            //FIXME check creation order! why first create a User and then the Tenant? Having a User without tenant does not make sense...
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
                registerToUserResolverService(user, profile, tenant);
            } catch (Exception e) {
                throw new DimeException("Failed to register at dime user register service. Aborting registration.\n"
                    +e.getClass().getName()+": "+e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getClass().getName()+": "+e.getMessage(), e);
            try{
                //roll back

                //FIXME: unregister at DNS
                if (user!=null){
                    user.remove();
                }
                if (tenant!=null){
                    tenant.remove();
                }
            }catch(Exception ex){
                logger.warn("Error when trying to roll-back: "+ex.getMessage(),ex);
            }
            //FIXME: remove profile from URS
            //FIXME remove profile from rdf
            
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
        Tenant tenant = TenantHelper.getCurrentTenant();
        User user = User.findByTenantAndByAccountUri(tenant, accountUri.toString());
        if (user == null) {
            throw new InfosphereException("Could not add profile/account. User with uri: " + accountUri + " does not exist.");
        }

        String name = contact.getPrefLabel();
        if (name == null) {
        	name = "Undefined";
        }

        Account account = modelFactory.getDAOFactory().createAccount(accountUri);
        account.setAccountType(DimeServiceAdapter.NAME);
        account.setPrefLabel(name);

        contact.getModel().addStatement(contact, NIE.dataSource, account);
        contact.setPrefLabel(name);

        Person person = personManager.create(contact);

        account.setCreator(person);
        accountManager.add(account);

        return account;
    }

    @Override
    public User add(String said) throws InfosphereException {
        Tenant tenant;
        try {
            tenant = TenantHelper.getCurrentTenant();
        } catch (TenantNotFoundException e) {
            throw new InfosphereException("Cannot add contact with said '" + said + "'.", e);
        }

        User user = User.findByTenantAndByUsername(tenant, said);
        if (user != null) {
        	logger.info("Cannot add contact with said '" + said + "' [tenant=" + tenant.getId() + "]:"
                        + " there's already another User with id " + user.getId() + " whose accountUri is " + user.getAccountUri());
        } else {
	        // creating GUEST of the tenant
	        // password is set to NULL, and the user is disabled until an item is shared with them
	        user = entityFactory.buildUser();
	        user.setUsername(said);
	        user.setPassword(null);
	        user.setEnabled(false);
	        user.setRole(Role.GUEST);
	        user.setAccountUri("urn:uuid:" + UUID.randomUUID());
	        user.setTenant(tenant);
	        user.merge();
	        user.flush();
	        logger.debug("Created new user [id=" + user.getId() + ", username=" + user.getUsername() + ", role=" + user.getRole() + "]");
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
    public void clear(String userId) {
       
    	Tenant tenant = TenantHelper.getCurrentTenant();
    	Long tenantId  = tenant.getId();
    		
    	removeFromUserResolver(userId, tenant);
    	 	    	   
    	dataStoreProvider.deleteTenantStore(tenantId);	    	    	    	
    	
    	removeFromDatabase(tenantId,userId);    	
    	
    	tenantManager.remove(tenantId.toString());
    	
    }

   
	private void removeFromUserResolver(String userId, Tenant tenant) {
        DimeUserResolverServiceAdapter userResolver;
		try {
			userResolver = serviceGateway.getDimeUserResolverServiceAdapter();
			userResolver.setTenant(tenant);
			userResolver._delete(userId);
		} catch (ServiceNotAvailableException e) {
			logger.error("Could not delete data of: "+userId+" from public resolver service.", e);
		} catch (AttributeNotSupportedException e) {
			logger.error("Could not delete data of: "+userId+" from public resolver service.", e);
		}	
	}

	private void removeFromDatabase(Long tenantId, String userId) {
    	
    	Tenant tenant= Tenant.find(tenantId);
    	
    	//ServiceAccount
    	 List<ServiceAccount> ServiceAccountList = ServiceAccount.findAllByTenant(tenant);
		for(ServiceAccount serviceAccount : ServiceAccountList)	serviceAccount.remove();			
					
		//User
		User.findByTenantAndByUsername(tenant, userId).remove();
		 		 
		//AccountCredentials
		 List<AccountCredentials> accountCredentialsList = AccountCredentials.findAllByTenant(tenant);
		 for(AccountCredentials accountCredentials : accountCredentialsList) accountCredentials.remove();		
		 
		//AttributeMatch 
		 List<AttributeMatch> attributeMatchList = AttributeMatch.findAllAttributeMatchByTenant(tenant);
		 for(AttributeMatch attributeMatch : attributeMatchList) attributeMatch.remove();
		 
		//CrawlerHandler		 
		 List<CrawlerHandler> crawlerHandlerList = CrawlerHandler.findAllByTenant(tenant);
		 for(CrawlerHandler crawlerHandler : crawlerHandlerList) crawlerHandler.remove();
		 
		 //CrawlerJob		 
		 List<CrawlerJob> crawlerJobList = CrawlerJob.findAllByTenant(tenant);
		 for(CrawlerJob crawlerJob : crawlerJobList) crawlerJob.remove();
		 
		 //HistoryCache
		 List<HistoryCache> historyCacheList = HistoryCache.findAllByTenant(tenant);
		 for(HistoryCache historyCache : historyCacheList) historyCache.remove();
		 
		 //Notification
		 List<Notification> notificationList = Notification.findAllNotificationsByTenant(tenant);
		 for(Notification notification : notificationList) notification.remove();
		 
		 //PersonMatch
		 List<PersonMatch> personMatchList = PersonMatch.findAllByTenant(tenant);
		 for(PersonMatch personMatch : personMatchList) personMatch.remove();
		
		 //ProfileMatch
		 List<ProfileMatch> profileMatchList = ProfileMatch.findAllProfileMatchByTenant(tenant);
		 for(ProfileMatch profileMatch : profileMatchList) profileMatch.remove();
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
    		return getByUsernameAndPassword(username, password) != null;
    	} catch (NoResultException e){
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
        if(user != null){
        	return populateAccountEntry(user);
        } else {
        	throw new NotFoundException("This is not the user you are looking for: "+userName);
        }
    }

    private void sendNotification(User oldUser) {
        Long t = TenantHelper.getCurrentTenantId();
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
	public int countFilteredUsers() {	
		List <User> list = null;
	
        String filter = null;
        try {
        	filter = getProps().getProperty("EVALUATION_FILTER_PREFIX", null);
            if (filter!=null){
            	filter = filter.trim();
            	list = User.findAllWithRoleFilteredBy(Role.OWNER, filter);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        if (list != null){
        	return list.size();
        }
        return 0;
	}

}
