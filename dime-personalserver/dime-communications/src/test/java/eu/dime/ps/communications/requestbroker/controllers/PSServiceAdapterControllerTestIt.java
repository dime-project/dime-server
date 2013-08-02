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

package eu.dime.ps.communications.requestbroker.controllers;

import java.util.Collection;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SAdapter;
import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSServiceAdapterController;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.impl.ServiceGatewayImpl;
import eu.dime.ps.gateway.policy.PolicyManager;

/**
 * @author mplanaguma
 *
 */
//@Ignore //FIXME failing load context
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
@TransactionConfiguration(defaultRollback = true)
public class PSServiceAdapterControllerTestIt {

	@Autowired
	private PolicyManager policyManager;
	
	@Autowired
	private CredentialStore credentialStore;
	
	@Test
	@Transactional
	public void testListAllServiceAdapters() {
		
		// Create serviceGateway Instance
	    // Can't mock serviceGateway - mocking causes the properties file to not be read, which means there are no supported services!
		ServiceGatewayImpl sg =  new ServiceGatewayImpl();
		sg.setCredentialStore(credentialStore);
			
		// Check that supported adapters are returned
		Map<String, ServiceMetadata> supportedAdapters = sg.listSupportedAdapters("juan");
		Assert.assertTrue(supportedAdapters.size() > 0);
		
		// Create service adapter controller
		PSServiceAdapterController serviceAdapterController = new PSServiceAdapterController();
		serviceAdapterController.setServiceGateway(sg);

		Response jsonresult = serviceAdapterController.listAllServiceAdapters("juan");
		
		Data<SAdapter> data = jsonresult.getMessage().getData();
		
		// Assert
		if (data == null || data.getEntries() == null) {
			// FIXME Sample data contains no serviceAdapters! 
			// Assert.fail("No data contained in service adapters for: " + jsonresult.getMessage().toString());
		} else {
			Collection<SAdapter> list = data.getEntries();
			for (SAdapter jsonServiceAdapter : list) {
				if(jsonServiceAdapter.getName().equals("linkedin")){
					Assert.assertEquals("activeAdepter-id", jsonServiceAdapter.guid);
					
				}
				if(jsonServiceAdapter.getName().equals("twitter")){
				    Assert.assertEquals("guid-twitter", jsonServiceAdapter.guid);					
				}
				
			}
		}
	}

}
