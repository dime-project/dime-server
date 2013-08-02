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

package eu.dime.ps.communications.web.services.authentication;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.communications.web.services.authentication.OAuthAuthenticationController.ServiceAdapterHolder;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;
import eu.dime.ps.storage.entities.Tenant;

/**
 * 
 * @author Sophie Wrobel
 * @author Marc Planaguma
 * @author Ismael Rivera
 */
@Controller
@RequestMapping("/services/{said}/facebook")
public class FacebookAuthenticationController extends OAuthAuthenticationController<FacebookServiceAdapter> {

	@RequestMapping(value = "/connect", method = RequestMethod.GET)
	public ModelAndView requestConnection(HttpSession session, @PathVariable("said") String said) {
		String error = UNKNOWN_ERROR;
		ModelAndView modelAndView = null;

		Tenant tenant = this.tenantManager.getByAccountName(said);
		
		if (tenant == null) {
			error = UNKNOWN_TENANT_ERROR;
		} else {
			try {
				FacebookServiceAdapter serviceAdapter = new FacebookServiceAdapter(TenantHelper.getCurrentTenant());
				try {

				Long tenantId = TenantHelper.getCurrentTenantId();
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
				// Note: We need to put the adapter ID in path variable as Facebook needs the callback URL to end with a trailing slash
				callbackURL.append("/"+adapterId+"/");
				serviceAdapter.setCallbackURL(callbackURL.toString());

				// Call oAuth Service
				OAuthService oAuthService = serviceAdapter.getOAuthService();
				pending.putIfAbsent(adapterId, new ServiceAdapterHolder(tenantId, serviceAdapter, oAuthService, null));
				// This never gets used! 
				session.setAttribute("scope", "user_about_me,friends_about_me,email");
				
				return new ModelAndView("redirect:" + oAuthService.getAuthorizationUrl(null));
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

			} catch (ServiceNotAvailableException e) {
			    error = "Could not reach service. Details: " + e.getStackTrace().toString();
				logger.error("Could not instantiate service: " + error, e);
			}

			// Dummy view to dump text to screen
			modelAndView = new ModelAndView("ajax_result");
			modelAndView.addObject("result", error);
		}
		return modelAndView;
	}


	@RequestMapping(value = "/{id}/", method = RequestMethod.GET)
	public ModelAndView authorizeCallback(HttpSession session,
			@PathVariable("said") String said,
			@PathVariable("id") String adapterId,
			@RequestParam("code") String verifierString) {

		String error = UNKNOWN_ERROR;

		try {
			if (!pending.containsKey(adapterId)) {
				error = PENDING_ADAPTER_NOT_FOUND.replace("%s", adapterId);
				logger.error(error);
			} else {
				// retrieve service adapter from pending list
				ServiceAdapterHolder holder = pending.get(adapterId);
				
				OAuthService oAuthService = holder.getService();
				Verifier verifier = new Verifier(verifierString);
				Token accessToken = oAuthService.getAccessToken(null, verifier);

				FacebookServiceAdapter serviceAdapter = (FacebookServiceAdapter) holder.getAdapter();
				serviceAdapter.setAccessToken(accessToken);

				// AccountManager reads the tenant from the TenantContextHolder
				TenantContextHolder.setTenant(tenantManager.getByAccountName(said).getId());

				// creating account for this service adapter
				accountManager.add(serviceAdapter);

				// remove from pending list once authentication flow is completed
				this.pending.remove(adapterId, serviceAdapter);

				return new ModelAndView("close_window");
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
		//return super.authorizeCallback(session, said, adapterId, null, verifierString);
	}
}
