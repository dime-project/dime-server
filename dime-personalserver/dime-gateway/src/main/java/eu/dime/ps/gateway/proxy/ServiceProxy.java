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
