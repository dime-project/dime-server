package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;

@Controller
@Path("/dime/rest/{said}/search")
public class PSSearchController implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSSearchController.class);

	private ServiceGateway serviceGateway;

	@Autowired
	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<JSONObject> getSearchOnPublicResolver(
			@QueryParam("query") String query) {

		logger.debug("search by query: " + query);
		
		try {
			DimeUserResolverServiceAdapter adapter = serviceGateway
					.getDimeUserResolverServiceAdapter();

			JSONArray jsons = adapter.search(query);

			Data<JSONObject> data = new Data<JSONObject>();

			for (Object json : jsons) {

				data.addEntry((JSONObject) json);

			}

			return Response.ok(data);

		} catch (ServiceNotAvailableException e) {
			return Response.serverError(e.getMessage(), e);
		}

	}

}
