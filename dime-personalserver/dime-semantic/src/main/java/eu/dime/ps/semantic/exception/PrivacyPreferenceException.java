package eu.dime.ps.semantic.exception;

public class PrivacyPreferenceException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public PrivacyPreferenceException() {
    	super();
    }

    public PrivacyPreferenceException(final String message) {
    	super(message);
    }

    public PrivacyPreferenceException(final Throwable cause) {
    	super(cause);
    }

    public PrivacyPreferenceException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
