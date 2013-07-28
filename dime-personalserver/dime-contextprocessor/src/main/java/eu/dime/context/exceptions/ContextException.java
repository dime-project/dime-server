package eu.dime.context.exceptions;

/**
 */
public class ContextException extends Exception
{
	public ContextException()
	{
		super();
	}

	public ContextException(final String message)
	{
		super(message);
	}

	public ContextException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
