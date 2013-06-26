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

import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@Ignore
@ContextConfiguration(locations = { "classpath*:**/applicationContext-credential-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class CredentialStoreTestIt {

	/* ids */
	private final String LOCAL_SAID = "juan-said";
	private final String REMOTE_SAID = "anna-said";
	private final String USERNAME = "juan";
	private final String PASSWORD = "juanpw";
	private final String ACCOUNT_ANNA = "uri:uuid:anna-account";
	private final String ACCOUNT_JUAN = "uri:uuid:juan-account";
	private final String ACCOUNT_NORBERT = "uri:uuid:norbert-account";
 	private final String SERVICE_NAME = "dime";
	private Tenant tenant1;
	private Tenant tenant2;

	
	@Autowired
	private CredentialStore credentialStore;
	
	@Autowired
	private EntityFactory entityFactory;
	
	@Before
	public void setup() throws Exception{
		setupTenant();
		
		User user = entityFactory.buildUser();
		user.setUsername("juan");
		user.setPassword("juanpw");
		user.setAccountUri(ACCOUNT_JUAN);
		user.setRole(Role.OWNER);
		user.setTenant(tenant1);
		user.persist();
		
		user = entityFactory.buildUser();
		user.setUsername("juan@anna");
		user.setPassword("juanpw@anna");
		user.setAccountUri("uri:anna:contact-juan");
		user.setRole(Role.GUEST);
		user.setTenant(tenant2);
		user.persist();
		
		ServiceProvider sp = entityFactory.buildServiceProvider();
		sp.setConsumerKey("consumer-key");
		sp.setConsumerSecret("consumer-secret");
		sp.setEnabled(true);
		sp.setServiceName(SERVICE_NAME);
		sp.persist();
		
		ServiceAccount sa = entityFactory.buildServiceAccount();
		sa.setTenant(tenant1);
		sa.setServiceProvider(sp);
		sa.setAccessSecret("none");
		sa.setAccessToken("none");
		sa.setName(LOCAL_SAID);
		sa.persist();
		
		AccountCredentials ac = entityFactory.buildAccountCredentials();
		ac.setSecret(PASSWORD);
		ac.setTarget(REMOTE_SAID);
		ac.setTenant(tenant1);
		ac.setSource(sa);
		ac.setTargetUri("uri:juan:contact-anna");
		ac.persist();
	}
	
	private void setupTenant() {
		tenant1 = entityFactory.buildTenant();
		tenant1.setName("juan");
		tenant1.setId(new Long(1));
		tenant1.persist();
		
		tenant2 = entityFactory.buildTenant();
		tenant2.setName("anna");
		tenant2.setId(new Long(2));
		tenant2.persist();
	}

	@After
	public void teardown(){
		
	}
	
	
	@Test
	public void testGetPassword() {
		String password = credentialStore.getPassword(REMOTE_SAID, LOCAL_SAID);
		Assert.assertEquals(PASSWORD, password);
	}
	
	@Test
	public void testGetUsername() {
		String username = credentialStore.getUsername(REMOTE_SAID, LOCAL_SAID);
		Assert.assertEquals(USERNAME, username);
	}
	
	@Test
	public void testGetNameSaid() {
		String name = credentialStore.getNameSaid(ACCOUNT_JUAN);
		Assert.assertEquals("juan", name);
	}
	
	@Test
	public void testGetProviderName() {
		String name = credentialStore.getProviderName(ACCOUNT_JUAN);
		Assert.assertEquals("di.me", name);
	}
	
	@Test
	public void testGetAccessToken() {
		String token = credentialStore.getAccessSecret(ACCOUNT_JUAN);
		Assert.assertEquals("none", token);
	}

	@Test
	public void testGetAccessSecret() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testStoreCredentialsForAccount() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testUpdateCredentialsForAccount() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testStoreServiceProvider() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetConsumerSecret() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetConsumerKey() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testStoreOAuthCredentials() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetUriForName() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetUriForAccountName() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testTryCreateAccountCredentials() {
		fail("Not yet implemented");
	}
}
