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
