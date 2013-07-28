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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Dime REST API Controller for a InfoSphere features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/group")
public class PSGroupController implements APIController {

    private static final Logger logger = LoggerFactory.getLogger(PSGroupController.class);

    
    private PersonGroupManager personGroupManager;

    
    private SharingManager sharingManager;

    
    @Autowired
    public void setPersonGroupManager(PersonGroupManager personGroupManager) {
	this.personGroupManager = personGroupManager;
    }
    @Autowired
    public void setSharingManager(SharingManager sharingManager) {
	this.sharingManager = sharingManager;
    }


    // /groups/

    /**
     * Return Collection of groups
     * 
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/@all")
    public Response<Resource> getAllPersonGroups(@PathParam("said") String said) {

	logger.info("called API method: GET /dime/rest/{said}/group/@me/@all");

	Data<Resource> data = null;

	try {
		Collection<PersonGroup> personGroups = personGroupManager.getAll();
	    data = new Data<Resource>(0, personGroups.size(), personGroups.size());
	    for (PersonGroup personGroup : personGroups) {
		data.getEntries().add(new Resource(personGroup,personGroupManager.getMe().asURI()));
	    }
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(data);

    }

    /**
     * Return group
     * 
     * @param groupID
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/{groupID}")
    public Response<Resource> getPersonGroupById(@PathParam("said") String said,
	    @PathParam("groupID") String groupID) {

	logger.info("called API method: GET /dime/rest/{said}/group/@me/{groupID}");

	Data<Resource> data = null;

	try {
	    PersonGroup personGroup = personGroupManager.get(groupID);
	    data = new Data<Resource>(0, 1, new Resource(personGroup,personGroupManager.getMe().asURI()));
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
		
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(data);
    }

    /**
     * POST Method to Create new Person Group
     * 
     * @param json
     * @param groupID
     * @return
     */
    @POST
    @Path("/@me")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Resource> createPersonGroup(@PathParam("said") String said,
	    Request<Resource> request) {

	logger.info("called API method: POST /dime/rest/{said}/group/@me");
	Data<Resource> data, returnData;

	try {
	    RequestValidator.validateRequest(request);

	    data = request.getMessage().getData();

	    Resource dto = data.getEntries().iterator().next();

	    // Remove guid because is a new object
	    dto.remove("guid");

	    PersonGroup group = dto.asResource(PersonGroup.class,personGroupManager.getMe().asURI());
	    if(!group.hasTrustLevel())group.setTrustLevel(0.0d);
	    personGroupManager.add(group);

	    returnData = new Data<Resource>(0, 1, new Resource(group,personGroupManager.getMe().asURI()));
	} catch (IllegalArgumentException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
		logger.warn("Group POST error: "+e.getMessage());
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(returnData);
    }

    /**
     * POST Method to Update PersonGroup
     * 
     * @param json
     * @param groupID
     * @return
     */
    @POST
    @Path("/@me/{groupID}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response<Resource> updatePersonGroup(@PathParam("said") String said,
	    Request<Resource> request, @PathParam("groupID") String groupID) {

	logger.info("called API method: POST /dime/rest/{said}/group/@me/{groupID}");
	Data<Resource> data, returnData;

	try {
	    RequestValidator.validateRequest(request);

	    data = request.getMessage().getData();
	    PersonGroup group = data.getEntries().iterator().next()
		    .asResource(new URIImpl(groupID), PersonGroup.class,personGroupManager.getMe().asURI());
	    personGroupManager.update(group);
	    PersonGroup returnGroup = personGroupManager.get(groupID);
	    returnData = new Data<Resource>(0, 1, new Resource(returnGroup,personGroupManager.getMe().asURI()));
	} catch (IllegalArgumentException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
		logger.warn("Group POST error: "+e.getMessage());
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok(returnData);
    }

    /**
     * DELETE Method to Removes Group
     * 
     * @param groupID
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/@me/{groupID}")
    public Response deletePersonGroupById(@PathParam("said") String said,
	    @PathParam("groupID") String groupID) {

	logger.info("called API method: DELETE /dime/rest/{said}/group/@me/{groupID}");

	try {
	    personGroupManager.remove(groupID);

	} catch (IllegalArgumentException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (InfosphereException e) {
	    return Response.badRequest(e.getMessage(), e);
	} catch (Exception e) {
	    return Response.serverError(e.getMessage(), e);
	}

	return Response.ok();
    }

}