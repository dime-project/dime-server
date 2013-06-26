package eu.dime.ps.semantic.exception;

public class QueryException extends RuntimeException {

	private static final long serialVersionUID = 1L;
    
	public QueryException() {
    	super();
    }

    public QueryException(final String message) {
    	super(message);
    }

    public QueryException(final Throwable cause) {
    	super(cause);
    }

    public QueryException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
