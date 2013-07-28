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

package eu.dime.ps.communications.requestbroker.controllers.evaluation;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Evaluation;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.RequestValidator;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.evaluationtool.EvaluationManager;
import eu.dime.ps.storage.entities.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Dime REST API Controller for a evaluation features
 *
 * @author ipintos (BDCT)
 *
 */
@Controller
@Path("/dime/rest/{said}/evaluation")
public class PSEvaluationController {

    private EvaluationManager evaluationManager;
    private UserManager userManager;

    @Autowired
    public void setEvaluationManager(EvaluationManager em) {
        evaluationManager = em;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @POST
    @Path("/@me")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Evaluation> saveEvaluation(@PathParam("said") String said, Request<Evaluation> request) {

        try {
            RequestValidator.validateRequest(request);

            Data<Evaluation> data = request.getMessage().getData();

            Evaluation ev = (Evaluation) data.getEntries().iterator().next();

            User user = userManager.getCurrentUser();

            boolean saved = false;
            if (userManager.validateUserCanLogEvaluationData(user)){
                if (evaluationManager.saveEvaluation(ev, said)){
                    return Response.ok(data);
                }else{
                    return Response.serverError("Could not save evaluation due to invalid data. Check server logs", null);
                }
            }//else
            return Response.serverError("Evaluation not saved, evaluation omitted for user.", null);

        } catch (Exception e) {
            return Response.serverError(e.getMessage(), e);
        }
    }
}
