package eu.dime.ps.semantic.exception;

public class RepositoryStorageException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public RepositoryStorageException() {
    	super();
    }

    public RepositoryStorageException(final String message) {
    	super(message);
    }

    public RepositoryStorageException(final Throwable cause) {
    	super(cause);
    }

    public RepositoryStorageException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
