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

package eu.dime.ps.communications.requestbroker.pubsub;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.SuspendResponse;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

import eu.dime.commons.dto.Response;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.communications.notifier.InternalNotifySchedule;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

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
    	
    	/*
    	Long tenant = TenantContextHolder.getTenant();
    	
    	if(tenant == null){
    		String json = JaxbJsonSerializer.jsonValue(Response.badRequest("Permission denied - No tenant ("+ tenant +") - Please log in! "));
    		
    		return new SuspendResponse.SuspendResponseBuilder<String>().period(20, TimeUnit.MILLISECONDS)
    				.entity(json).outputComments(false).lastModified(new Date()).build();
    	}
    	
    	
    	if(!tenant.toString().equals(said)){
    		logger.info("tenant: " + tenant + " said: " + said);
    		String json = JaxbJsonSerializer.jsonValue(Response.badRequest("Permission denied with said: " + said + " and tenant: " + tenant));
    		
    		return new SuspendResponse.SuspendResponseBuilder<String>().period(20, TimeUnit.MILLISECONDS)
    				.entity(json).outputComments(false).lastModified(new Date()).build();
    	}
    	*/

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
