/**
 *
 */
package eu.dime.ps.gateway.service.internal;

import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sophie.Wrobel
 *
 */
public class DimeIPResolverTestIt {

    /**
     * Test method for {@link eu.dime.ps.controllers.service.dime.DimeIPResolver#resolve(java.lang.String)}.
     *
     * Note: This method assumes that "sit" is registered at the given IP
     * address. Update this before enabling the test!
     */
    @Ignore
    @Test
    public void testResolve() {
        DimeIPResolver resolver = new DimeIPResolver();
        try {
            assertTrue(resolver.resolve("sit").equals("https://137.251.22.69:8443/dime-communications"));
        } catch (DimeDNSException e) {
            fail(e.getMessage());
        }
    }
}
