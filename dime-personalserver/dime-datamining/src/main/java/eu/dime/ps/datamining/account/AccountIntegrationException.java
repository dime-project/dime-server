package eu.dime.ps.datamining.account;

public class AccountIntegrationException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public AccountIntegrationException() {
    	super();
    }

    public AccountIntegrationException(final String message) {
    	super(message);
    }

    public AccountIntegrationException(final Throwable cause) {
    	super(cause);
    }

    public AccountIntegrationException(final String message, final Throwable cause) {
    	super(message, cause);
    }
    
}
