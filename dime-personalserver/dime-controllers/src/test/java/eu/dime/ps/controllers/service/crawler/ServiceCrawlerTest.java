package eu.dime.ps.controllers.service.crawler;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.datamining.service.AbstractCrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.datamining.service.UniqueCrawlerConstraint;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.service.external.oauth.LinkedInServiceAdapter;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.nco.PersonContact;

/**
 *
 * @author Will Fleury
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/transformer-tests-context.xml")
public class ServiceCrawlerTest extends Assert {

    private static Logger logger = LoggerFactory.getLogger(ServiceCrawlerTest.class);
    
    @Autowired
    protected ServiceGateway gateway;
    
    @Autowired
    protected AccountManager accountManager;
    
    @Autowired
    protected ServiceCrawlerRegistry registry;  
    
    @Autowired
    protected CredentialStore credentialStore;
    
    
    @BeforeClass
    public static void setUpClass() throws Exception {    }
    
    @Before
    public void setUp() throws Exception {  }

    @After
    public void tearDown() throws Exception {  }
    
    /**
     * Can only be unignored when credentials are stored.. 
     */
    @Ignore
    @Test
    public void testLinkedInCrawl() throws Exception {
// FIXME constructor has changed...        LinkedInServiceAdapter adapter = new LinkedInServiceAdapter(accountManager, credentialStore);
        LinkedInServiceAdapter adapter = new LinkedInServiceAdapter();
        //Hack for the moment as its not possible to automate tests at the moment
        //even with credentials stored in services.properties with the current
        //api design of the service adapters/gateway.
        adapter.setIdentifer("urn:account:linkedin");
        String serviceIdentifier = adapter.getIdentifier();
        
        // FIXME why this method doesn't exist anymore? 
//        adapter.restoreAuthToken();

//        gateway.setServiceAdapter(adapter);   
        
        assertTrue(registry.getAvailableServices().contains("LinkedIn"));
        
        //If you want to test memory consumption or long term crawler stuff just
        //increase this count to whatever..
        final CountDownLatch doneSignal = new CountDownLatch(1);
        
        DummyCrawlerHandler handler = new DummyCrawlerHandler(doneSignal);
        
        PathDescriptor personPath = new PathDescriptor("/persons/@me/@all", PersonContact.class);
        PathDescriptor profilePath = new PathDescriptor("/profiles/@me/@all", PersonContact.class);
        PathDescriptor livepostPath = new PathDescriptor("/livepost/@me/@all", Status.class);
        
        ServiceCrawler crawler = null;//registry.add(serviceIdentifier, "*/15 * * * * *", 
//                new CrawlerHandler[] {handler} , 
//                new PathDescriptor[] {profilePath, personPath, livepostPath});
        
        boolean completed = true; doneSignal.await();//(10, TimeUnit.SECONDS);
        
        //these two checks are the same but check anyway
        assertTrue(completed);
        assertEquals(0, doneSignal.getCount());
        
        assertTrue(handler.error == null);
        
        assertTrue(handler.resources != null);
        assertTrue(!handler.resources.get(personPath).isEmpty());
        assertTrue(!handler.resources.get(profilePath).isEmpty());
        
        //My liveposts are empty so...
        //assertTrue(!handler.resources.get(personPath).isEmpty());
        
        registry.remove(new UniqueCrawlerConstraint(crawler));
    }
    
    class DummyCrawlerHandler extends AbstractCrawlerHandler {

        private CountDownLatch doneSignal;
        public volatile Throwable error;
        public volatile Map<PathDescriptor, Collection<? extends Resource>> resources;

        public DummyCrawlerHandler(CountDownLatch doneSignal) {
            this.doneSignal = doneSignal;
        }

        @Override
        public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
            this.resources = resources;
            doneSignal.countDown();
        }

        @Override
        public void onError(Throwable error) {
            logger.error("Error running crawl..", error);
            this.error = error;
            doneSignal.countDown();
        }
    }

}
