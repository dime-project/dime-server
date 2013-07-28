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

package eu.dime.ps.datamining.service.api.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.datamining.service.UniqueCrawlerConstraint;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 *
 * @author Will Fleury
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/datamining-tests-context.xml")
public class ServiceCrawlerRegistryImplTestCase extends Assert{
    
    @Autowired
    protected ServiceCrawlerRegistry registry;

    @Autowired
    protected ServiceGateway gateway;
    
	@Autowired
	private EntityFactory entityFactory;

    protected String accountIdentifier = "urn:mock-service-adapter";
    
	protected PathDescriptor dummyPath = new PathDescriptor("/dummypath", Resource.class);
 
    private static final String TENANT_ID = "12345";
	private Tenant tenant;
    
    @BeforeClass
    public static void setUpClass() throws Exception {    }
    
    @Before
    public void setUp() throws Exception {

		// create dummy tenant for tests
		tenant = Tenant.findByName(TENANT_ID);
		if (tenant == null) {
			tenant = entityFactory.buildTenant();
			tenant.setName(TENANT_ID);
			tenant.persist();
		}
    	
    	//reset mock object
        reset(gateway);
        
        //remove any cross test contamination
        registry.removeAll();
        
        //setup the serviceGateway mock to return the list of available adapter
        //identifiers
        final Map<String, ServiceMetadata> supportedAdapters = new HashMap<String, ServiceMetadata>();
        supportedAdapters.put(accountIdentifier, null);
        expect(gateway.listSupportedAdapters()).andReturn(supportedAdapters).anyTimes();
        
        //now setup a mock ServiceAdapter to return the name (for the moment
        //-we will have sample data here once the methods are known better)
        ServiceAdapter adapter = createNiceMock(ServiceAdapter.class);
        expect(adapter.getIdentifier()).andReturn(accountIdentifier).anyTimes();
        
        expect(adapter.get(dummyPath.getPath(), dummyPath.getReturnType())).andReturn(null).anyTimes();
        
        replay(adapter);
        
        //now setup the gateway to return the adapter bridge mock object
        expect(gateway.getServiceAdapter(accountIdentifier)).andReturn(adapter).anyTimes();
        
        // Setup is finished need to activate the mock
        replay(gateway);
    }

    @After
    public void tearDown() throws Exception {
        //verify all mock objects were called as expected.. (note don't really
        //need this in this test case as we're not specifiying how many times
        //the methods should be called (i.e. using anyTimes()..)
        verify(gateway);

		// remove test tenant
		try {
			tenant = Tenant.findByName(TENANT_ID);
			if (tenant != null)
				tenant.remove();
		} catch (Exception e) {}
    }

    @Test
    public void testGetAvailableServices() throws Exception {
        Collection<String> results = registry.getAvailableServices();
        
        assertTrue(!results.isEmpty());
        assertEquals(accountIdentifier, results.iterator().next());
    }
    
    @Test
    public void testSetDefaults() throws Exception {
        MockCrawlerHandler defaultHandler = new MockCrawlerHandler();
        
        registry.setDefaultHandler(defaultHandler);
        
        //schedule cron task to execute every minute.
        String cronSchedule = "1 * * * * *";
        
        ServiceCrawler service = registry.add(tenant, accountIdentifier, cronSchedule, dummyPath);
        
        assertEquals(1, service.getHandlers().size());
        assertEquals(defaultHandler, service.getHandlers().iterator().next());
        
        registry.remove(new UniqueCrawlerConstraint(service));
    }
    
