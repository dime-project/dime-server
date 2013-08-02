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

import eu.dime.ps.storage.entities.Tenant;

/**
 * Implementations of this class are used to manage the persistence of the 
 * service crawler state. This may be to database, file, network, etc. 
 * <br/>
 * 
 * 
 * @author Will Fleury
 */
public interface PersistenceManager {
    
    /**
     * This method is responsible for restoring the registry state to whatever
     * it was when the last successful persist was. Note that calling classes
     * should take care to ensure the same instance of 
     * <code>ServiceCrawlerRegistry</code> is not called by more than one thread
     * at a time. Otherwise implementing classes should use 
     * synchronize(registry) { do restore work here.. }
     * 
     */
    public void restoreRegistryState(ServiceCrawlerRegistry registry);
    
    
    /**
     * Indicates that a <code>ServiceCrawler</code> has been added 
     * 
     * @param crawler the crawler which has been added
     */
    public void crawlerAdded(ServiceCrawler crawler);

    
    /**
     * Indicates that a <code>ServiceCrawler</code> has been removed 
     * 
     * @param crawler the crawler which has been removed
     */
    public void crawlerRemoved(ServiceCrawler crawler);
    
    
    /**
     * Indicates that a <code>ServiceCrawler</code> has been modified
     * 
     * @param crawler the crawler which has been modified
     * @param suspended whether or not the crawler is active or suspended
     */
    public void crawlerUpdated(ServiceCrawler crawler, boolean suspended);
    
    
    /**
     * Indicates all crawlers were stopped/removed.
     */
    public void crawlersCleared();

    
    /**
     * Indicates that the <code>ServiceCrawler</code> <code>ServiceHandler</code>'s
     * were updated.
     * 
     * @param crawler the ServiceCrawler who's handlers were modified
     */
    public void handlersUpdated(ServiceCrawler crawler);
    
}
