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

package eu.dime.ps.gateway.service.dns;

import java.util.Hashtable;
import javax.naming.NameNotFoundException;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * @author Sophie.Wrobel
 */
public class DnsResolver {

    /**
     * @param dns the custom dns server to resolve against
     * @param host di.me hostname to resolve
     * @return IP address of hostname, throws NameNotFoundException if non-existant
     * @throws NamingException
     */
    public static String resolve(String dns, String host) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns://" + dns);
        DirContext ictx = new InitialDirContext(env);
        Attributes ipAddr = ictx.getAttributes(host, new String[]{"A"});

        // Trap for non-existant entries
        if (!ipAddr.getAll().hasMoreElements()) {
            throw new NameNotFoundException("No entry found for host: " + host);
        }

        return (String) ipAddr.getAll().nextElement().get();
    }
}
