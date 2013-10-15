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

package eu.dime.ps.controllers.notifier.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.dto.UserNotificationDTO;
import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.NotifyFIFOExternal;
import eu.dime.ps.controllers.notifier.NotifyFIFOMultiTenant;
import eu.dime.ps.controllers.notifier.NotifyHistory;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.storage.entities.Tenant;

public class NotifierManagerImpl implements NotifierManager {

	private static final Logger logger = LoggerFactory
			.getLogger(NotifierManagerImpl.class);
	private NotifyFIFOMultiTenant internalNotifyFifo;
	private NotifyFIFOExternal externalNotifyFifo;
	private NotifyHistory notifyHistory;

	// Internal

	@Override
	public void pushInternalNotification(DimeInternalNotification notification)
			throws NotifierException {
		
		logger.info("Addind a notification in the storage: "
				+ notification.toString());
		
		if(DimeInternalNotification.USER_NOTIFICATION_TYPE.equals(notification.getNotificationType())){
			Long id = notifyHistory.addNotificationOnHistory(notification);
			
			SystemNotification systemNotification = new SystemNotification(
						notification.getTenant(), 
						SystemNotification.OP_CREATE, 
						String.valueOf(id), 
						DimeInternalNotification.USER_NOTIFICATION_TYPE, 
						notification.getUserID());
			
			logger.info("Addind a User Notification in the queue: "
					+ notification.toString());
			
			systemNotification.setId(UUID.randomUUID().toString());
			internalNotifyFifo.pushNotification(systemNotification);
		}
		
		if(DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE.equals(notification.getNotificationType())){
			
			logger.info("Addind a System Notification in the queue: "
					+ notification.toString());
			
			notification.setId(UUID.randomUUID().toString());
			internalNotifyFifo.pushNotification(notification);
		}

		


	}

	@Override
	public DimeInternalNotification popInternalNotification(Long tenant) {

		return internalNotifyFifo.popNotification(tenant);
	}

	@Override
	public List<DimeInternalNotification> popInternalNotifications(Long tenant,
			Integer num) {

		Vector<DimeInternalNotification> result = new Vector<DimeInternalNotification>(num);

		for (int i = 0; i < num; i++) {
			
			DimeInternalNotification n = internalNotifyFifo.popNotification(tenant);
			if(n != null){
				result.add(n);
			}
		}

		return result;
	}

	@Override
	public List<DimeInternalNotification> popInternalNotifications(Long tenant) {

		return popInternalNotifications(tenant, internalNotifyFifo.size(tenant));
	}

	// External

	@Override
	public void pushExternalNotification(DimeExternalNotification notification)
			throws NotifierException {
		logger.info("Addind a notification in the queue: "
				+ notification.toString());
		externalNotifyFifo.pushNotification(notification);

	}

	@Override
	public DimeExternalNotification popExternalNotification() {

		return externalNotifyFifo.popNotification();
	}

	@Override
	public List<DimeExternalNotification> popExternalNotifications(Integer num) {

		Vector<DimeExternalNotification> result = new Vector<DimeExternalNotification>();

		for (int i = 0; i < num; i++) {
			result.add(externalNotifyFifo.popNotification());
		}

		return result;
	}

	@Override
	public List<DimeExternalNotification> popExternalNotifications() {

		return popExternalNotifications(externalNotifyFifo.size());
	}

	
	// Historic
	
	@Override
	public List<DimeInternalNotification> getAllNotifications(Tenant tenant, Integer firstResult, Integer maxResults) {
		return notifyHistory.getNotificationHistory();
	}
	
	@Override
	public DimeInternalNotification getNotificationById(Long id) {
		return notifyHistory.getNotificationById(id);
	}

	@Override
	public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to) {
		return notifyHistory.getNotificationsByDate(from, to);
	}
	
	@Override
	public List<DimeInternalNotification> getAllMyUserNotifications(
			Tenant tenant, Integer firstResult, Integer maxResults) {
		return notifyHistory.getUserNotificationHistory(tenant, firstResult, maxResults);
	}

	@Override
	public List<DimeInternalNotification> getAllMyUserUnReadedNotifications(Tenant tenant, Integer firstResult, Integer maxResults) {
		return notifyHistory.getUnreadedUserNotificationHistory(tenant, firstResult, maxResults);
		
	}
	
	@Override
	public void markNotificationAsRead(Long id){
		notifyHistory.markAsRead(id);
	}	
	
	@Override
	public void purgeNotifications(){
		internalNotifyFifo.purgeNotifications();
	}

	// SETTERS

	public void setExternalNotifyFifo(NotifyFIFOExternal externalNotifyFifo) {
		this.externalNotifyFifo = externalNotifyFifo;
	}

	public void setInternalNotifyFifo(NotifyFIFOMultiTenant internalNotifyFifo) {
		this.internalNotifyFifo = internalNotifyFifo;
	}

	public void setNotifyHistory(NotifyHistory notifyHistory) {
		this.notifyHistory = notifyHistory;
	}

	@Override
	public DimeInternalNotification updateUserNotification(Long id,
			UserNotificationDTO notificationToUpdate) {
		return 	notifyHistory.updateNotification(id,notificationToUpdate);
	}

	



}
