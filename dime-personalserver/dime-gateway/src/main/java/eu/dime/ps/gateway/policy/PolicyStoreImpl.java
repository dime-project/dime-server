package eu.dime.ps.gateway.policy;

import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.UserDefaults;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Responsible to store and retrieve server or user/tenant specific settings 
 * @author marcel
 *
 */
public class PolicyStoreImpl {
	
	private EntityFactory entityFactory;

	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void storeOrUpdate(String key, String value, Long tenantId){
		if (tenantId == null || key == null || value == null){
			throw new IllegalArgumentException("Parameters should not be null.");
		}
		storeOrUpdate(key, value, tenantId, "n/a","n/a", true);
	}
	
	public void storeOrUpdate(String key, String value, Long tenantId, String appliesTo,
			String targetElement, Boolean allowOveride){
		if (tenantId == null || key == null || value == null){
			throw new IllegalArgumentException("Parameters should not be null.");
		}
		UserDefaults ud = UserDefaults.findAllByTenantAndName(Tenant.find(tenantId), key);
		if (ud == null){
			ud = entityFactory.buildUserDefaults();
		} 
		ud.setTenant(Tenant.find(tenantId));
		ud.setName(key);
		ud.setValue(value);
		ud.setAppliesTo(appliesTo);
		ud.setAllowOverride(allowOveride);
		ud.setTargetElementName(targetElement);
		ud.merge();
	}
	
	public String getValue(String key, Long tenantId){
		return UserDefaults.findAllByTenantAndName(Tenant.find(tenantId), key).getValue();
	}
}
