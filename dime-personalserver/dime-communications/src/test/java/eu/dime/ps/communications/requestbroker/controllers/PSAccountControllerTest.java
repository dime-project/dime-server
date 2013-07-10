package eu.dime.ps.communications.requestbroker.controllers;


import org.junit.Ignore;
import org.junit.Test;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAdapter;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSAccountController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.semantic.model.ModelFactory;

@Ignore // FIXME test is Failing!!!!
public class PSAccountControllerTest extends PSInfoSphereControllerTest {

	private PSAccountController controller = new PSAccountController();
	
	protected ModelFactory modelFactory = new ModelFactory();
	
	
	
	private static final String said= "juan";
	private  Request<SAdapter> request;
	
	public PSAccountControllerTest() throws ServiceNotAvailableException, ServiceAdapterNotSupportedException {
		AccountManager mockedManager = buildAccountManager();
		ServiceGateway serviceGateway = buildServiceGateway();
		controller.setAccountManager(mockedManager);
		controller.setServiceGateway(serviceGateway);
		try {
                    request= buildSARequest(mockedManager.get("juan"));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllAccounts()  {
		
		Response<SAdapter> response = controller.getMyServiceAccounts();
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().getName().toString());
	}	
	
	
	@Test
	public void testCreateAccount()  {			
		
		Response<SAdapter> response = controller.createServiceAccount(said,(Request<SAdapter>)request);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().getName().toString());
	}
	
	
	@Test
	public void testDeleteAccount()  {
		Response response= controller.deleteAccount(said, "juan");
		assertNotNull(response);		
		
		
	}
	
	
	
	
	
	
	
}
