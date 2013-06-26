package eu.dime.commons.notifications.user;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * Notification related with user actions.
 * This Notification extends DimeInternalNotification and can be added on NotifierManager 
 * queue to be deal on UI. 
 * 
 * @author mplanaguma
 *
 */
public class UserNotification extends DimeInternalNotification {

	public UserNotification(Long tenant, UserNotificationEntry unEntry) {
		super(tenant);
		super.setNotificationType(USER_NOTIFICATION_TYPE);
		super.setCreateTS(System.currentTimeMillis());
		super.setUpdateTS(System.currentTimeMillis());
		super.setIsRead(false);
		
		super.setUnEntry(unEntry);
		
		super.setUserID(DimeInternalNotification.USERID_ME);
	}

}
