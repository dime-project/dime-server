/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.storage.entities;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = CrawlerHandler.class)
public class CrawlerHandlerDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<CrawlerHandler> data;

	@Autowired
    private CrawlerJobDataOnDemand crawlerJobDataOnDemand;

	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public CrawlerHandler getNewTransientCrawlerHandler(int index) {
        eu.dime.ps.storage.entities.CrawlerHandler obj = new eu.dime.ps.storage.entities.CrawlerHandler();
        setClassName(obj, index);
        setJobId(obj, index);
        setTenant(obj, index);
        return obj;
    }

	public void setClassName(CrawlerHandler obj, int index) {
        java.lang.String className = "className_" + index;
        obj.setClassName(className);
    }

	public void setJobId(CrawlerHandler obj, int index) {
        eu.dime.ps.storage.entities.CrawlerJob jobId = crawlerJobDataOnDemand.getRandomCrawlerJob();
        obj.setJobId(jobId);
    }

	public void setTenant(CrawlerHandler obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }

	public CrawlerHandler getSpecificCrawlerHandler(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        CrawlerHandler obj = data.get(index);
        return CrawlerHandler.findCrawlerHandler(obj.getId());
    }

	public CrawlerHandler getRandomCrawlerHandler() {
        init();
        CrawlerHandler obj = data.get(rnd.nextInt(data.size()));
        return CrawlerHandler.findCrawlerHandler(obj.getId());
    }

	public boolean modifyCrawlerHandler(CrawlerHandler obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandlerEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'CrawlerHandler' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.CrawlerHandler>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.CrawlerHandler obj = getNewTransientCrawlerHandler(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
