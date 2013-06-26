package eu.dime.ps.communications.requestbroker.controllers.system;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Response;

@Controller
@Path("/dime/system")
public class PSPingController {

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response ping(@PathParam("said") String said) {
	return Response.ok();
    }

}
