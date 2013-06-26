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
import eu.dime.ps.gateway.service.noauth.LocationServiceAdapter;

/**
 * 
 * @author Cristina Fra'
 */
@Controller()
@RequestMapping("/services/{said}/location")
public class LocationAuthenticationController {
	
	private final Logger logger = LoggerFactory.getLogger(LocationAuthenticationController.class);
	
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
				accountManager.add(new LocationServiceAdapter());
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