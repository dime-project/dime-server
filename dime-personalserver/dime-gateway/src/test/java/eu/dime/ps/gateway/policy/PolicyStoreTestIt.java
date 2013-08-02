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

package eu.dime.ps.gateway.policy;

import static junit.framework.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.UserDefaults;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * 
 * @author marcel
 *
 */
@TransactionConfiguration(defaultRollback = true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/storage-loading-tests-context.xml")
public class PolicyStoreTestIt {
	
	@Autowired
	private PolicyStoreImpl policyStore;
	
	@Autowired
	private EntityFactory entityFactory;
	
	
	private void createTenant(){
		Tenant tenant = entityFactory.buildTenant();
		tenant.setName("test-dummy");
		tenant.persist();
		tenant.flush();
	}
	
	@Test
	@Transactional
	public void testStore(){
		createTenant();

		Tenant t = Tenant.findByName("test-dummy");
		
		policyStore.storeOrUpdate("test", "12345", t.getId());
		UserDefaults ud = UserDefaults.findAllByTenantAndName(t, "test");
		assertNotNull(ud);
		assertEquals("test", ud.getName());
		assertEquals("12345", ud.getValue());
	}
	
	@Test
	@Transactional
	public void testStoreGlobal(){
		
		policyStore.storeOrUpdate("test", "12345");
		UserDefaults ud = UserDefaults.findAllByNameAndAppliesTo("test", "GLOBAL");
		assertNotNull(ud);
		assertEquals("test", ud.getName());
		assertEquals("12345", ud.getValue());
	}
	
	@Test
	@Transactional
	public void testGet(){
		createTenant();
		Tenant t = Tenant.findByName("test-dummy");
		
		UserDefaults ud = entityFactory.buildUserDefaults();
		ud.setName("test");
		ud.setValue("12345");
		ud.setTenant(t);
		ud.persist();
		ud.flush();
		
		assertEquals("12345", policyStore.getValue("test", t.getId()));
		
	}
	
	@Test
	@Transactional
	public void testGetGlobal(){		
		UserDefaults ud = entityFactory.buildUserDefaults();
		ud.setName("test-global");
		ud.setValue("12345");
		ud.setAppliesTo("GLOBAL");
		ud.persist();
		ud.flush();
		
		assertEquals("12345", policyStore.getValue("test-global"));
		
	}
}
