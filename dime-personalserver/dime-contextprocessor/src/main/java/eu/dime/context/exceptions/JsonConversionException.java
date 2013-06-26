package eu.dime.context.exceptions;

public class JsonConversionException extends Exception {
	public JsonConversionException()
	{
		super();
	}

	public JsonConversionException(final String message)
	{
		super(message);
	}

	public JsonConversionException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
