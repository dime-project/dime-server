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

package eu.dime.ps.gateway.service.internal;

import java.io.IOException;
import java.util.Properties;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.ps.gateway.util.DnsResolver;
import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;

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
	
	public String resolve (String targetURI) throws DimeDNSException {
		
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
                return "https://" + resolveSaid(said) + ":" + this.port + "/dime-communications";
	}

        public String resolveSaid (String said) throws DimeDNSException {
            try{
		return DnsResolver.resolve(dimeDns, said + ".dns.dime-project.eu");

            }catch (NameNotFoundException ex){
                throw new DimeDNSCannotResolveException("DNS failure: unable to resolve said: "
                        +said+" at "+dimeDns+"\nDetails: "+ex.getExplanation(),ex);

            }catch (CommunicationException ex){
                 throw new DimeDNSCannotConnectException("DNS failure: CommunicationException, propably server not accessible: "
                        +dimeDns+" (for said:"+said+")\nDetails: "+ex.getExplanation(),ex);
            }catch(NamingException ex){
                throw new DimeDNSException("DNS failure when trying to retrieve said: "+said+" at "+dimeDns+"\nDetails: " + ex.getExplanation(), ex);            }
	}

        public String getDimeDns(){
            return dimeDns;
        }

}
