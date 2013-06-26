package eu.dime.ps.storage;

public class StorageException extends Exception {
	public StorageException()
	{
		super();
	}

	public StorageException(final String message)
	{
		super(message);
	}

	public StorageException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
