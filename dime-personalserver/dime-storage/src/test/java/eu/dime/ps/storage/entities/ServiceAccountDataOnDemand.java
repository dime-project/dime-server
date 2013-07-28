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
@RooDataOnDemand(entity = ServiceAccount.class)
public class ServiceAccountDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<ServiceAccount> data;
	
	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public ServiceAccount getNewTransientServiceAccount(int index) {
        eu.dime.ps.storage.entities.ServiceAccount obj = new eu.dime.ps.storage.entities.ServiceAccount();
        setName(obj, index);        
        setAccountUri(obj, index);
        setTenant(obj, index);
        return obj;
    }
	
	public void setName(ServiceAccount obj, int index) {
        java.lang.String name = "name_" + index;
        if (name.length() > 255) {
            name = name.substring(0, 255);
        }
        obj.setName(name);
    }

	public void setAccountUri(ServiceAccount obj, int index) {
        java.lang.String account = "accountUri_" + index;
        if (account.length() > 255) {
        	account = account.substring(0, 255);
        }
        obj.setAccountURI(account);
    }
	
	
	public void setTenant(ServiceAccount obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }

	
	public ServiceAccount getSpecificServiceAccount(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        ServiceAccount obj = data.get(index);
        return ServiceAccount.find(obj.getId());
    }

	public ServiceAccount getRandomServiceAccount() {
        init();
        ServiceAccount obj = data.get(rnd.nextInt(data.size()));
        return ServiceAccount.find(obj.getId());
    }

	public boolean modifyServiceAccount(ServiceAccount obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.ServiceAccount.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'ServiceAccount' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.ServiceAccount>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.ServiceAccount obj = getNewTransientServiceAccount(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
