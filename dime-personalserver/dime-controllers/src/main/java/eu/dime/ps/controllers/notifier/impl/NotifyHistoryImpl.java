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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.commons.notifications.user.UNAdhocGroupRecommendation;
import eu.dime.commons.notifications.user.UNMergeRecommendation;
import eu.dime.commons.notifications.user.UNMessage;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UNSituationRecommendation;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.commons.notifications.user.UserNotificationEntry;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.controllers.notifier.NotifyHistory;
import eu.dime.ps.storage.entities.Notification;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

public class NotifyHistoryImpl implements NotifyHistory {

	private EntityFactory entityFactory;

	public Long addNotificationOnHistory(DimeInternalNotification notification) {

		Notification notificationEntity = entityFactory.buildNotification();

		notificationEntity.setName(notification.getName());

		notificationEntity.setSender(notification.getSender());
		notificationEntity.setTarget(notification.getTarget());
		Date time = new Date(System.currentTimeMillis());
		notificationEntity.setTs(time);
		notificationEntity.setUpdateTs(time);

		notificationEntity.setIsRead(false);

		// TODO exception if Tenant not exist
		notificationEntity.setTenant(Tenant.find(notification.getTenant()));

		String type = notification.getNotificationType();

		if (DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE.equals(type)) {

			notificationEntity.setNotificationType(type);

			notificationEntity.setOperation(notification.getOperation());
			notificationEntity.setItemType(notification.getItemType());
			notificationEntity.setItemID(notification.getItemID());

		} else if (DimeInternalNotification.USER_NOTIFICATION_TYPE.equals(type)) {

			notificationEntity.setNotificationType(type);

			String unEntryPayload = JaxbJsonSerializer.jsonValue(notification
					.getUnEntry());
			notificationEntity.setUnEntry(unEntryPayload);

		} else {
			notificationEntity
					.setNotificationType(DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE);
		}

		notificationEntity.persist();

		return notificationEntity.getId();

	}

	public List<DimeInternalNotification> getNotificationHistory() {

		List<Notification> list = Notification.findAllNotificationses();

		return toResult(list);

	}

	private List<DimeInternalNotification> toResult(List<Notification> list) {

		List<DimeInternalNotification> result = new LinkedList<DimeInternalNotification>();

		for (Notification notification : list) {

			DimeInternalNotification n = nToDN(notification);
			result.add(n);

		}
		return result;
	}

	private DimeInternalNotification nToDN(Notification notification) {

		if (notification == null) {
			return null;
		}

		DimeInternalNotification n;

		if (DimeInternalNotification.USER_NOTIFICATION_TYPE.equals(notification
				.getNotificationType()) && notification.getUnEntry() != null) {

			String json = notification.getUnEntry();
			LinkedHashMap map = JaxbJsonSerializer.getMapFromJSON(json);

			String type = (String) map.get(UserNotificationEntry.UNTYPE_LABEL);

			UserNotificationEntry unEntry;

			if (DimeInternalNotification.UN_TYPE_ADHOC_GROUP_RECOMENDATION
					.equals(type)) {
				unEntry = new UNAdhocGroupRecommendation();
				unEntry.putAll(map);

			} else if (DimeInternalNotification.UN_TYPE_LINK_TO_ITEM
					.equals(type)) {
				unEntry = new UNRefToItem();
				unEntry.putAll(map);

			} else if (DimeInternalNotification.UN_TYPE_MERGE_RECOMENDATION
					.equals(type)) {
				unEntry = new UNMergeRecommendation();
				unEntry.putAll(map);

			} else if (DimeInternalNotification.UN_TYPE_MESSAGE.equals(type)) {
				unEntry = new UNMessage();
				unEntry.putAll(map);

			} else if (DimeInternalNotification.UN_TYPE_SITUATION_RECOMENDATION
					.equals(type)) {
				unEntry = new UNSituationRecommendation();
				unEntry.putAll(map);

			} else {
				unEntry = new UserNotificationEntry();
				unEntry.putAll(map);
			}

			n = new UserNotification(notification.getTenant().getId(), unEntry);

		} else {

			n = new SystemNotification(notification.getTenant().getId(),
					notification.getOperation(), notification.getItemID(),
					notification.getItemType(), null);

		}

		n.setId(String.valueOf(notification.getId()));
		return n;
	}

	public EntityFactory getEntityFactory() {
		return entityFactory;
	}

	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	@Override
	public DimeInternalNotification getNotificationById(Long id) {

		Notification notification = Notification.findNotifications(id);
		return nToDN(notification);
	}

	@Override
	public List<DimeInternalNotification> getNotificationsByDate(Date from,
			Date to) {

		List<Notification> list = Notification.findAllNotificationsByDate(from,
				to, 0, 1000);

		return toResult(list);
	}

	@Override
	public List<DimeInternalNotification> getUserNotificationHistory(
			Tenant tenant, Integer firstResult, Integer maxResults) {
		List<Notification> list = Notification
				.findAllUserNotificationsesByTenant(tenant, firstResult,
						maxResults);
		return toResult(list);

	}

	@Override
	public List<DimeInternalNotification> getUnreadedUserNotificationHistory(
			Tenant tenant, Integer firstResult, Integer maxResults) {

		List<Notification> list = Notification
				.findAllUserNotificationsesUnreadedByTenant(tenant,
						firstResult, maxResults);

		// mark as read automatic?????
		// markAsRead(list);
		return toResult(list);

	}

	@Override
	public void markAsRead(Long id) {
		Notification n = Notification.findNotifications(id);
		markAsRead(n);
	}

	private void markAsRead(List<Notification> notifications) {

		for (Notification notification : notifications) {
			markAsRead(notification);
		}
	}

	private void markAsRead(Notification notification) {

		Notification n = Notification.findNotifications(notification.getId());
		n.setIsRead(true);
		n.merge();

	}

}
