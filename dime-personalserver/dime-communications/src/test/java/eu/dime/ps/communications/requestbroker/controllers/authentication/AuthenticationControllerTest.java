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

package eu.dime.ps.communications.requestbroker.controllers.authentication;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.when;

import eu.dime.commons.dto.AccountEntry;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.accesscontrol.AccessControlManager;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.User;

public class AuthenticationControllerTest {
	
	private AuthenticationController authenticationController = new AuthenticationController();
	
	/* ids etc */
	private final String SAID_LOCAL = "local";
	private final String SAID_REMOTE = "remote";
	
	@Mock AccessControlManager accessControlManager;
	@Mock UserManager userManager;
	
    @Before
    public void setUp() throws Exception {
    	MockitoAnnotations.initMocks(this);
    	User user1 = new User();
    	user1.setId(new Long(1));
    	user1.setPassword("juanpw");
    	user1.setUsername("juan");
    	user1.setRole(Role.GUEST);
    	user1.setEnabled(false);
    	
    	User user2 = new User();
    	user2.setPassword("juanpw");
    	user2.setUsername("juan");
    	user2.setRole(Role.GUEST);
    	user2.setEnabled(true);
    	
    	when(userManager.getUserForAccountAndTenant(SAID_REMOTE, SAID_LOCAL)).thenReturn(user1);
    	//when(userManager.enable(new Long(1))).thenReturn(user2);
    
    	authenticationController.setAccessControlManager(accessControlManager);
    	authenticationController.setUserManager(userManager);
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testDeleteAccount() {
    	
    }
    
    @Test
    public void testChangePassword() {
    	
    }
    
    @Test
    public void testGetCredentials() {
    	//TODO: test if correct pw is returned
    	//TODO: check if new password is retured after calling twice
    	Response<AccountEntry> response  = authenticationController.getCredentials(SAID_LOCAL, SAID_REMOTE);
    	Collection<AccountEntry> col = response.getMessage().getData().getEntries();
    	AccountEntry account = col.iterator().next();
    	String tmpPassword = account.getPassword();
    	response  = authenticationController.getCredentials(SAID_LOCAL, SAID_REMOTE);
    	col = response.getMessage().getData().getEntries();
    	account = col.iterator().next();
    	
    	Assert.assertFalse(!account.getPassword().equals(tmpPassword));
    }
    
    @Test
    public void testConfirmCredentials() {
    	
    	authenticationController.confirmCredentials("", "");
    	//TODO: check if enabled successful
    	//TODO: check if not possible to getCred again
    }
}
