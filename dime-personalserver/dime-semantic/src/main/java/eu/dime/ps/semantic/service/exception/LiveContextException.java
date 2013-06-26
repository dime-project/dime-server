package eu.dime.ps.semantic.service.exception;

public class LiveContextException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public LiveContextException() {
    	super();
    }

    public LiveContextException(final String message) {
    	super(message);
    }

    public LiveContextException(final Throwable cause) {
    	super(cause);
    }

    public LiveContextException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
