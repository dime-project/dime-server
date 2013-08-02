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
