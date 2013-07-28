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

package eu.dime.ps.gateway.service.external.oauth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.auth.impl.CredentialStoreImpl;
import eu.dime.ps.gateway.exception.RateLimitException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapterBase;

/**
 * @author Sophie Wrobel
 * @author Ismael Rivera
 */
public abstract class OAuthServiceAdapter extends ServiceAdapterBase {

	private static final Logger logger = LoggerFactory.getLogger(OAuthServiceAdapter.class);

	private final Class<? extends Api> provider;
	private OAuthService oAuthService;
	private String callbackURL;
	private Token accessToken;
	private Token consumerToken;
	private String scope;	
	protected CredentialStore credentialStore;

	/**
	 * Note: After initialization, you still need to set the callback URL (which will
	 *       in turn create the auth service to authenticate with the OAuthService).
	 * @param identifier
	 * @throws ServiceNotAvailableException
	 */
	public OAuthServiceAdapter(Class<? extends Api> provider) throws ServiceNotAvailableException {
		super();
		this.provider = provider;
		this.scope = this.policyManager.getPolicyString("SCOPE", this.getAdapterName());
		this.callbackURL = "oob"; // by default if no callback URL is specified

		this.credentialStore = CredentialStoreImpl.getInstance();
		this.consumerToken = new Token (this.credentialStore.getConsumerKey(this.getAdapterName()),
				this.credentialStore.getConsumerSecret(this.getAdapterName()));
		try {
			this.accessToken = new Token (this.credentialStore.getAccessToken(this.getIdentifier()),
					this.credentialStore.getAccessSecret(this.getIdentifier()));
		} catch (NoResultException e) {
			this.accessToken = null;
		}

	}
	
	/**
	 * @param input
	 * @return OAuth-encoded string
	 */
	protected String encode(String input) {
		URI uri;
		try {
			uri = new URI(input);
			return uri.toASCIIString();
		} catch (URISyntaxException e) {
			return input.replace(" ", "%20");
		}
	}
	
	/**
	 * Creates the Authorization Service to authenticate against.
	 * updated version, using the credentialStore
	 */
	private void createAuthService() {
		// Really, do you think anything would work without an
		// API Key / Secret? Well, just in case...
		if (this.consumerToken == null) {
			this.oAuthService = new ServiceBuilder()
				.provider(this.provider)
				.callback(callbackURL)
				.build();
		}
		// Use default scope if none is specified
		else if (this.scope == null) {
			this.oAuthService = new ServiceBuilder()
				.provider(this.provider)
				.apiKey(this.consumerToken.getToken())
				.apiSecret(this.consumerToken.getSecret())
				.callback(callbackURL)
				.build();
		// For cases where scope is specified
		} else {
			this.oAuthService = new ServiceBuilder()
				.provider(this.provider)
				.apiKey(this.consumerToken.getToken())
				.apiSecret(this.consumerToken.getSecret())
				.scope(this.scope)
		       	.callback(callbackURL)
		       	.build();
		}
	}
	
	public OAuthService getOAuthService() {
		if (this.oAuthService == null)
			createAuthService();
		return this.oAuthService;
	}
	
	public void setCallbackURL(String url) {
		this.callbackURL = url;
		createAuthService();
	}
	
	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.ServiceAdapter#isConnected()
	 */
	@Override
	public Boolean isConnected() {
 		if (this.consumerToken == null || this.callbackURL == null) {
 			return false;
 		} else {
			return true;
		}
	}

	/**
	 * Retrieves the token & secret to authenticate on behalf of the user.
	 * @param token
	 */
	public Token getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Specifies the token & secret to authenticate on behalf of the user.
	 * @param token
	 */
	public void setAccessToken(Token token) {
		this.accessToken = token;
	}

	/**
	 * Retrieves the token & secret to authenticate on behalf of the application.
	 * @param token
	 */
	public Token getConsumerToken() {
		return this.consumerToken;
	}

	/**
	 * Specifies the token & secret to authenticate on behalf of the application.
	 * @param token
	 */
	public void setConsumerToken(Token token) {
		this.consumerToken = token;
		createAuthService();
	}
	
	/**
	 * Performs a request to the service.
	 * 
	 * @param verb
	 * @param url
	 * @return
	 */
	protected Response sendRequest(Verb verb, String url) throws RateLimitException {
		return sendRequest(verb, url, new HashMap<String, String>());
	}
	
	/**
	 * Performs a request to the service with parameters in the body (e.g. POST request).
	 * 
	 * @param verb
	 * @param url
	 * @return
	 * @throws RateLimitException 
	 */
	protected Response sendRequest(Verb verb, String url, Map<String, String> params) throws RateLimitException {
		logger.debug(verb+" request to "+url);
		
		OAuthRequest request = new OAuthRequest(verb, url);
		for (String key : params.keySet()) {
			request.addBodyParameter(key, this.encode(params.get(key)));
		}
		
		this.oAuthService.signRequest(getAccessToken(), request);
		this.checkRateLimit();
		
		return request.send();
	}
	
}
