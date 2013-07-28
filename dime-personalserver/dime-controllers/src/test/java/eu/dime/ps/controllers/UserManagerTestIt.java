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

package eu.dime.ps.controllers;

import eu.dime.commons.dto.UserRegister;
import eu.dime.commons.exception.DimeException;
import eu.dime.ps.gateway.service.internal.DimeDNSRegisterFailedException;
import java.util.Random;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.controllers.account.register.DimeDNSRegisterService;
import eu.dime.ps.controllers.infosphere.InfoSphereManagerTest;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@TransactionConfiguration(defaultRollback = true)
public class UserManagerTestIt extends InfoSphereManagerTest {

	private UserManagerImpl userManager;

	/* dependencies */
	@Autowired
	private AccountManager accountManager;
	@Autowired
	private PersonManager personManager;
	@Autowired
	private ProfileManager profileManager;
	@Autowired
	private ProfileCardManager profileCardManager;
	@Autowired
	private EntityFactory entityFactory;
	@Mock
	private DimeDNSRegisterService dimeDNSRegisterService;

	@Autowired
	private ConnectionProvider connectionProvider;
	private TenantManagerImpl tenantManager;
	
	private static ShaPasswordEncoder dimePasswordEncoder = new ShaPasswordEncoder(256);

	private boolean setup = false;

	/* values */
	private static String NAME_JUAN;
	private static String NAME_ANNA;
	private static String ACCOUNT_JUAN;
	private static String ACCOUNT_ANNA;
	private static String ACCOUNT_URI_GUEST = "uri:juan:contact-annana";
	private static String JUAN_OWNER_PW = "pass";
	private static String JUAN_GUEST_PW = "123";
	
	private User user1;
	private User user2;
	private Tenant one;
	private Tenant two;

	// private User mockUser;

	@BeforeClass
	public static void setupOnce() {
		
		Random r = new Random();
		NAME_JUAN = "juan-" + r.nextInt();
		NAME_ANNA = "anna-" + r.nextInt();
		ACCOUNT_JUAN = "uri:account-" + NAME_JUAN;
		ACCOUNT_ANNA = "uri:account-" + NAME_ANNA;
		
	}

	@AfterClass
	public static void tearDownAll() {
	}

	@Before
	@Transactional
	public void setupDB() {

		one = entityFactory.buildTenant();
		one.setName(NAME_JUAN);
		one.persist();
		one.flush();

		two = entityFactory.buildTenant();
		two.setName(NAME_ANNA);
		two.persist();
		two.flush();

		user1 = entityFactory.buildUser();
		user1.setUsername(NAME_JUAN);
		user1.setPassword(dimePasswordEncoder.encodePassword(JUAN_OWNER_PW, NAME_JUAN));
		user1.setRole(Role.OWNER);
		user1.setAccountUri(ACCOUNT_JUAN);
		user1.setEnabled(true);
		user1.setTenant(one);
		user1.persist();
		user1.flush();

		user2 = entityFactory.buildUser();
		user2.setUsername(NAME_JUAN);
		user2.setPassword(dimePasswordEncoder.encodePassword(JUAN_GUEST_PW, NAME_JUAN));
		user2.setEnabled(true);
		user2.setRole(Role.GUEST);
		user2.setAccountUri(ACCOUNT_URI_GUEST);
		user2.setTenant(two);
		user2.persist();
		user2.flush();
	}

	@Before
	@Transactional
	public void setup() {
		if (setup) { // run setup only once
			return;
		} else {
			// setup = true;

			MockitoAnnotations.initMocks(this);
			tenantManager = new TenantManagerImpl();
			tenantManager.setConnectionProvider(connectionProvider);
			tenantManager.setEntityFactory(entityFactory);

			// when(tenantManager.create(anyString(),
			// any(User.class))).thenReturn(one);
			userManager = new UserManagerImpl();

			userManager.setDimeDNSRegisterService(dimeDNSRegisterService);
			userManager.setTenantManager(tenantManager);
			userManager.setAccountManager(accountManager);
			userManager.setPersonManager(personManager);
			userManager.setProfileCardManager(profileCardManager);
			userManager.setProfileManager(profileManager);
			userManager.setEntityFactory(entityFactory);
			userManager.setShaPasswordEncoder(dimePasswordEncoder);

		}

	}

