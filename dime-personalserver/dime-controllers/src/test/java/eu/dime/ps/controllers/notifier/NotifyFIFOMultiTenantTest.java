package eu.dime.ps.controllers.notifier;

import junit.framework.Assert;

import org.junit.Test;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;

public class NotifyFIFOMultiTenantTest {

	NotifyFIFOMultiTenant fifoMulti;
	
	@Test
	public void testPopPushNotification() {
		
		fifoMulti = new NotifyFIFOMultiTenant();
		
		Long tenant = 666l;

		fifoMulti.pushNotification(new SystemNotification(tenant, "operation", "itemID", "itemType", "userID"));
		
		Assert.assertEquals(1, fifoMulti.size(tenant));
		
		fifoMulti.popNotification(tenant);
		
		Assert.assertEquals(0, fifoMulti.size(tenant));
		
	}

	@Test
	public void testOrderFIFONotification() {

		fifoMulti = new NotifyFIFOMultiTenant();
		
		Long tenant = 666l;

		DimeInternalNotification n1 = (new SystemNotification(tenant, "operation", "itemID", "itemType", "userID"));
		n1.setItemID("n1");
		
		fifoMulti.pushNotification(n1);
		
		DimeInternalNotification n2 = (new SystemNotification(tenant, "operation", "itemID", "itemType", "userID"));
		n2.setItemID("n2");
		
		fifoMulti.pushNotification(n2);
		
		Assert.assertEquals(2, fifoMulti.size(tenant));
		
		Assert.assertEquals(n1.getItemID(),fifoMulti.popNotification(tenant).getItemID());
		Assert.assertEquals(n2.getItemID(),fifoMulti.popNotification(tenant).getItemID());
		
		Assert.assertEquals(0, fifoMulti.size(tenant));
		
	}
	
	@Test
	public void testOrderFIFONotificationMulti() {

		fifoMulti = new NotifyFIFOMultiTenant();
		
		Long tenant = 666l;

		DimeInternalNotification n1 = (new SystemNotification(tenant, "operation", "itemID", "itemType", "userID"));
		n1.setItemID("n1");
		
		fifoMulti.pushNotification(n1);
		
		DimeInternalNotification n2 = (new SystemNotification(tenant, "operation", "itemID", "itemType", "userID"));
		n2.setItemID("n2");
		
		fifoMulti.pushNotification(n2);
		
		Long tenant2 = 1l;

		DimeInternalNotification ntenant2 = (new SystemNotification(tenant2, "operation", "itemID", "itemType", "userID"));
		ntenant2.setItemID("ntenant2");
		
		fifoMulti.pushNotification(ntenant2);
		
		Assert.assertEquals(2, fifoMulti.size(tenant));
		Assert.assertEquals(1, fifoMulti.size(tenant2));
		
		Assert.assertEquals(n1.getItemID(),fifoMulti.popNotification(tenant).getItemID());
		Assert.assertEquals(n2.getItemID(),fifoMulti.popNotification(tenant).getItemID());
		
		Assert.assertEquals(0, fifoMulti.size(tenant));
		
		Assert.assertEquals(ntenant2.getItemID(),fifoMulti.popNotification(tenant2).getItemID());
		
		Assert.assertEquals(0, fifoMulti.size(tenant));
		
	}

}
