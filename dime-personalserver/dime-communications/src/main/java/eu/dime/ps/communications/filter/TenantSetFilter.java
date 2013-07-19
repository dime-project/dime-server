package eu.dime.ps.communications.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Intercepts all requests to the API in order to find out the tenant to
 * be used for the API call, depending on the account identifier (said)
 * passed in the request URL.
 * 
 * The tenant identifier is stored in the {@TenantContextHolder}.
 * 
 * @author Ismael Rivera
 */
public class TenantSetFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(TenantSetFilter.class);
	
    @Autowired
    private TenantManager tenantManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String said = null;
		Tenant tenant = null;
		String url = ((HttpServletRequest) request).getRequestURL().toString();

		int start = url.indexOf("api/dime/rest/");
		if (start < 0) {
			logger.error("Couldn't find 'said' in the request URL "+url+": the account identifier should be supplied as 'api/dime/rest/<said>'");
		} else {
			start += 14; // adds length of 'api/dime/rest/'
			try {
				said = url.substring(start, url.indexOf("/", start));
				tenant = tenantManager.getByAccountName(said);
				
				logger.debug("Request intercepted at {} - Setting request data: [tenant={}]", new Object[]{url, tenant.getId()});
	
				// setting tenant identifier
				TenantContextHolder.setTenant(tenant.getId());
				chain.doFilter(request, response);
			} catch (IndexOutOfBoundsException e) {
				logger.error("Couldn't find 'said' in the request URL "+url+": "+e.getMessage(), e);
			}
		}
		
		start = url.indexOf("push/");
		if (start < 0) {
			logger.error("Couldn't find 'said' in the request URL "+url+": the account identifier should be supplied as 'api/dime/rest/<said>'");
		} else {
			start += 5; // adds length of 'push/'
			try {
				said = url.substring(start, url.indexOf("/", start));
				tenant = tenantManager.getByAccountName(said);
				
				logger.info("Request intercepted at {} - Setting request data: [tenant={}]", new Object[]{url, tenant.getId()});
	
				// setting tenant identifier
				TenantContextHolder.setTenant(tenant.getId());
				chain.doFilter(request, response);
			} catch (IndexOutOfBoundsException e) {
				logger.error("Couldn't find 'said' in the request URL "+url+": "+e.getMessage(), e);
			}
		}
	}

	@Override
	public void destroy() {
		// no-op
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// no-op
	}

}
