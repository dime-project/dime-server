package eu.dime.ps.semantic.exception;

public class TripleStoreException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public TripleStoreException() {
    	super();
    }

    public TripleStoreException(final String message) {
    	super(message);
    }

    public TripleStoreException(final Throwable cause) {
    	super(cause);
    }

    public TripleStoreException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
