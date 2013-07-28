/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

@Configurable
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/ps-db-applicationContext.xml")
@Transactional
@RooIntegrationTest(entity = User.class, findAll=false)
public class UserIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private UserDataOnDemand dod;
	
	@Autowired
    private TenantDataOnDemand tod;

	@Test
    public void testCountUserCredentials() {
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", dod.getRandomUser());
        long count = eu.dime.ps.storage.entities.User.count();
        org.junit.Assert.assertTrue("Counter for 'UserCredential' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindUserCredential() {
        eu.dime.ps.storage.entities.User obj = dod.getRandomUser();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.User.find(id);
        org.junit.Assert.assertNotNull("Find method for 'UserCredential' illegally returned null for id '" + id + "'", obj);
        org.junit.Assert.assertEquals("Find method for 'UserCredential' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindUserCredentialEntries() {
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", dod.getRandomUser());
        long count = eu.dime.ps.storage.entities.User.count();
        if (count > 20) count = 20;
        java.util.List<eu.dime.ps.storage.entities.User> result = eu.dime.ps.storage.entities.User.find(0, (int) count);
        org.junit.Assert.assertNotNull("Find entries method for 'UserCredential' illegally returned null", result);
        org.junit.Assert.assertEquals("Find entries method for 'UserCredential' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        eu.dime.ps.storage.entities.User obj = dod.getRandomUser();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.User.find(id);
        org.junit.Assert.assertNotNull("Find method for 'UserCredential' illegally returned null for id '" + id + "'", obj);
        obj.flush();
    }

	@Test
    public void testMerge() {
        eu.dime.ps.storage.entities.User obj = dod.getRandomUser();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.User.find(id);
        eu.dime.ps.storage.entities.User merged = (eu.dime.ps.storage.entities.User) obj.merge();
        obj.flush();
        org.junit.Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
    }

	@Test
    public void testPersist() {
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", dod.getRandomUser());
        eu.dime.ps.storage.entities.User obj = dod.getNewTransientUser(Integer.MAX_VALUE);
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to provide a new transient entity", obj);
        org.junit.Assert.assertNull("Expected 'UserCredential' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        org.junit.Assert.assertNotNull("Expected 'UserCredential' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        eu.dime.ps.storage.entities.User obj = dod.getRandomUser();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", obj);
        java.lang.Long id = obj.getId();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to provide an identifier", id);
        obj = eu.dime.ps.storage.entities.User.find(id);
        obj.remove();
        obj.flush();
        org.junit.Assert.assertNull("Failed to remove 'UserCredential' with identifier '" + id + "'", eu.dime.ps.storage.entities.User.find(id));
    }
	
	@Ignore //FIXME: expected exception is never thrown
	@Test(expected=org.springframework.orm.jpa.JpaSystemException.class)
    public void testDuplicate() {
		eu.dime.ps.storage.entities.User u1= dod.getNewTransientUser(Integer.MAX_VALUE+1);
        eu.dime.ps.storage.entities.Tenant tenant = tod.getRandomTenant();
        String username= "test";
        u1.setTenant(tenant);
        u1.setUsername(username); 
        u1.persist();
        u1.flush();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", u1.getId());
        eu.dime.ps.storage.entities.User u2= dod.getNewTransientUser(Integer.MAX_VALUE);
        u2.setTenant(tenant);
        u2.setUsername(username+"a");
        u2.persist();
        u2.flush();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", u2.getId());
      
        eu.dime.ps.storage.entities.User u3= dod.getNewTransientUser(Integer.MAX_VALUE);
        eu.dime.ps.storage.entities.Tenant tenant2 = tod.getRandomTenant();
        u3.setTenant(tenant2);
        u3.setUsername(username);
        u3.persist();
        u3.flush();
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", u3.getId());
      
        
        eu.dime.ps.storage.entities.User u4= dod.getNewTransientUser(Integer.MAX_VALUE);
        
        u4.setTenant(tenant);
        u4.setUsername(username);
        u4.persist();
        u4.flush();        
        org.junit.Assert.assertNotNull("Data on demand for 'UserCredential' failed to initialize correctly", u4.getId());
        
    }
	
	
	
	
}
