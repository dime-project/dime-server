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

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.noauth.ProximityServiceAdapter;

/**
 * 
 * @author Cristina Fra'
 */
@Controller()
@RequestMapping("/services/{said}/proximity")
public class ProximityAuthenticationController {
	
	private final Logger logger = LoggerFactory.getLogger(ProximityAuthenticationController.class);
	
	@Autowired
	private ServiceGateway serviceGateway;
	private TenantManager tenantManager;
	private AccountManager accountManager;

	@Autowired
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	@RequestMapping(value = "/connect", method = RequestMethod.GET)
    public ModelAndView requestConnection(HttpSession session,  @PathVariable("said") String said) {

			// AccountManager reads the tenant from the TenantContextHolder
			TenantContextHolder.setTenant(tenantManager.getByAccountName(said).getId());
			// creating account for this service adapter
			try {
				accountManager.add(new ProximityServiceAdapter());
			} catch (ServiceAdapterNotSupportedException e) {
				logger.error(e.getMessage(),e);
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(),e);
			} catch (InfosphereException e) {
				logger.error(e.getMessage(),e);
			}
			
			TenantContextHolder.unset();

			return new ModelAndView("close_window");

    }

}
