package eu.dime.ps.controllers.trustengine.exception;

public class TrustThresholdException extends TrustException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -807857374660010648L;

	
	public TrustThresholdException (){
		super("Trust not high enough for privacy level!");
	}
	
}
