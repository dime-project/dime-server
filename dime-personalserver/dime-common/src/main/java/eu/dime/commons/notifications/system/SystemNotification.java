package eu.dime.commons.notifications.system;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * Notification related with system changes.
 * This Notification extends DimeInternalNotification and can be added on NotifierManager 
 * queue to be deal on UI. 
 * 
 * @author mplanaguma
 *
 */
public class SystemNotification extends DimeInternalNotification {

	public SystemNotification(
			Long tenant, String operation,
			String itemID, 
			String itemType,
			String userID) {
		
		super(tenant);
		
		super.setNotificationType(DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE);
		super.setCreateTS(System.currentTimeMillis());
		super.setUpdateTS(System.currentTimeMillis());
		
		super.setOperation(operation);
		super.setItemID(itemID);
		super.setItemType(itemType);
		
		if (userID == null || userID.equals("") || userID.endsWith(":me")) {
			super.setUserID(DimeInternalNotification.USERID_ME);
		} else {
			super.setUserID(userID);
		}
	}

}
