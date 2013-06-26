/**
 * 
 */
package eu.dime.ps.gateway.auth;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.openrdf.repository.RepositoryException;

import eu.dime.ps.gateway.auth.impl.CredentialStoreImpl;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * @author marcel
 *
 */
@Ignore
public class CredentialStoreTest {

	private CredentialStore credentialStore;
	
	@Mock ConnectionProvider connectionProvider;
	@Mock EntityFactory entityFactory;
	
	@Mock Connection connection;
	
	
	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		
		when(connectionProvider.getConnection("test")).thenReturn(connection);
		
		credentialStore = new CredentialStoreImpl();
		
		credentialStore.setEntityFactory(entityFactory);
		credentialStore.setConnectionProvider(connectionProvider);
	}
	@Test
	public void testGetPassword() {
		String password = credentialStore.getPassword("sender", "receiver");
	}
	
	@Test
	public void testGetUsername() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNameSaid() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetProviderName() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetAccessToken() {
		fail("Not yet implemented");
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
