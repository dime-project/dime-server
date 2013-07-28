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

package eu.dime.ps.controllers.notifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.DimeInternalNotification;

public class NotifyFIFOMultiTenant {

    private static final Logger logger = LoggerFactory.getLogger(NotifyFIFOMultiTenant.class);
    
    // thread-safe queue
    private ConcurrentHashMap<Long,ConcurrentLinkedQueue<DimeInternalNotification>> mapFifoLists;

    public NotifyFIFOMultiTenant() {
    	mapFifoLists = new ConcurrentHashMap<Long,ConcurrentLinkedQueue<DimeInternalNotification>>();
    	logger.info("FIFO MultiTenant Queue Created");
    }

    public DimeInternalNotification popNotification(Long tenant) {
    	
    	ConcurrentLinkedQueue<DimeInternalNotification> fifo = mapFifoLists.get(tenant);
    	
    	if(fifo == null){
    		return null;
    	}
    	
    	DimeInternalNotification notification = fifo.poll();
    	
    	logger.debug("Pop Notification from Multitenant FIFO: " + notification);
	
    	return notification;
    }

    public void pushNotification(DimeInternalNotification notification) {
    	logger.debug("Pushed Notification on Multitenant FIFO: " + notification.toString());
    	
    	Long tenant = notification.getTenant();    	
    	
    	if(mapFifoLists.containsKey(tenant)){
    		mapFifoLists.get(tenant).add(notification);
    	}else {
    		ConcurrentLinkedQueue<DimeInternalNotification> newFifo = new ConcurrentLinkedQueue<DimeInternalNotification>();
    		newFifo.add(notification);
    		mapFifoLists.put(tenant, newFifo);
		}
    	
    }

    public int size(Long tenant) {    	
    	ConcurrentLinkedQueue<DimeInternalNotification> fifo = mapFifoLists.get(tenant);
    	
    	if(fifo == null){
    		return -1;
    	}
    	
    	return fifo.size();
    }

}
