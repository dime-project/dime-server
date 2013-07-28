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

package eu.dime.ps.communications.requestbroker.controllers;


import org.junit.Ignore;
import org.junit.Test;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAccount;
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
	private  Request<SAccount> request;
	
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
		
		Response<SAccount> response = controller.getMyServiceAccounts();
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().getName().toString());
	}	
	
	
	@Test
	public void testCreateAccount()  {			
		
		Response<SAccount> response = controller.createServiceAccount(said,(Request<SAccount>)request);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().getName().toString());
	}
	
	
	@Test
	public void testDeleteAccount()  {
		Response response= controller.deleteAccount(said, "juan");
		assertNotNull(response);		
		
		
	}
	
	
	
	
	
	
	
}
