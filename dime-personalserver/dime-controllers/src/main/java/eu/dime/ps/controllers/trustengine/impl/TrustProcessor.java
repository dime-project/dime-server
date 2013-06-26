package eu.dime.ps.controllers.trustengine.impl;

import eu.dime.ps.controllers.trustengine.PrivacyLevel;
import eu.dime.ps.controllers.trustengine.exception.PrivacyValueNotValidException;
import eu.dime.ps.controllers.trustengine.exception.TrustValueNotValidException;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;

/**
 * 
 * @author marcel
 *
 */
public class TrustProcessor {

	
	/**
	 * Calculates the direct trust value resulting from sharing a thing
	 * @param person
	 * @param sharedThing
	 * @return
	 */
	public static double calculateAdopted3AbasedDirectTrust(double weight, int shares){
		//shares++;
		if (shares <= 0){
			throw new IllegalArgumentException("Number of shares has to be larger than 0.");
		}
		return weight / (((1-weight)*(shares-1)) + 1);
	}
	
	/**
	 * Method for checking if trust value is high enough for privacy level
	 * @param trustValue
	 * @param privacy_level
	 * @return true if trust level is high enough for privacy level
	 * @throws PrivacyValueNotValidException 
	 * @throws TrustValueNotValidException 
	 */
	public static boolean isTrusted(double trustValue, double privacyValue) throws PrivacyValueNotValidException, TrustValueNotValidException {
		if (privacyValue > 1.0 || privacyValue < 0.0){
			throw new PrivacyValueNotValidException(privacyValue);
		} else if(trustValue > 1.0 || trustValue < 0.0){
			throw new TrustValueNotValidException(trustValue);
		}
		PrivacyLevel pl = PrivacyLevel.getLevelForValue(privacyValue);
		return (trustValue >= pl.getNextLowerLevel().getValue());
		
	}
	
	/**
	 * Method to determine the threshold, if privacy level needs to be adapted
	 * (e.g. when sharing very private things with many people, those things are probably not private)
	 * @param numberOfPersons Number of persons receiving the thing
	 * @param privacy_level Privacy level of the thing
	 * @return true if privacy level of thing (probably) too high, false if no adaption
	 */
	public static boolean getThreshold(int numberOfPersons, double privacy_level) {
        //logger.info("GET Treshold: numberOfPersons: "+numberOfPersons+ "pl: "+privacy_level);
		if (privacy_level == AdvisoryConstants.PV_MAX){
			privacy_level = AdvisoryConstants.PV_SECRET;
		}
		double trustValue = calculateAdopted3AbasedDirectTrust(privacy_level, numberOfPersons);
		//logger.info("Resulting trust value: "+ trustValue);
		//PrivacyLevel currentValue = PrivacyLevel.getLevelForValue(privacy_level);
		PrivacyLevel trustPL = PrivacyLevel.getLevelForValue(trustValue);
		if ((privacy_level - trustValue) >= 0.4){
			//logger.info("was true");
			return true;
		}
		//logger.info("was false");
		return false;
	}
}
