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

package eu.dime.ps.communications.web.services.authentication;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.external.oauth.OAuthServiceAdapter;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Allows to get the authentication tokens from the OAuth API.
 * 
 * @author Marc Planaguma
 * @author Ismael Rivera
 */
public abstract class OAuthAuthenticationController<T extends OAuthServiceAdapter> {

	protected static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationController.class);

	public static final String UNKNOWN_ERROR = "An unknown error occurred.";
	public static final String UNKNOWN_TENANT_ERROR = "di.me could not determine your tenant.";
	public static final String PENDING_ADAPTER_NOT_FOUND = "Sorry, no adapter was found awaiting for authentication for identifier %s";

	// when the user initialize the service account integration, an adapter is
	// saved here until the authentication is completed
	protected ConcurrentMap<String, ServiceAdapterHolder> pending = new ConcurrentHashMap<String, ServiceAdapterHolder>();

	protected TenantManager tenantManager;
	protected AccountManager accountManager;
	protected PolicyManager policyManager;
	protected CredentialStore credentialStore;
	
	@Autowired
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Autowired
	public void setPolicyManager(PolicyManager policyManager) {
		this.policyManager = policyManager;
	}

	@Autowired
	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}
	
	public ModelAndView requestConnection(HttpSession session, String said, Class<T> adapterClass) {
			
		String error = UNKNOWN_ERROR;
		ModelAndView modelAndView = null;

		Tenant tenant = this.tenantManager.getByAccountName(said);
		
		if (tenant == null) {
			error = UNKNOWN_TENANT_ERROR;
		} else {
			try {
				T serviceAdapter = adapterClass.getConstructor(new Class[] {Tenant.class}).newInstance(tenant);
				try {
					String adapterId = UUID.randomUUID().toString();
	
					// Create callback URL
					ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
					requestAttributes.setAttribute("adapter_id", adapterId, ServletRequestAttributes.SCOPE_REQUEST);
					StringBuilder callbackURL = new StringBuilder("https://");
					callbackURL.append(this.policyManager.getPolicyString("AUTHSERVLET_HOST", null));
					callbackURL.append(":");
					callbackURL.append(this.policyManager.getPolicyString("AUTHSERVLET_PORT_SECURE", null));
					callbackURL.append("/");
					callbackURL.append(this.policyManager.getPolicyString("AUTHSERVLET_PATH", null));
					callbackURL.append("/services/");
					callbackURL.append(said);
					callbackURL.append(this.policyManager.getPolicyString("callbackURL", serviceAdapter.getAdapterName()));
					callbackURL.append(adapterId);
					serviceAdapter.setCallbackURL(callbackURL.toString());
	
					// Call oAuth Service
					OAuthService oAuthService = serviceAdapter.getOAuthService();
					Token requestToken = oAuthService.getRequestToken();
					pending.putIfAbsent(adapterId, new ServiceAdapterHolder(tenant.getId(), serviceAdapter, oAuthService, requestToken));
					requestAttributes.setAttribute("request_token", requestToken, ServletRequestAttributes.SCOPE_REQUEST);
					// This never gets used! 
					// session.setAttribute("scope", "user_about_me,friends_about_me,email");
					
					return new ModelAndView("redirect:" + oAuthService.getAuthorizationUrl(requestToken));
				} catch (IllegalArgumentException e) {
				    error = "Could not connect to "+serviceAdapter.getAdapterName() + ". Details: " + e.getMessage();
					logger.error("Could not instantiate service" + e.getMessage(), e);
				} catch (SecurityException e) {
				    error = "Could not connect to "+serviceAdapter.getAdapterName() + ". Details: " + e.getMessage();
					logger.error("Could not instantiate service" + e.getMessage(), e);
				} catch (OAuthException e) {
				    error = "Could not connect to "+serviceAdapter.getAdapterName() + ". Details: " + e.getMessage();
					logger.error(error, e);
				}

			} catch (InstantiationException e) {
			    error = "Could not reach service. Details: " + e.getMessage();
				logger.error("Could not instantiate service" + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				error = "Could not reach service. Details: " + e.getMessage();
				logger.error("Could not instantiate service" + e.getMessage(), e);
			} catch (NoSuchMethodException e) {
			    error = "Could not reach service. Details: " + e.getMessage();
				logger.error("Could not instantiate service" + e.getMessage(), e);
			} catch (InvocationTargetException e) {
				// this wraps check exceptions thrown by the constructor
				error = "Could not reach service. Details: " + e.getMessage();
				Throwable target = e.getTargetException();
				// add exception message if exception was thrown and wrapped from the constructor
				if (target != null) {
				    error += ": " + target.getMessage();
				}
				logger.error(error, e);
			}

			// Dummy view to dump text to screen
			modelAndView = new ModelAndView("ajax_result");
			modelAndView.addObject("result", error);
		}
		
		return modelAndView;
	}

	public ModelAndView authorizeCallback(HttpSession session, String said, String adapterId, String oauthToken,
			String verifierString) {

		String error = UNKNOWN_ERROR;

		try {
			if (!pending.containsKey(adapterId)) {
				error = PENDING_ADAPTER_NOT_FOUND.replace("%s", adapterId);
				logger.error(error);
			} else {
				// retrieve service adapter from pending list
				ServiceAdapterHolder holder = pending.get(adapterId);
				
				OAuthService oAuthService = holder.getService();
				Token requestToken = holder.getRequestToken();
				Verifier verifier = new Verifier(verifierString);
				Token accessToken = oAuthService.getAccessToken(requestToken, verifier);

				T serviceAdapter = holder.getAdapter();
				serviceAdapter.setAccessToken(accessToken);

				Tenant tenant = tenantManager.getByAccountName(said);
				if (tenant == null) {
					error = UNKNOWN_TENANT_ERROR;
				} else {
					// AccountManager reads the tenant from the TenantContextHolder
					TenantContextHolder.setTenant(tenant.getId());

					// creating account for this service adapter
					accountManager.add(serviceAdapter);
	
					// remove from pending list once authentication flow is completed
					this.pending.remove(adapterId, serviceAdapter);
	
					return new ModelAndView("close_window");
				}
			}
		} catch (OAuthException e) {
			error = "Authentication error. OAuthException: " + e.getMessage();
			logger.error(error, e);
		} catch (InfosphereException e) {
			error = "Could not create account: " + e.getMessage();
			logger.error(error, e);
		} catch (ServiceAdapterNotSupportedException e) {
			error = "Could not create account: " + e.getMessage();
			logger.error(error, e);
		} catch (IllegalArgumentException e) {
			error = "Illegal Argument Exception: " + e.getMessage();
			logger.error(error, e);
		}

		// Dummy view to dump text to screen
		ModelAndView modelAndView = new ModelAndView("ajax_result");
		modelAndView.addObject("result", error);

		return modelAndView;
	}
	
	// used to store tenant + adapter for a service adapter pending of being authorized 
	protected class ServiceAdapterHolder {
		
		private Long tenantId;
		private T adapter;
		private OAuthService service;
		private Token token;
		
		public ServiceAdapterHolder(Long tenantId, T adapter, OAuthService oAuthService, Token requestToken) {
			this.tenantId = tenantId;
			this.adapter = adapter;
			this.service = oAuthService;
			this.token = requestToken;
		}
		
		public Long getTenantId() {
			return tenantId;
		}
		
		public T getAdapter() {
			return adapter;
		}
		
		public OAuthService getService() {
			return service;
		}
		
		public Token getRequestToken() {
			return token;
		}
		
	}
	
}
