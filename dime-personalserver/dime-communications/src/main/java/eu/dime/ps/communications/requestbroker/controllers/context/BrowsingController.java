package eu.dime.ps.communications.requestbroker.controllers.context;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.sun.jersey.api.client.ClientResponse.Status;

import eu.dime.context.exceptions.ContextException;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSControllerBase;
import eu.dime.ps.controllers.context.browing.BrowsingEvent;
import eu.dime.ps.controllers.context.browing.BrowsingManager;

/**
 * Allows to update the user's context with browsing activity.
 * API path <a href="http://www.dime-project.eu/docs/ps/api/context/browsing">/context/browsing</a>
 * 
 * @author Ismael Rivera
 */
@Controller
@Path("/dime/rest/{said}/context/browsing")
public class BrowsingController extends PSControllerBase {

	private static final Logger logger = LoggerFactory.getLogger(BrowsingController.class);

//    @Autowired FIXME this is not yet defined; will be activated after EKAW
    private BrowsingManager browsingManager;
	
    public void setBrowsingManager(BrowsingManager browingManager) {
    	this.browsingManager = browingManager;
    }
    
    @POST
    @Path("/session")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response onSessionEvent(@PathParam("said") String said, Object event) {
    	// not implemented in di.me
    	return Response.status(Status.NOT_IMPLEMENTED).build();
	}

    @POST
    @Path("/window")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response onWindowEvent(@PathParam("said") String said, Object event) {
    	// not implemented in di.me
    	return Response.status(Status.NOT_IMPLEMENTED).build();
	}

    @POST
    @Path("/browse")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response onBrowseEvent(@PathParam("said") String said, BrowsingEvent event) {
    	try {
			browsingManager.register(event);
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
			Response.status(Status.INTERNAL_SERVER_ERROR).tag(e.getMessage()).build();
		}
    	return Response.status(Status.OK).build();
	}

}
