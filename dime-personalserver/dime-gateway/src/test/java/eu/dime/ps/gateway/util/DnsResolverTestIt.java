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
public class DnsResolverTestIt {

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
