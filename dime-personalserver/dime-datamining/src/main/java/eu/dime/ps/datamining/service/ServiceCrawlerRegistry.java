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

import java.util.Collection;

import org.openrdf.repository.RepositoryException;

import eu.dime.ps.datamining.exceptions.DataMiningException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.storage.entities.Tenant;

/**
 * The methods of this interface are used in the DataMinning module 
 * to keep track of which Services it is subscribed to. 
 * Note that implementations should communicate with the appropriate service
 * modules (or communications etc) to get the available services etc.. 
 * 
 * @author Will Fleury
 */
public interface ServiceCrawlerRegistry {
    
    /**
     * Returns all of the services which are available to subscribe to in the 
     * system. This is returned from the Communications Module ServiceGateway
     * class.
     * 
     * @return list of available service identifiers
     */
    public Collection<String> getAvailableServices();
    
    /**
     * Returns all of the active ServiceCrawler instances.
     * Note that this is an Unmodifiable Collection and if you try and add/remove
     * from it, an exception will be thrown.
     * {@see java.util.Collections#unmodifiableCollection(Collection) }
     * 
     * @return all active service crawlers 
     */
    public Collection<ServiceCrawler> getActiveCrawlers();   
    
    /**
     * Returns the <code>UniqueCrawlerConstraint</code> for all active crawlers keys
     * 
     * @return a collection of the identifiers
     */
    public Collection<UniqueCrawlerConstraint> getActiveCrawlerKeys();
    
    /**
     * Returns all of the suspended ServiceCrawler instances.
     * Note that this is an Unmodifiable Collection and if you try and add/remove
     * from it, an exception will be thrown.
     * {@see java.util.Collections#unmodifiableCollection(Collection) }
     * 
     * @return all suspended service crawlers 
     */
    public Collection<ServiceCrawler> getSuspendedCrawlers();   
    
    /**
     * Returns the <code>UniqueCrawlerConstraint</code> for all suspended crawlers keys
     * 
     * @return a collection of the identifiers
     */
    public Collection<UniqueCrawlerConstraint> getSuspendedCrawlerKeys();
    
    /**
     * Returns an active {@see ServiceCrawler } instance for the given service 
     * name.
     * 
     * @param key the key representing the service crawler 
     * @return the ServiceCrawler instance or null if there is none.
     */
    public ServiceCrawler getActiveCralwer(UniqueCrawlerConstraint key);
    
    /**
     * Returns a suspended {@see ServiceCrawler } instance for the given service 
     * name.
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler 
     * @return the ServiceCrawler instance or null if there is none.
     */
    public ServiceCrawler getSuspendedCralwer(UniqueCrawlerConstraint key);
    
    /**
     * Checks to see if the given service identifier is crawling or not (i.e.
     * is it active).
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler 
     * @return true if the service is actively crawling, false otherwise
     */
    public boolean isServiceCrawling(UniqueCrawlerConstraint key);
    
    /**
     * Checks to see if the given service identifier is suspended or not (i.e.
     * is it active).
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler 
     * @return true if the service is suspended, false otherwise
     */
    public boolean isServiceSuspended(UniqueCrawlerConstraint key);
    
	/**
	 * It registers the service adapter with the default settings specified in the
	 * crawler configuration files.
	 * 
	 * @param adapter
	 * @throws RepositoryException if can't access data repository for the tenant.
	 * @throws DataMiningException if error setting up datamining services.
	 */
	public void add(Long tenantId, ServiceAdapter adapter, CrawlerHandler[] handlers);

    /**
     * This method is used to start crawling a service. The crawl schedule is
     * specified in cron format for maximum flexibility. 
     * 
     * <br/>
     * For more details on how to specify a schedule using cron format refer to 
     * {@see <a href="http://www.nncron.ru/help/EN/working/cron-format.htm">Cron Format</a>}
     * <br/>
     * 
     * It should add some default handler which simply stores all the
     * retrieved data to the semantic store..? It should also use a default
     * api path such as /people?? Need to see exactly later when the services
     * are complete etc.. If there are no default handlers /path options then the full
     * version of this method should be called and this removed.
     * {@see #add(String, String, CrawlerHandler[], String[])}
     * 
     * @param tenantId the tenant where this crawler lives in
     * @param accountIdentifier the account this crawler is for
     * @param cronSchedule the schedule for the service in cron format.
     * @param path the specific path to crawl
     * @return an instance of the {@see ServiceCrawler} created
     * 
     * @throws IllegalArgumentException if the cron task format is invalid, or 
     * the service is already running or suspended.
     */
    public ServiceCrawler add(Tenant tenant, String accountIdentifier, String cronSchedule, PathDescriptor path);
    
    /**
     * This method is used to start crawling a service. The crawl schedule is
     * specified in cron format for maximum flexibility. Multiple CrawlerHandlers
     * can be specified to deal with the retrieved data and multiple service
     * paths can be specified. The crawler will fire for the first time based on
     * the cron next execution time.
     * 
     * Note: had to change the handlers argument from varargs to array in order
     * to allow the extra argument for paths without changing the order of the
     * method arguments. 
     * 
     * @param tenant the tenant where this crawler lives in
     * @param accountIdentifier the account this crawler is for
     * @param cronSchedule the schedule for the service in cron format.
     * @param path the specific path to crawl
     * @param handlers the specific handlers used to deal with the data retrieved during crawling (@see CrawlerHandler}
     * @return an instance of the {@see ServiceCrawler} created
     * 
     * @throws IllegalArgumentException if the cron schedule format is invalid, or 
     * the service is already running or suspended.
     */
    public ServiceCrawler add(Tenant tenant, String accountIdentifier, String cronSchedule, 
    		PathDescriptor path, CrawlerHandler[] handlers);

