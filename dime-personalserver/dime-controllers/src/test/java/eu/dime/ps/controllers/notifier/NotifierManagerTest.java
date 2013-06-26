package eu.dime.ps.controllers.notifier;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.commons.notifications.user.UserNotificationEntry;
import eu.dime.ps.controllers.notifier.exception.NotifierException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/test-notifier-applicationContext.xml")
public class NotifierManagerTest {

	@Autowired
	private NotifierManager notifierManager;

	@Test
	public void testPopPushNotification() throws NotifierException {

		Long tenant = 1l;

		notifierManager.pushInternalNotification(new UserNotification(tenant,
				new UserNotificationEntry()));
		Assert.assertNotNull(notifierManager.popInternalNotification(tenant));

		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));
		Assert.assertNotNull(notifierManager.popExternalNotification());

	}

	@Test
	public void testPushNotifications() throws NotifierException {

		Long tenant = 1l;

		notifierManager.pushInternalNotification(new SystemNotification(tenant,
				"operation", "itemID", "itemType", "userID"));
		notifierManager.pushInternalNotification(new SystemNotification(tenant,
				"operation", "itemID", "itemType", "userID"));
		notifierManager.pushInternalNotification(new SystemNotification(tenant,
				"operation", "itemID", "itemType", "userID"));

		Assert.assertEquals(3, notifierManager.popInternalNotifications(tenant)
				.size());

		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));
		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));
		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));

		Assert.assertEquals(3, notifierManager.popExternalNotifications()
				.size());

	}

	@Test
	public void testPushNotificationsByNum() throws NotifierException {

		Long tenant = 1l;

		notifierManager.pushInternalNotification(new SystemNotification(tenant,
				"operation", "itemID", "itemType", "userID"));
		notifierManager.pushInternalNotification(new SystemNotification(tenant,
				"operation", "itemID", "itemType", "userID"));
		notifierManager.pushInternalNotification(new UserNotification(tenant,
				new UserNotificationEntry()));

		notifierManager.popInternalNotifications(tenant, 2);

		Assert.assertEquals(1, notifierManager.popInternalNotifications(tenant)
				.size());

		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));
		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));
		notifierManager.pushExternalNotification(new DimeExternalNotification(
				"target", "sender", DimeInternalNotification.OP_CREATE, "id",
				"name", "type", tenant));

		notifierManager.popExternalNotifications(2);

		Assert.assertEquals(1, notifierManager.popExternalNotifications()
				.size());

	}

}
