package eu.dime.ps.gateway.proxy;

import java.net.URL;

import eu.dime.ps.gateway.exception.ServiceNotAvailableException;

/**
 * Factory to create proxy objects.
 * 
 * @author Ismael Rivera
 */
public class ProxyFactory {

	public HttpRestProxy createProxy(URL url) throws ServiceNotAvailableException {
		return new HttpRestProxy(url);
	}

	public HttpRestProxy createProxy(URL url, int serverPort, String realm, 
			String username, String password) throws ServiceNotAvailableException {
		return new HttpRestProxy(url, serverPort, realm, username, password);
	}

}
