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
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 *
 * @author Will Fleury
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/datamining-tests-context.xml")
public class ServiceCrawlerImplTestCase extends Assert {
    
    protected ServiceAdapter service;
    protected ServiceCrawlerImpl crawler;
    protected PathDescriptor path;
    protected Set<CrawlerHandler> handlers;
    
    protected Long tenantId = 12345L;
    protected String adapterIdentifier = "MockServiceAdapter";
    protected String cron = "1 * * * * *";
    
    @BeforeClass
    public static void setUpClass() throws Exception {    }
    
    @Before
    public void setUp() throws Exception {
        //now setup a mock ServiceAdapter to return the name (for the moment
        //-we will have sample data here once the methods are known better)
        service = createNiceMock(ServiceAdapter.class);
        expect(service.getIdentifier()).andReturn(adapterIdentifier).anyTimes();
        
        expect(service.get(null, Resource.class)).andReturn(null).anyTimes();
        
        replay(service);
        
        path = new PathDescriptor("/dummypath1", Resource.class);
        
        handlers = new HashSet<CrawlerHandler>();
        handlers.add(new MockCrawlerHandler());
        
        crawler = new ServiceCrawlerImpl(tenantId, service, cron, path, handlers);
    }

    @After
    public void tearDown() throws Exception {
        //verify all mock objects were called as expected.. (note don't really
        //need this in this test case as we're not specifiying how many times
        //the methods should be called (i.e. using anyTimes()..)
        verify(service);
    }
    
    @Test
    public void testGetServiceName() throws Exception {        
        assertEquals(adapterIdentifier, crawler.getAccountIdentifier());
    }
    
    @Test
    public void testGetCronSchedule() throws Exception {
        assertEquals(cron, crawler.getCronSchedule());
    }
    
    @Test
    public void testHandlers() throws Exception {
        Set<CrawlerHandler> emptyHandlers = new HashSet<CrawlerHandler>();
        ServiceCrawlerImpl crawler = new ServiceCrawlerImpl(tenantId, service, cron, path, emptyHandlers);
        
        CrawlerHandler handler = new MockCrawlerHandler();
        crawler.addHandler(handler);
                
        assertEquals(1, crawler.getHandlers().size());
        
        crawler.addHandler(new MockCrawlerHandler());
        assertEquals(2, crawler.getHandlers().size());
        
        crawler.addHandler(handler);
        assertEquals(2, crawler.getHandlers().size());
        
        crawler.removeHandler(handler);
        assertEquals(1, crawler.getHandlers().size());
    }
    
    
    @Test
    public void testFireResult() throws Exception {    
        CrawlerHandler handler = new MockCrawlerHandler();
        crawler.addHandler(handler);
        
        crawler.fireResult(null);
        
        assertTrue(((MockCrawlerHandler)handler).wasOnResultCalled());
    }
    
    @Test
    public void testFireError() throws Exception {
        CrawlerHandler handler = new MockCrawlerHandler();
        crawler.addHandler(handler);
        
        crawler.fireError(new Throwable("testing firing onError.."));
        
        assertTrue(((MockCrawlerHandler)handler).wasOnErrorCalled());
    }
    
    
    @Test
    public void testRun() throws Exception {
        //will test this code once i have it finished - i.e. finished the integration
        //talks.
    }
}
