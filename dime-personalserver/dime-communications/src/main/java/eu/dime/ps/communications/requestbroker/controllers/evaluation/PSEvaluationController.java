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
