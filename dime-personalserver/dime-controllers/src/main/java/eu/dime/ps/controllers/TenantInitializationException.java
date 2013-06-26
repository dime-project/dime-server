package eu.dime.ps.controllers;

public class TenantInitializationException extends RuntimeException {

	public TenantInitializationException() {
		super();
	}

	public TenantInitializationException(final String message) {
		super(message);
	}

	public TenantInitializationException(final Throwable cause) {
		super(cause);
	}

	public TenantInitializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
