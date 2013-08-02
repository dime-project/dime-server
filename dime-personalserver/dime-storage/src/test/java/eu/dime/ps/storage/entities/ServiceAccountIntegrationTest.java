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

import org.junit.Ignore;
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
@RooIntegrationTest(entity = ServiceAccount.class)
public class ServiceAccountIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private ServiceAccountDataOnDemand dod;

	@Test
    public void testCountServiceAccounts() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", dod.getRandomServiceAccount());
        long count = eu.dime.ps.storage.entities.ServiceAccount.count();
        org.junit.Assert.assertTrue("Counter for 'ServiceAccount' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindServiceAccount() {
        eu.dime.ps.storage.entities.ServiceAccount obj = dod.getRandomServiceAccount();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceAccount.find(id);
        org.junit.Assert.assertNotNull("Find method for 'ServiceAccount' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'ServiceAccount' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllServiceAccounts() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", dod.getRandomServiceAccount());
        long count = eu.dime.ps.storage.entities.ServiceAccount.count();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'ServiceAccount', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<eu.dime.ps.storage.entities.ServiceAccount> result = eu.dime.ps.storage.entities.ServiceAccount.findAll();
        org.junit.Assert.assertNotNull("Find all method for 'ServiceAccount' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'ServiceAccount' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindServiceAccountEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", dod.getRandomServiceAccount());
        long count = eu.dime.ps.storage.entities.ServiceAccount.count();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.ServiceAccount> result = eu.dime.ps.storage.entities.ServiceAccount.find(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'ServiceAccount' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'ServiceAccount' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.ServiceAccount obj = dod.getRandomServiceAccount();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceAccount.find(id);
        org.junit.Assert.assertNotNull("Find method for 'ServiceAccount' illegally returned null for id '" + id + "'", obj);
        obj.flush();
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.ServiceAccount obj = dod.getRandomServiceAccount();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceAccount.find(id);
        eu.dime.ps.storage.entities.ServiceAccount merged = (eu.dime.ps.storage.entities.ServiceAccount) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", dod.getRandomServiceAccount());
        eu.dime.ps.storage.entities.ServiceAccount obj = dod.getNewTransientServiceAccount(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'ServiceAccount' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'ServiceAccount' identifier to no longer be null", obj.getId());
    }

	
	@Ignore
	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.ServiceAccount obj = dod.getRandomServiceAccount();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'ServiceAccount' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.ServiceAccount.find(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'ServiceAccount' with identifier '" + id + "'", eu.dime.ps.storage.entities.ServiceAccount.find(id));
    }
	
	
	@Test
    public void testNullNames() {
		eu.dime.ps.storage.entities.ServiceAccount sa1 = dod.getNewTransientServiceAccount(Integer.MAX_VALUE+1);
		eu.dime.ps.storage.entities.ServiceAccount sa2 = dod.getNewTransientServiceAccount(Integer.MAX_VALUE);
		sa1.setName(null);
		sa2.setName(null);	
		sa1.persist();		
		sa1.flush();
		sa2.persist();
		sa2.flush();
		org.junit.Assert.assertNotNull("Expected 'ServiceAccount' identifier to no longer be null", sa1.getId());
		 org.junit.Assert.assertNotNull("Expected 'ServiceAccount' identifier to no longer be null", sa2.getId());
		 org.junit.Assert.assertNull(sa1.getName());
		 org.junit.Assert.assertNull(sa2.getName());	 
		 
		 
	}
	
	
}
