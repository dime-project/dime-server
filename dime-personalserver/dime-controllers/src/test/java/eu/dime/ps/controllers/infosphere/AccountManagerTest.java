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

package eu.dime.ps.controllers.infosphere;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManagerImpl;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Tests {@link AccountManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class AccountManagerTest extends InfoSphereManagerTest {

	@Autowired
	private AccountManagerImpl accountManager;
	
	private Long tenantId;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		Tenant tenant = Tenant.findByName("___test");
		if (tenant == null) {
			tenant = EntityFactory.getInstance().buildTenant();
			tenant.setName("___test");
			tenant = tenant.merge();
		}
		tenantId = tenant.getId();
		TenantContextHolder.setTenant(tenantId);
		
		// mocking Crawler & Gateway components
		ServiceCrawlerRegistry serviceCrawlerRegistry = mock(ServiceCrawlerRegistry.class);
		doAnswer(new Answer<Void>() {
	        public Void answer(InvocationOnMock invocation) {
	        	// do nothing
	            return null;
	        }
	    }).when(serviceCrawlerRegistry).remove(anyString());
		accountManager.setServiceCrawlerRegistry(serviceCrawlerRegistry);

		ServiceGateway gateway = mock(ServiceGateway.class);
		doAnswer(new Answer<Void>() {
	        public Void answer(InvocationOnMock invocation) {
	        	// do nothing
	            return null;
	        }
	    }).when(gateway).unsetServiceAdapter(anyString());
		accountManager.setServiceGateway(gateway);
	}

	@After
	public void tearDown() throws Exception {
		if (tenantId != null) {
			Tenant tenant = Tenant.find(tenantId);
			try {
				for (ServiceAccount account : ServiceAccount.findAllByTenant(tenant))
					account.remove();
				tenant.remove();
			} catch (Exception e) {}
		}
		TenantContextHolder.clear();
		super.tearDown();
	}

	@Test
	public void testExist() throws Exception {
		Account account = buildAccount("facebook", "Facebook");
		accountManager.add(account);
		assertTrue(accountManager.exist(account.toString()));
	}

	@Test
	public void testGet() throws Exception {
		Account account = buildAccount("facebook", "Facebook");
		accountManager.add(account);
		Account another = accountManager.get(account.asResource().toString());
		assertEquals(account, another);
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		accountManager.get("urn:12345");
	}

	@Test
	public void testGetAll() throws Exception {
		Account twitter = buildAccount("twitter", "Twitter");
		Account ym = buildAccount("yellowmap", "YellowMap");
		accountManager.add(twitter);
		accountManager.add(ym);
		assertEquals(2, accountManager.getAll().size());
	}
	
	public void testGetAllByPerson() throws Exception {
		Account twitter = buildAccount("twitter", "Twitter");
		accountManager.add(twitter);
		Account ym = buildAccount("yellowmap", "YellowMap");
		accountManager.add(ym);
		assertEquals(1, accountManager.getAllByCreator(pimoService.getUser()).size());
	}
	
	@Test
	public void testGetAllByType() throws Exception {
		Account account = buildAccount("di.me", "di.me");
		accountManager.add(account);
		Collection<Account> accounts = accountManager.getAllByType("di.me");
		assertEquals(1, accounts.size());
		assertTrue(accounts.contains(account));
	}

	@Test
	public void testAdd() throws Exception {
		Account account = buildAccount("facebook", "Facebook");
		accountManager.add(account);
		Collection<Account> accounts = accountManager.getAll();
		assertEquals(1, accounts.size());
		assertTrue(accounts.contains(account));
	}

	@Test
	public void testUpdate() throws Exception {
		Account account = buildAccount("linkedin", "LinkedIn");
		accountManager.add(account);
		
		account.setPrefLabel("LinkedIn");
		accountManager.update(account);
		
		Account galaxy = accountManager.get(account.asResource().toString());
		assertEquals("LinkedIn", galaxy.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		Account account1 = buildAccount("facebook", "Facebook");
		Account account2 = buildAccount("twitter", "Twitter");
		accountManager.add(account1);
		accountManager.add(account2);
		accountManager.remove(account1.asResource().toString());
		assertEquals(1, accountManager.getAll().size());
	}

}
