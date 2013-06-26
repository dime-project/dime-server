package eu.dime.ps.gateway.exception;

public class InvalidDataException extends ServiceException {

	private static final long serialVersionUID = 1L;
    
    public InvalidDataException(final String message) {
    	super(message, "SERV-005");
    }

    public InvalidDataException(final String message, final Throwable cause) {
    	super(message, "SERV-005", cause);
    }

}
