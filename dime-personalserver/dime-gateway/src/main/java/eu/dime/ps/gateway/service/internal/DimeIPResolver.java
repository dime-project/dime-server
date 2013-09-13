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

package eu.dime.ps.gateway.service.internal;

import java.io.IOException;
import java.util.Properties;

import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.ps.gateway.service.dns.DimeDNSCannotConnectException;
import eu.dime.ps.gateway.service.dns.DimeDNSCannotResolveException;
import eu.dime.ps.gateway.service.dns.DimeDNSException;
import eu.dime.ps.gateway.service.dns.DnsResolver;

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

	private final String dimeDns;

	public DimeIPResolver() throws DimeDNSException {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
			this.dimeDns = properties.getProperty("GLOBAL_DIME_DNS");
			if (this.dimeDns == null) {
				throw new DimeDNSException("Could not load GLOBAL_DIME_DNS from services.properties.");
			}
		} catch (IOException e) {
			throw new DimeDNSException("Could not load GLOBAL_DIME_DNS from services.properties: " + e.getMessage(), e);
		}
	}
	
	public String resolve(String said) throws DimeDNSCannotConnectException,
			DimeDNSCannotResolveException, DimeDNSException {
		try {
			return DnsResolver.resolve(dimeDns, said + ".dns.dime-project.eu");
		} catch (NameNotFoundException e) {
			throw new DimeDNSCannotResolveException(
					"DNS failure: unable to resolve said " + said + " at "
							+ dimeDns + ": " + e.getMessage(), e);

		} catch (CommunicationException e) {
			throw new DimeDNSCannotConnectException(
					"DNS failure: CommunicationException, propably server not accessible: "
							+ dimeDns + " (for said:" + said + "): " + e.getMessage(), e);
		} catch (NamingException e) {
			throw new DimeDNSException(
					"DNS failure when trying to retrieve said " + said
							+ " at " + dimeDns + ": " + e.getMessage(), e);
		}
	}

	public String getDimeDns() {
		return dimeDns;
	}

}
