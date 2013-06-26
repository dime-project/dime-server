package eu.dime.ps.communications.requestbroker.pubsub;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.communications.notifier.InternalNotifySchedule;

public class EventsLogger implements AtmosphereResourceEventListener {

	private static final Logger logger = LoggerFactory
			.getLogger(EventsLogger.class);

	private InternalNotifySchedule internalNotifySchedule;

	public EventsLogger() {
	}

	public EventsLogger(InternalNotifySchedule internalNotifySchedule) {

		this.internalNotifySchedule = internalNotifySchedule;
	}

	public void onSuspend(final AtmosphereResourceEvent event) {
		logger.debug("onResume: " + event);
	}

	public void onResume(AtmosphereResourceEvent event) {
		logger.debug("onResume: " + event);
	}

	public void onDisconnect(AtmosphereResourceEvent event) {
		event.broadcaster().destroy();
		internalNotifySchedule.removeBroadcaster(event.broadcaster());
		logger.info("onDisconnect: " + event);
	}

	public void onBroadcast(AtmosphereResourceEvent event) {
		logger.info("onBroadcast: " + event);
	}

	public void onThrowable(AtmosphereResourceEvent event) {
		// called when a push connection is broken
		
		try {
			JSONObject dim = (JSONObject) event.getMessage();
			internalNotifySchedule.pushNotAtendedNotification(dim);
			logger.info("Sending again the Notification: " + dim);
			
		} catch (Exception e) {
			logger.debug("Not correct message!");
		}
		
		event.broadcaster().destroy();
		internalNotifySchedule.removeBroadcaster(event.broadcaster());
                
                if (logger.isDebugEnabled()){
                    logger.error("Atmosphere connection broken: " + event);
                }else{
                    logger.warn("Atmosphere connection broken: " + event.getMessage());
                }
                
        
	}

}