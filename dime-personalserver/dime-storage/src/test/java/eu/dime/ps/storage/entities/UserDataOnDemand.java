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
@RooDataOnDemand(entity = User.class)
public class UserDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<User> data;

	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public User getNewTransientUser(int index) {
        eu.dime.ps.storage.entities.User obj = new eu.dime.ps.storage.entities.User();
        setUsername(obj, index);
        setPassword(obj, index);
        setEnabled(obj, index);
        setTenant(obj, index);
        return obj;
    }

	public void setUsername(User obj, int index) {
        java.lang.String username = "username_" + index;
        obj.setUsername(username);
    }

	public void setPassword(User obj, int index) {
        java.lang.String password = "password_" + index;
        obj.setPassword(password);
    }

	public void setEnabled(User obj, int index) {
        java.lang.Boolean enabled = Boolean.TRUE;
        obj.setEnabled(enabled);
    }

	public void setTenant(User obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getSpecificTenant(index);
        obj.setTenant(tenant);
    }

	public User getSpecificUser(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        User obj = data.get(index);
        return User.find(obj.getId());
    }

	public User getRandomUser() {
        init();
        User obj = data.get(rnd.nextInt(data.size()));
        return User.find(obj.getId());
    }

	public boolean modifyUser(User obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.User.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'User' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.User>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.User obj = getNewTransientUser(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