	@After
	@Transactional
	public void tearDown() {
		// delete db
		user1.remove();
		user2.remove();
		one.remove();
		two.remove();
	}

	@Test
	@Transactional
	public void testGetByUsername() {
		TenantContextHolder.setTenant(one.getId());
		User user = userManager.getByUsername(NAME_JUAN);
		assertNotNull(user);
		assertEquals(ACCOUNT_JUAN, user.getAccountUri());

		TenantContextHolder.setTenant(two.getId());
		user = userManager.getByUsername(NAME_JUAN);
		assertFalse("Different juan account", ACCOUNT_JUAN.equals(user.getAccountUri()));

	}

	@Test
	@Transactional
	public void testGetByUsernameAndPassword() {
		TenantContextHolder.setTenant(one.getId());
		User user = userManager.getByUsernameAndPassword(NAME_JUAN, JUAN_OWNER_PW);
		assertNotNull(user);
		assertEquals(ACCOUNT_JUAN, user.getAccountUri());
		user = userManager.getByUsernameAndPassword(NAME_JUAN, "WRONG");
		assertNull(user);
	}

	/**
         * This test is throwing a NPE in ConnectionProvider:32 repositoryFactory seems to be null
         * probably it is required to mock the connection provider
         * e.g. like in SharingNotifierTest
         * @throws Exception
         */
        @Ignore
	@Test
	@Transactional
	public void testRegister() throws Exception{
                String tenantName = "tenant";
                String username = "juan";
                String password = "juan123";

                UserRegister userRegister = new UserRegister();
                userRegister.setCheckbox_agree(Boolean.TRUE);
                userRegister.setEmailAddress("dummy@email.com");
                userRegister.setFirstname("Juan");
                userRegister.setLastname("Alvarez");
                userRegister.setPassword(password);
                userRegister.setUsername(username);
                userRegister.setNickname(username);


                try {


                    assertNotNull(userManager.register(userRegister));
                } catch (DimeDNSRegisterFailedException ex) {
                    fail(ex.getMessage());
                } catch (IllegalArgumentException e) {
                    fail("Illegal Argument Exception but arguments are ok");
                } catch (DimeException e) {
                    fail("Unexpected DimeException");
                }
                //second registration should fail
                User user2 = userManager.register(userRegister);
		assertNull(user2); //should never go there...
	}

	@Test
	@Transactional
	public void testAddAndRemove() throws Exception {
		TenantContextHolder.setTenant(one.getId());
		PersonContact profile = buildProfile("newname", "anna@mail.com", "555-12345");
		URIImpl uri = new URIImpl("urn:uuid:" + UUID.randomUUID());
		User tmp = userManager.add("someGuestSAID", uri);
		assertNotNull(tmp);
		Account account = userManager.addProfile(uri, profile);
		assertNotNull(account);

		User user = User.findByAccountUri(uri.toString());
		if (user != null) {
			String userId = user.getId().toString();
			userManager.remove(userId);

			assertFalse(userManager.exists(userId));
		}

	}

