package eu.dime.ps.semantic.exception;

public class RepositoryNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public RepositoryNotFoundException() {
    	super();
    }

    public RepositoryNotFoundException(final String message) {
    	super(message);
    }

    public RepositoryNotFoundException(final Throwable cause) {
    	super(cause);
    }

    public RepositoryNotFoundException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
