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
package eu.dime.ps.gateway.service;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.impl.ServiceGatewayImpl;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.service.external.AMETICDummyAdapter;

/**
 * @author Sophie.Wrobel
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/storage-loading-tests-context.xml")
public class ServiceGatewayTestIt {

	private ServiceGatewayImpl serviceGateway;
	private PolicyManagerImpl policyManager;
	final Logger logger = LoggerFactory.getLogger(ServiceGatewayTestIt.class);
	
	public ServiceGatewayTestIt() {
		super();
	}

	/**
	 * Test method for
	 * {@link eu.dime.ps.controllers.service.ServiceAdapterManagerImpl#ServiceGatewayImpl()}.
	 */
	@Test
	public void testServiceGateway() {
		this.serviceGateway = new ServiceGatewayImpl();
		this.policyManager = PolicyManagerImpl.getInstance();
		if (this.serviceGateway == null) {
			fail("Could not create new ServiceGateway.");
		}
	}
	
	@Test
	public void testGetHost() {
		String tenantName = "juan";
		this.serviceGateway = new ServiceGatewayImpl();
		this.policyManager = PolicyManagerImpl.getInstance();
		this.serviceGateway.getHost("juan").equals("http://localhost:8443/services/connect/twitter?id=");
	}

//	THIS METHOD HAS BEEN REMOVED
//	/**
//	 * Test method for
//	 * {@link eu.dime.ps.controllers.service.ServiceAdapterManagerImpl#setServiceAdapter(java.lang.String, eu.dime.ps.controllers.service.ServiceAdapter)}
//	 * and {@link eu.dime.ps.controllers.service.ServiceAdapterManagerImpl#getServiceAdapter(java.lang.String)}.
//	 */
//	@Test
//	public void testGetSetServiceAdapter() {
//		this.serviceGateway = new ServiceGatewayImpl();
//		ServiceAdapter serviceAdapter;
//		try {
//			serviceAdapter = new AMETICDummyAdapter();
//			serviceAdapter.setIdentifer("test");
//			this.serviceGateway.setServiceAdapter(serviceAdapter);
//			assert (this.serviceGateway.getServiceAdapter("test").getClass().getName()
//					.equals(serviceAdapter.getClass().getName()));
//		} catch (ServiceNotAvailableException e) {
//			fail("AMETIC Dummy Service not available.");
//		} catch (ServiceAdapterNotSupportedException e) {
//			fail("AMETICDummyService is not supported. Check your services.properties file to make sure it is enabled.");
//		}
//	}

//	FIXME setServiceAdapter HAS BEEN REMOVED
//	/**
//	 * Test method for
//	 * {@link eu.dime.ps.communications.services.MockServiceGateway#unsetServiceAdapter(java.lang.String)}.
//	 */
//	@Test
//	public void testUnsetServiceAdapter() {
//		this.serviceGateway = new ServiceGatewayImpl();
//		ServiceAdapter serviceAdapter;
//		try {
//			serviceAdapter = new AMETICDummyAdapter();
//			serviceAdapter.setIdentifer("deleteMe");
//			this.serviceGateway.setServiceAdapter(serviceAdapter);
//			this.serviceGateway.unsetServiceAdapter("deleteMe");		
//			if (this.serviceGateway.getServiceAdapter("deleteMe") != null) {
//				fail("Could not delete service adapter deleteMe.");
//			}
//		} catch (ServiceNotAvailableException e) {
//			fail("AMETIC Dummy Service Service not available.");
//		} catch (ServiceAdapterNotSupportedException e) {
//			fail("AMETICDummyService is not supported. Check your services.properties file to make sure it is enabled.");
//		}		
//	}

	/**
	 * Test method for
	 * {@link eu.dime.ps.communications.services.MockServiceGateway#listSupportedAdapters()}.
	 */
	@Test
	public void testListSupportedAdapters() {
		this.serviceGateway = new ServiceGatewayImpl();
		Map<String, ServiceMetadata> supportedAdapters = this.serviceGateway
				.listSupportedAdapters("juan");
		try {
			// Make sure that LinkedIn is actually supported
			if (this.serviceGateway.makeServiceAdapter("LinkedIn") != null) {
				assert (supportedAdapters.size() > 0);
				assert (supportedAdapters.containsKey("LinkedIn"));
			}
		} catch (Exception e) {
			// LinkedIn is not a supported adapter - it has not been activated!
			logger.warn("To run this test, please enable LinkedIn in services.properties.");
			e.printStackTrace();
		}
	}

}
