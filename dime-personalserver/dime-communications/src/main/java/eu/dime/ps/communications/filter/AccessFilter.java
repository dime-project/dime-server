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
import jcifs.dcerpc.rpc;

/**
 * Intercepts all requests to the API in order to check authentication for requested resources
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 */
public class AccessFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AccessFilter.class);
    private final static String API_PREFIX = "api/dime/rest/";
    private final static int API_PREFIX_LENGTH = API_PREFIX.length();

    @Autowired
    private TenantManager tenantManager;
    
    @Autowired
    private UserManager userManager;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String said = ""; 
		String username = "";
		String password = "";
		Tenant tenant = null;
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getRequestURL().toString();
		logger.info("request: "+url);
		
		int index = url.indexOf("api/dime/rest/");
		if ((index < 0) 
                   || (url.length() <= (index + API_PREFIX_LENGTH+1))){
                    logger.error("Unable to handle url: "+ url);
		} else {
			index += API_PREFIX_LENGTH; // adds length of 'api/dime/rest/'
			try {
				said = url.substring(index, url.indexOf("/", index));
				tenant = tenantManager.getByAccountName(said);
				String auth = req.getHeader("Authorization");
				if (auth == null){
					// not authenticated
					HttpSession session = req.getSession();
					SecurityContext secContext = 
							(SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
					if (secContext != null){
						username = secContext.getAuthentication().getName();
					    Collection<GrantedAuthority> authorities = secContext.getAuthentication().getAuthorities();
					    boolean pass = false;
					    for (Iterator<GrantedAuthority> iterator = authorities.iterator(); iterator.hasNext();) {
							GrantedAuthority grantedAuthority = (GrantedAuthority) iterator.next();
							if (grantedAuthority.getAuthority().equals(Role.OWNER.toString()) ||
									grantedAuthority.getAuthority().equals(Role.ADMIN.toString())){
								pass = true;
								logger.debug(username + "  ---> authenticated as "+grantedAuthority.getAuthority());
							}
						}
					    User user = User.findByTenantAndByUsername(tenant, username);
					    //boolean sameId = tenant.getId().equals(tenantManager.getByAccountName(username).getId());
						if (pass && (user != null)){
							chain.doFilter(request, response);
							logger.debug("Access granted to: "+ url + " for user: "+username + "  --- ");
							return;
						} else {
							logger.error("Access denied to: "+ url + " for user: "+username);
							throw new AccessControlException("Not authorized for requested ressources");
						}
					} else {
						logger.debug("Access denied to: "+ url + " for user: "+username);
						throw new AccessControlException("Not authorized for requested ressources");
					}
				} else { // basic auth
					auth = auth.substring(5);
					String decoded_auth = Base64encoding.decode(auth.trim());
		
					int d_index = decoded_auth.indexOf(":");
					username = decoded_auth.substring(0, d_index);
					password = decoded_auth.substring(d_index+1);
				
					if (url.endsWith("/api/dime/rest/"+said+"/user/credentials/"+username)){
						// get credentials request -> no password check
						User user = User.findByTenantAndByUsername(tenant, username);
						if (user.getTenant().getId().equals(tenant.getId())){
							chain.doFilter(request, response);
						} else {
							logger.error("Access denied to: "+ url + " for user: "+username);
							throw new AccessControlException("Not authorized for requested ressources");
						}
					} else { // check if guests belong to tenant
						User user = userManager.getByUsernameAndPassword(username, password);
						if ((user.getRole() == Role.GUEST) && user.getTenant().getId().equals(tenant.getId())){
							// authenticated as guest of tenant, so continue
							if (url.contains("/api/dime/rest/"+said+"/shared")){ //let only requests to new GUEST API pass
								logger.debug("Access granted to: "+ url + " for user: "+username + "  --- authenticated as GUEST of tenant "+user.getTenant().getName());
								chain.doFilter(request, response);
							}
						} else if (user.getRole() == Role.OWNER && user.getTenant().getId().equals(tenant.getId())){
							// authenticated as owner and requesting own ressources, so continue
							logger.debug("Access granted to: "+ url + " for user: "+username + "  --- authenticated as OWNER");
							chain.doFilter(request, response);
						} else if (user.getRole() == Role.ADMIN){ // ADMIN may access all tenants paths
							logger.debug("Access granted to: "+ url + " for user: "+username + "  --- authenticated as ADMIN");
							chain.doFilter(request, response);
						}
						else {
							// not owner and not guest and not admin of said
							logger.error("Access denied to: "+ url + " for user: "+username);
							throw new AccessControlException("Not authorized for requested ressources");
						}
					}
				}
				//chain.doFilter(request, response);
			} catch (IndexOutOfBoundsException e) {
				logger.error("Couldn't find 'said' in the request URL "+url);
			} catch (NoResultException e) {
				logger.error("User not found: "+username);
				throw new AccessControlException("Not authorized for requested ressources");
			}
		}//else		
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
