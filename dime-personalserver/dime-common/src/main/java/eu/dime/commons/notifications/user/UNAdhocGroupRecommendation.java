package eu.dime.commons.notifications.user;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * 
 * {
         //[..]   //general fields as specified in _general user notification body_
         "unType":"adhoc_group_recommendation",
         "unEntry":{
              // Proposal by TI, to be checked by NUIG
              "guid":"",  //guid of the created ad-hoc group             
              "nao:creator": "urn:auto-generated",
              "name": "" // group name
         }
}
 * 
 * @author mplanaguma
 *
 */
public class UNAdhocGroupRecommendation extends UserNotificationEntry {

	public UNAdhocGroupRecommendation(){
		this.put(UNTYPE_LABEL, DimeInternalNotification.UN_TYPE_ADHOC_GROUP_RECOMENDATION);
	}
	
	public String getUnType() {
		return (String) this.get("unType");
	}
	public String getGuid() {
		return (String) this.get("guid");
	}
	public void setGuid(String guid) {
		this.put("guid", guid);
	}
	public String getNao_creator() {
		return (String) this.get("nao:creator");
	}
	public void setNao_creator(String nao_creator) {
		this.put("nao:creator", nao_creator);
	}
	public String getName() {
		return (String) this.get("name");
	}
	public void setName(String name) {
		this.put("name", name);
	}
	
	
}
