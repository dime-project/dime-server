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
import java.security.AccessControlException;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.NoResultException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import eu.dime.ps.communications.utils.Base64encoding;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

/**
 * Intercepts all requests to the API in order to check authentication for requested resources
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 */
public class AccessFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AccessFilter.class);
    
    private final static String API_PREFIX = "api/dime/rest/";
    private final static int API_PREFIX_LENGTH = API_PREFIX.length();
    private final static String PUSH_PREFIX = "push/";
    private final static int PUSH_PREFIX_LENGTH = PUSH_PREFIX.length();

    @Autowired
    private TenantManager tenantManager;
    
    @Autowired
    private UserManager userManager;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String said = ""; 
		Tenant tenant = null;
		
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getRequestURL().toString();
		logger.debug("AccessFilter processing "+req.getMethod()+" "+url+" request.");
		
		int apiPrefixIdx = url.indexOf(API_PREFIX);
		int pushPrefixIdx = url.indexOf(PUSH_PREFIX);
		
		if ((apiPrefixIdx < 0 && pushPrefixIdx < 0)
				|| (url.length() <= (apiPrefixIdx+API_PREFIX_LENGTH+1) && url.length() <= (apiPrefixIdx+PUSH_PREFIX_LENGTH+1))) {
			logger.error("Unable to handle url: "+ url);
			return;
		}
		
		if (apiPrefixIdx > 0) {
			apiPrefixIdx += API_PREFIX_LENGTH; // adds length of API_PREFIX
			said = url.substring(apiPrefixIdx, url.indexOf("/", apiPrefixIdx));
		} else if (pushPrefixIdx > 0) {
			pushPrefixIdx += PUSH_PREFIX_LENGTH; // adds length of PUSH_PREFIX
			said = url.substring(pushPrefixIdx, url.indexOf("/", pushPrefixIdx));
		} else {
			logger.error("Unable to handle url: "+ url);
            return;
		}
		
		try {
			tenant = tenantManager.getByAccountName(said);
			String auth = req.getHeader("Authorization");
			
			if (auth == null){
				// not authenticated
				HttpSession session = req.getSession();
				SecurityContext secContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
				if (secContext != null){
					String username = secContext.getAuthentication().getName();
				    
					Collection<GrantedAuthority> authorities = secContext.getAuthentication().getAuthorities();
				    boolean pass = false;
				    for (Iterator<GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext();) {
						GrantedAuthority grantedAuthority = (GrantedAuthority) iterator.next();
						if (Role.OWNER.toString().equals(grantedAuthority.getAuthority())
								|| Role.ADMIN.toString().equals(grantedAuthority.getAuthority())) {
							pass = true;
							logger.debug(username+"  ---> authenticated as "+grantedAuthority.getAuthority());
						}
					}
				    
				    User user = User.findByTenantAndByUsername(tenant, username);
					if (pass && user != null) {
						chain.doFilter(request, response);
						logger.debug(req.getMethod()+" "+url+" granted for user "+username);
						return;
					} else {
						throw new AccessControlException(req.getMethod()+" "+url+" request not authorized for user "+username);
					}
				} else {
					throw new AccessControlException(req.getMethod()+" "+url+" request not authorized. No authorization data provided.");
				}
			} else { // basic auth
				String decodedAuth = Base64encoding.decode(auth.substring(5).trim());
				String[] credentials = decodedAuth.split(":");
				String username = credentials[0];
				String password = credentials[1];
			
				if (url.endsWith(API_PREFIX+said+"/user/credentials/"+username)) {
					// get credentials request -> no password check
					User user = User.findByTenantAndByUsername(tenant, username);
					if (user.getTenant().getId().equals(tenant.getId())){
						chain.doFilter(request, response);
					} else {
						throw new AccessControlException(req.getMethod()+" "+url+" request not authorized for user "+username);
					}
				} else {
					// check if guests belong to tenant
					User user = userManager.getByUsernameAndPassword(username, password);
					if (user == null) {
						// maybe username doesn't exist, or password is incorrect
						throw new AccessControlException(req.getMethod()+" "+url+" request not authorized for user "+username+": username or password incorrect.");
					} else if (Role.GUEST.equals(user.getRole()) && user.getTenant().getId().equals(tenant.getId())) {
						// authenticated as guest of tenant, so continue
						if (url.contains("/api/dime/rest/"+said+"/shared")){ //let only requests to new GUEST API pass
							logger.debug("Access granted to: "+ url+" for user: "+username+"  --- authenticated as GUEST of tenant "+user.getTenant().getName());
							chain.doFilter(request, response);
						}
					} else if (Role.OWNER.equals(user.getRole())  && user.getTenant().getId().equals(tenant.getId())){
						// authenticated as owner and requesting own ressources, so continue
						logger.debug("Access granted to: "+ url+" for user: "+username+"  --- authenticated as OWNER");
						chain.doFilter(request, response);
					} else if (Role.ADMIN.equals(user.getRole())){ // ADMIN may access all tenants paths
						logger.debug("Access granted to: "+ url+" for user: "+username+"  --- authenticated as ADMIN");
						chain.doFilter(request, response);
					} else {
						// not owner and not guest and not admin of said
						throw new AccessControlException(req.getMethod()+" "+url+" request not authorized for user "+username);
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			logger.error("Couldn't find 'said' in the request URL "+url);
		} catch (NoResultException e) {
			throw new AccessControlException(req.getMethod()+" "+url+" request not authorized: "+e.getMessage());
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
