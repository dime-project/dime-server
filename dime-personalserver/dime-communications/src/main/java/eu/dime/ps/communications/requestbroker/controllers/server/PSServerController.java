package eu.dime.ps.communications.requestbroker.controllers.server;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.ServerInformation;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.APIController;
import eu.dime.ps.communications.requestbroker.controllers.register.PSUserController;
import eu.dime.ps.controllers.ServerInformationFactory;
import eu.dime.ps.gateway.service.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@Path("/dime/server")
public class PSServerController implements APIController {

    private static final Logger logger = LoggerFactory.getLogger(PSUserController.class);
    
    /**
     * get server information
     *     
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<ServerInformation> getServerInformation() {

        Data<ServerInformation> data = new Data<ServerInformation>(0, 0, 0);
        
        try {
            data.addEntry(ServerInformationFactory.getServerInformation());
        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request: " + e.getMessage());
            return Response.badRequest(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.serverError(e.getMessage(), e);
        }

        return Response.ok(data);
    }

}
