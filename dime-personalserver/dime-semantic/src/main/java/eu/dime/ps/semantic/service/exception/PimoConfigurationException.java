package eu.dime.ps.semantic.service.exception;

/**
 * Thrown when the PIMO Service cannot be configured.
 * 
 * @author Ismael Rivera
 */
public class PimoConfigurationException extends Exception {

	private static final long serialVersionUID = 2293776964332753598L;

	public PimoConfigurationException() {
    }

    public PimoConfigurationException(String message) {
        super(message);
    }

    public PimoConfigurationException(Throwable cause) {
        super(cause);
    }

    public PimoConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
