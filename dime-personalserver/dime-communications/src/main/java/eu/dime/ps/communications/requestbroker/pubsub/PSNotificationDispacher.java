package eu.dime.ps.communications.requestbroker.pubsub;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;
import org.atmosphere.jersey.SuspendResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

import eu.dime.ps.communications.notifier.InternalNotifySchedule;
import eu.dime.ps.communications.notifier.ExternalNotifySchedule;

@Path("/{said}/{deviceGUID}/@comet")
@Produces("application/json")
public class PSNotificationDispacher {

	private static final Logger logger = LoggerFactory.getLogger(PSNotificationDispacher.class);

    private @PathParam("deviceGUID")
    Broadcaster topic;
    public void setTopic(Broadcaster topic) {
		this.topic = topic;
	}
    
    private InternalNotifySchedule internalNotifySchedule;
	public void setInternalNotifySchedule(
			InternalNotifySchedule internalNotifySchedule) {
		this.internalNotifySchedule = internalNotifySchedule;
	}
    

    @GET
    public SuspendResponse<String> subscribe(@Context ServletContext context, @PathParam("said") String said) {

	if (internalNotifySchedule == null) {
	    XmlWebApplicationContext appContext = (XmlWebApplicationContext) context
		    .getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");

	    internalNotifySchedule = (InternalNotifySchedule) appContext.getBean("internalNotifySchedule");
	}

	internalNotifySchedule.addBroadcaster(this, topic, said);

	return new SuspendResponse.SuspendResponseBuilder<String>().broadcaster(topic)
		.outputComments(true).addListener(new EventsLogger(internalNotifySchedule)).resumeOnBroadcast(true)
		.build();
    }

	public boolean publishIntern(String json, Broadcaster topic) {

		try {
			JSONObject j = new JSONObject(json);
			topic.broadcast(j);
			logger.info("Senden Push Notifications: " + json);
			internalNotifySchedule.removeBroadcaster(topic);

		} catch (Exception e) {
			return false;
		}

		return true;

	}


    // Standard Atmosphere Server
    // **************************
    // @GET
    // @Suspend(listeners = { EventsLogger.class })
    // public Broadcastable subscribe() {
    // return new Broadcastable(topic);
    // }
    // @POST
    // @Broadcast(resumeOnBroadcast = true)
    // public Broadcastable publish(@FormParam("message") String message) {
    // return new Broadcastable(message, "", topic);
    // }
    // @GET
    // @Path("/resume/{uuid}")
    // @Resume
    // public String resume() {
    // return "Resumed";
    // }
    // @Schedule(period = 5)
    // @POST
    // @Path("/schedule")
    // public Broadcastable schedule(@FormParam("message") String message) {
    // if (!isSet.getAndSet(true)) {
    // count = 0;
    // }
    // count++;
    //
    // return broadcast(message + " " + count);
    // }
    // Broadcastable broadcast(String m) {
    // return new Broadcastable(m + "\n", topic);
    // }

}
