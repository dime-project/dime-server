package eu.dime.ps.gateway.userresolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.testng.log4testng.Logger;

import eu.dime.ps.gateway.userresolver.client.IdemixClient;
import eu.dime.ps.gateway.userresolver.client.ResolverClient;

/**
 * 
 * @author marcel
 *
 */
public class IdemixClientTest {

	private Logger logger = Logger.getLogger(IdemixClientTest.class);
	
	private final String ISSUER_ENDPOINT = "http://dime.itsec-siegen.info/issuer/api/issuer";
	private static final String AUTH_ENDPOINT = 
			"http://dime.itsec-siegen.info//user-resolver/api/oauth";
	
	private static final String RESOLVER_ENDPOINT = 
			"http://dime.itsec-siegen.info//user-resolver/api/users";
	
	@Test
	public void testGenMasterSecret() throws Exception{
		IdemixClient client = new IdemixClient(ISSUER_ENDPOINT);
		String secret = client.generateMasterSecret();
		Assert.assertNotNull(secret);
		System.out.println(secret);
	}
	@Test
	public void testGetCredential() throws Exception{
		IdemixClient client = new IdemixClient(ISSUER_ENDPOINT);
		String secret = client.generateMasterSecret();
		Map<String, String> values = new HashMap<String, String>();
		values.put("name", "juan");
		values.put("surname", "martinez");
		values.put("nickname", "juanito");
		String credential = client.getCredential(secret, "dime-credential", values);
		Assert.assertNotNull(credential);

	}
	
	
	/**
	 * Tests complete flow: credential issuance, show proof, get oAuth token, register & search at URS
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception{
		IdemixClient idemixClient = new IdemixClient(ISSUER_ENDPOINT);
		ResolverClient resolverClient = new ResolverClient(
				RESOLVER_ENDPOINT, AUTH_ENDPOINT, idemixClient);
		
		
		String masterSecret = idemixClient.generateMasterSecret();
		
		String name = "foo";
		String surname = "bar";
		String nickname = "foobar";
		String said = "F00B4R2" 
				+ (new Random(System.currentTimeMillis()).nextInt() % 1000);
		
		Map<String, String> values = new HashMap<String, String>();
		values.put("name", name);
		values.put("surname", surname);
		values.put("nickname", nickname);
		String credential = 
				idemixClient.getCredential(
						masterSecret, "dime-credential", values);
		logger.info("Credential: {" + credential +"}");
		
		////////////////////////////////////////////////////////////////////////
		// Test register and search at URS
		
		String scope = "register"; //defines proofSpec
		String token = resolverClient.getToken(scope, masterSecret, credential);
		logger.info("Token: {"+ token+"}"); //OAuth2 Bearer token
		Assert.assertNotNull(token);
		name=null;
		//said=null;
		//403 if scope == search
		//resolverClient.register(token ,name, surname, nickname, said);
		resolverClient.search(token, name, surname, nickname);
	}
}
