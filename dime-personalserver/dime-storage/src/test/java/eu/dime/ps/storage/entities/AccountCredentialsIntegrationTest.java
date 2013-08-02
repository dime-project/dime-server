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
@RooIntegrationTest(entity = AccountCredentials.class)
public class AccountCredentialsIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private AccountCredentialsDataOnDemand dod;
	
	@Autowired
    private TenantDataOnDemand tod;
	
	@Autowired
    private ServiceAccountDataOnDemand sod;

	@Test
    public void testCountAccountCredentials() {
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", dod.getRandomAccountCredentials());
        long count = eu.dime.ps.storage.entities.AccountCredentials.count();
        org.junit.Assert.assertTrue("Counter for 'AccountCredentials' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindAccountCredentials() {
        eu.dime.ps.storage.entities.AccountCredentials obj = dod.getRandomAccountCredentials();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.AccountCredentials.find(id);
        org.junit.Assert.assertNotNull("Find method for 'AccountCredentials' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'AccountCredentials' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllAccountCredentials() {
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", dod.getRandomAccountCredentials());
        long count = eu.dime.ps.storage.entities.AccountCredentials.count();
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'AccountCredentials', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        java.util.List<eu.dime.ps.storage.entities.AccountCredentials> result = eu.dime.ps.storage.entities.AccountCredentials.findAll();
        org.junit.Assert.assertNotNull("Find all method for 'AccountCredentials' illegally returned null", result);
        org.junit.Assert.assertTrue("Find all method for 'AccountCredentials' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindAccountCredentialsEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", dod.getRandomAccountCredentials());
        long count = eu.dime.ps.storage.entities.AccountCredentials.count();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.AccountCredentials> result = eu.dime.ps.storage.entities.AccountCredentials.find(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'AccountCredentials' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'AccountCredentials' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.AccountCredentials obj = dod.getRandomAccountCredentials();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.AccountCredentials.find(id);
        org.junit.Assert.assertNotNull("Find method for 'AccountCredentials' illegally returned null for id '" + id + "'", obj);
        obj.flush();
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.AccountCredentials obj = dod.getRandomAccountCredentials();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.AccountCredentials.find(id);
        eu.dime.ps.storage.entities.AccountCredentials merged = (eu.dime.ps.storage.entities.AccountCredentials) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", dod.getRandomAccountCredentials());
        eu.dime.ps.storage.entities.AccountCredentials obj = dod.getNewTransientAccountCredentials(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'AccountCredentials' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'AccountCredentials' identifier to no longer be null", obj.getId());
    }

	
	@Ignore
	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.AccountCredentials obj = dod.getRandomAccountCredentials();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'AccountCredentials' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.AccountCredentials.find(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'AccountCredentials' with identifier '" + id + "'", eu.dime.ps.storage.entities.AccountCredentials.find(id));
    }
	
	
	@Test(expected=org.springframework.orm.jpa.JpaSystemException.class)
    public void testDuplication() {
        eu.dime.ps.storage.entities.AccountCredentials sa1 = dod.getNewTransientAccountCredentials(Integer.MAX_VALUE+1);

        eu.dime.ps.storage.entities.AccountCredentials sa2 = dod.getNewTransientAccountCredentials(Integer.MAX_VALUE);

	
		eu.dime.ps.storage.entities.Tenant tenant = tod.getRandomTenant();
		eu.dime.ps.storage.entities.ServiceAccount serviceAccount = sod.getRandomServiceAccount();
		sa1.setTenant(tenant);
		sa1.setSource(serviceAccount);
		sa1.setSecret("sa1");
		sa1.setTarget("123");
		sa1.persist();
		sa1.flush();
		
		eu.dime.ps.storage.entities.AccountCredentials sa3 = eu.dime.ps.storage.entities.AccountCredentials.findAllByTenantAndBySourceAndByTargetUri(sa1.getTenant(),sa1.getSource(),sa1.getTargetUri());
		org.junit.Assert.assertNotNull("Expected 'AccountCredentials' identifier to no longer be null", sa3.getId());			
		org.junit.Assert.assertEquals(sa1.getId(), sa3.getId());
		sa2.setTenant(tenant);
		sa2.setSource(serviceAccount);			
		sa2.setTarget("123");
		sa2.setSecret("sa2");
		sa2.persist();
		sa2.flush();		
		
	}
	
	
}
