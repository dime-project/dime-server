package eu.dime.ps.communications.requestbroker.controllers.advisory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.AdvisoryRequestEntry;
import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.GroupDistanceWarning;
import eu.dime.commons.dto.ProfileWarning;
import eu.dime.commons.dto.ReceiverWarning;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.ResourcesWarning;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.TrustWarning;
import eu.dime.commons.dto.Warning;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.trustengine.TrustEngine;
import eu.dime.ps.controllers.trustengine.impl.AdvisoryController;
import eu.dime.ps.controllers.trustengine.impl.TrustEngineImpl;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * API to request trust and privacy recommendations
 * @author Marcel Heupel (mheupel)
 *
 */
@Controller()
@Path("/dime/rest/{said}/advisory/")
public class PrivacyAdvisoryController {
	
	Logger logger = Logger.getLogger(PrivacyAdvisoryController.class);

	TrustEngine trustEngine;
	AdvisoryController advisoryController;
	
	public void setAdvisoryController(AdvisoryController advisoryController) {
		this.advisoryController = advisoryController;
	}
	
	public void setTrustEngine(TrustEngine trustEngine) {
		this.trustEngine = trustEngine;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("@request")
    public Response getAdvisory(@PathParam("said") String said,
    		Request<AdvisoryRequestEntry> request) {
		
		Collection<AdvisoryRequestEntry>entries = request.getMessage().getData().getEntries();

		List <String> agentIDs = null, sharedThingIDs = null;
		String profileId = "";
		
		if (entries.iterator().hasNext()) {
			AdvisoryRequestEntry advisoryRequestEntry = (AdvisoryRequestEntry) entries.iterator().next();
			agentIDs = advisoryRequestEntry.agentGuids;
			sharedThingIDs = advisoryRequestEntry.shareableItems;
			profileId = advisoryRequestEntry.getProfileGuid();
		}
		Data<Warning> data = new Data<Warning>();
		
		int i = profileId.indexOf("urn:uuid");
		while (i>0){
			profileId = profileId.substring(i);
			i = profileId.indexOf("urn:uuid");
		}
		
		//check if shared item is profilecard and remove "pc_"
		List <String> checkedThings = new ArrayList<String>();
		for (String id :sharedThingIDs){
			int index = id.indexOf("urn:uuid");
			while (index > 0){
				id = id.substring(index);
				index = id.indexOf("urn:uuid");
				checkedThings.add(id);
			}
		}
		try {
			Collection <Warning> warnings = advisoryController.getAdvisory(agentIDs, checkedThings, profileId);
			
			for (Warning warning :warnings){
				data.addEntry(warning);
			}
			
		} catch (Exception e) {
			logger.error("Catched exception when calculating advisory", e);
		}
		
		return Response.ok(data);
	}
}
