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

package eu.dime.ps.communications.requestbroker.controllers.register;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.UserRegister;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.APIController;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.RequestValidator;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.eventlogger.data.LogType;
import eu.dime.ps.controllers.eventlogger.manager.LogEventManager;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.storage.entities.User;

@Controller
@Path("/dime/user")
public class PSUserController implements APIController {

    private static final Logger logger = LoggerFactory.getLogger(PSUserController.class);
    
    private UserManager userManager;
    private LogEventManager logEventManager;

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Autowired
    public void setLogEventManager(LogEventManager logEventManager) {
        this.logEventManager = logEventManager;
    }

    /**
     * register user
     *
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<UserRegister> createUser(Request<UserRegister> request) {

        Data<UserRegister> data = new Data<UserRegister>(0, 0, 0);

        try {
            //validations
            RequestValidator.validateRequest(request);

            UserRegister userRegister = request.getMessage().getData().getEntries().iterator().next();

            //register and store in database
            User user = userManager.register(userRegister);

            if (userManager.validateUserCanLogEvaluationData(user)){
                // Logging the register in EvaluationData Table
                logEventManager.setLog(LogType.RESISTER, user.getEvaluationId());
            }

            //prepare response
            userRegister.setUsername(user.getUsername());
            userRegister.setPassword(user.getPassword());
            data.addEntry(userRegister);

        } catch (IllegalArgumentException e) {
            logger.warn("Bad Request: " + e.getMessage());
            return Response.badRequest(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Response.serverError(e.getMessage(), e);
        }

        return Response.ok(data);
    }
    
}
