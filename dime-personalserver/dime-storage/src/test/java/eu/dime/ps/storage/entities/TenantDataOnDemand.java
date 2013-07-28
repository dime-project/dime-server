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

@Configurable
@Component
@RooDataOnDemand(entity = Tenant.class)
public class TenantDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<Tenant> data;

	@Autowired
    private UserDataOnDemand userDataOnDemand;

	public Tenant getNewTransientTenant(int index) {
        eu.dime.ps.storage.entities.Tenant obj = new eu.dime.ps.storage.entities.Tenant();
        setName(obj, index);
        return obj;
    }

	public void setName(Tenant obj, int index) {
	        java.lang.String username = "name_" + index;
	        obj.setName(username);
	    }
	
	public Tenant getSpecificTenant(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        Tenant obj = data.get(index);
        return Tenant.find(obj.getId());
    }

	public Tenant getRandomTenant() {
        init();
        Tenant obj = data.get(rnd.nextInt(data.size()));
        return Tenant.find(obj.getId());
    }

	public boolean modifyTenant(Tenant obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.Tenant.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Tenant' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.Tenant>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.Tenant obj = getNewTransientTenant(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
