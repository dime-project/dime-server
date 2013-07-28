package eu.dime.ps.datamining.service.api.impl;

import eu.dime.ps.datamining.service.AbstractCrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import java.util.Collection;
import java.util.Map;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a mock implementation of a CrawlerHandler which simply informs of 
 * the call via the logger and keeps note of whether the service methods were
 * called at least once or not.
 * 
 * 
 * @author Will Fleury
 */
public class MockCrawlerHandler extends AbstractCrawlerHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MockCrawlerHandler.class);
    
    /*
     * These variables are used for testing to ensure that each of the service
     * methods were called at least once..
     */
    private volatile boolean onResultCalled = false;
    
    private volatile boolean onErrorCalled = false;

    /*
     * (non-Javadoc)
     *
     * @see CrawlerHandler#onResult(Collection)
     */
    @Override
    public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {    
        //for (PathDescriptor descriptor : resources.keySet()) {
        //    if (descriptor.getReturnType().equals(Contact.class)) {
        //        Collection<Contact> contacts = extractTypedCollection(
        //                resources, descriptor, Contact.class);
        //        
        //        ....
        //    }
        //}
        
        onResultCalled = true;
        logger.info("received an CrawlerHandler.onResult() call");
    }

    
    /*
     * (non-Javadoc)
     *
     * @see CrawlerHandler#onError(Throwable)
     */
    @Override
    public void onError(Throwable error) {
        onErrorCalled = true;
        //expect null as its a test object..
        logger.error(error != null ? error.getMessage() : "Throwable was null");
    }

    
    public boolean wasOnErrorCalled() {
        return onErrorCalled;
    }

    public boolean wasOnResultCalled() {
        return onResultCalled;
    }
   
}
