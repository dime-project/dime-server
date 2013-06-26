package eu.dime.ps.storage.entities;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/ps-db-applicationContext.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = ServiceProvider.class)
//@TransactionConfiguration(defaultRollback = true)
public class ServiceProviderIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private ServiceProviderDataOnDemand dod;

	@Test
    public void testCountServiceProviders() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", dod.getRandomServiceProvider());
        long count = eu.dime.ps.storage.entities.ServiceProvider.count();
        org.junit.Assert.assertTrue("Counter for 'ServiceProvider' incorrectly reported there were no entries", count > 0);
    }
	

	@Ignore // FIXME error Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'serviceName_2147483647' for key 'serviceName'
	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", dod.getRandomServiceProvider());
        eu.dime.ps.storage.entities.ServiceProvider obj = dod.getNewTransientServiceProvider(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'ServiceProvider' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'ServiceProvider' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testFindServiceProvider() {
        eu.dime.ps.storage.entities.ServiceProvider obj = dod.getRandomServiceProvider();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceProvider.find(id);
        org.junit.Assert.assertNotNull("Find method for 'ServiceProvider' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'ServiceProvider' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllServiceProviders() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", dod.getRandomServiceProvider());
        long count = eu.dime.ps.storage.entities.ServiceProvider.count();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'ServiceProvider', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<eu.dime.ps.storage.entities.ServiceProvider> result = eu.dime.ps.storage.entities.ServiceProvider.findAll();
        org.junit.Assert.assertNotNull("Find all method for 'ServiceProvider' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'ServiceProvider' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindServiceProviderEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", dod.getRandomServiceProvider());
        long count = eu.dime.ps.storage.entities.ServiceProvider.count();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.ServiceProvider> result = eu.dime.ps.storage.entities.ServiceProvider.find(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'ServiceProvider' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'ServiceProvider' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.ServiceProvider obj = dod.getRandomServiceProvider();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceProvider.find(id);
        org.junit.Assert.assertNotNull("Find method for 'ServiceProvider' illegally returned null for id '" + id + "'", obj);
        obj.flush();
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.ServiceProvider obj = dod.getRandomServiceProvider();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceProvider.find(id);
        eu.dime.ps.storage.entities.ServiceProvider merged = (eu.dime.ps.storage.entities.ServiceProvider) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
    }

	@Ignore // FIXME
	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.ServiceProvider obj = dod.getRandomServiceProvider();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceProvider' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceProvider.find(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'ServiceProvider' with identifier '" + id + "'", eu.dime.ps.storage.entities.ServiceProvider.find(id));
    }
}
