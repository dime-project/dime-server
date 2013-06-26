package eu.dime.ps.controllers.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.storage.entities.Tenant;

/**
 * It simply stores the notifications received in two lists: external and internal.
 */
public class NotifierManagerMock implements NotifierManager {

	public List<DimeInternalNotification> internal = new ArrayList<DimeInternalNotification>();
	public List<DimeExternalNotification> external = new ArrayList<DimeExternalNotification>();
	
	@Override
	public void pushInternalNotification(DimeInternalNotification notification)
			throws NotifierException {
		internal.add(notification);
	}

	@Override
	public void pushExternalNotification(
			DimeExternalNotification notification) throws NotifierException {
		external.add(notification);
	}

	@Override
	public DimeInternalNotification popInternalNotification(Long tenant) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> popInternalNotifications(
			Long tenant, Integer num) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> popInternalNotifications(
			Long tenant) {
		return null;
	}

	@Override
	public DimeExternalNotification popExternalNotification() {
		return null;
	}

	@Override
	public List<DimeExternalNotification> popExternalNotifications(
			Integer num) {
		return null;
	}

	@Override
	public List<DimeExternalNotification> popExternalNotifications() {
		return null;
	}

	@Override
	public List<DimeInternalNotification> getAllNotifications(
			Tenant tenant, Integer firstResult, Integer maxResults) {
		return null;
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
	public List<DimeInternalNotification> getAllMyUserUnReadedNotifications(
			Tenant tenant, Integer firstResult, Integer maxResults) {
		return null;
	}

	@Override
	public void markNotificationAsRead(Long id) {
	}

	@Override
	public List<DimeInternalNotification> getAllMyUserNotifications(
			Tenant tenant, Integer firstResult, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}
}
