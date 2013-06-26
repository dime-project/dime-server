package eu.dime.ps.storage.entities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/ps-db-applicationContext.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = CrawlerJob.class, findAll=false)
public class CrawlerJobIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private CrawlerJobDataOnDemand dod;

	@Test
    public void testCountCrawlerJobs() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", dod.getRandomCrawlerJob());
        long count = eu.dime.ps.storage.entities.CrawlerJob.count();
        org.junit.Assert.assertTrue("Counter for 'CrawlerJob' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindCrawlerJob() {
        eu.dime.ps.storage.entities.CrawlerJob obj = dod.getRandomCrawlerJob();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerJob.find(id);
        org.junit.Assert.assertNotNull("Find method for 'CrawlerJob' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'CrawlerJob' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindCrawlerJobEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", dod.getRandomCrawlerJob());
        long count = eu.dime.ps.storage.entities.CrawlerJob.count();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.CrawlerJob> result = eu.dime.ps.storage.entities.CrawlerJob.find(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'CrawlerJob' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'CrawlerJob' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.CrawlerJob obj = dod.getRandomCrawlerJob();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerJob.find(id);
        org.junit.Assert.assertNotNull("Find method for 'CrawlerJob' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyCrawlerJob(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'CrawlerJob' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.CrawlerJob obj = dod.getRandomCrawlerJob();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerJob.find(id);
        boolean modified =  dod.modifyCrawlerJob(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        eu.dime.ps.storage.entities.CrawlerJob merged = (eu.dime.ps.storage.entities.CrawlerJob) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'CrawlerJob' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", dod.getRandomCrawlerJob());
        eu.dime.ps.storage.entities.CrawlerJob obj = dod.getNewTransientCrawlerJob(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'CrawlerJob' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'CrawlerJob' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.CrawlerJob obj = dod.getRandomCrawlerJob();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerJob' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerJob.find(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'CrawlerJob' with identifier '" + id + "'", eu.dime.ps.storage.entities.CrawlerJob.find(id));
    }
}
