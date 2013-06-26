package eu.dime.ps.semantic.exception;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public NotFoundException() {
    	super();
    }

    public NotFoundException(final String message) {
    	super(message);
    }

    public NotFoundException(final Throwable cause) {
    	super(cause);
    }

    public NotFoundException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