    @Test
    public void testAddRemoveCrawler() throws Exception {        
        //create a simple service and check if its started.
        //schedule cron task to execute every minute.
        String cronSchedule = "1 * * * * *";
        ServiceCrawler crawler = registry.add(tenant, accountIdentifier, cronSchedule, dummyPath);
        
        UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(crawler);
        
        assertEquals(accountIdentifier, crawler.getAccountIdentifier());
        
        assertTrue(registry.isServiceCrawling(key));
        
        assertEquals(1, registry.getActiveCrawlers().size());
        
        //now remove it and check if its removed..
        registry.remove(key);
        
        assertTrue(!registry.isServiceCrawling(key));
        
        
        //test bad cron format
        try {
            registry.add(tenant, accountIdentifier, "* * * * ", dummyPath);
            
            fail("Didn't throw IllegalArgumentException for bad cron format");
        } catch (IllegalArgumentException e) { }
        
        // FIXME the mock will return null for the adapter, not an exception...
//        //test unknown service identifier
//        try {
//            registry.add(tenant, "unknown:serviceIdentifier", cronSchedule);
//            
//            fail("Didn't throw IllegalArgumentException for unknown service identifier");
//        } catch (IllegalArgumentException e) { }
//
//        //test bad format service identifier
//        try {
//            registry.add(tenant, "badformatidentifier", cronSchedule);
//            
//            fail("Didn't throw IllegalArgumentException for unknown service identifier");
//        } catch (IllegalArgumentException e) { }
    }
    
    @Test
    public void testSuspendResumeCrawler() throws Exception {
        //setup with simple service add
        String cronSchedule = "1 * * * * *";
        ServiceCrawler crawler = registry.add(tenant, accountIdentifier, cronSchedule, dummyPath);
        
        UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(crawler);
        
        //make sure its running
        assertTrue(registry.isServiceCrawling(key));
        
        //pause it and check if its still running
        registry.suspend(key);
        
        assertTrue(!registry.isServiceCrawling(key));
        
        
        //test that it throws an exception when trying to suspend a service
        //if its not running 
        try {
            registry.suspend(key);
            
            fail("Didn't throw IllegalArgumentException when suspend called for non running service");
        } catch (IllegalArgumentException e) { }
        
        
        //resume it and make sure its still running.
        registry.resume(key);
        
        assertTrue(registry.isServiceCrawling(key));
        
        //test that it throws an exception when trying to resume it if its
        //not suspended
        try {
            registry.resume(key);
            
            fail("Didn't throw IllegalArgumentException when resume() called for non suspended service");
        } catch (IllegalArgumentException e) { }

        
        //now test suspend all / resume all
        registry.suspendActive();
        
        assertTrue(registry.getActiveCrawlers().isEmpty());
        
        registry.resumeSuspended();
        
        assertEquals(1, registry.getActiveCrawlers().size());
        
        //cleanup with removal
        registry.remove(key);
    }
   
    @Test
    public void testHandlerCalled() throws Exception {
        //schedule cron task to execute every second.
        String cronSchedule = "*/1 * * * * * ";
        
        //number of executions we want to wait for..
        final int executionCount = 2;
        
        //this is the best way to test for threading / task execution.
        final CountDownLatch doneSignal = new CountDownLatch(executionCount);
        
        PathDescriptor path = new PathDescriptor("/person/@me/@all", Person.class);
        
        //create a handler which decrements the counter when it is called 
        //we dont care which method is called or its value, only that its called!
        CrawlerHandler handler = new CrawlerHandler() {
            @Override
            public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
                doneSignal.countDown();
            }

            @Override
            public void onError(Throwable error) {
                doneSignal.countDown();
            }
        };
        
        ServiceCrawler crawler = registry.add(tenant, accountIdentifier, cronSchedule, path, new CrawlerHandler[]{ handler });
        
        //make sure it executes the correct number of times but also make 
        //sure that this doesn't hang the tests if it doesn't. So todo this we
        //have a timeout for the waiting.. 
        //Note may need to increase this if the test setup times /pc is slow
        boolean completed = doneSignal.await(5, TimeUnit.SECONDS);
        
        //these two checks are the same but check anyway
        assertTrue(completed);
        assertEquals(0, doneSignal.getCount());
        
        registry.remove(new UniqueCrawlerConstraint(crawler));
    }   
}
