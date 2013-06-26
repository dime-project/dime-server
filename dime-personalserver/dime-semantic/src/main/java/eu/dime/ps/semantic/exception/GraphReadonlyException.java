package eu.dime.ps.semantic.exception;

public class GraphReadonlyException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public GraphReadonlyException() {
    	super();
    }

    public GraphReadonlyException(final String message) {
    	super(message);
    }

    public GraphReadonlyException(final Throwable cause) {
    	super(cause);
    }

    public GraphReadonlyException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
