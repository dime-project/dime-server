/**
 * 
 */
package eu.dime.ps.gateway.proxy;

import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;

/**
 * Acts as a proxy between the di.me ServiceAdapter and the external service API.
 * 
 * @author Sophie.Wrobel
 *
 */
public interface ServiceProxy {

	/**
	 * Authenticates with service using given credentials
	 * 
	 * @param authToken
	 * @return true on success, false on failure
	 * @throws ServiceNotAvailableException
	 */
	public boolean authenticate(AbstractAuthenticationToken authToken) throws ServiceNotAvailableException;
	
	/**
	 * Sends a GET request to the service
	 * 
	 * @param query - URI with query parameters (see API)
	 * @return String with body of returned result
	 * @throws ServiceNotAvailableException
	 */
	public String get(String query) throws ServiceNotAvailableException, ServiceException;
	
	/**
	 * Same as {@link #get(String)}, except it also accepts the HTTP headers.
	 * 
	 * @param query - URI with query parameters (see API)
	 * @param headers - HTTP headers for the request
	 * @return String with body of returned result
	 * @throws ServiceNotAvailableException
	 */
	public String get(String query, Map<String, String> headers) throws ServiceNotAvailableException, ServiceException;
	
	/**
	 * @param query - URI with query parameters (see API)
	 * @param post - Serialized object to post
	 * @return the resulting httpStatus code for transaction
	 * @throws ServiceNotAvailableException
	 */
	public int post(String query, String post) throws ServiceNotAvailableException;
	
	/**
	 * @param query - URI with query parameters (see API)
	 * @return the resulting httpStatus code for transaction
	 * @throws ServiceNotAvailableException
	 */
	public int delete(String query) throws ServiceNotAvailableException;
	
	/**
	 * Closes the current connection (if one exists).
	 */
	public void close();
	
}
