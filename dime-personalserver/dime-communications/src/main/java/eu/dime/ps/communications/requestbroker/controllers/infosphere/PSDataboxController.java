package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.vocabulary.NIE;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SharedTo;
import eu.dime.commons.dto.TrustEntry;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Databox;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

/**
 * Dime REST API Controller for a InfoSphere Methods to access on Databox
 * features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/databox/")
public class PSDataboxController extends PSSharingControllerBase implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSDataboxController.class);
	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(NIE.hasPart, "items");
	}

	private DataboxManager databoxManager;
	private AccountManager accountManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;
	private SharingManager sharingManager;

	public void setDataboxManager(DataboxManager databoxManager) {
		this.databoxManager = databoxManager;
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	public void setSharingManager(SharingManager sharingManager) {
		this.sharingManager = sharingManager;
	}

	@Override
	public PersonManager getPersonManager() {
		return this.personManager;
	}

	@Override
	public AccountManager getAccountManager() {
		return this.accountManager;
	}

	@Override
	public PersonGroupManager getPersonGroupManager() {
		return this.personGroupManager;
	}

	/**
	 * Retrieves all DB
	 * 
	 * @return collection containing all live posts
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@all")
	public Response<Resource> getAllDatabox() {
		Data<Resource> data = null;	

		try {
			Collection<DataContainer> databoxs = databoxManager.getAll();
			data = new Data<Resource>(0, databoxs.size(), databoxs.size());
			for (DataContainer databox : databoxs) {				
				Resource resource =new Resource(databox,null,RENAMING_RULES,databoxManager.getMe().asURI());
				writeIncludes(resource,databox);
				data.getEntries().add(resource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}




	/**
	 * Return Collection of DB from one person
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@all")
	public Response<Resource> getAllMyDataboxes(@PathParam("said") String said) {

		Data<Resource> data = null;

		try {
			Collection<DataContainer> databoxes = databoxManager
					.getAllByCreator(databoxManager.getMe().asURI());

			data = new Data<Resource>(0, databoxes.size(), databoxes.size());
			for (DataContainer databox : databoxes) {
				Resource resource =new Resource(databox,null,RENAMING_RULES,databoxManager.getMe().asURI());
				writeIncludes(resource,databox);
				data.getEntries().add(resource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Return Collection of DB from one person
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personID}/@all")
	public Response<Resource> getAllMyDataboxesByPerson(@PathParam("said") String said,
			@PathParam("personID") String personID) {

		Data<Resource> data = null;

		try {
			Collection<DataContainer> databoxes = databoxManager
					.getAllByCreator(personManager.get(personID).asURI());

			data = new Data<Resource>(0, databoxes.size(), databoxes.size());
			for (DataContainer databox : databoxes) {
				Resource resource =new Resource(databox,null,RENAMING_RULES,databoxManager.getMe().asURI());
				writeIncludes(resource,databox);
				data.getEntries().add(resource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Return DB
	 * 
	 * @param dbID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/{dbID}")
	public Response<Resource> getMyDataboxById(@PathParam("said") String said,
			@PathParam("dbID") String dbID) {

		Data<Resource> data = null;

		try {
			DataContainer databox = databoxManager.get(dbID);

			data = new Data<Resource>(0, 1, 1);
			Resource resource =new Resource(databox,null,RENAMING_RULES,databoxManager.getMe().asURI());
			writeIncludes(resource,databox);
			data.getEntries().add(resource);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Create DB
	 * 
	 * @param json
	 * @param dbID
	 * @return
	 */
	@POST
	@Path("@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Databox> postCreateMyDatabox(
			@PathParam("said") String said, Request<Databox> request) {

		// trustEngineInterface.processTrust(resourceType, resourceId, agentID,
		// dbId, groupId, adapt, jsonData);

		Data<Databox> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();

			Databox dto = data.getEntries().iterator().next();


			// Remove guid because is a new object
			dto.remove("guid");

			DataContainer databox = dto.asResource(DataContainer.class,databoxManager.getMe().asURI());
			if(!databox.hasPrivacyLevel())databox.setPrivacyLevel(0.9d);
			databoxManager.add(databox);
			readIncludes(dto,databox);
			DataContainer returnDatabox = databoxManager.get(databox.asURI()
					.toString());
			Databox resource = new Databox(returnDatabox,databoxManager.getMe().asURI());
			writeIncludes(resource,returnDatabox);
			returnData = new Data<Databox>(0, 1,resource);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}

	/**
	 * Update DB
	 * 
	 * @param json
	 * @param dbID
	 * @return
	 */
	@POST
	@Path("@me/{dbID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> postUpdateMyDataboxById(
			@PathParam("said") String said, Request<Resource> request,
			@PathParam("dbID") String dbID) {

		Data<Resource> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();		
			Resource resource = data.getEntries().iterator().next();			

			DataContainer databox = data.getEntries().iterator().next()
					.asResource(new URIImpl(dbID), DataContainer.class,databoxManager.getMe().asURI());


			databoxManager.update(databox);

			readIncludes(resource,databox);
			DataContainer returnDatabox = databoxManager.get(dbID);
			Databox returnResource = new Databox(returnDatabox,databoxManager.getMe().asURI());
			writeIncludes(returnResource,returnDatabox);
			returnData = new Data<Resource>(0, 1, returnResource);
			return Response.ok(returnData);
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}
	}



	/**
	 * Remove DB
	 * 
	 * @param dbID
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/{dbID}")
	public Response deleteMyDataboxById(@PathParam("said") String said,
			@PathParam("dbID") String dbID) {

		try {
			databoxManager.remove(dbID);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}



	////
	//Method for reading the privacyPreferences of a databox
	////
	@Override
	public List<Include> readIncludes(eu.dime.ps.dto.Resource resource,eu.dime.ps.semantic.model.RDFReactorThing datacontainer)
			throws InfosphereException {			

		List<Include> includes = buildIncludesFromMap(resource);
		if (!includes.isEmpty()){
			//TODO manage the unsharing 
			ArrayList<Include> shared = new ArrayList<Include>();
			ArrayList<Include> excludes = new ArrayList<Include>(); 					

			for(Include include: includes){
				for(String group : include.groups){	
					sharingManager.shareDatabox(datacontainer.asURI().toString(), include.getSaidSender(),  new String[]{group});
				}
				for(String service: include.services){	
					sharingManager.shareDatabox(datacontainer.asURI().toString(), include.getSaidSender(),  new String[]{service});						
				}
				for (HashMap<String, String> person : include.persons){
					if(person.get("saidReceiver") == null){						
						sharingManager.shareDatabox(datacontainer.asURI().toString(), include.getSaidSender(),  new String[]{person.get("personId")});	
					}
					else{	
						sharingManager.shareDatabox(datacontainer.asURI().toString(), include.getSaidSender(),  new String[]{person.get("saidReceiver")});	
					}					
				}

			}
		}
		return includes;
	}



	/**
	 * 
	 * @deprecated Do not use this method! 
	 * 			   Sharing is done by Posting/Updating a databox
	 */
	@Deprecated
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@sharedTo/{agentId}/@all")
	public Response<SharedTo> getAllSharedDatabox(
			@PathParam("said") String said, @PathParam("agentId") String agentId) {

		Data<SharedTo> data = null;

		try {
			Collection<PrivacyPreference> databoxes = sharingManager
					.getSharedDataboxes(agentId);
			data = new Data<SharedTo>(0, databoxes.size(), databoxes.size());

			for (PrivacyPreference databox : databoxes) {
				SharedTo sharedTo = new SharedTo();

				sharedTo.setGuid(UUID.randomUUID().toString());
				sharedTo.setAgentId(agentId);
				if (personManager.isPerson(agentId))
					sharedTo.setAgentType("person");
				if (personGroupManager.isPersonGroup(agentId))
					sharedTo.setAgentType("group");

				String itemUri = databox.asURI().toString();
				if (itemUri.contains("/"))
					itemUri = StringUtils.substringAfterLast(itemUri, "/");
				itemUri = URLDecoder.decode(itemUri, "UTF-8");

				sharedTo.setItemId(itemUri);

				sharedTo.setType("databox");

				data.getEntries().add(sharedTo);
			}

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path(value = "@me/@all/shared")
	public Response<Resource> getAllSharedDataboxWithAgent(
			@QueryParam("sharedWithAgent") String agentId,
			@QueryParam("sharedWithService") String serviceId) {

		Data<Resource> data = null;

		try {
			Collection<PrivacyPreference> databoxes = sharingManager
					.getSharedDataboxes(serviceId==null? agentId: serviceId);
			data = new Data<Resource>(0, databoxes.size(), databoxes.size());

			for (PrivacyPreference databox : databoxes) {	

				Resource resource =new Resource(databox,null,RENAMING_RULES,databoxManager.getMe().asURI());
				writeIncludes(resource,databox);
				data.getEntries().add(resource);				
			}

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * 
	 * @deprecated Do not use this method! 
	 * 			   Sharing is done by Posting/Updating a databox
	 */
	@Deprecated
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@sharedTo/{agentId}/{dbId}")
	public Response<Resource> hasAccessToDatabox(
			@PathParam("said") String said,
			@PathParam("agentId") String agentId, @PathParam("dbId") String dbId) {

		Boolean access;
		Data<LinkedHashMap<String, Boolean>> returnData;

		try {
			access = sharingManager.hasAccessToDatabox(dbId, agentId);

			LinkedHashMap<String, Boolean> result = new LinkedHashMap<String, Boolean>();
			result.put("access", access);
			returnData = new Data<LinkedHashMap<String, Boolean>>(0, 1, result);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}


	/**
	 * 
	 * @deprecated Do not use this method! 
	 * 			   Sharing is done by Posting/Updating a databox
	 */
	@Deprecated
	@POST
	@Path("@me/@sharedTo/{agentId}/{dbId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<TrustEntry> postShareDatabox(
			@PathParam("said") String said,
			@PathParam("agentId") String agentId,
			@PathParam("dbId") String dbId, @QueryParam("adapt") String adapt,
			Request<SharedTo> request) {

		// adapt values: privacy, trust, none or ""
		// if (adapt == null) {
		// adapt = "";
		// }
		//
		// TrustRecommendation trustRecomendation =
		// trustEngineInterface.processTrustForShareDatabox(
		// dbId, agentId, adapt, null);
		//
		// trustRecomendation.getConflictMap();
		// trustRecomendation.getMessage();
		//
		// // Sharing
		//
		// if (trustRecomendation.updateModel()) {

		Collection<SharedTo> entries = request.getMessage().getData()
				.getEntries();

		for (SharedTo sharedTo : entries) {
			try {
				sharingManager.shareDatabox(dbId, sharedTo.getSaidSender(), new String[]{agentId});
			} catch (IllegalArgumentException e) {
				return Response.badRequest(e.getMessage(), e);
			} catch (InfosphereException e) {
				return Response.badRequest(e.getMessage(), e);
			} catch (Exception e) {

				return Response.serverError(e.getMessage(), e);
			}
		}

		return Response.ok();

		// } else {
		//
		// Map<String, TrustConflict> conflictMap =
		// trustRecomendation.getConflictMap();
		// Data<TrustEntry> data = new Data<TrustEntry>();
		// Set<String> keySet = conflictMap.keySet();
		// for (String key : keySet) {
		// TrustEntry jsonTrustConflict = new TrustEntry();
		// TrustConflict trustConflict = conflictMap.get(key);
		// jsonTrustConflict.setAgent_guid(trustConflict.getAgentId());
		// jsonTrustConflict.setThing_guid(trustConflict.getThingId());
		// jsonTrustConflict.setMessage(trustConflict.getMessage());
		// data.addEntry(jsonTrustConflict);
		// }
		//
		// return Response.ok(data);
		// }

	}



}