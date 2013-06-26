package eu.dime.ps.gateway.service.internal;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * @author Sophie.Wrobel
 *
 */
public interface InternalServiceAdapter extends ServiceAdapter {

	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidDataException;

}
