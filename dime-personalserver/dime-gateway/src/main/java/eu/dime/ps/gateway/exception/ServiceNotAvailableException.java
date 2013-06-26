package eu.dime.ps.gateway.exception;

/**
 * @author Sophie.Wrobel
 */
public class ServiceNotAvailableException extends ServiceException {

	private static final long serialVersionUID = 1L;

	public static final String CODE = "SERV-001";
	
	public ServiceNotAvailableException() {
		super("The service could not be reached.", CODE);
	}
	
	public ServiceNotAvailableException(Exception e) {
		super("The service could not be reached. Details: " + e.getMessage(), CODE);
	}

	public ServiceNotAvailableException(String exceptionMessage) {
		super(exceptionMessage, CODE);
	}
	
	public ServiceNotAvailableException(String exceptionMessage, String errorCode) {
		super(exceptionMessage, errorCode);
	}

	public ServiceNotAvailableException(String exceptionMessage, Throwable cause) {
		super(exceptionMessage, CODE, cause);
	}

	public ServiceNotAvailableException(int httpStatus) {
		super("The service returned an error response. HTTP Error code: " + httpStatus, CODE);
	}
	
}
