package eu.dime.ps.gateway.service.internal.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.ps.gateway.service.internal.AccountCannotResolveException;
import eu.dime.ps.gateway.service.internal.AccountRegistrar;
import eu.dime.ps.storage.entities.ServiceAccount;

/**
 * 
 * @author Ismael Rivera
 */
public class SelfContainedAccountRegistrar implements AccountRegistrar {

	private static final Logger logger = LoggerFactory.getLogger(SelfContainedAccountRegistrar.class);

	private static final String BAD_CONFIG = "SelfContainedAccountRegistrar configuration is not correct." +
			" Make sure services.properties is loaded correctly, and it contains values for GLOBAL_SERVER.";
	
	private String server = null;
	
	public SelfContainedAccountRegistrar() {
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
			this.server = properties.getProperty("GLOBAL_SERVER");
		} catch (IOException e) {
			logger.error("Could not load properties from services.properties. SelfContainedAccountRegistrar will" +
					" not be able to register or resolver account identifiers.", e);
		}
	}

	private boolean isConfigured() {
		return this.server != null; 
	}

	@Override
	public boolean register(String accountId) {
		// no-op
		return true;
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
		accountId = accountId.concat("@").concat(this.server);
		
		return accountId;
	}

	@Override
	public URL resolve(String accountId) throws AccountCannotResolveException {
		if (!isConfigured()) {
			throw new AccountCannotResolveException(BAD_CONFIG);
		}

		if (accountId == null) {
			throw new AccountCannotResolveException("Cannot resolve account since the accountId parameter is null");
		}
		
		String server = null;
		String[] parts = accountId.split("@");
		if (parts.length == 2) {
			server = parts[1];
		} else {
//			// transparently accepting 'clean' ids such as eab4d140-1955-11e3-8ffd-0800200c9a66+example.org+8080
//			parts = accountId.split("\\+");
//			if (parts.length == 3) {
//				server = parts[1] + ":" + parts[2];
//			} else {
				throw new AccountCannotResolveException("Cannot resolve account " + accountId + ": server name part of the" +
						" account identifier is missing. Example: eab4d140-1955-11e3-8ffd-0800200c9a66@example.org:8080");
//			}
		}
		
		try {
			return new URL("https://" + server + "/dime-communications");
		} catch (MalformedURLException e) {
			throw new AccountCannotResolveException("Cannot resolve account " + accountId + ": " + e.getMessage(), e);
		}
	}

}
