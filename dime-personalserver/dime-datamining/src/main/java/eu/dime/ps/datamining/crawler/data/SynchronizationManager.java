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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.ps.datamining.crawler.data;



/**
 *
 * @author Will Fleury
 */
public class SynchronizationManager {
    
    private static SynchronizationManager instance = null;
    
    public synchronized static SynchronizationManager get() {
        if (instance == null)
            instance = new SynchronizationManager();
        
        return instance;
    }
    
    private SynchronizationManager() {
        
    }
    
    public boolean checkEntryExists(String hash) {
        CrawlerResource res = CrawlerResource.findResource(hash);
        
        return res != null;
    }
    
    public int getEntryInstances(String hash) {
        CrawlerResource res = CrawlerResource.findResource(hash);
        
        return (res == null ? -1 : res.getInstances());
    }
    
    public void createEntry(String hash) {
        CrawlerResource res = new CrawlerResource(hash);
        res.setInstances(1);
        
        res.persist();
    }
    
    public void removeEntry(String hash) {
        CrawlerResource res = new CrawlerResource(hash);
        
        if (res != null)
            res.remove();
    }
    
    public int deductInstance(String hash) {
        CrawlerResource res = CrawlerResource.findResource(hash);
        res.decrementInstances();
        
        if (res == null)
            return -1;
        
        res.merge();
        
        return res.getInstances();
    }
    
    public void duplicateEntry(String hash) {
        CrawlerResource res = CrawlerResource.findResource(hash);
        res.incrementInstances();
        
        res.merge();
    }
}
