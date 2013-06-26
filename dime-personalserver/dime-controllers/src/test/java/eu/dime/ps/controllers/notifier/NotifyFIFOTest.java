package eu.dime.ps.controllers.notifier;

import junit.framework.Assert;

import org.junit.Test;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;

public class NotifyFIFOTest {

	NotifyFIFOExternal fifo;
	
	@Test
	public void testPopPushNotification() {
		
		Long tenant = 1l;
		
		fifo = new NotifyFIFOExternal();

		fifo.pushNotification(new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		
		Assert.assertEquals(1, fifo.size());
		
		fifo.popNotification();
		
		Assert.assertEquals(0, fifo.size());
		
	}

	@Test
	public void testOrderFIFOhNotification() {
		
		Long tenant = 1l;

		fifo = new NotifyFIFOExternal();

		DimeExternalNotification n1 = (new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		n1.setItemID("n1");
		
		fifo.pushNotification(n1);
		
		DimeExternalNotification n2 = (new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		n2.setItemID("n2");
		
		fifo.pushNotification(n2);
		
		Assert.assertEquals(n1.getItemID(),fifo.popNotification().getItemID());
		Assert.assertEquals(n2.getItemID(),fifo.popNotification().getItemID());
		
	}

}
