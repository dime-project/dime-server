package eu.dime.ps.gateway.userresolver;

import org.junit.Assert;
import org.junit.Test;

import eu.dime.ps.gateway.userresolver.client.IdemixClient;

/**
 * 
 * @author marcel
 *
 */
public class IdemixClientTest {

	private final String issuerEndpoint = "http://dime.itsec-siegen.info/issuer/api/issuer";
	
	@Test
	public void testGenMasterSecret() throws Exception{
		IdemixClient client = new IdemixClient(issuerEndpoint);
		String secret = client.generateMasterSecret();
		Assert.assertNotNull(secret);
	}
}
