package eu.dime.ps.semantic.service.exception;

public class PimoException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public PimoException() {
    	super();
    }

    public PimoException(final String message) {
    	super(message);
    }

    public PimoException(final Throwable cause) {
    	super(cause);
    }

    public PimoException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
