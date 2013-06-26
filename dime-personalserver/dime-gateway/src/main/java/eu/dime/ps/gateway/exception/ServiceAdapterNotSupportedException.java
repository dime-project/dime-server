/**
 * 
 */
package eu.dime.ps.gateway.exception;

/**
 * @author Sophie.Wrobel
 *
 */
public class ServiceAdapterNotSupportedException extends ServiceException {

	public ServiceAdapterNotSupportedException() {
		super("The service adapter is not supported.", "SERV-004");
	}

	public ServiceAdapterNotSupportedException(String message) {
		super("The service adapter is not supported. Details: " + message, "SERV-004");
	}
	
	public ServiceAdapterNotSupportedException(Exception e) {
		super("The service adapter is not supported. Details: " + e.getMessage(), "SERV-004");
	}
}