	@Test
	@Transactional
	public void testRemove() {
		TenantContextHolder.setTenant(one.getId());

		User user = entityFactory.buildUser();
		user.setUsername("testguy");
		user.setPassword("pw");
		user.setRole(Role.OWNER);
		user.setAccountUri(ACCOUNT_JUAN);
		user.setEnabled(true);
		user.setTenant(one);
		user.persist();
		Long id = user.getId();

		userManager.remove(id.toString());
		User us = User.find(id);
		assertNull(us);

	}

	
	@Test
	@Transactional
	public void testRemoveByUsername() {
		TenantContextHolder.setTenant(one.getId());
		User user = entityFactory.buildUser();
		user.setUsername("testuser");
		user.setPassword("pw");
		user.setRole(Role.OWNER);
		user.setAccountUri(ACCOUNT_JUAN);
		user.setEnabled(true);
		user.setTenant(one);
		user.persist();
		user.flush();
		
		Long id = user.getId();
		userManager.removeByUsername(user.getUsername());
		User us = User.find(id);
		assertNull(us);
		//assertFalse(userManager.exists(id.toString()));
	}

	@Test
	public void testExists() {
		boolean exists = userManager.exists(user1.getId().toString());
		assertTrue(exists);
		exists = userManager.exists("99999999999");
		assertFalse(exists);
	}

	@Test
	public void testExistsByUsername() {
		TenantContextHolder.setTenant(one.getId());
		boolean exists = userManager.existsByUsername(user1.getUsername());
		assertTrue(exists);
		exists = userManager.existsByUsername("dasdla..sejasd");
		assertFalse(exists);
	}

	@Test
	@Transactional
	public void testExistsByUsernameAndPassword() {
		TenantContextHolder.setTenant(one.getId());
		boolean exists = userManager.existsByUsernameAndPassword(NAME_JUAN, JUAN_OWNER_PW);
		assertTrue(exists);
		exists = userManager.existsByUsernameAndPassword(user1.getUsername(), "false");
		assertFalse(exists);
		exists = userManager.existsByUsernameAndPassword("false", user1.getPassword());
		assertFalse(exists);
		exists = userManager.existsByUsernameAndPassword("false", "false");
		assertFalse(exists);
	}

	@Test
	@Transactional
	public void testChangePassword() {
		TenantContextHolder.setTenant(one.getId());
		String password = user1.getPassword();
		assertTrue(userManager.changePassword(user1.getUsername(), "newpw"));
		User user1new = User.find(user1.getId());
		assertFalse(password.equals(user1new.getPassword()));
	}

	@Test
	@Transactional
	public void testDisable() {
		TenantContextHolder.setTenant(one.getId());
		assertTrue(user1.isEnabled());
		assertTrue(userManager.disable(user1.getUsername()));
		User user1new = User.find(user1.getId());
		assertFalse(user1new.isEnabled());
	}

	@Test
	@Transactional
	public void testEnable() {
		TenantContextHolder.setTenant(one.getId());
		user1.setEnabled(false);
		user1.merge();
		user1.flush();
		assertFalse(user1.isEnabled());
		assertFalse(User.find(user1.getId()).isEnabled());
		
		User user = userManager.enable(user1.getId());
		assertNotNull(user);
		User user1new = User.find(user1.getId());
		assertTrue(user1new.isEnabled());
	}

	@Test
	public void testGetUserForAccountAndTenant() {
		TenantContextHolder.setTenant(one.getId());
		userManager.getUserForAccountAndTenant(user1.getUsername(), one.getName());
	}

	@Test
	public void testGetEvalPreferences() {
		// TODO
	}

	@Test
	public void testSetEvalPreferences() {
		// TODO
	}

	@Test
	@Transactional
	public void testGeneratePassword() {
		User user = entityFactory.buildUser();
		user.setAccountUri("uri://dummy");
		user.setEnabled(false);
		user.setRole(Role.GUEST);
		user.setTenant(user1.getTenant());
		user.setUsername(UUID.randomUUID().toString());
		user.setPassword("");
		user.persist();
		user.flush();
		User userR = userManager.generatePassword(user.getId());
		String password = userR.getPassword();
		assertNotNull(password);

		String hashedPW= dimePasswordEncoder.encodePassword(password, user.getUsername());
		assertTrue(User.find(user.getId()).getPassword().equals(hashedPW));
	}

}
