package eu.dime.ps.gateway.service.internal;

import java.io.IOException;
import java.util.Properties;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.ps.gateway.util.DnsResolver;

/**
 * THROW-AWAY CLASS
 * 
 * Maps IP address with Target URI. This should ideally be handled by a proxy,
 * but we don't have a proxy for the PoC.
 * 
 * @author Sophie.Wrobel
 * 
 */
public class DimeIPResolver {

	private static final Logger logger = LoggerFactory.getLogger(DimeIPResolver.class);

	private static String port;
	private String dimeDns;
	
	public DimeIPResolver() {
		// TODO: Resolve port properly
		try {
			if (this.port == null || this.dimeDns == null) {
				Properties mapping = PropertiesLoaderUtils
					.loadAllProperties("services.properties");
				this.port = mapping.getProperty("GLOBAL_AUTHSERVLET_PORT_SECURE");
				this.dimeDns = mapping.getProperty("DIME_DNS");
			}
		} catch (IOException e) {
			logger.warn("Could not load dime DNS. Assuming standard DNS.", e);
			this.port = "443";
		}
	}
	
	public String resolve (String targetURI) throws NamingException {
		
		/*
		 * TODO: Generalize resolution for all possible paths, including 
		 * http://my.domain.com:4021/long/path/to/dime-communications 
		 * (custom port, custom sub-path, custom subdomain, http/https) as war deployment path!
		 * 
		 * Assumptions for Segovia:
		 *  - should resolve to https://<TargetDNS>/dime-communications 
		 *  - <TargetURI> system is already configured to use the dime-DNS (no special DNS configuration takes place here).
		 */
		
		// FIXME doing this for the ametic event as well, getting only the last part of the account URI
		String said = targetURI.substring(targetURI.lastIndexOf(":") + 1, targetURI.length());
		
		String targetLocation = "https://" + DnsResolver.resolve(dimeDns, said + ".dns.dime-project.eu") + ":" + this.port + "/dime-communications";

		return targetLocation;
	}

}
