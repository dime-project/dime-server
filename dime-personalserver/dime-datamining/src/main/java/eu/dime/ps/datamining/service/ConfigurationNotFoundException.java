package eu.dime.ps.datamining.service;

public class ConfigurationNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public ConfigurationNotFoundException() {
    	super();
    }

    public ConfigurationNotFoundException(final String message) {
    	super(message);
    }

    public ConfigurationNotFoundException(final Throwable cause) {
    	super(cause);
    }

    public ConfigurationNotFoundException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
