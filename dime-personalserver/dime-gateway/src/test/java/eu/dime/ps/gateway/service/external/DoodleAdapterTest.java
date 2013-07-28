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
package eu.dime.ps.gateway.service.external;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.FacebookAdapterTest.AdapterNotEnabledException;
import eu.dime.ps.gateway.service.external.oauth.DoodleServiceAdapter;
import eu.dime.ps.gateway.transformer.Transformer;

/**
 * @author Sophie.Wrobel
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
public class DoodleAdapterTest {

	// Test credentials
	private String APP_ID="test";
	private String APP_SECRET="test";
	private String ACCESS_ID="test";
	private String ACCESS_SECRET="test";

	// @Autowired
	// private Transformer transformer;

	@Autowired
	private PolicyManager policyManager;

	final Logger logger = LoggerFactory.getLogger(LinkedInAdapterTest.class);
	class AdapterNotEnabledException extends Exception {
		
	}

	private DoodleServiceAdapter initAdapter() throws ServiceNotAvailableException, MalformedURLException, IOException, AdapterNotEnabledException {
		DoodleServiceAdapter adapter = new DoodleServiceAdapter();
		adapter.setConsumerToken(new Token(APP_ID, APP_SECRET));
		//adapter.setTransformer(transformer);
		
		// Set token
		Properties properties = new Properties();
		properties.load(new FileInputStream("services.properties"));
		if (!properties.getProperty("GLOBAL_ENABLED").contains("Doodle")) {
			throw new AdapterNotEnabledException();
		}
		adapter.setAccessToken(new Token(ACCESS_ID, ACCESS_SECRET));
		return adapter;
	}
	
	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.DoodleServiceAdapter#getRaw(java.lang.String)}.
	 */
	@Test
	public void testGetRaw() {
		try {
			// Retrieve User Information
			DoodleServiceAdapter adapter = initAdapter();
			ServiceResponse[] result = adapter.getRaw("/profile/@me/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));
	
			// Retrieve Events
			result = adapter.getRaw("/event/@me/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));
			
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable Doodle Adapter in services.properties to run this test.");
		}  catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.DoodleServiceAdapter#_set(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void test_set() {
		// Not supported
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.DoodleServiceAdapter#getAdapterName()}.
	 */
	@Test
	public void testGetAdapterName() {
		try {
			DoodleServiceAdapter adapter = initAdapter();
			assert (adapter.getAdapterName() == "Doodle");
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable Doodle Adapter in services.properties to run this test.");
		}  catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

}
