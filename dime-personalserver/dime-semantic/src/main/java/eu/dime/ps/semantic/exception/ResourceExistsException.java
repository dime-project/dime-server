package eu.dime.ps.semantic.exception;

public class ResourceExistsException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public ResourceExistsException() {
    	super();
    }

    public ResourceExistsException(final String message) {
    	super(message);
    }

    public ResourceExistsException(final Throwable cause) {
    	super(cause);
    }

    public ResourceExistsException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
