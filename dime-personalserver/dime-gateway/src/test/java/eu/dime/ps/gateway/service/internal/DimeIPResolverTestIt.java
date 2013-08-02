/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
