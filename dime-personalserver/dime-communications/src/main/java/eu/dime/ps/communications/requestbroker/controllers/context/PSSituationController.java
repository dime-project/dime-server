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

package eu.dime.ps.communications.requestbroker.controllers.context;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.context.LiveContextManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.dto.SituationDTO;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.service.exception.LiveContextException;

/**
 * Dime REST API Controller for situation-related features
 * 
 * @author rgimenez (BDCT)
 * 
 */
@Controller
@Path("/dime/rest/{said}/situation")
public class PSSituationController {

	private static final Logger logger = LoggerFactory
			.getLogger(PSSituationController.class);

	private SituationManager situationManager;
	private LiveContextManager liveContextManager;

	public void setSituationManager(SituationManager situationManager) {
		this.situationManager = situationManager;
	}

	public void setLiveContextManager(LiveContextManager liveContextManager) {
		this.liveContextManager = liveContextManager;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<SituationDTO> getMeAll(@PathParam("said") String said) {
		logger.info("called API method: GET /dime/rest/" + said
				+ "/person/@me/@all");

		Data<SituationDTO> data = new Data<SituationDTO>(0, 1, 1);

		try {
			Collection<Situation> situations = situationManager.getAll();
			data = new Data<SituationDTO>(0, situations.size(),
					situations.size());
			for (Situation situation : situations) {

				SituationDTO dto = new SituationDTO(situation,situationManager.getMe());			
				data.getEntries().add(dto);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@POST
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<SituationDTO> createSituation(
			@PathParam("said") String said, Request<SituationDTO> request) {

		logger.info("called API method: POST /dime/rest/"+said+"/situation/@me");
		
		Data<SituationDTO> data, returnData = new Data<SituationDTO>(0, 1, 1);

		data = request.getMessage().getData();
		String name = data.getEntries().iterator().next().get("name")
				.toString();
		try {
			eu.dime.ps.semantic.model.dcon.Situation situation = liveContextManager
					.saveAsSituation(name);

			SituationDTO dto = new SituationDTO(situation,situationManager.getMe());
			returnData.getEntries().add(dto);

			situationManager.activate(dto.get("guid").toString());

		} catch (LiveContextException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}
	
	@POST
	@Path("/@me/{situationID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<SituationDTO> updateSituation(
			@PathParam("said") String said, 
			Request<SituationDTO> request,
			@PathParam("situationID") String situationID) {

		logger.info("called API method: POST /dime/rest/"+said+"/situation/@me/update/"+situationID);
		
		Data<SituationDTO> data, returnData = new Data<SituationDTO>(0, 1, 1);

		data = request.getMessage().getData();
		SituationDTO situationDtO = data.getEntries().iterator().next();
		String active = situationDtO.get("active").toString();
				
		try {
			
			Situation situation = situationDtO.asResource(new URIImpl(situationID),Situation.class, situationManager.getMe().asURI());
			situationManager.update(situation);
			
			if(active.equals("true")){
				situationManager.activate(situationID);
			}else if(active.equals("false")){
				situationManager.deactivate(situationID);
			}					
			eu.dime.ps.semantic.model.dcon.Situation response = situationManager
					.get(situationID);

			SituationDTO dto = new SituationDTO(response,situationManager.getMe());

			returnData.getEntries().add(dto);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}
	

}
