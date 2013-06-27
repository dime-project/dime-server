/**
 * 
 */
package eu.dime.ps.gateway.service.external;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scribe.model.Token;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.LinkedInAdapterTest.AdapterNotEnabledException;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;

/**
 * Note: Please put in your developer keys before enabling this test!
 * 
 * @author Sophie.Wrobel
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
public class FacebookAdapterTest {
	
	// The di.me Facebook App is managed by Borja
	// To create a facebook application, visit here: https://developers.facebook.com/apps/
	// You need to put a valid app token and account access token here before you enable this test.
	private static final String APPID = "...";
	private static final String APPSECRET = "...";
	
	// To get an APP_ACCESS_TOKEN, visit here:
	// https://graph.facebook.com/oauth/access_token?client_id=APPID&client_secret=APPSECRET&grant_type=client_credentials
	private String app_access_token = "...";

	// You can find test users registered for an account here:
	// https://graph.facebook.com/APPID/accounts/test-users?access_token=APP_ACCESS_TOKEN
	private String id = "...";
	private String access_token = "...";

	final Logger logger = LoggerFactory.getLogger(FacebookAdapterTest.class);

	class AdapterNotEnabledException extends Exception {
		
	}

	private FacebookServiceAdapter initAdapter() throws ServiceNotAvailableException, MalformedURLException, IOException, AdapterNotEnabledException {
		FacebookServiceAdapter adapter = new FacebookServiceAdapter();
		adapter.setConsumerToken(new Token(APPID, APPSECRET));
		adapter.setAccessToken(new Token(id, access_token));
		
		Properties properties = new Properties();
		properties.load(new FileInputStream("services.properties"));
		if (!properties.getProperty("GLOBAL_ENABLED").contains("LinkedIn")) {
			throw new AdapterNotEnabledException();
		}
		return adapter;
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter#getRaw(java.lang.String)}.
	 */
	@Ignore
	@Test
	public void testGetRaw() {
		try {
			// Retrieve Profiles
			FacebookServiceAdapter adapter = initAdapter();
			ServiceResponse[] result = adapter.getRaw("/profileattribute/@me/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));
			result = adapter.getRaw("/profileattribute/"+this.id+"/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));

			// Retrieve Liveposts
			result = adapter.getRaw("/livepost/@me/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));
			result = adapter.getRaw("/livepost/"+this.id+"/@all");
			assert (result[0].getResponse().length() > 0);
			assertFalse (result[0].getResponse().contains("\"error\""));
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable Facebook Adapter in services.properties to run this test.");
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter#getAdapterName()}.
	 */
	@Test
	public void testGetAdapterName() {
		try {
			FacebookServiceAdapter adapter = initAdapter();
			assert (adapter.getAdapterName() == "Facebook");
		} catch (AdapterNotEnabledException e) {
			logger.warn("Please enable Facebook Adapter in services.properties to run this test.");
		}  catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

	/**
	 * Test method for {@link eu.dime.ps.gateway.service.ServiceAdapterBase#get(java.lang.String, java.lang.Class)}.
	 */
	@Ignore
	@Test
	public void testGet() {
		fail("Not yet implemented");
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
