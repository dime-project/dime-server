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
