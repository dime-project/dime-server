package eu.dime.ps.controllers.notifier;

import java.util.Date;
import java.util.List;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Set and Get the notifications from persistence 
 * 
 * @author mplanaguma
 */
public interface NotifyHistory {
    
    public Long addNotificationOnHistory(DimeInternalNotification notification);
    
    public List<DimeInternalNotification> getNotificationHistory();
    
    public DimeInternalNotification getNotificationById(Long id);
    
    public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to);

	public List<DimeInternalNotification> getUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults);
	
	public List<DimeInternalNotification> getUnreadedUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults);
	
	public void markAsRead(Long id);

}
