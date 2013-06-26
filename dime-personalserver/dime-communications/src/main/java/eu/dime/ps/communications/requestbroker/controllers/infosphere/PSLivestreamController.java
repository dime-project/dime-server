package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.MediaType;

@Controller
@Path("/dime/rest/{said}/livestream/")
public class PSLivestreamController extends PSControllerBase implements APIController {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@all")
	public Response<Resource> getMyLivestreams() {
		Data<Resource> data = new Data<Resource>(0, 0, 0);
		return Response.ok(data);
	}

}