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

package eu.dime.commons.notifications;

import eu.dime.commons.notifications.user.UserNotificationEntry;
import eu.dime.commons.object.AbstractDimeObject;
import eu.dime.commons.util.JaxbJsonSerializer;

/**
 * Generic Internal Notification object to define 2 different Notifications
 * 
 * 1- Use child class SystemNotification: di.me personal server to notify the UI from
 * a system event
 * 
 * 2- Use child class UserNotification: di.me personal server to notify the UI from
 * different user actions
 * 
 * @author Marc Planaguma (BDCT)
 * 
 */
public class DimeInternalNotification implements AbstractDimeObject {

	private String id;
	private String name="Notification"; 

	private Long tenant;
	private String target;
	private String sender;
	private String userId="@me";

	// User or System Notification
	private String notificationType;
	
	// for System Notification
	private String operation;
	private String itemType;
	private String itemID;

	// for User notification
	private UserNotificationEntry unEntry;
	
	// info from stored notifications
	private Long createTS;
	private Long updateTS;
	private Boolean isRead;

	public static final String SYSTEM_NOTIFICATION_TYPE = "systemnotification";
	// operations for system notifications
	public static final String OP_CREATE = "create";
	public static final String OP_UPDATE = "update";
	public static final String OP_REMOVE = "remove";
	public static final String OP_SHARE = "share";
	public static final String OP_REGISTER = "register";
	public static final String OP_MATCHING = "matching";

	public static final String USER_NOTIFICATION_TYPE = "usernotification";
	// operations for user notifications
	public static final String UN_TYPE_MERGE_RECOMENDATION = "merge_recommendation";
	public static final String UN_TYPE_SITUATION_RECOMENDATION = "situation_recommendation";
	public static final String UN_TYPE_ADHOC_GROUP_RECOMENDATION = "adhoc_group_recommendation";
	public static final String UN_TYPE_LINK_TO_ITEM = "ref_to_item";
	public static final String UN_TYPE_MESSAGE = "message";

	// item types
	public static final String ITEM_TYPE_RESOURCE = "resource";
	public static final String ITEM_TYPE_LIVEPOST = "livepost";
	public static final String ITEM_TYPE_DATABOX = "databox";
	public static final String ITEM_TYPE_GROUP = "group";
	public static final String ITEM_TYPE_SERVICE = "service";
	public static final String ITEM_TYPE_PERSON = "person";
	public static final String ITEM_TYPE_PROFILE = "profile";
	public static final String ITEM_TYPE_EVENT = "event";
	public static final String ITEM_TYPE_SITUATION = "situation";
	public static final String ITEM_TYPE_CONTEXT = "context";
	public static final String ITEM_TYPE_NOTIFICATION = "notification";
	public static final String ITEM_TYPE_PLACE = "place";
        public static final String ITEM_TYPE_USER = "auth";

	public final static String SENDER_ME =  "@me";
	public final static String TARGET_ME =  "@me";
	public final static String USERID_ME =  "@me";
	
	
	public DimeInternalNotification(Long tenant) {
		super();
		this.tenant = tenant;
		this.sender = SENDER_ME;
		this.target = TARGET_ME;
	}

	/**
	 * Fully initializing constructor
	 */
	// FIXME candidate to be removed
	@Deprecated
	public DimeInternalNotification(String target, String sender,
			String operation, String itemID, String name, String itemType,
			Long tenant) {
		this.target = target;
		this.sender = sender;
		this.operation = operation;
		this.itemID = itemID;
		this.itemType = itemType;
		this.name = name;
		this.tenant = tenant;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserID() {
		return userId;
	}

	public void setUserID(String userID) {
		this.userId = userID;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public UserNotificationEntry getUnEntry() {
		return unEntry;
	}

	public void setUnEntry(UserNotificationEntry unEntry) {
		this.unEntry = unEntry;
	}

	public Long getCreateTS() {
		return createTS;
	}

	public void setCreateTS(Long createTS) {
		this.createTS = createTS;
	}

	public Long getUpdateTS() {
		return updateTS;
	}

	public void setUpdateTS(Long updateTS) {
		this.updateTS = updateTS;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	@Override
	public String toString() {
		return JaxbJsonSerializer.jsonValue(this);
		
	}

}
