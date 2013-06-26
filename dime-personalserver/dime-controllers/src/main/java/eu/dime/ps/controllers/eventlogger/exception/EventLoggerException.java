package eu.dime.ps.controllers.eventlogger.exception;

/**
 * @author mplanaguma (BDigital)
 *
 */
public class EventLoggerException extends Exception
{
    public EventLoggerException()
    {
	super();
    }

    public EventLoggerException(final String message)
    {
	super(message);
    }

    public EventLoggerException(final String message, final Throwable cause)
    {
	super(message, cause);
    }
}