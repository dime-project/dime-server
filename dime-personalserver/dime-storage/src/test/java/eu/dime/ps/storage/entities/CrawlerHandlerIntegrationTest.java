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

package eu.dime.ps.storage.entities;

import org.junit.Test;
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
@RooIntegrationTest(entity = CrawlerHandler.class, findAll=false)
public class CrawlerHandlerIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private CrawlerHandlerDataOnDemand dod;

	@Test
    public void testCountCrawlerHandlers() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", dod.getRandomCrawlerHandler());
        long count = eu.dime.ps.storage.entities.CrawlerHandler.countCrawlerHandlers();
        org.junit.Assert.assertTrue("Counter for 'CrawlerHandler' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindCrawlerHandler() {
        eu.dime.ps.storage.entities.CrawlerHandler obj = dod.getRandomCrawlerHandler();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandler(id);
        org.junit.Assert.assertNotNull("Find method for 'CrawlerHandler' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'CrawlerHandler' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindCrawlerHandlerEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", dod.getRandomCrawlerHandler());
        long count = eu.dime.ps.storage.entities.CrawlerHandler.countCrawlerHandlers();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.CrawlerHandler> result = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandlerEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'CrawlerHandler' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'CrawlerHandler' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.CrawlerHandler obj = dod.getRandomCrawlerHandler();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandler(id);
        org.junit.Assert.assertNotNull("Find method for 'CrawlerHandler' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyCrawlerHandler(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'CrawlerHandler' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.CrawlerHandler obj = dod.getRandomCrawlerHandler();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandler(id);
        boolean modified =  dod.modifyCrawlerHandler(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        eu.dime.ps.storage.entities.CrawlerHandler merged = (eu.dime.ps.storage.entities.CrawlerHandler) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'CrawlerHandler' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", dod.getRandomCrawlerHandler());
        eu.dime.ps.storage.entities.CrawlerHandler obj = dod.getNewTransientCrawlerHandler(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'CrawlerHandler' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'CrawlerHandler' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.CrawlerHandler obj = dod.getRandomCrawlerHandler();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'CrawlerHandler' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandler(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'CrawlerHandler' with identifier '" + id + "'", eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandler(id));
    }
}
