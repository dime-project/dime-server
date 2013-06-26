package eu.dime.commons.notifications.user;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * 
 * {
         //[..]   //general fields as specified in _general user notification body_
         "unType":"situation_recommendation",
         "unEntry":{              
         	"guid": "<situationId>"          
         	"nao:score": "<scoreValue>"          
         }
}
 * 
 * @author mplanaguma
 *
 */
public class UNSituationRecommendation extends UserNotificationEntry {

	public UNSituationRecommendation() {
		super();
		this.put(UNTYPE_LABEL, DimeInternalNotification.UN_TYPE_SITUATION_RECOMENDATION);
	}
	
	public UNSituationRecommendation(String guid, Float score) {
		this();
		setGuid(guid);
		setScore(score);
	}
	
	public String getGuid() {
		return (String) this.get("guid");
	}

	public void setGuid(String guid) {
		this.put("guid", guid);
	}
	
	public Float getScore() {
		return (Float) this.get("nao:score");
	}

	public void setScore(Float score) {
		this.put("nao:score", score);
	}
	
}
