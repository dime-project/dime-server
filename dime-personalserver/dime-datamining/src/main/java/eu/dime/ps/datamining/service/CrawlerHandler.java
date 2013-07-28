package eu.dime.ps.datamining.service;

import java.util.Collection;

import java.util.Map;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * A class should implement this interface if it wants to register and deal with
 * service crawling results.
 * 
 * @author Will Fleury
 */
public interface CrawlerHandler {
    
    /**
     * This method is called when the crawler successfully returns a result. 
     * Note that this is called after every crawl for the service it is registered
     * with. 
     * 
     * @param resources the resources which were found when crawling
     */
    public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources);
    
    /**
     * This method is called whenever this was an error during the crawling 
     * service..
     * 
     * @param error the cause
     */
    public void onError(Throwable error);
}
