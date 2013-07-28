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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.dod.RooDataOnDemand;
import org.springframework.stereotype.Component;

@Component
@Configurable
@RooDataOnDemand(entity = ServiceProvider.class)
public class ServiceProviderDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<ServiceProvider> data;

	public ServiceProvider getNewTransientServiceProvider(int index) {
        eu.dime.ps.storage.entities.ServiceProvider obj = new eu.dime.ps.storage.entities.ServiceProvider();
        setServiceName(obj, index);
        setConsumerKey(obj, index);
        setConsumerSecret(obj, index);
        return obj;
    }

	public void setServiceName(ServiceProvider obj, int index) {
        java.lang.String serviceName = "serviceName_" + index;
        obj.setServiceName(serviceName);
    }

	public void setConsumerKey(ServiceProvider obj, int index) {
        java.lang.String consumerKey = "consumerKey_" + index;
        obj.setConsumerKey(consumerKey);
    }

	public void setConsumerSecret(ServiceProvider obj, int index) {
        java.lang.String consumerSecret = "consumerSecret_" + index;
        obj.setConsumerSecret(consumerSecret);
    }

	public ServiceProvider getSpecificServiceProvider(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        ServiceProvider obj = data.get(index);
        return ServiceProvider.find(obj.getId());
    }

	public ServiceProvider getRandomServiceProvider() {
        init();
        ServiceProvider obj = data.get(rnd.nextInt(data.size()));
        return ServiceProvider.find(obj.getId());
    }

	public boolean modifyServiceProvider(ServiceProvider obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.ServiceProvider.find(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'ServiceProvider' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.ServiceProvider>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.ServiceProvider obj = getNewTransientServiceProvider(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
