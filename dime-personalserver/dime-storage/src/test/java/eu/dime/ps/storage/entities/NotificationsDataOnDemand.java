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
@RooDataOnDemand(entity = Notification.class)
public class NotificationsDataOnDemand {

	private Random rnd = new java.security.SecureRandom();

	private List<Notification> data;

	@Autowired
    private TenantDataOnDemand tenantDataOnDemand;

	public Notification getNewTransientNotifications(int index) {
        eu.dime.ps.storage.entities.Notification obj = new eu.dime.ps.storage.entities.Notification();
        setTarget(obj, index);
        setSender(obj, index);
        setOperation(obj, index);
        setItemType(obj, index);
        setItemID(obj, index);
        setName(obj, index);
        setTs(obj, index);
        setTenant(obj, index);
        setNotificationType(obj, index);
        setIsRead(obj, index);
        setUpdateTs(obj, index);
        setUnEntry(obj, index);
        return obj;
    }

	public void setTarget(Notification obj, int index) {
        java.lang.String target = "target_" + index;
        if (target.length() > 255) {
            target = target.substring(0, 255);
        }
        obj.setTarget(target);
    }

	public void setSender(Notification obj, int index) {
        java.lang.String sender = "sender_" + index;
        if (sender.length() > 255) {
            sender = sender.substring(0, 255);
        }
        obj.setSender(sender);
    }

	public void setOperation(Notification obj, int index) {
        java.lang.String operation = "operation_" + index;
        if (operation.length() > 255) {
            operation = operation.substring(0, 255);
        }
        obj.setOperation(operation);
    }

	public void setItemType(Notification obj, int index) {
        java.lang.String itemType = "itemType_" + index;
        if (itemType.length() > 255) {
            itemType = itemType.substring(0, 255);
        }
        obj.setItemType(itemType);
    }

	public void setItemID(Notification obj, int index) {
        java.lang.String itemID = "itemID_" + index;
        if (itemID.length() > 255) {
            itemID = itemID.substring(0, 255);
        }
        obj.setItemID(itemID);
    }

	public void setName(Notification obj, int index) {
        java.lang.String name = "name_" + index;
        if (name.length() > 255) {
            name = name.substring(0, 255);
        }
        obj.setName(name);
    }

	public void setTs(Notification obj, int index) {
        java.util.Date ts = new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setTs(ts);
    }
	
	public void setUpdateTs(Notification obj, int index) {
        java.util.Date updateTs = new java.util.GregorianCalendar(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR), java.util.Calendar.getInstance().get(java.util.Calendar.MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH), java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY), java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE), java.util.Calendar.getInstance().get(java.util.Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setUpdateTs(updateTs);
    }
	
	public void setIsRead(Notification obj, int index) {
		if (index % 2 == 0)  obj.setIsRead(false);
		else obj.setIsRead(true);
    }
	
	public void setUnEntry(Notification obj, int index) {
		String simbols = new String(new char[10000]).replace('\0', 'X');
        java.lang.String unEntry = "UnEntry_" + simbols + "_"+ index;
    
        obj.setUnEntry(unEntry);
    }

	public void setTenant(Notification obj, int index) {
        eu.dime.ps.storage.entities.Tenant tenant = tenantDataOnDemand.getRandomTenant();
        obj.setTenant(tenant);
    }
	
	public void setNotificationType(Notification obj, int index) {
		if (index % 2 == 0)  obj.setNotificationType("userNotification");
		else obj.setNotificationType("systemNotification");		
       
    }
	
	public Notification getSpecificNotifications(int index) {
        init();
        if (index < 0) index = 0;
        if (index > (data.size() - 1)) index = data.size() - 1;
        Notification obj = data.get(index);
        return Notification.findNotifications(obj.getId());
    }

	public Notification getRandomNotifications() {
        init();
        Notification obj = data.get(rnd.nextInt(data.size()));
        return Notification.findNotifications(obj.getId());
    }
	
	

	public boolean modifyNotifications(Notification obj) {
        return false;
    }

	public void init() {
        data = eu.dime.ps.storage.entities.Notification.findNotificationsEntries(0, 10);
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Notifications' illegally returned null");
        if (!data.isEmpty()) {
            return;
        }
        
        data = new java.util.ArrayList<eu.dime.ps.storage.entities.Notification>();
        for (int i = 0; i < 10; i++) {
            eu.dime.ps.storage.entities.Notification obj = getNewTransientNotifications(i);
            obj.persist();
            obj.flush();
            data.add(obj);
        }
    }
}
