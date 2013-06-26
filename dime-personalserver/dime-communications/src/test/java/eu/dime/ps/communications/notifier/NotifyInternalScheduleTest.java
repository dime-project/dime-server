package eu.dime.ps.communications.notifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.util.SimpleBroadcaster;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.system.SystemNotification;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.communications.requestbroker.pubsub.PSNotificationDispacher;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.storage.entities.Tenant;

@RunWith(MockitoJUnitRunner.class)
public class NotifyInternalScheduleTest {

	private final String said = "marc";
	private final Long id = 1l;
	private Tenant tenant;
	
	@Spy InternalNotifySchedule internalNotifySchedule = new InternalNotifySchedule();
	@Mock NotifierManager notifierManager;
	@Mock TenantManager tenantManager;
	
	@Spy PSNotificationDispacher notificationDispacher =  new PSNotificationDispacher();
	@Mock SimpleBroadcaster simpleBroadcaster;
	
	@Captor ArgumentCaptor<String> captor;
	
	@Before
	public void before(){
		 MockitoAnnotations.initMocks(this);
		
		tenant = new Tenant();
		tenant.setId(id);
		//internalNotifySchedule
		
		Mockito.when(tenantManager.getByAccountName(said)).thenReturn(tenant);

		List<DimeInternalNotification> dins = new Vector<DimeInternalNotification>();
		
		UNRefToItem refToItem = new UNRefToItem();
		refToItem.setGuid(UUID.randomUUID().toString());
		refToItem.setOperation(UNRefToItem.OPERATION_SHARED);
		refToItem.setType(UNRefToItem.TYPE_DATABOX);
		refToItem.setUserID(UUID.randomUUID().toString());
	
		DimeInternalNotification un = new SystemNotification(tenant.getId(), "operation", "001", DimeInternalNotification.USER_NOTIFICATION_TYPE, "@me");
		un.setId("1234");
		dins.add(un);
		
		DimeInternalNotification sn = new SystemNotification(tenant.getId(), "operation", "002", "itemType", null);
		sn.setId("5678");
		dins.add(sn);
		
		Mockito.when(notifierManager.popInternalNotifications(tenant.getId(), 10)).thenReturn(dins);
		
		internalNotifySchedule.setNotifierManager(notifierManager);
		internalNotifySchedule.setTenantManager(tenantManager);
				
	}
	
    @Test
    public void testDealNotifications() throws InterruptedException {
    	
    	// Mocking
		notificationDispacher.setTopic(simpleBroadcaster);
		notificationDispacher.setInternalNotifySchedule(internalNotifySchedule);
		internalNotifySchedule.addBroadcaster(notificationDispacher,simpleBroadcaster, said);
	
		// Run
    	internalNotifySchedule.dealNotifications();
    	
    	// Asserts
    	Mockito.verify(notificationDispacher).publishIntern(captor.capture(), (Broadcaster) Mockito.anyObject());
    	
    	Mockito.verify(simpleBroadcaster).broadcast(Mockito.anyObject());
    	
    	Mockito.verify(internalNotifySchedule).removeBroadcaster(simpleBroadcaster);
    	
    	String json = captor.getValue();
    	LinkedHashMap map = JaxbJsonSerializer.getMapFromJSON(json);
    	List entries = (ArrayList) ((LinkedHashMap)((LinkedHashMap)map.get("response")).get("data")).get("entry");
    	
    	LinkedHashMap n1 = (LinkedHashMap) entries.get(0);
    	
    	org.junit.Assert.assertTrue(n1.containsKey("guid"));
    	org.junit.Assert.assertTrue(n1.containsKey("type"));
    	org.junit.Assert.assertTrue(n1.containsKey("operation"));
    	LinkedHashMap e1 = (LinkedHashMap) n1.get("element");
    	org.junit.Assert.assertTrue(e1.containsKey("guid"));
    	org.junit.Assert.assertTrue(e1.containsKey("type"));
    	org.junit.Assert.assertTrue(e1.containsKey("userId"));
    	org.junit.Assert.assertEquals("001", e1.get("guid"));
    	org.junit.Assert.assertEquals("@me", e1.get("userId"));
    	
    	LinkedHashMap n2 = (LinkedHashMap) entries.get(1);
    	org.junit.Assert.assertTrue(n2.containsKey("guid"));
    	org.junit.Assert.assertTrue(n2.containsKey("type"));
    	org.junit.Assert.assertTrue(n2.containsKey("operation"));
    	LinkedHashMap e2 = (LinkedHashMap) n2.get("element");
    	org.junit.Assert.assertTrue(e2.containsKey("guid"));
    	org.junit.Assert.assertTrue(e2.containsKey("type"));
    	org.junit.Assert.assertTrue(e2.containsKey("userId"));
    	org.junit.Assert.assertEquals("002", e2.get("guid"));
    	org.junit.Assert.assertEquals("itemType", e2.get("type"));
    	org.junit.Assert.assertEquals("@me", e2.get("userId"));
    	

    }

    
//    @Test
//    public void testDealNotificationsException(){
//    	
//    	// Mocking
//    	Mockito.when(simpleBroadcaster.broadcast(Mockito.anyObject())).thenThrow(throwableClasses)
//		notificationDispacher.setTopic(simpleBroadcaster);
//		notificationDispacher.setInternalNotifySchedule(internalNotifySchedule);
//		internalNotifySchedule.addBroadcaster(notificationDispacher,simpleBroadcaster, said);
//	
//		// Run
//    	internalNotifySchedule.dealNotifications();
//    	
//    }
}
