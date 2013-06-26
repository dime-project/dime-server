package eu.dime.ps.controllers.trustengine;

import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;

public enum PrivacyLevel {
	ZERO (AdvisoryConstants.PV_ZERO),
	PUBLIC(AdvisoryConstants.PV_PUBLIC), 
	NEUTRAL(AdvisoryConstants.PV_NEUTRAL), 
	CONFIDENTIAL(AdvisoryConstants.PV_CONFIDENTIAL), 
	PRIVATE(AdvisoryConstants.PV_PRIVATE),
	SECRET(AdvisoryConstants.PV_SECRET); 

	private final double value;
	
	PrivacyLevel(double value){
		this.value = value;
	}

	public double getValue() {
		return value;
	}
	
	public static PrivacyLevel getLevelForValue(double value){
		PrivacyLevel result = null;
		if (value > SECRET.value){
			result = SECRET;
		}
		if (value == SECRET.value){
			result = SECRET;
		}
		if (value < SECRET.value && value >= PRIVATE.value){
			result = PRIVATE;
		}
		if (value < PRIVATE.value && value >= CONFIDENTIAL.value){
			result = CONFIDENTIAL;
		}
		if (value < CONFIDENTIAL.value && value >= NEUTRAL.value){
			result = NEUTRAL;
		}
		if (value < NEUTRAL.value && value >= PUBLIC.value){
			result = PrivacyLevel.PUBLIC;
		}
		if (value < PUBLIC.value){
			result = ZERO;
		}
		return result;
	}
	public PrivacyLevel getNextLowerLevel(){
		switch (this) {
		case SECRET:
			return PRIVATE;
		case PRIVATE:
			return CONFIDENTIAL;		
		case CONFIDENTIAL:
			return NEUTRAL;
		case NEUTRAL:
			return PUBLIC;
		case PUBLIC:
			return ZERO;
		default:
			return ZERO;
		}
	}
	
}