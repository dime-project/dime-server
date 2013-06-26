package eu.dime.ps.controllers.notifier;

import java.util.Date;
import java.util.List;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Manager on Dime Notifications
 * 
 * @author mplanaguma (BDCT)
 *
 */
public interface NotifierManager {

	
	/**
	 * Add a notification on a FIFO queue to be consumed for UI
	 */
	public void pushInternalNotification(DimeInternalNotification notification) throws NotifierException;
	
	/**
	 * Add a notification on a FIFO queue to be consumed for other PS
	 */
	public void pushExternalNotification(DimeExternalNotification notification) throws NotifierException;
	
	// Internal
	
	/**
	 * Obtain the fist Notification of the Internal Notifications FIFO queue
	 * 
	 * @return
	 */
	public DimeInternalNotification popInternalNotification(Long tenant);
	
	/**
	 * Obtain the Notifications of the Internal Notifications FIFO queue
	 * 
	 * @param num The number of Notification
	 * @return The notification List by order
	 */
	public List<DimeInternalNotification> popInternalNotifications(Long tenant, Integer num);
	
	/**
	 * Obtain All the Notifications of the Internal Notifications FIFO queue
	 * 
	 * @return All the notification List by order
	 */
	public List<DimeInternalNotification> popInternalNotifications(Long tenant);
	
	// EXternal
	
	/**
	 * Obtain the fist Notification of the External Notifications FIFO queue
	 * 
	 * @return
	 */
	public DimeExternalNotification popExternalNotification();
	
	/**
	 * Obtain the Notifications of the External Notifications FIFO queue
	 * 
	 * @param num The number of Notification
	 * @return The notification List by order
	 */
	public List<DimeExternalNotification> popExternalNotifications(Integer num);
	
	/**
	 * Obtain All the Notifications of the External Notifications FIFO queue
	 * 
	 * @return All the notification List by order
	 */
	public List<DimeExternalNotification> popExternalNotifications();
	
	/**
	 * Obtain all the historic of Notifications ordered by timestamp
	 * 
	 * @return All the notification List by timestamp order
	 */
	public List<DimeInternalNotification> getAllNotifications(Tenant tenant, Integer firstResult, Integer maxResults);
	
	
	/**
	 * Obtain a specific notification from the historic
	 * 
	 * @param id
	 * @return notification
	 */
	public DimeInternalNotification getNotificationById(Long id);
	
	/**
	 *  Obtain the historic of Notifications between dates 
	 * 
	 * @param from date
	 * @param to date
	 * @return notifications
	 */
	public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to);
	
	/**
	 * Obtain all the historic of User Notifications  ordered by timestamp
	 * 
	 * @return All the notification with type:userNotification List by timestamp order
	 */
	public List<DimeInternalNotification> getAllMyUserNotifications(Tenant tenant, Integer firstResult, Integer maxResults);
	
	
	/**
	 * Obtain all the Unread of User Notifications ordered by timestamp
	 * 
	 * @return All the notification with type:userNotification List by timestamp order
	 */
	public List<DimeInternalNotification> getAllMyUserUnReadedNotifications(Tenant tenant, Integer firstResult, Integer maxResults);
	
	/**
	 * Mark as read the notification in the storage
	 */
	public void markNotificationAsRead(Long id);
}
