/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
