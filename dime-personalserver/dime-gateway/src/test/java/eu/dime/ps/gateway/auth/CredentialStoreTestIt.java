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

package eu.dime.ps.gateway.auth;

import static org.junit.Assert.fail;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-credential-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class CredentialStoreTestIt {

	/* ids */
	private final String LOCAL_SAID = "juan-said";
	private final String REMOTE_SAID = "anna-said";
	private final String USERNAME = "juan";
	private final String PASSWORD = "juanpw";
	private final String ACCOUNT_ANNA_AT_JUAN = "uri:juan:anna-account";
	private final String ACCOUNT_JUAN = "uri:uuid:juan-account";
	private final String ACCOUNT_NORBERT = "uri:uuid:norbert-account";
 	private final String SERVICE_NAME = "dime";
	private Tenant tenant1;
	private Tenant tenant2;
	private User user1;
	private User user2;

	ServiceAccount sa;
	AccountCredentials ac;
	ServiceProvider sp;

	
	@Autowired
	private CredentialStore credentialStore;
	
	@Autowired
	private EntityFactory entityFactory;
	
	@Before
	public void setup() throws Exception{
		setupTenant();
		
		user1 = entityFactory.buildUser();
		user1.setUsername("juan");
		user1.setPassword("juanpw");
		user1.setAccountUri(ACCOUNT_JUAN);
		user1.setRole(Role.OWNER);
		user1.setTenant(tenant1);
		user1.persist();
		user1.flush();
		
		user2 = entityFactory.buildUser();
		user2.setUsername("juan@anna");
		user2.setPassword("juanpw@anna");
		user2.setAccountUri("uri:anna:contact-juan");
		user2.setRole(Role.GUEST);
		user2.setTenant(tenant2);
		user2.persist();
		user2.flush();
		
		sp = entityFactory.buildServiceProvider();
		sp.setConsumerKey("consumer-key");
		sp.setConsumerSecret("consumer-secret");
		sp.setEnabled(true);
		sp.setServiceName(SERVICE_NAME);
		sp.persist();
		sp.flush();
		
		sa = entityFactory.buildServiceAccount();
		sa.setTenant(tenant1);
		sa.setServiceProvider(sp);
		sa.setAccessSecret("none");
		sa.setAccessToken("none");
		sa.setName(LOCAL_SAID);
		sa.setAccountURI(ACCOUNT_JUAN);
		sa.persist();
		sa.flush();
		
		ac = entityFactory.buildAccountCredentials();
		ac.setSecret(PASSWORD);
		ac.setTarget(REMOTE_SAID);
		ac.setTenant(tenant1);
		ac.setSource(sa);
		ac.setTargetUri(ACCOUNT_ANNA_AT_JUAN);
		ac.persist();
		ac.flush();
	}
	
	private void setupTenant() {
		tenant1 = entityFactory.buildTenant();
		tenant1.setName("juan");
		tenant1.persist();
		tenant1.flush();
		
		tenant2 = entityFactory.buildTenant();
		tenant2.setName("anna");
		tenant2.persist();
		tenant2.flush();
	}

	@After
	public void teardown(){

		user1.remove();
		user2.remove();
		ac.remove();
		
		sa.remove();
		sp.remove();

		tenant1.remove();
		tenant2.remove();
	}
	
	
	@Test
	@Transactional
	public void testGetPassword() {
		String password = credentialStore.getPassword(ACCOUNT_JUAN, ACCOUNT_ANNA_AT_JUAN, tenant1);
		Assert.assertEquals(PASSWORD, password);
	}
	
	@Test
	@Transactional
	public void testGetUsername() {
		String username = credentialStore.getUsername(ACCOUNT_JUAN, ACCOUNT_ANNA_AT_JUAN, tenant1);
		Assert.assertEquals(LOCAL_SAID, username);
	}
	
	@Test
	@Transactional
	public void testGetNameSaid() {
		//get saidname without target
		String name = credentialStore.getNameSaid(ACCOUNT_JUAN, ACCOUNT_ANNA_AT_JUAN, tenant1);
		Assert.assertEquals(REMOTE_SAID, name);
	}
	
	@Test
	@Transactional
	public void testGetProviderName() {
		String name = credentialStore.getProviderName(ACCOUNT_JUAN, tenant1);
		Assert.assertEquals(SERVICE_NAME, name);
	}
	
	@Test
	@Transactional
	public void testGetAccessToken() {
		String token = credentialStore.getAccessSecret(ACCOUNT_JUAN, tenant1);
		Assert.assertEquals("none", token);
	}

//	@Test
//	public void testGetAccessSecret() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testStoreCredentialsForAccount() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testUpdateCredentialsForAccount() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testStoreServiceProvider() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testGetConsumerSecret() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testGetConsumerKey() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testStoreOAuthCredentials() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testGetUriForName() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testGetUriForAccountName() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testTryCreateAccountCredentials() {
//		fail("Not yet implemented");
//	}
}
