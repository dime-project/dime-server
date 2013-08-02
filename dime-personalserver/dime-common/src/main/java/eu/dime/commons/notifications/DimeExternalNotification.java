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

package eu.dime.commons.notifications;

import eu.dime.commons.object.AbstractDimeObject;
import eu.dime.commons.util.JaxbJsonSerializer;

/**
 * External Notification: one di.me service to notify another di.me
 * service about an event. This is triggered asynchronously via a push from
 * di.me server to di.me server.
 * 
 * @author Sophie.Wrobel
 * @author Marc Planaguma (BDCT)
 * 
 */
public class DimeExternalNotification implements AbstractDimeObject {

	private Long id;
    private String target;
	private String sender;
	private String notificationType;
	private String operation;
	private String itemType;
	private String itemID;
	private String name;
	private Long tenant;
	private String userID;
	
	public static final String OP_CREATE = "create";
	public static final String OP_UPDATE = "update";
	public static final String OP_REMOVE = "remove";
	public static final String OP_SHARE = "share";
	public static final String OP_REGISTER = "register";
	public static final String OP_MATCHING = "matching";
	
	public static final String TYPE_RESOURCE = "resource";
	public static final String TYPE_LIVEPOST = "livepost";
	public static final String TYPE_DATABOX = "databox";
	public static final String TYPE_GROUP = "group";
	public static final String TYPE_SERVICE = "service";
	public static final String TYPE_PERSON = "person";
	public static final String TYPE_PROFILE = "profile";
	public static final String TYPE_EVENT = "event";
	public static final String TYPE_SITUATION = "situation";
	public static final String TYPE_CONTEXT = "context";
	public static final String TYPE_NOTIFICATION = "notification";
	public static final String TYPE_PLACE = "place";
	
	public DimeExternalNotification(Long tenant){
		super();
		this.tenant=tenant;
	}
	
	/**
	 * Fully initializing constructor
	 */
	public DimeExternalNotification(String target, String sender,
			String operation, String itemID, String name, String itemType,Long tenant) {
		this.target = target;
		this.sender = sender;
		this.operation = operation;
		this.itemID = itemID;
		this.itemType = itemType;
		this.name = name;
		this.tenant= tenant;
	}

	/**
	 * Specify the target
	 * 
	 * @param target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Retrieve the target
	 * 
	 * @return target
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * Specify the sender's identifier
	 * 
	 * @param sender
	 *            the identifier of the sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * Retrieve the sender's identifier
	 * 
	 * @return the identifier of the sender
	 */
	public String getSender() {
		return this.sender;
	}

	/**
	 * Specify what kind of operation the notification is about (e.g. update a
	 * profile with the information in element)
	 * 
	 * @param operation
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * Retrieve the kind of operation the notification is about (e.g. update a
	 * profile with the information in element)
	 * 
	 * @return 
	 */
	public String getOperation() {
		return this.operation;
	}

	public String getItemType() {
	    return itemType;
	}

	public void setItemType(String itemType) {
	    this.itemType = itemType;
	}

	public String getItemID() {
	    return itemID;
	}

	public void setItemID(String itemID) {
	    this.itemID = itemID;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
	
	public Long getTenant() {
		return tenant;
	}

	public void setTenant(Long tenant) {
		this.tenant = tenant;
	}

	public Long getId() {
	    return id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	
	@Override
	public String toString() {
		return JaxbJsonSerializer.jsonValue(this);
		
	}


}
