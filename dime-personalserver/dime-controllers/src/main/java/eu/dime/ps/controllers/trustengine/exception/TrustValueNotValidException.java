package eu.dime.ps.controllers.trustengine.exception;

public class TrustValueNotValidException extends TrustException {

	public TrustValueNotValidException(double trustValue) {
		super("Trust value has to be in [0..1]. It is now: " + trustValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -825281557178824751L;

}
