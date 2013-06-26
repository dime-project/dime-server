package eu.dime.ps.controllers.notifier.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.controllers.notifier.NotifyHistory;
import eu.dime.ps.storage.entities.Tenant;

public class NotifyHistoryMock  implements NotifyHistory  {
    
    
    public Long addNotificationOnHistory(DimeInternalNotification notification){
	
    	return 1l;
    }
    
    public List<DimeInternalNotification> getNotificationHistory(){
	
	List<DimeInternalNotification> result = new LinkedList<DimeInternalNotification>();
	return result;
	
    }

	@Override
	public DimeInternalNotification getNotificationById(Long id) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> getUnreadedUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults) {		
		return null;
	}

	@Override
	public List<DimeInternalNotification> getUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults) {
		return null;
	}

	@Override
	public void markAsRead(Long id) {
		// TODO Auto-generated method stub
		
	}

}
