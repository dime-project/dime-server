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

package eu.dime.ps.gateway.policy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.UserDefaults;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Responsible to store and retrieve server or user/tenant specific settings 
 * @author marcel
 *
 */
public class PolicyStoreImpl implements PolicyStore{
	
	@Autowired
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

	@Override
	public void storeOrUpdate(String key, String value, String appliesTo, Boolean allowOveride, String targetElement) {
		if (key == null || value == null){
			throw new IllegalArgumentException("Parameters should not be null.");
		}
		List<UserDefaults> ud_list = UserDefaults.findAllByName(key);
		//TODO: should be more than one allowed?
		UserDefaults ud = null;
		if (!ud_list.isEmpty()){
			ud = ud_list.get(0);
		} else { 
			ud = entityFactory.buildUserDefaults();
		}
		ud.setName(key);
		ud.setValue(value);
		ud.setAppliesTo(appliesTo);
		ud.setAllowOverride(allowOveride);
		ud.setTargetElementName(targetElement);
		ud.merge();
	}
	
	@Override
	public void storeOrUpdate(String key, String value) {
		if (key == null || value == null){
			throw new IllegalArgumentException("Parameters should not be null.");
		}
		List<UserDefaults> ud_list = UserDefaults.findAllByName(key);
		//TODO: should be more than one allowed?
		UserDefaults ud = null;
		if (!ud_list.isEmpty()){
			ud = ud_list.get(0);
		} else { 
			ud = entityFactory.buildUserDefaults();
		}
		storeOrUpdate(key, value, "GLOBAL", true, "n/a");
	}

	@Override
	public String getValue(String key) {
		return getValueThatAppliesTo(key, "GLOBAL");
	}
	
	@Override
	public String getValueThatAppliesTo(String key, String appliesTo) {
		UserDefaults ud = UserDefaults.findAllByNameAndAppliesTo(key, appliesTo);
		if (ud == null){
			return null;
		}
		return ud.getValue();
	}
}
