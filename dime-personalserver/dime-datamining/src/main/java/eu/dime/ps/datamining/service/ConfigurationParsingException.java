package eu.dime.ps.datamining.service;

public class ConfigurationParsingException extends Exception {

	private static final long serialVersionUID = 1L;
    
	public ConfigurationParsingException() {
    	super();
    }

    public ConfigurationParsingException(final String message) {
    	super(message);
    }

    public ConfigurationParsingException(final Throwable cause) {
    	super(cause);
    }

    public ConfigurationParsingException(final String message, final Throwable cause) {
    	super(message, cause);
    }

}
