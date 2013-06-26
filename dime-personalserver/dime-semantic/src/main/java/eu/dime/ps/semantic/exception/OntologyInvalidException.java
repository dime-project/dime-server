package eu.dime.ps.semantic.exception;

public class OntologyInvalidException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public OntologyInvalidException() {
    	super();
    }

    public OntologyInvalidException(final String message) {
    	super(message);
    }

    public OntologyInvalidException(final Throwable cause) {
    	super(cause);
    }

    public OntologyInvalidException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
