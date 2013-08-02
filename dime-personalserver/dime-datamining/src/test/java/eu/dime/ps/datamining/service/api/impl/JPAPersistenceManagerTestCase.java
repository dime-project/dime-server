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

package eu.dime.ps.datamining.service.api.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.datamining.service.UniqueCrawlerConstraint;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.storage.entities.CrawlerJob;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * 
 * @author Will Fleury
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-config/datamining-tests-context.xml" })
public class JPAPersistenceManagerTestCase extends AbstractJUnit4SpringContextTests {

	@Autowired
	protected ServiceCrawlerRegistry registry;

	@Autowired
	protected TaskScheduler scheduler;

	@Autowired
	protected ServiceGateway gateway;

	@Autowired
	protected JPAPersistenceManager persistence;
	
	@Autowired
	protected Connection connection;

	@Autowired
	protected ConnectionProvider connectionProvider;

	@Autowired
	private EntityFactory entityFactory;

	protected String accountIdentifier = "urn:mock-service-adapter";

	protected PathDescriptor dummyPath = new PathDescriptor("/dummypath", Resource.class);

	protected String cron = "* 1 * * * *";
	
	private Tenant tenant;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		// create dummy tenant for tests
		tenant = Tenant.findByName(connection.getName());
		if (tenant == null) {
			tenant = entityFactory.buildTenant();
			tenant.setName(connection.getName());
			tenant.persist();
		}
		
		// set up connection provider to always return the same connection '12345'
		reset(connectionProvider);
		expect(connectionProvider.getConnection(tenant.getId().toString())).andReturn(connection).anyTimes();
		replay(connectionProvider);
		
		// Load DB context
		persistence.setEntityFactory(entityFactory);
		persistence.setConnectionProvider(connectionProvider);

		// empty the persistence store so that we have a clean test
		persistence.crawlersCleared();

		// set the persistence guy
		registry.setPersitenceManager(persistence);

		// reset mock object
		reset(gateway);

		// remove any cross test contamination
		// registry.removeAll();

		// setup the serviceGateway mock to return the list of available adapter
		// identifiers
		final Map<String, ServiceMetadata> supportedAdapters = new HashMap<String, ServiceMetadata>();
		supportedAdapters.put(accountIdentifier, null);
		expect(gateway.listSupportedAdapters()).andReturn(supportedAdapters).anyTimes();

		// now setup a mock ServiceAdapter to return the name (for the moment
		// -we will have sample data here once the methods are known better)
		ServiceAdapter adapter = createNiceMock(ServiceAdapter.class);
		expect(adapter.getIdentifier()).andReturn(accountIdentifier).anyTimes();

		expect(adapter.get(dummyPath.getPath(), dummyPath.getReturnType())).andReturn(null).anyTimes();

		replay(adapter);

		// now setup the gateway to return the adapter bridge mock object
		expect(gateway.getServiceAdapter(accountIdentifier, tenant)).andReturn(adapter).anyTimes();

		// Setup is finished need to activate the mock
		replay(gateway);
	}

	@After
	public void tearDown() throws Exception {
		// verify all mock objects were called as expected.. (note don't really
		// need this in this test case as we're not specifying how many times
		// the methods should be called (i.e. using anyTimes()..)
		verify(gateway);

		// remove crawlers data
		persistence.crawlersCleared();

		// remove test tenant
		try {
			tenant = Tenant.findByName(connection.getName());
			if (tenant != null)
				tenant.remove();
		} catch (Exception e) {}
	}

	@Test
	public void testRestoreRegistryState() throws Exception {
		ServiceCrawler crawler = setupDummyData();

		// must
		ServiceCrawlerRegistry restoredRegistry = new ServiceCrawlerRegistryImpl(scheduler, gateway, persistence);

		// if it contains the service after being created then it must have
		// successfully restored the registry state..
		Collection<UniqueCrawlerConstraint> keys = restoredRegistry.getActiveCrawlerKeys();
		assertEquals(1, keys.size());
		assertTrue(keys.contains(new UniqueCrawlerConstraint(crawler)));
	}

	@Test
	public void testCrawlerAddRemove() throws Exception {

		// Test Add
		ServiceCrawler crawler = registry.add(tenant, accountIdentifier, cron, dummyPath);

		UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(crawler);

		CrawlerJob job = JPAPersistenceManager.getCrawlerJob(tenant, key);

		assertEquals(crawler.getAccountIdentifier(), job.getAccountIdentifier());
		assertEquals(crawler.getPath(), JPAPersistenceManager.constructPathDescriptor(job));

		// Test Remove
		registry.remove(key);

		job = JPAPersistenceManager.getCrawlerJob(tenant, new UniqueCrawlerConstraint(crawler));

		assertEquals(job, null);
	}

	@Test
	public void testCrawlerUpdated() throws Exception {
		// To test updated, lets add and suspend one..
		ServiceCrawler crawler = registry.add(tenant, accountIdentifier, cron, dummyPath);

		UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(crawler);

		registry.suspend(key);

		// check that its suspended in the database
		CrawlerJob dao = JPAPersistenceManager.getCrawlerJob(tenant, key);
		assertTrue(dao.isSuspended());

		registry.remove(key);
	}

	private ServiceCrawler setupDummyData() throws Exception {
		Set<CrawlerHandler> handlers = new HashSet<CrawlerHandler>();
		handlers.add(new MockCrawlerHandler());

		ServiceCrawler crawler = new ServiceCrawlerImpl(tenant.getId(), gateway.getServiceAdapter(accountIdentifier, tenant), cron, dummyPath, handlers);

		persistence.crawlerAdded(crawler);

		return crawler;
	}
	
}
