/**
 * 
 */
package eu.dime.ps.gateway.service.external;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scribe.model.Token;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;

/**
 * @author Sophie.Wrobel
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-config/adapter-tests-context.xml"})
public class FacebookAdapterTest {
	
	// The di.me Facebook App is managed by Borja
	// To create a facebook application, visit here: https://developers.facebook.com/apps/
	// You need to put a valid app token and account access token here before you enable this test.
	private static final String APPID = "495923027090815";
	private static final String APPSECRET = "6e69f6b6c4623ba0bfd6bf5ba6e8bfef";
	
	// To get an APP_ACCESS_TOKEN, visit here:
	// https://graph.facebook.com/oauth/access_token?client_id=APPID&client_secret=APPSECRET&grant_type=client_credentials
	private String app_access_token = "495923027090815|qlGcR6Oh7ws07yfrQCXoNf-atZg";

	// You can find test users registered for an account here:
	// https://graph.facebook.com/APPID/accounts/test-users?access_token=APP_ACCESS_TOKEN
	private String id = "100004086272271";
	private String access_token = "AAAHDChPdnX8BAFIxxbQu3af56qKm1yzE6Mdkf7nWTatDpZBqgAQrgCbCWZAKkdpUhmtd15Y6V0Deh7HCFOzvuCpjdZAt5Vlj1LfA7qlwrhr2JpMTEsf";
	

	private FacebookServiceAdapter initAdapter() throws ServiceNotAvailableException, MalformedURLException, IOException {
		FacebookServiceAdapter adapter = new FacebookServiceAdapter();
		adapter.setConsumerToken(new Token(APPID, APPSECRET));
		adapter.setAccessToken(new Token(id, access_token));
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
		} catch (Exception e) {
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
