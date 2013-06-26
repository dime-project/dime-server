package eu.dime.ps.storage.entities;

import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = HistoryCache.class)
public class HistoryCacheDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<HistoryCache> data;

	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public HistoryCache getNewTransientHistoryCache(int index) {
        eu.dime.ps.storage.entities.HistoryCache obj = new eu.dime.ps.storage.entities.HistoryCache();
        setEntity(obj, index);
        setCacheScope(obj, index);
        setCacheTimestamp(obj, index);
        setCacheExpire(obj, index);
        setCtxelstr(obj, index);
        setCtxelobjId(obj, index);
        setTenant(obj, index);
        return obj;
    }

	public void setEntity(HistoryCache obj, int index) {
        java.lang.String entity = "entity_" + index;
        obj.setEntity(entity);
    }

	public void setCacheScope(HistoryCache obj, int index) {
        java.lang.String cacheScope = "cacheScope_" + index;
        obj.setCacheScope(cacheScope);
    }

	public void setCacheTimestamp(HistoryCache obj, int index) {
        java.util.Date cacheTimestamp = new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setCacheTimestamp(cacheTimestamp);
    }

	public void setCacheExpire(HistoryCache obj, int index) {
        java.util.Date cacheExpire = new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setCacheExpire(cacheExpire);
    }

	public void setCtxelstr(HistoryCache obj, int index) {
        java.lang.String ctxelstr = "ctxelstr_" + index;
        obj.setCtxelstr(ctxelstr);
    }

	public void setCtxelobjId(HistoryCache obj, int index) {
        java.lang.String ctxelobjId = "ctxelobjId_" + index;
        obj.setCtxelobjId(ctxelobjId);
    }

	public void setTenant(HistoryCache obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }

	public HistoryCache getSpecificHistoryCache(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        HistoryCache obj = data.get(index);
        return HistoryCache.findHistoryCache(obj.getId());
    }

	public HistoryCache getRandomHistoryCache() {
        init();
        HistoryCache obj = data.get(rnd.nextInt(data.size()));
        return HistoryCache.findHistoryCache(obj.getId());
    }

	public boolean modifyHistoryCache(HistoryCache obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.HistoryCache.findHistoryCacheEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'HistoryCache' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.HistoryCache>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.HistoryCache obj = getNewTransientHistoryCache(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
