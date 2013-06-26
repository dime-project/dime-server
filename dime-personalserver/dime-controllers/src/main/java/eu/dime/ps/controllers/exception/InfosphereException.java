package eu.dime.ps.controllers.exception;

/**
 * @author mplanaguma (BDigital)
 *
 */
public class InfosphereException extends Exception
{
    public InfosphereException()
    {
	super();
    }

    public InfosphereException(final String message)
    {
	super(message);
    }

    public InfosphereException(final String message, final Throwable cause)
    {
	super(message, cause);
    }
}