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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.EventManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Dime REST API Controller for a InfoSphere features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/event/")
public class PSEventController implements APIController {

    private static final Logger logger = LoggerFactory.getLogger(PSPersonController.class);

    private EventManager eventManager;
    @Autowired
    private PersonManager personManager;
    

    public void setEventManager(EventManager eventManager) {
	this.eventManager = eventManager;
    }

    public void setPersonManager(PersonManager personManager) {
    	this.personManager = personManager;
        }
    /**
     * Return Collection of Meetings
     * 
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("@me/@all")
    public Response<Resource> getMyEvents(@PathParam("said") String said) {

	Data<Resource> data = null;
	logger.info("called API method: GET /dime/rest" + said + "/events/@me/@all");
	try {
	    Collection<SocialEvent> events = eventManager.getAll();
	    data = new Data<Resource>(0, events.size(), events.size());
	    for (SocialEvent event : events) {
		data.getEntries().add(new Resource(event,eventManager.getMe().asURI()));
	    }
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(data);

    }

    /**
     * Return Meeting
     * 
     * @param eventID
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("@me/{eventID}")
    public Response<Resource> getMyEvent(@PathParam("said") String said,
	    @PathParam("eventID") String eventID) {

	Data<Resource> data = null;

	try {
	    SocialEvent event = eventManager.get(eventID);
	    data = new Data<Resource>(0, 1, 1);
	    data.getEntries().add(new Resource(event,eventManager.getMe().asURI()));

	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(data);

    }

    /**
     * Create Event
     * 
     * @param json
     * @return
     */
    @POST
    @Path("@me")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Resource> createMyEvent(@PathParam("said") String said,
	    Request<Resource> request) {
	Data<Resource> data, returnData;

	try {
	    RequestValidator.validateRequest(request);

	    data = request.getMessage().getData();
	    Resource dto = data.getEntries().iterator().next();

	    // Remove guid because is a new object
	    dto.remove("guid");

	    SocialEvent event = dto.asResource(SocialEvent.class,eventManager.getMe().asURI());
	    eventManager.add(event);

	    returnData = new Data<Resource>(0, 1, new Resource(event,eventManager.getMe().asURI()));
	} catch (IllegalArgumentException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(returnData);
    }

    /**
     * Update Event
     * 
     * @param json
     * @param eventID
     * @return
     */
    @POST
    @Path("@me/{eventID}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Resource> updateMyEvent(@PathParam("said") String said,
	    Request<Resource> request, @PathParam("eventID") String eventID) {

	Data<Resource> data, returnData;

	try {
	    RequestValidator.validateRequest(request);

	    data = request.getMessage().getData();
	    SocialEvent event = data.getEntries().iterator().next()
		    .asResource(new URIImpl(eventID), SocialEvent.class,eventManager.getMe().asURI());
	    eventManager.update(event);

	    returnData = new Data<Resource>(0, 1, new Resource(event,eventManager.getMe().asURI()));
	} catch (IllegalArgumentException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(returnData);
    }

    /**
     * Remove Event and Attendee
     * 
     * @param eventID
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("@me/{eventID}")
    public Response deleteMyEvent(@PathParam("said") String said,
	    @PathParam("eventID") String eventID) {

	try {

	    eventManager.remove(eventID);

	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok();

    }
 

    /**
     * Create Attendee
     * 
     * @param json
     * @param eventID
     * @param personID
     * @return
     */
    @POST
    @Path("@me/{eventID}/{personID}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Resource> postMyEvent(@PathParam("said") String said,
	    Request<Resource> request, @PathParam("eventID") String eventID,
	    @PathParam("personID") String personID) {

	
    	Data<Resource> returnData;   	
    	
    	 try {
    		  
    		 Person person= personManager.get(personID);    		 
			eventManager.addAttendee(person, eventID);			
			SocialEvent event = eventManager.get(eventID);
			returnData = new Data<Resource>(0, 1, new Resource(event,eventManager.getMe().asURI()));
			
		} catch (InfosphereException e) {
			 return Response.badRequest(e.getMessage(), e);
		}catch (Exception e) {
		    return Response.serverError(e.getMessage(), e);
		}
	return Response.ok(returnData);

    }

    /**
     * Remove Attendee
     * 
     * @param eventID
     * @param personID
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("@me/{eventID}/{personID}")
    public Response<Resource> deleteMyEvent(@PathParam("said") String said,
	    @PathParam("eventID") String eventID, @PathParam("personID") String personID) {

	
    	Data<Resource> returnData;    	
    	
   	 try {   		  
   		 Person person= personManager.get(personID);    		 
		 eventManager.removeAttendee(person, eventID);			
		 SocialEvent event = eventManager.get(eventID);
		 returnData = new Data<Resource>(0, 1, new Resource(event,eventManager.getMe().asURI()));
		 
		} catch (InfosphereException e) {
			 return Response.badRequest(e.getMessage(), e);
		}catch (Exception e) {
		    return Response.serverError(e.getMessage(), e);
		}
	return Response.ok(returnData);
    }
}