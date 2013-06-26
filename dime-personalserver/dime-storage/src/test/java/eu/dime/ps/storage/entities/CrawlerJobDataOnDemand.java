package eu.dime.ps.storage.entities;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Configurable
@Component
@RooDataOnDemand(entity = CrawlerJob.class)
public class CrawlerJobDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<CrawlerJob> data;

	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public CrawlerJob getNewTransientCrawlerJob(int index) {
        eu.dime.ps.storage.entities.CrawlerJob obj = new eu.dime.ps.storage.entities.CrawlerJob();
        setCron(obj, index);
        setSuspended(obj, index);
        setTenant(obj, index);
        setAccountIdentifier(obj, index);
        setPath(obj, index);
        setReturnType(obj, index);
        return obj;
    }

	public void setCron(CrawlerJob obj, int index) {
        java.lang.String cron = "cron_" + index;
        obj.setCron(cron);
    }

	public void setAccountIdentifier(CrawlerJob obj, int index) {
        java.lang.String accountIdentifier = "accountIdentifier_" + index;
        obj.setAccountIdentifier(accountIdentifier);
    }
	
	public void setPath(CrawlerJob obj, int index) {
        java.lang.String path = "path_" + index;
        obj.setPath(path);
    }

	public void setReturnType(CrawlerJob obj, int index) {
        java.lang.String returnType = "returnType_" + index;
        obj.setReturnType(returnType);
    }

	public void setSuspended(CrawlerJob obj, int index) {
        java.lang.Boolean suspended = Boolean.TRUE;
        obj.setSuspended(suspended);
    }

	public void setTenant(CrawlerJob obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }

	public CrawlerJob getSpecificCrawlerJob(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        CrawlerJob obj = data.get(index);
        return CrawlerJob.find(obj.getId());
    }

	public CrawlerJob getRandomCrawlerJob() {
        init();
        CrawlerJob obj = data.get(rnd.nextInt(data.size()));
        return CrawlerJob.find(obj.getId());
    }

	public boolean modifyCrawlerJob(CrawlerJob obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.CrawlerJob.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'CrawlerJob' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.CrawlerJob>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.CrawlerJob obj = getNewTransientCrawlerJob(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
