package eu.dime.commons.exception;

/**
 * Digital.me Exception
 * 
 * @author mplanaguma
 *
 */
public class DimeException extends Exception {

    public DimeException() {
        super();
    }

    public DimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DimeException(String message) {
        super(message);
    }

    public DimeException(Throwable cause) {
        super(cause);
    }

}
