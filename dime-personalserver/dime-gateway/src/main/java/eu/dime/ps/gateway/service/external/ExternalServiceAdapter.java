package eu.dime.ps.gateway.service.external;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.ServiceResponse;

public interface ExternalServiceAdapter extends ServiceAdapter {

	public ServiceResponse[] getRaw(String fields)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException;
	
}
