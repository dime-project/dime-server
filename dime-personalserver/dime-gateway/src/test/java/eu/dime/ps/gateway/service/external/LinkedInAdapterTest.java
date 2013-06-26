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
 * @author Sophie.Wrobel
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
public class LinkedInAdapterTest {

	// The di.me Facebook App is managed by Borja
	// To create a facebook application, visit here: https://developers.facebook.com/apps/
	// Test users can be created from that page. It seems that the graph API for creating test users programmatically is not working.
	private static final String APPID = "251215821668348";
	private static final String APPSECRET = "25e53670ae161465165bac4cc5c14640";

	// To get an APP_ACCESS_TOKEN, visit here:
	// https://graph.facebook.com/oauth/access_token?client_id=APPID&client_secret=APPSECRET&grant_type=client_credentials
	private String app_access_token = "251215821668348|_kMuaDDGBDt0Lhc4WEI_vvdhuNQ";

	// You can find test users registered for an account here:
	// https://graph.facebook.com/APPID/accounts/test-users?access_token=APP_ACCESS_TOKEN
	private String id = "100004484774596";
	private String access_token = "AAADker3PZCZCwBAIFYpAdcuBkXljX3OLQlcil3B4x93KjZBUu1ZB0hs1cwTbV6wIcVAmDIRXlkqRIEkpYXYQSsXvTCAbyo4s0nWxcOrQnlVM4CIfxfhH";
	
	@Autowired
	private Transformer transformer;

	@Autowired
	private PolicyManager policyManager;

	/**
	 * Test method for
	 * {@link eu.dime.ps.controllers.service.ametic.AMETICDummyAdapter#get(java.lang.String)}
	 * .
	 */
	// These tests will only work if you add your linkedin token to the
	// services.properties:
	//   LinkedIn_TestToken=...
	//   LinkedIn_TestSecret=...
	// After doing that you can remove the Ignore annotation.
	private LinkedInServiceAdapter initAdapter()
			throws ServiceNotAvailableException, FileNotFoundException, IOException {
		LinkedInServiceAdapter adapter = new LinkedInServiceAdapter();
		adapter.setConsumerToken(new Token(APPID, APPSECRET));
		adapter.setTransformer(transformer);
		
		// Set token
		Properties properties = new Properties();
		properties.load(new FileInputStream("services.properties"));
		adapter.setAccessToken(new Token(properties.getProperty("LinkedIn_TestToken"), properties.getProperty("LinkedIn_TestSecret")));

		return adapter;
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.LinkedInServiceAdapter#getRaw(java.lang.String)}.
	 */
	@Ignore
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
