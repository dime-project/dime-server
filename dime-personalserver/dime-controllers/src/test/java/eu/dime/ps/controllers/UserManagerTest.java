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

import eu.dime.ps.controllers.account.register.DimeDNSRegisterService;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
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
    private DimeDNSRegisterService dimeDNSRegisterService;
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

        userManager.setDimeDNSRegisterService(dimeDNSRegisterService);
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
