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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.commons.dto.DNSRegister;
import eu.dime.commons.util.HttpUtils;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.gateway.service.internal.AccountCannotResolveException;
import eu.dime.ps.gateway.service.internal.AccountRegistrar;
import eu.dime.ps.gateway.service.internal.DimeIPResolver;
import eu.dime.ps.storage.entities.ServiceAccount;

public class DNSAccountRegistrar implements AccountRegistrar {

	private static final Logger logger = LoggerFactory.getLogger(DNSAccountRegistrar.class);

	private static final String BAD_CONFIG = "DNSAccountRegistrar configuration is not correct. Make sure" +
			" services.properties is loaded correctly, and it contains values for GLOBAL_IPADDRESS and GLOBAL_DIME_DNS.";

	private DimeIPResolver ipResolver;
	
	private String ipAddress = null;
	private String dns = null;
	private String port = null;
	
	public DNSAccountRegistrar() {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
			this.ipAddress = properties.getProperty("GLOBAL_SERVER_IPADDRESS");
			this.port = properties.getProperty("GLOBAL_SERVER_PORT");
			
			this.ipResolver = new DimeIPResolver();
			this.dns = this.ipResolver.getDimeDns();
		} catch (IOException e) {
			logger.error("Could not load properties from services.properties. DNSAccountRegistrar will not be able "
					+ "to register or resolver account identifiers.", e);
		} catch (DimeDNSException e) {
			
		}
	}

	private boolean isConfigured() {
		return this.ipAddress != null && this.dns != null && this.port != null; 
	}
	
	@Override
	public boolean register(String accountId) {
		if (!isConfigured()) {
			logger.error(BAD_CONFIG);
			return false;
		}
		
		try {
			ipResolver.resolve(accountId);
		} catch (DimeDNSCannotResolveException e){
			String today = String.valueOf(System.nanoTime()).substring(0, 6);
	        DNSRegister payload = new DNSRegister(today, ipAddress, accountId + ".dns.dime-project.eu", "");
			return doPost(payload);
		} catch (DimeDNSException e) {
			logger.error("Problem ocurred when trying to reach the DNS server: " + e.getMessage(), e);
		}
		return false;
	}

	@Override
	public String register(ServiceAccount account) {
		if (!isConfigured()) {
			logger.error(BAD_CONFIG);
			return null;
		}

		if (account.getAccountURI() == null) {
			return null;
		}

		String accountUri = account.getAccountURI().toString();
		String accountId = accountUri.substring(accountUri.lastIndexOf(":") + 1, accountUri.length());
		if (register(accountId)) {
			return accountId;
		}
		return null;
	}
	
	@Override
	public URL resolve(String accountId) throws AccountCannotResolveException {
		if (!isConfigured()) {
			throw new AccountCannotResolveException(BAD_CONFIG);
		}

		/*
		 * TODO: Generalize resolution for all possible paths, including
		 * http://my.domain.com:4021/long/path/to/dime-communications (custom
		 * port, custom sub-path, custom subdomain, http/https) as war
		 * deployment path!
		 */

		URL url = null;
		try {
			url = new URL("https://" + ipResolver.resolve(accountId) + ":8443/dime-communications");
		} catch (MalformedURLException e) {
			throw new AccountCannotResolveException("Cannot resolve account " + accountId + ": " + e.getMessage(), e);
		} catch (DimeDNSException e) {
			throw new AccountCannotResolveException("Cannot resolve account " + accountId + ": " + e.getMessage(), e);
		}
		return url;
	}
	
	private boolean doPost(DNSRegister payload) {
		HttpClient httpClient = HttpUtils.createHttpClient();
		String dnsRegisterUrl = "http://" + this.dns + ":8080/dime_dns_registry/recordses";

		try {
			HttpPost request = new HttpPost(dnsRegisterUrl);
			
			String payloadString = JaxbJsonSerializer.jsonValue(payload);
			StringEntity params = new StringEntity(payloadString);
			request.addHeader("Content-type", "application/json");
			request.setEntity(params);
			
			httpClient.execute(request);
			return true;
		} catch (ClientProtocolException ex) {
			logger.error("Unable to register at" + dnsRegisterUrl + ": " + ex.getMessage(), ex);
		} catch (UnsupportedEncodingException ex) {
			logger.error("Unable to register at" + dnsRegisterUrl + ": " + ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error("Unable to register at" + dnsRegisterUrl + ": " + ex.getMessage(), ex);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return false;
	}

}
