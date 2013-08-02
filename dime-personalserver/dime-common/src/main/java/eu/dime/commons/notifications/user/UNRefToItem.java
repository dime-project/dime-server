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
         "unType":"ref_to_item",
         "unEntry":{
              "guid":"",  //guid of the resource/databox/... etc.
              "type":"livepost",  //type of the resource/databox/... etc.
              "userID":"" //userID / tenantId of the "owner" of the object
              "operation":"shared|unshared"
          }
}
 * 
 * @author mplanaguma
 *
 */
public final class UNRefToItem extends UserNotificationEntry {

	public static final String OPERATION_SHARED = "shared";
	public static final String OPERATION_UNSHARED = "unshared";
	public static final String OPERATION_INC_PRIVACY = "inc_priv";
	public static final String OPERATION_DEC_PRIVACY = "dec_priv";	
	public static final String OPERATION_INC_TRUST = "inc_trust";
	public static final String OPERATION_DEC_TRUST = "dec_trust";


	public static final String TYPE_RESOURCE = DimeInternalNotification.ITEM_TYPE_RESOURCE;
	public static final String TYPE_LIVEPOST = DimeInternalNotification.ITEM_TYPE_LIVEPOST;
	public static final String TYPE_DATABOX = DimeInternalNotification.ITEM_TYPE_DATABOX;
	public static final String TYPE_PERSON = DimeInternalNotification.ITEM_TYPE_PERSON;
	
	public UNRefToItem() {
		super();
		this.put(UNTYPE_LABEL, DimeInternalNotification.UN_TYPE_LINK_TO_ITEM);
	}
	
	public UNRefToItem(String guid, String name, String type, String userID, String operation) {
		this();
		this.setGuid(guid);
		this.setType(type);
		this.setUserID(userID);
		this.setOperation(operation);
                this.setName(name);
	}
	
	public String getGuid() {
		return (String) this.get("guid");
	}
	
	public void setGuid(String guid) {
		this.put("guid", guid);
	}

	public String getName() {
		return (String) this.get("name");
	}

	public void setName(String name) {
		this.put("name", name);
	}

	public String getType() {
		return (String) this.get("type");
	}
	
	public void setType(String type) {
		this.put("type", type);
	}
	
	public String getUserID() {
		return (String) this.get("userId");
	}
	
	public void setUserID(String userID) {
		this.put("userId", userID);
	}
	
	public String getOperation() {
		return (String) this.get("operation");
	}
	
	public void setOperation(String operation) {
		this.put("operation", operation);
	}
	
}
