package eu.dime.ps.gateway.service.external;

import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapterBase;

/**
 * Service adapter to interact with HTTP services using
 * Basic HTTP Authentication.
 * 
 * @author Ismael Rivera
 */
public abstract class BasicAuthServiceAdapter extends ServiceAdapterBase {

	public BasicAuthServiceAdapter(String identifier) throws ServiceNotAvailableException {
		super(identifier);
	}

	public BasicAuthServiceAdapter(String identifier, String username, String password)
			throws ServiceNotAvailableException {
		super(identifier);
		this.username = username;
		this.password = password;
	}

	protected String realm;
	protected String username;
	protected String password;
	
	public String getRealm() {
		return realm;
	}
	
	public void setRealm(String realm) {
		this.realm = realm;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
