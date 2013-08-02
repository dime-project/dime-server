/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;

public class NotifyFIFOExternal {

    private static final Logger logger = LoggerFactory.getLogger(NotifyFIFOExternal.class);

    // thread-safe queue
    private ConcurrentLinkedQueue<DimeExternalNotification> fifoList;

    public NotifyFIFOExternal() {
    	fifoList = new ConcurrentLinkedQueue<DimeExternalNotification>();
    	logger.info("FIFO Queue Created");
    }

    public DimeExternalNotification popNotification() {
    	DimeExternalNotification notification = fifoList.poll();
    	logger.debug("Pop Notification from FIFO: " + notification);
    	return notification;
    }

    public void pushNotification(DimeExternalNotification notification) {
    	logger.debug("Pushed Notification on FIFO: " + notification.toString());
    	fifoList.add(notification);
    }

    public int size() {
    	return fifoList.size();
    }

}