    /**
     * Same as {@link add(Tenant, String, String, CrawlerHandler[], PathDescriptor[]} but 
     * the crawler can be set to fire immediately if desired.
     * 
     * @param tenant the tenant where this crawler lives in
     * @param accountIdentifier the account this crawler is for
     * @param cronSchedule the schedule for the service in cron format.
     * @param path the specific path to crawl
     * @param handlers the specific handlers used to deal with the data retrieved during crawling (@see CrawlerHandler}
     * @param fireImmediately if true the crawler will start executing right-away
     * @return an instance of the {@see ServiceCrawler} created
     */
    public ServiceCrawler add(Tenant tenant, String accountIdentifier, String cronSchedule, 
    		PathDescriptor path, CrawlerHandler[] handlers, boolean fireImmediately);
    
	/**
	 * It deactivates and removes all crawlers for a given account.
	 * 
	 * @param accountIdentifier identifier of the account
	 */
	public void remove(String accountIdentifier);

    /**
     * Removes the ServiceCrawler represented by the given service name
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to remove
     * 
     * @throws IllegalArgumentException if no service exists with the given key
     */
    public void remove(UniqueCrawlerConstraint key);
    
    /**
     * Removes all active/suspended service crawlers..
     */
    public void removeAll();

    /**
     * Reschedule a the specified ServiceCrawler. Note that this is equivalent
     * to suspending it, changing the cron schedule and resuming it. 
     * If it was suspended then it is also resumed after the change.
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to reschedule
     * @param cronSchedule the new cron schedule
     * 
     * @throws IllegalArgumentException if the cron schedule format is invalid, or 
     * the service is already running or suspended.
     */
    public void reschedule(UniqueCrawlerConstraint key, String cronSchedule);
    
    /**
     * Suspends all Active ServiceCrawlers
     */
    public void suspendActive();    
    
     /**
     * Resumes all suspended service crawlers
     */
    public void resumeSuspended();
    
    /**
     * Suspends the given service crawler if its active.
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to resume
     * 
     * @throws IllegalArgumentException if the service was not active
     */
    public void suspend(UniqueCrawlerConstraint key);
    
    /**
     * Resumes the given service crawler instance if its suspended. 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to resume
     * 
     * @throws IllegalArgumentException if the service was not suspended
     */
    public void resume(UniqueCrawlerConstraint key);
    
    /**
     * Sets the default service crawler handler to use when none is specified.
     * 
     * @param handler the default handler
     */
    public void setDefaultHandler(CrawlerHandler handler);
    
    /**
     * Returns the default handler
     * 
     * @return the default handler
     */
    public CrawlerHandler getDefaultHandler();
    
    /**
     * Add more crawl handler to the {@see ServiceCrawler} instance if they do 
     * not already exist. Note you can also use the following methods to modify 
     * or access the crawl handlers.
     * <br/><br/>
     * {@see ServiceCrawler#addHandler(eu.dime.ps.datamining.service.CrawlerHandler)}<br/>
     * {@see ServiceCrawler#removeHandler(eu.dime.ps.datamining.service.CrawlerHandler)}<br/>
     * {@see ServiceCrawler#getHandlers()}
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to update
     * @param handler the handler to add
     */
    public void addServiceHandler(UniqueCrawlerConstraint key, CrawlerHandler handler);
    
    /**
     * Removes the crawl handler to the {@see ServiceCrawler} instance. Note you can
     * also use the following methods to modify or access the crawl handlers.
     * <br/><br/>
     * {@see ServiceCrawler#addHandler(eu.dime.ps.datamining.service.CrawlerHandler)}<br/>
     * {@see ServiceCrawler#removeHandler(eu.dime.ps.datamining.service.CrawlerHandler)}<br/>
     * {@see ServiceCrawler#getHandlers()}
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to update
     * @param handler the handler to add
     */
    public void removeServiceHandler(UniqueCrawlerConstraint key, CrawlerHandler handler);
    
    /**
     * Sets the persistence manager being used..
     * 
     * @param persistence the persistence manager
     */
    public void setPersitenceManager(PersistenceManager persistence);
    
    /**
     * Checks to see if there is a persistence manager set/ in use.
     * 
     * @return true if there is a persistence provider set 
     */
    public boolean isPersistenceEnabled();

    /**
     * Fires immediately all crawler for a given account, even if they're scheduled to run in the future.
     * The crawler will run asynchronously.
     * 
     * @param accountIdentifier account identifier of the crawlers'
     */
    public void fireCrawler(String accountIdentifier);
    	
    /**
     * Fires immediately a crawler even if it's scheduled to run in the future.
     * The crawler will run asynchronously.
     * 
     * @param key the <code>UniqueCrawlerConstraint</code> representing the service crawler to fire/run
     */
    public void fireCrawler(UniqueCrawlerConstraint key);
    
}
