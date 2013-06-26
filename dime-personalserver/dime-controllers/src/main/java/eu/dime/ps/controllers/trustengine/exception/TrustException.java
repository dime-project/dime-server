package eu.dime.ps.controllers.trustengine.exception;

/** 
 * @author <a href="mailto:heupel@wiwi.uni-siegen.de">
 * 	Marcel Heupel (mheupel)</a>
 *
 */
public class TrustException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3425625188008874740L;

	public TrustException(){
		super();
	}
	
	public TrustException(String message) {
		super(message);
	}
	

}
