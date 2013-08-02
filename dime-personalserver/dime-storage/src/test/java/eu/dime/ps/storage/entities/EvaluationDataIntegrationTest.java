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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/ps-db-applicationContext.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = EvaluationData.class, findAll=false)
public class EvaluationDataIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private EvaluationDataDataOnDemand dod;

	@Test
    public void testCountEvaluationDatas() {
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", dod.getRandomEvaluationData());
        long count = eu.dime.ps.storage.entities.EvaluationData.countEvaluationDatas();
        org.junit.Assert.assertTrue("Counter for 'EvaluationData' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindEvaluationData() {
        eu.dime.ps.storage.entities.EvaluationData obj = dod.getRandomEvaluationData();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.EvaluationData.findEvaluationData(id);
        org.junit.Assert.assertNotNull("Find method for 'EvaluationData' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'EvaluationData' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindEvaluationDataEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", dod.getRandomEvaluationData());
        long count = eu.dime.ps.storage.entities.EvaluationData.countEvaluationDatas();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.EvaluationData> result = eu.dime.ps.storage.entities.EvaluationData.findEvaluationDataEntries(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'EvaluationData' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'EvaluationData' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.EvaluationData obj = dod.getRandomEvaluationData();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.EvaluationData.findEvaluationData(id);
        org.junit.Assert.assertNotNull("Find method for 'EvaluationData' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyEvaluationData(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        obj.flush();
        org.junit.Assert.assertTrue("Version for 'EvaluationData' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.EvaluationData obj = dod.getRandomEvaluationData();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.EvaluationData.findEvaluationData(id);
        boolean modified =  dod.modifyEvaluationData(obj);
        java.lang.Integer currentVersion = obj.getVersion();
        eu.dime.ps.storage.entities.EvaluationData merged = (eu.dime.ps.storage.entities.EvaluationData) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        org.junit.Assert.assertTrue("Version for 'EvaluationData' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", dod.getRandomEvaluationData());
        eu.dime.ps.storage.entities.EvaluationData obj = dod.getNewTransientEvaluationData(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'EvaluationData' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'EvaluationData' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.EvaluationData obj = dod.getRandomEvaluationData();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'EvaluationData' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.EvaluationData.findEvaluationData(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'EvaluationData' with identifier '" + id + "'", eu.dime.ps.storage.entities.EvaluationData.findEvaluationData(id));
    }
}
