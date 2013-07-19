package eu.dime.ps.gateway.util;

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
