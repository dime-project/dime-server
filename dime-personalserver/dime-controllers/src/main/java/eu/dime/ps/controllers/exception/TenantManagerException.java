package eu.dime.ps.controllers.exception;

/**
 * @author mplanaguma (BDigital)
 *
 */
public class TenantManagerException extends RuntimeException
{
    public TenantManagerException()
    {
	super();
    }

    public TenantManagerException(final String message)
    {
	super(message);
    }

    public TenantManagerException(final String message, final Throwable cause)
    {
	super(message, cause);
    }
}