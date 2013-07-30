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
import eu.dime.ps.storage.entities.Tenant;
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

	Tenant tenant1;
	
	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		
		when(connectionProvider.getConnection("test")).thenReturn(connection);
		
		credentialStore = new CredentialStoreImpl();
		
		credentialStore.setEntityFactory(entityFactory);
		credentialStore.setConnectionProvider(connectionProvider);
        setupTenant();
	}

    private void setupTenant() {
		tenant1 = entityFactory.buildTenant();
		tenant1.setName("juan");
		tenant1.setId(new Long(1));
		tenant1.persist();

	}

	@Test
	public void testGetPassword() {
		String password = credentialStore.getPassword("sender", "receiver", tenant1);
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
