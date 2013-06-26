package eu.dime.ps.controllers.trustengine.exception;

/** 
 * @author <a href="mailto:heupel@wiwi.uni-siegen.de">
 * 	Marcel Heupel (mheupel)</a>
 *
 */
public class PrivacyValueNotValidException extends TrustException {

	public PrivacyValueNotValidException(double privacyValue) {
		super("Privacy value has to be between 0-1. It is now: "+privacyValue);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4540577722963394207L;

}
