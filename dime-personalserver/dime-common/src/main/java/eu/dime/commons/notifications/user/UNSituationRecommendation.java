/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

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
