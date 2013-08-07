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
	
	private static final String[] ALLOWED_PATHS = new String[]{"api/dime/rest/", "push/"};
	
    @Autowired
    private TenantManager tenantManager;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		String said = null;
		Tenant tenant = null;
		String url = ((HttpServletRequest) request).getRequestURL().toString();

		for (String path : ALLOWED_PATHS) {
			if (url.contains(path)) {
				int start = url.indexOf(path);
				int end = -1;
				
				if (start < 0) {
					logger.error("Couldn't find 'said' in the request URL "+url+": the account identifier should be supplied as '"+path+"<said>'");
				} else {
					start += path.length(); // adds length of path
					end = url.indexOf("/", start);
					if (end > start) {
						said = url.substring(start, end);
						tenant = tenantManager.getByAccountName(said);
						
						logger.debug("Request intercepted at {} - Setting request data: [tenant={}]", new Object[]{url, tenant.getId()});
		
						// setting tenant identifier
						TenantContextHolder.setTenant(tenant.getId());
						chain.doFilter(request, response);
					} else {
						logger.error("Couldn't find 'said' in the request URL "+url+" following '"+path+"<said>' pattern.");
					}
				}
				
				return;
			}
		}
		
		logger.error("TenantSetFilter is called for URL "+url+", but it does not matches any of the allowed patterns: "+ALLOWED_PATHS+ 
				". Please check that TenantSetFilter is called only for the expected URLs.");	
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
