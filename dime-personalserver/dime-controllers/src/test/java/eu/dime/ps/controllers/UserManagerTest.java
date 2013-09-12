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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.gateway.service.internal.AccountRegistrar;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@Ignore //some things not mockable...so skipped atm
public class UserManagerTest {

    private UserManagerImpl userManager;
    /*
     * Mocked dependencies
     */
    @Mock
    private TenantManager tenantManager;
    @Mock
    private AccountManager accountManager;
    @Mock
    private PersonManager personManager;
    @Mock
    private ProfileManager profileManager;
    @Mock
    private ProfileCardManager profileCardManager;
    @Mock
    private AccountRegistrar accountRegistrar;
    @Mock
    private EntityFactory entityFactory;
    @Mock
    private PersonContact profileMock;
    @Mock
    private User mockUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);


        doNothing().when(mockUser).persist();
        doNothing().when(mockUser).flush();
        doNothing().when(mockUser).remove();

        doThrow(new Exception("ping")).when(mockUser).persist();
        when(entityFactory.buildUser()).thenReturn(mockUser);

        userManager.setAccountRegistrar(accountRegistrar);
        userManager.setTenantManager(tenantManager);
        userManager.setAccountManager(accountManager);
        userManager.setPersonManager(personManager);
        userManager.setProfileCardManager(profileCardManager);
        userManager.setProfileManager(profileManager);
    }

    @After
    public void teardown() {
    }

    @Before
    public void initTest() {
    }

    @After
    public void tearDownTest() {
    }

   

    @Test
    public void testEnableRegister() {
        fail("TODO: implement test");
    }

    @Test
    public void testGetUserForAccountAndTenant() {
        fail("TODO: implement test");

    }

    @Test
    public void testGetEvalPreferences() {
        fail("TODO: implement test");

    }

    @Test
    public void testSetEvalPreferences() {
        fail("TODO: implement test");

    }
}
