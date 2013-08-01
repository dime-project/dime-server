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

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import org.semanticdesktop.aperture.vocabulary.NIE;
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
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

/**
 * Dime REST API Controller for a InfoSphere Methods GET, POST and DELETE to
 * access on LivePost features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/livepost/")
public class PSLivePostController extends PSSharingControllerBase implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSLivePostController.class);

	private static final URI[] payload = new URI[] { NAO.prefSymbol,
		NAO.privacyLevel, DLPO.timestamp, NIE.mimeType, NAO.created,
		NAO.lastModified, NAO.privacyLevel, NAO.prefLabel, NSO.sharedBy,
		NSO.sharedWith,	DLPO.textualContent,NAO.creator };

	private LivePostManager livePostManager;
	private AccountManager accountManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;	
	private SharingManager sharingManager;

	public void setLivePostManager(LivePostManager livePostManager) {
		this.livePostManager = livePostManager;
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
	 * Retrieves all live posts.
	 * 
	 * @return collection containing all live posts
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@all")
	public Response<Resource> getAllLivePosts(@PathParam("said") String said) {
		Data<Resource> data = null;
		logger.info("called API method: POST /dime/rest/" + said + "/livepost/@all");
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);

		try {
			Collection<Status> liveposts = livePostManager.getAllByType(
					Status.class, properties);
			data = new Data<Resource>(0, liveposts.size(), liveposts.size());
			for (LivePost livepost : liveposts) {
				Resource resource = new Resource(livepost,livePostManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(livepost.asURI().toString(), PrivacyPreferenceType.LIVEPOST);
				writeIncludes(resource,pp);
				setUserId(resource);
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
	 * Retrieves all live posts create by  a given person.
	 * 
	 * @return collection containing all live posts of a given person.
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personId}/@all")
	public Response<Resource> getAllLivePostsByPerson(
			@PathParam("personId") String personId,@PathParam("said") String said) {		
		logger.info("called API method: POST /dime/rest/" + said + "/livepost/"+personId+"/@all");		
		Data<Resource> data = null;
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);

		try {
			
			
			URI personURI ="@me".equals(personId) ? livePostManager.getMe().asURI()
					: new URIImpl(personId); 

			Collection<LivePost> liveposts = livePostManager.getAllByPerson(
					personURI, properties);

			data = new Data<Resource>(0, liveposts.size(), liveposts.size());
			for (LivePost livepost : liveposts) {
				Resource resource = new Resource(livepost,livePostManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(livepost.asURI().toString(), PrivacyPreferenceType.LIVEPOST);
				writeIncludes(resource,pp);
				setUserId(resource);
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
	 * Retrieves a specific live post.
	 * 
	 * @param personId
	 *            identifier of the person who created the live post
	 * @param livePostId
	 *            live post identifier
	 * @return the response containing the live post
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("{personId}/{livePostId}")
	public Response<Resource> getLivePosts(
			@PathParam("said") String said,
			@PathParam("personId") String personId,
			@PathParam("livePostId") String livePostId) {
		logger.info("called API method: POST /dime/rest/" + said + "/livepost/"+personId+"/"+livePostId);	

		Data<Resource> data = null;
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);


		try {			
			LivePost livepost = livePostManager.get(livePostId, properties);
			Resource resource = new Resource(livepost,livePostManager.getMe().asURI());
			PrivacyPreference pp = sharingManager.findPrivacyPreference(livepost.asURI().toString(), PrivacyPreferenceType.LIVEPOST);
			writeIncludes(resource,pp);	
			setUserId(resource);
			data = new Data<Resource>(0, 1,resource );

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}




	/**
	 * Creates a new live post.
	 * 
	 * @param request
	 *            contains the contents for the live post
	 * @return an OK response with the live post created, or an error message
	 */
	@POST
	@Path("@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> createLivePost(Request<Resource> request,
			@PathParam("said") String said) {
		Data<Resource> data, returnData;
		logger.info("called API method: POST /dime/rest/" + said + "/livepost/@me");
		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();		

			Resource dto = data.getEntries().iterator().next();
			//check for int
			if (dto.containsKey("nao:privacyLevel")){
				try {
					double privacy = ((Integer)dto.get("nao:privacyLevel")).doubleValue();
					dto.put("nao:privacyLevel", privacy);	
				} catch (ClassCastException e){
					//value already double
				}	
			}

			// Remove guid because is a new object
			dto.remove("guid");

			LivePost livepost = dto.asResource(LivePost.class,livePostManager.getMe().asURI());
			//	livepost.setCreator(person);
			if(!livepost.hasPrivacyLevel()){
				livepost.setPrivacyLevel(AdvisoryConstants.DEFAULT_PRIVACY_LEVEL);
			}
			livePostManager.add(livepost);
			readIncludes(dto,livepost);
			returnData = new Data<Resource>(0, 1, new Resource(livepost,livePostManager.getMe().asURI()));

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
	 * Update a live post.
	 * 
	 * @param request
	 * @param personId
	 * @param livePostId
	 * @return an OK response with the live post created, or an error message
	 */
	@POST
	@Path("{personId}/{livePostId}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> updateLivePost(
			Request<Resource> request,
			@PathParam("said") String said,
			@PathParam("personId") String personId,
			@PathParam("livePostId") String livePostId) {
		Data<Resource> data, returnData;
		logger.info("called API method: UPDATE /dime/rest/" + said + "/livepost/"+personId+"/"+livePostId);

		try {
			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();
			Resource dto = data.getEntries().iterator().next();

			//check for int values (semantic engine will crash)
			if (dto.containsKey("nao:privacyLevel")){
				try {
					double privacy = ((Integer)dto.get("nao:privacyLevel")).doubleValue();
					dto.put("nao:privacyLevel", privacy);	
				} catch (ClassCastException e){
					//value already double
				}	
			}
			LivePost livepost = dto
					.asResource(new URIImpl(livePostId), LivePost.class,livePostManager.getMe().asURI());
			livePostManager.update(livepost);
			readIncludes(dto,livepost);
			returnData = new Data<Resource>(0, 1, new Resource(livepost,livePostManager.getMe().asURI()));

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}

	@DELETE
	@Path("{personId}/{livePostId}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response deleteLivePost(
			@PathParam("said") String said,
			@PathParam("personId") String personId,
			@PathParam("livePostId") String livePostId) {
		logger.info("called API method: POST /dime/rest/" + said + "/livepost/@me");
		try {
			livePostManager.remove(livePostId);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}


	////
	//Method for reading the privacyPreferences of a livepost
	////
	@Override
	public List<Include> readIncludes(eu.dime.ps.dto.Resource resource,eu.dime.ps.semantic.model.RDFReactorThing livepost)
			throws InfosphereException {			

		List<Include> includes = buildIncludesFromMap(resource);
		if (!includes.isEmpty()){
			//TODO manage the unsharing 
			ArrayList<Include> shared = new ArrayList<Include>();
			ArrayList<Include> excludes = new ArrayList<Include>(); 						

			for(Include include: includes){
				for(String group : include.groups){	
					sharingManager.shareLivePost(livepost.asURI().toString(), include.getSaidSender(),  new String[]{group});
				}
				for(String service: include.services){	
					sharingManager.shareLivePost(livepost.asURI().toString(), include.getSaidSender(),  new String[]{service});						
				}
				for (HashMap<String, String> person : include.persons){
					if(person.get("saidReceiver") == null){						
						sharingManager.shareLivePost(livepost.asURI().toString(), include.getSaidSender(),  new String[]{person.get("personId")});	
					}
					else{	
						sharingManager.shareLivePost(livepost.asURI().toString(), include.getSaidSender(),  new String[]{person.get("saidReceiver")});	
					}					
				}

			}
		}
		return includes;
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path(value = "@me/@all/shared")
	public Response<Resource> getAllSharedLivePostByQuery(
			@QueryParam("sharedWithAgent") String agentId,
			@QueryParam("sharedWithService") String serviceId) {
		Data<Resource> data = null;

		try {
			Collection<LivePost> livePosts = sharingManager
					.getSharedLivePost(serviceId==null? agentId: serviceId);
			data = new Data<Resource>(0, livePosts.size(), livePosts.size());

			for (LivePost livepost : livePosts) {
				Resource resource = new Resource(livepost,livePostManager.getMe().asURI());
				PrivacyPreference pp = sharingManager.findPrivacyPreference(livepost.asURI().toString(), PrivacyPreferenceType.LIVEPOST);
				writeIncludes(resource,pp);
				data.getEntries().add(resource);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);

	}




}