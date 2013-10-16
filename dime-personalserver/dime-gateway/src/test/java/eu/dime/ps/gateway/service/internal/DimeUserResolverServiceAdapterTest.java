package eu.dime.ps.gateway.service.internal;

import static org.junit.Assert.*;
import net.sf.json.JSONArray;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@TransactionConfiguration(defaultRollback = true)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/storage-loading-tests-context.xml")
@Ignore // test ignored because requires external urs service
public class DimeUserResolverServiceAdapterTest{
  
	   @Autowired
	   DimeUserResolverServiceAdapter dimeURSAdapter;
	   
	   @Autowired
	   EntityFactory entityFactory;
	   
	   Tenant tenant;

		@Before
		public void setupTenant() {
			tenant = entityFactory.buildTenant();
			tenant.setName("juan");
			tenant.persist();
			tenant.flush();
		}
		
		@After
		public void clearTenant() {
			tenant.remove();
			tenant.flush();
		}
	
	@Test
	public void testRegisterAndDelete()throws Exception{
		DimeUserResolverServiceAdapter ursAdapter = new DimeUserResolverServiceAdapter();
		String random = RandomStringUtils.randomAlphanumeric(5);
		String said = "mysaid"+random;
		String nick = "nick"+random;
		String firstname = "name"+random;
		String surname = "surname"+random;
		
		ursAdapter.setTenant(tenant);
		ursAdapter.registerAtURS(said, nick, firstname, surname);
		JSONArray response = ursAdapter.search(nick);
		assertNotNull(response);
		assertTrue(response.size() == 1);
		ursAdapter._delete(said);
		response = ursAdapter.search(nick);
		assertTrue(response.size() == 0);
	}
	
	@Test
	public void testDelete() throws Exception{
		DimeUserResolverServiceAdapter ursAdapter = new DimeUserResolverServiceAdapter();
		ursAdapter.setTenant(tenant);
		
	}
}
