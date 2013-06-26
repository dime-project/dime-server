package eu.dime.ps.controllers.notifier.exception;

/**
 * @author mplanaguma (BDigital)
 *
 */
public class NotifierException extends Exception
{
    public NotifierException()
    {
	super();
    }

    public NotifierException(final String message)
    {
	super(message);
    }

    public NotifierException(final String message, final Throwable cause)
    {
	super(message, cause);
    }
}