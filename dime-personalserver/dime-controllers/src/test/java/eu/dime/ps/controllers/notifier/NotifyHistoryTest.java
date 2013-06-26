package eu.dime.ps.controllers.notifier;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.ps.storage.entities.Notification;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/test-notifier-applicationContext.xml")
@TransactionConfiguration(defaultRollback = true)
public class NotifyHistoryTest {

	private static final Logger logger = LoggerFactory
			.getLogger(NotifyHistoryTest.class);

	@Autowired
	private EntityFactory entityFactory;
	@Autowired
	private NotifyHistory notifyHistory;
	
	private Tenant tenant;

	@Before
	@Transactional
	public void build() {
		tenant = entityFactory.buildTenant();
		tenant.setName("NotifyHistoryTest-name-tenant");
		tenant.persist();
	}
	
	@After
	@Transactional
	public void end() {
		tenant.remove();

	}
	
	@Test
	@Transactional
	public void testAddSystemNotificationOnHistory() {

		DimeInternalNotification notification = new SystemNotification(tenant.getId(), "operation", "itemID", "itemType", "userID");
		
		notification.setNotificationType(DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE);
		
		notifyHistory.addNotificationOnHistory(notification);

		Assert.assertTrue(notifyHistory.getNotificationHistory().size() > 0);

		List<Notification> list = entityFactory.buildNotification()
				.findAllNotificationsOrderedbyTime(0, 1000);
		list.get(0).remove();

	}
	
	@Test
	@Transactional
	public void testAddUserNotificationOnHistory() {
		
		DimeInternalNotification notification =  new SystemNotification(tenant.getId(), "operation", "itemID", "itemType", "userID");
		
		UNRefToItem refToItem = new UNRefToItem();
		refToItem.setGuid("guid");
		refToItem.setOperation(UNRefToItem.OPERATION_SHARED);
		refToItem.setType(UNRefToItem.TYPE_DATABOX);
		refToItem.setUserID("userID");
		
		notification.setUnEntry(refToItem);
		
		notification.setNotificationType(DimeInternalNotification.USER_NOTIFICATION_TYPE);
		
		Long id = notifyHistory.addNotificationOnHistory(notification);

		Assert.assertTrue(notifyHistory.getUserNotificationHistory(tenant, 0, 1000).size() > 0);
		
		DimeInternalNotification n = notifyHistory.getNotificationById(id);
		
		Assert.assertTrue(refToItem.getGuid().equals(((UNRefToItem)n.getUnEntry()).getGuid()));

		List<Notification> list = entityFactory.buildNotification()
				.findAllNotificationsOrderedbyTime(0, 1000);
		list.get(0).remove();

	}
	
	@Test
	@Transactional
	public void testAddUserNotificationOnHistoryByTenantAndUnreaded() {
		
		DimeInternalNotification notification =  new SystemNotification(tenant.getId(), "operation", "itemID", "itemType", "userID");
		
		UNRefToItem refToItem = new UNRefToItem();
		refToItem.setGuid("guid");
		refToItem.setOperation(UNRefToItem.OPERATION_SHARED);
		refToItem.setType(UNRefToItem.TYPE_DATABOX);
		refToItem.setUserID("userID");
		
		notification.setUnEntry(refToItem);
		
		notification.setNotificationType(DimeInternalNotification.USER_NOTIFICATION_TYPE);
		
		Long id = notifyHistory.addNotificationOnHistory(notification);

		Assert.assertTrue(notifyHistory.getUnreadedUserNotificationHistory(tenant, 0 , 1000).size() > 0);
		
		DimeInternalNotification n = notifyHistory.getNotificationById(id);
		
		Assert.assertTrue(refToItem.getGuid().equals(((UNRefToItem)n.getUnEntry()).getGuid()));
		
		// Assert mark as readed
		notifyHistory.markAsRead(id);
		Assert.assertTrue(notifyHistory.getUnreadedUserNotificationHistory(tenant, 0 ,1000).isEmpty());

		List<Notification> list = entityFactory.buildNotification()
				.findAllNotificationsOrderedbyTime(0, 1000);
		list.get(0).remove();

	}

}
