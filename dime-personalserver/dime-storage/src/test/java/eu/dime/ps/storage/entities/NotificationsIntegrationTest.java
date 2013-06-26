package eu.dime.ps.storage.entities;

import java.util.List;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/ps-db-applicationContext.xml")
@Transactional
//@Ignore
@RooIntegrationTest(findAll=false, entity = Notification.class)
public class NotificationsIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private NotificationsDataOnDemand dod;

	@Test
    public void testCountNotificationses() {
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", dod.getRandomNotifications());
        long count = eu.dime.ps.storage.entities.Notification.countNotificationses();
        org.junit.Assert.assertTrue("Counter for 'Notifications' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindNotifications() {
        eu.dime.ps.storage.entities.Notification obj = dod.getRandomNotifications();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.Notification.findNotifications(id);
        org.junit.Assert.assertNotNull("Find method for 'Notifications' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'Notifications' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindNotificationsEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", dod.getRandomNotifications());
        long count = eu.dime.ps.storage.entities.Notification.countNotificationses();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.Notification> result = eu.dime.ps.storage.entities.Notification.findNotificationsEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'Notifications' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Notifications' returned an incorrect number of entries", count, result.size());
    }
	
	@Test
    public void testFindUserNotificationsEntries() {
        Notification n = dod.getRandomNotifications();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", n);
        long count = eu.dime.ps.storage.entities.Notification.countUserNotificationses();
        java.util.List<eu.dime.ps.storage.entities.Notification> result = eu.dime.ps.storage.entities.Notification.findAllUserNotificationses(0, 10000);
        org.junit.Assert.assertNotNull("Find entries method for 'Notifications' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'Notifications' returned an incorrect number of entries", count, result.size());
    }
	
	@Test
    public void testFindUserNotificationsEntriesByTenant() {
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", dod.getRandomNotifications());
        
        Notification n = dod.getRandomNotifications();
        Tenant tenant = n.getTenant();
        
        List<Notification> result = Notification.findAllUserNotificationsesByTenant(tenant, 0,20);
        
        org.junit.Assert.assertNotNull("FindUserNotificationsEntriesByTenant entries method for 'Notifications' illegally returned null", result);
    }
	
	@Test
    public void testFindUserNotificationsEntriesUnReadedByTenant() {
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", dod.getRandomNotifications());
        
        Notification n;
        do {
        	n = dod.getRandomNotifications();			
		} while (n.getIsRead());
        
        Tenant tenant = n.getTenant();
        
        List<Notification> result = Notification.findAllUserNotificationsesUnreadedByTenant(tenant, 0, 20);
        
        org.junit.Assert.assertNotNull("FindUserNotificationsEntriesByTenant entries method for 'Notifications' illegally returned null", result);
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.Notification obj = dod.getRandomNotifications();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.Notification.findNotifications(id);
        org.junit.Assert.assertNotNull("Find method for 'Notifications' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyNotifications(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'Notifications' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.Notification obj = dod.getRandomNotifications();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.Notification.findNotifications(id);
        boolean modified =  dod.modifyNotifications(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        eu.dime.ps.storage.entities.Notification merged = (eu.dime.ps.storage.entities.Notification) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'Notifications' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", dod.getRandomNotifications());
        eu.dime.ps.storage.entities.Notification obj = dod.getNewTransientNotifications(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'Notifications' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'Notifications' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.Notification obj = dod.getRandomNotifications();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'Notifications' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.Notification.findNotifications(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'Notifications' with identifier '" + id + "'", eu.dime.ps.storage.entities.Notification.findNotifications(id));
    }
}
