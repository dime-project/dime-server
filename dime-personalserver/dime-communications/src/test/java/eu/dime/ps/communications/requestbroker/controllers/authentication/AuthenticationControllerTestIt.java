package eu.dime.ps.communications.requestbroker.controllers.authentication;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.AccountEntry;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class AuthenticationControllerTestIt {

	private AuthenticationController authenticationController;
	
	/* ids etc */
	private final String SAID_LOCAL = "local";
	private final String SAID_REMOTE = "remote";
	
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetCredentials() {
    	//FIXME: implement
    }
    
    @Test
    public void testConfirmCredentials() {
    	//FIXME: implement
    }
    @Ignore
    @Test
    public void testChangePassword() {
    }
}
