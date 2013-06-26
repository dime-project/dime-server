/**
 * 
 */
package eu.dime.ps.gateway.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.naming.NamingException;

import org.junit.Ignore;
import org.junit.Test;

import eu.dime.ps.gateway.util.DnsResolver;

/**
 * @author Sophie.Wrobel
 *
 */
@Ignore
public class DnsResolverTest {

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.util.DnsResolver#resolve(java.lang.String)}.
	 */
	@Test
	public void testResolve() {
		try {
			assertEquals(DnsResolver.resolve("91.126.187.167", "sit.dns.dime-project.eu"), "137.251.22.69");
			assertEquals(DnsResolver.resolve("8.8.8.8", "dime-project.eu"), "83.136.184.86");
		} catch (NamingException e) {
			fail ("Could not reach DNS server.");
		}
	}
}
