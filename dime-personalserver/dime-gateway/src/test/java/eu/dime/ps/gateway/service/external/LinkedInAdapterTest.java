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

package eu.dime.ps.gateway.service.external;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.LinkedInServiceAdapter;
import eu.dime.ps.gateway.transformer.Transformer;

/**
 * Note: Please put in your developer keys before enabling this test!
 * 
 * @author Sophie.Wrobel
 * 
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
public class LinkedInAdapterTest {

	// To create a facebook application, visit here: https://developers.facebook.com/apps/
	// Test users can be created from that page. It seems that the graph API for creating test users programmatically is not working.
	private static final String APPID = "...";
	private static final String APPSECRET = "...";

	// To get an APP_ACCESS_TOKEN, visit here:
	// https://graph.facebook.com/oauth/access_token?client_id=APPID&client_secret=APPSECRET&grant_type=client_credentials
	private String app_access_token = "...";

	// You can find test users registered for an account here:
	// https://graph.facebook.com/APPID/accounts/test-users?access_token=APP_ACCESS_TOKEN
	private String id = "...";
	private String access_token = "...";
	
	@Autowired
	private Transformer transformer;

	@Autowired
	private PolicyManager policyManager;
	
	final Logger logger = LoggerFactory.getLogger(LinkedInAdapterTest.class);

	class AdapterNotEnabledException extends Exception {
		
	}

	/**
	 * Test method for
	 * {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#get(java.lang.String)}
	 * .
	 * @throws AdapterNotEnabledException 
	 */
	// These tests will only work if you add your linkedin token to the
	// services.properties:
	//   LinkedIn_TestToken=...
	//   LinkedIn_TestSecret=...
	// After doing that you can remove the Ignore annotation.
	private LinkedInServiceAdapter initAdapter()
			throws ServiceNotAvailableException, FileNotFoundException, IOException, AdapterNotEnabledException {
		LinkedInServiceAdapter adapter = new LinkedInServiceAdapter();
		adapter.setConsumerToken(new Token(APPID, APPSECRET));
		adapter.setTransformer(transformer);
		
		// Set token
		Properties properties = new Properties();
		properties.load(new FileInputStream("services.properties"));
		if (!properties.getProperty("GLOBAL_ENABLED").contains("LinkedIn")) {
			throw new AdapterNotEnabledException();
		}
		adapter.setAccessToken(new Token(properties.getProperty("LinkedIn_TestToken"), properties.getProperty("LinkedIn_TestSecret")));

		return adapter;
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.LinkedInServiceAdapter#getRaw(java.lang.String)}.
	 */
	@Test
	public void testGetRaw() {
		try {
			// Stuff to test
			ArrayList<String> attributes = new ArrayList<String>();
			attributes.add("/livepost/@me/@all");
			attributes.add("/livepost/@all");
			attributes.add("/person/@me/@all");
			attributes.add("/profileattribute/@me/@all");
			attributes.add("/profile/@me/@all");
			
			// Check that conversion was ok
			LinkedInServiceAdapter adapter = initAdapter();
			Iterator<String> iter = attributes.iterator();
			while (iter.hasNext()) {
				ServiceResponse[] s = adapter.getRaw(iter.next());
				assertNotNull(s);
				assertTrue(s[0].getResponse().length() > 0);
				assertFalse (s[0].getResponse().contains("\"error\""));
			}
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable LinkedIn Adapter in services.properties to run this test.");
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link eu.dime.ps.communications.services.ametic.LinkedInServiceAdapter#getAdapterName()}
	 * .
	 */
	@Test
	public void testGetAdapterName() {
		try {
			LinkedInServiceAdapter adapter = initAdapter();
			assert (adapter.getAdapterName() == "LinkedIn");
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable LinkedIn Adapter in services.properties to run this test.");
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.ServiceAdapterBase#set(java.lang.String, java.lang.Object)}.
	 */
	@Ignore
	@Test
	public void testSet() {
		fail("Not yet implemented");
	}

}
