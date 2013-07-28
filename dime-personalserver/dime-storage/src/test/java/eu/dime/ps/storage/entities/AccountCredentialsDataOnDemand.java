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
@RooDataOnDemand(entity = AccountCredentials.class)
public class AccountCredentialsDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<AccountCredentials> data;
	
	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;
	
	@Autowired
	
	private ServiceAccountDataOnDemand ServiceAccountOnDemand;

	public AccountCredentials getNewTransientAccountCredentials(int index) {
        eu.dime.ps.storage.entities.AccountCredentials obj = new eu.dime.ps.storage.entities.AccountCredentials();
        setSecret(obj, index);        
        setTarget(obj, index);
        setTargetUri(obj, index);
        setTenant(obj, index);
        setSource(obj, index);
        return obj;
    }
	
	public void setSecret(AccountCredentials obj, int index) {
        java.lang.String secret = "secret_" + index;
        if (secret.length() > 255) {
            secret = secret.substring(0, 255);
        }
        obj.setSecret(secret);
    }

	public void setTarget(AccountCredentials obj, int index) {
        java.lang.String target = "target_" + index;
        if (target.length() > 255) {
        	target = target.substring(0, 255);
        }
        obj.setTarget(target);
    }
	
	
	public void setTargetUri(AccountCredentials obj, int index) {
        java.lang.String targetUri = "target_" + index;
        if (targetUri.length() > 255) {
        	targetUri = targetUri.substring(0, 255);
        }
        obj.setTargetUri(targetUri);
    }
	
	
	
	public void setTenant(AccountCredentials obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }

	public void setSource(AccountCredentials obj, int index) {
        eu.dime.ps.storage.entities.ServiceAccount tenant = ServiceAccountOnDemand.getRandomServiceAccount();
        obj.setSource(tenant);
    }

	
	
	public AccountCredentials getSpecificAccountCredentials(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        AccountCredentials obj = data.get(index);
        return AccountCredentials.find(obj.getId());
    }

	public AccountCredentials getRandomAccountCredentials() {
        init();
        AccountCredentials obj = data.get(rnd.nextInt(data.size()));
        return AccountCredentials.find(obj.getId());
    }

	public boolean modifyAccountCredentials(AccountCredentials obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.AccountCredentials.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'AccountCredentials' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.AccountCredentials>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.AccountCredentials obj = getNewTransientAccountCredentials(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
