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

package eu.dime.ps.datamining.service;

import java.util.Set;

/**
 * 
 * Implementing classes should take care of concurrency and thread safety issues.
 * In particular given that access is provided to the handlers and paths via
 * get methods these should be backed by classes in the java.util.concurrency 
 * package to avoid {@see ConcurrentModificationExceptions} etc.
 * 
 * @author Will Fleury
 */
public interface ServiceCrawler extends Runnable {
    
    /**
     * TODO: Note this is only temporary. Will fix pattern soon. 
     * Need a standard way for all Crawler Implementations to notify the 
     * registry if the handlers are modified. Will more than likely just make
     * the handlers immutable for simplicity.
     */
    public static interface HandlersChangedListener {
        public void handlersChanged(Set<CrawlerHandler> handlers);
    }
    
    /**
     * Returns the tenant identifier where this crawler belongs to.
     * 
     * @return the tenant identifier
     */
    public Long getTenant();
    
    /**
     * Returns the account identifier used by this crawler.
     * 
     * @return the account identifier
     */
    public String getAccountIdentifier();
    
    /**
     * Sets the cron schedule for the crawler
     * 
     * @param cronSchedule the new cron schedule
     */
    public void setCronSchedule(String cronSchedule);
    
    /**
     * Returns the crawler's cron schedule
     * 
     * @return the cron schedule
     */
    public String getCronSchedule();
    
    /**
     * Retrieves the crawler's service path.
     * 
     * @return the service path
     */
    public PathDescriptor getPath();    
    
    /**
     * Add a {@see CrawlerHandler} to the list of registered handlers.
     * This will be called each time the service is crawled. This method does
     * nothing if the handler is already registered.
     * 
     * @param handler the new handler to add
     */
    public void addHandler(CrawlerHandler handler);
    
    /**
     * Remove a {@see CrawlerHandler} from the list of registered handlers.
     * This method does nothing if the handler is not already registered.
     * 
     * @param handler the handler to remove
     */
    public void removeHandler(CrawlerHandler handler);
    
    /**
     * Retrieves the current list of registered handlers as an unmodifiable 
     * set using Collections.unmodifiableSet(..)
     * 
     * @return the handlers..
     */
    public Set<CrawlerHandler> getHandlers();
    
    /**
     * Removed access to the futures because this could allow canceling the 
     * task from somewhere other than the registry which would leave the 
     * registry inconsistent.
     */
    //public void setScheduledFuture(ScheduledFuture scheduledFuture);
    //public ScheduledFuture getScheduledFuture();
    
}
