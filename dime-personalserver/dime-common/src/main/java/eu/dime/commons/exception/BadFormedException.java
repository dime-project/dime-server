package eu.dime.commons.exception;

public class BadFormedException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public BadFormedException() {
    	super();
    }

    public BadFormedException(final String message) {
    	super(message);
    }

    public BadFormedException(final Throwable cause) {
    	super(cause);
    }

    public BadFormedException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
