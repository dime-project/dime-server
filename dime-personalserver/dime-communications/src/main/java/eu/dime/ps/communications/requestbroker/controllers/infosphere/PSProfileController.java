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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Profile;
import eu.dime.ps.dto.ProfileCard;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.gateway.util.JSONLDUtils;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

/**
 * Dime REST API Controller for a InfoSphere features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/profile")
public class PSProfileController extends PSSharingControllerBase implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSProfileController.class);

	private static final String KEY_PROFILECARD = "nao:includes";

	private final ModelFactory modelFactory = new ModelFactory();

	private ProfileManager profileManager;
	private ProfileCardManager profileCardManager;
	private AccountManager accountManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;
	private SharingManager sharingManager;

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public void setProfileCardManager(ProfileCardManager profileCardManager) {
		this.profileCardManager = profileCardManager;
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

	// /profile/

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@all")
	public Response<eu.dime.ps.dto.Resource> getAllProfiles(@PathParam("said") String said) {

		logger.info("called API method: GET /dime/rest/" + said + "/profile/@all");

		Data<eu.dime.ps.dto.Resource> data = null;

		try {
			//prepare the response
			Collection<PersonContact> profiles = profileManager.getAll();
			Collection<PrivacyPreference> cards = profileCardManager.getAll();
			data = new Data<eu.dime.ps.dto.Resource>(0, profiles.size() + cards.size(),
					profiles.size() + cards.size());
			Person me = profileManager.getMe();
			//add all the profiles to the response
			for (PersonContact profile : profiles) {
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,me.asURI());
				String personId = findPersonId(profile,me);
				profileDTO.setUserId(profile,personId);
				data.addEntry(profileDTO);
			}
			//add all the profile cards to the response						
			for (PrivacyPreference card : cards) {
				String serviceAccountId = findSaid(card);				
				eu.dime.ps.dto.Resource resource = new ProfileCard(card,serviceAccountId,me.asURI());
				writeIncludes(resource,card);
				data.getEntries().add(resource);
			}
			
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	private String findPersonId(PersonContact profile, Person me) throws InfosphereException {
		
		Collection<Person> persons = personManager.getAllByProfile(profile);
		for (Person person : persons)
			if(person.equals(me)) return "@me";
			else return person.asURI().toString();
		throw new InfosphereException("profile "+profile.asURI()+" has no Person as PIMO.occurence");
	}

	/**
	 * Return Collection of profiles
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<eu.dime.ps.dto.Resource> getAllMyProfiles(@PathParam("said") String said) {

		logger.info("called API method: GET /dime/rest/" + said + "/profile/@me/@all");

		Data<eu.dime.ps.dto.Resource> data = null;

		try {
			Person me = profileManager.getMe();
			Collection<PersonContact> profiles = profileManager.getAllByPerson(me);
			Collection<PrivacyPreference> cards = profileCardManager.getAllByPerson(me);
			data = new Data<eu.dime.ps.dto.Resource>(0, profiles.size() + cards.size(),
					profiles.size() + cards.size());
			for (PersonContact profile : profiles) {
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,"@me");
				data.getEntries().add(profileDTO);
			}

			for (PrivacyPreference card : cards) {
				String serviceAccountId = findSaid(card);				
				eu.dime.ps.dto.Resource resource = new ProfileCard(card,serviceAccountId,me.asURI());
				writeIncludes(resource,card);
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
	 * Return Collection of profiles
	 * 
	 * @param personId
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{personID}/@all")
	public Response<eu.dime.ps.dto.Resource> getAllProfilesFromPersonById(
			@PathParam("said") String said, @PathParam("personID") String personId) {

		logger.info("called API method: GET /dime/rest/" + said + "/profile/{personID}/@all");

		Data<eu.dime.ps.dto.Resource> data = null;

		try {
			Person person = personManager.get(personId);
			Collection<PersonContact> profiles = profileManager.getAllByPerson(person);
			Collection<PrivacyPreference> cards = profileCardManager.getAllByPerson(person);
			data = new Data<eu.dime.ps.dto.Resource>(0, profiles.size() + cards.size(),
					profiles.size() + cards.size());
			for (PersonContact profile : profiles) {
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,personId);
				data.getEntries().add(profileDTO);
			}
			for (PrivacyPreference card : cards) {
				String serviceAccountId = findSaid(card);				
				eu.dime.ps.dto.Resource resource = new ProfileCard(card,serviceAccountId,profileManager.getMe().asURI());
				writeIncludes(resource,card);
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
	 * Return profile
	 * 
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{profileID}")
	public Response<eu.dime.ps.dto.Resource> getProfileFromMe(
			@PathParam("said") String said, @PathParam("personID") String personID,
			@PathParam("profileID") String profileID) {

		logger.info("called API method: GET /dime/rest/" + said + "/profile/@me/{profileID}");
		Data<eu.dime.ps.dto.Resource> data = null;

		try {
			data = new Data<eu.dime.ps.dto.Resource>(0, 1, 1);
			if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				PersonContact profile = profileManager.get(profileID);
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,"@me");
				data.getEntries().add(profileDTO);		
			} else if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");
				PrivacyPreference card = profileCardManager.get(profileID);
				String serviceAccountId = findSaid(card);
				eu.dime.ps.dto.Resource resource = new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI());
				writeIncludes(resource,card);
				data.getEntries().add(resource);
			} else {
				throw new InfosphereException("profile or Profile Card with Id: " + profileID
						+ " has not been found.");
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}



	/**
	 * Return profile
	 * 
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{personID}/{profileID}")
	public Response<eu.dime.ps.dto.Resource> getProfileFromPersonById(
			@PathParam("said") String said, @PathParam("personID") String personID,
			@PathParam("profileID") String profileID) {
		logger.info("called API method: GET /dime/rest/" + said + "/profile/{personID}/{profileID}");
		Data<eu.dime.ps.dto.Resource> data = null;

		try {
			data = new Data<eu.dime.ps.dto.Resource>(0, 1, 1);
			if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				Person person = personManager.get(personID);
				for (PersonContact profile : profileManager.getAllByPerson(person)) {
					if (profile.asURI().toString().equals(profileID)) {
						String serviceAccountId = findSaid(profile);
						Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
						profileDTO.setUserId(profile,personID);
						data.getEntries().add(profileDTO);
					}
				}
				if (data.getEntries() == null)
					throw new InfosphereException("User: " + personID
							+ " has not profile or profileCard: " + profileID);
			} else if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");
				PrivacyPreference card = profileCardManager.get(profileID);
				String serviceAccountId = findSaid(card);
				eu.dime.ps.dto.Resource resource = new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI());
				writeIncludes(resource,card);
				data.getEntries().add(resource);
			} else {
				throw new InfosphereException("profile or Profile Card with Id: " + profileID
						+ " has not been found.");
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Create new profile or profile card
	 * 
	 * @param json
	 * @return
	 */

	@POST
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<eu.dime.ps.dto.Resource> createProfile(@PathParam("said") String said,
			Request<eu.dime.ps.dto.Resource> request) throws InfosphereException {

		logger.info("called API method: POST /dime/rest/" + said + "/profile/@me");
		Data<eu.dime.ps.dto.Resource> data, returnData;

		try {

			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();
			eu.dime.ps.dto.Resource resource = data.getEntries().iterator().next();
			if (data.getEntries().iterator().next().containsKey(KEY_PROFILECARD)) {
				// If it is a profilecard

				PrivacyPreference profilecard = data.getEntries().iterator().next()
						.asResource(PrivacyPreference.class,profileCardManager.getMe().asURI());
				
				addAccessSpaceAndDimeAccount(profilecard);	                       
				profileCardManager.add(profilecard);
				
				String serviceAccountId = findSaid(profilecard);
				readIncludes(resource,profilecard);
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1, new ProfileCard(profilecard,serviceAccountId,profileCardManager.getMe().asURI()));
			} else {
				// If it is a profile
				
				PersonContact profile =data.getEntries().iterator().next().asResource(PersonContact.class,profileManager.getMe().asURI());	 	
				profileManager.add(profile);			

				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,"@me");
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1,profileDTO);

			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);

	}

	private void addAccessSpaceAndDimeAccount(PrivacyPreference profilecard) throws InfosphereException {
		
		//create a di.me account related to the profile	card			
		Account dimeAccount = modelFactory.getDAOFactory().createAccount();
		dimeAccount.setAccountType(DimeServiceAdapter.NAME);
		if(profilecard.getPrefLabel() != null)
		dimeAccount.setPrefLabel(profilecard.getPrefLabel() + "@di.me");
		else{
			logger.warn("Profile card "+profilecard.asURI().toString()+" was created with no name");
			dimeAccount.setPrefLabel(profilecard.asURI().toString() + "@di.me");
		}
		 accountManager.add(dimeAccount);
		 
		// create an access space and set di.me account as sharedThrough
        AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
        accessSpace.setSharedThrough(dimeAccount);
        profilecard.setAccessSpace(accessSpace);
        profilecard.getModel().addAll(accessSpace.getModel().iterator());       
		
	}
	
	

	/**
	 * Update profile
	 * 
	 * @param json
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@POST
	@Path("/{personID}/{profileID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<eu.dime.ps.dto.Resource> updateProfile(@PathParam("said") String said,
			Request<eu.dime.ps.dto.Resource> request,
			@PathParam("personID") String personID, @PathParam("profileID") String profileID) {
		logger.info("called API method: POST /dime/rest/" + said
				+ "/profile/{personID}/{profileID}");

		Data<eu.dime.ps.dto.Resource> data, returnData = null;

		try {

			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();


			eu.dime.ps.dto.Resource resource = data.getEntries().iterator().next();


			if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");
				PrivacyPreference card = data.getEntries().iterator().next()
						.asResource(new URIImpl(profileID), PrivacyPreference.class,profileCardManager.getMe().asURI());
				profileCardManager.update(card);
				String serviceAccountId = findSaid(card);	
				readIncludes(resource,card);
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1, new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI()));
			} else if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				PersonContact profile = data.getEntries().iterator().next()
						.asResource(new URIImpl(profileID), PersonContact.class,profileManager.getMe().asURI());
				profileManager.update(profile);
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,personID);
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1,profileDTO);
			} else {
				throw new InfosphereException("profile or Profile Card with Id: " + profileID
						+ " has not been found.");
			}
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
	 * Update profile
	 * 
	 * @param json
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@POST
	@Path("/@me/{profileID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<eu.dime.ps.dto.Resource> updateMyProfile(@PathParam("said") String said,
			Request<eu.dime.ps.dto.Resource> request,
			@PathParam("profileID") String profileID) {
		logger.info("called API method: POST /dime/rest/" + said
				+ "/profile/@me/{profileID}");

		Data<eu.dime.ps.dto.Resource> data, returnData = null;

		try {

			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();

			//process includes
			eu.dime.ps.dto.Resource resource = data.getEntries().iterator().next();			

			if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");

				PrivacyPreference card = data.getEntries().iterator().next()
						.asResource(new URIImpl(profileID), PrivacyPreference.class,profileCardManager.getMe().asURI());
				profileCardManager.update(card);
				String serviceAccountId = findSaid(card);
				readIncludes(resource,card);
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1, new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI()));
			} else if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				PersonContact profile = data.getEntries().iterator().next()
						.asResource(new URIImpl(profileID), PersonContact.class,profileManager.getMe().asURI());
				profileManager.update(profile);
				String serviceAccountId = findSaid(profile);
				Profile profileDTO = new Profile(profile, serviceAccountId,profileManager.getMe().asURI());
				profileDTO.setUserId(profile,"@me");
				returnData = new Data<eu.dime.ps.dto.Resource>(0, 1, profileDTO);
			} else {
				throw new InfosphereException("profile or Profile Card with Id: " + profileID
						+ " has not been found.");
			}
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
	 * Remove profile
	 * 
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{personID}/{profileID}")
	public Response removeProfile(@PathParam("said") String said,
			@PathParam("personID") String personID, @PathParam("profileID") String profileID) {

		logger.info("called API method: DELETE /dime/rest/" + said
				+ "/profile/{personID}/{profileID}");

		try {
			if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");
				profileCardManager.remove(profileID);
			} else if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				profileManager.remove(profileID);
			}
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
	 * Remove profile
	 * 
	 * @param personID
	 * @param profileID
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{profileID}")
	public Response removeProfile(@PathParam("said") String said,@PathParam("profileID") String profileID) {

		logger.info("called API method: DELETE /dime/rest/" + said
				+ "/profile/{personID}/{profileID}");

		try {
			if (profileID.startsWith("pc_")) {
				profileID = profileID.replaceFirst("pc_", "");
				profileCardManager.remove(profileID);
			} else if (profileID.startsWith("p_")) {
				profileID = profileID.replaceFirst("p_", "");
				profileManager.remove(profileID);
			}
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
	//Method for reading the privacyPreferences of a profileCard
	////
	@Override
	public List<Include> readIncludes(eu.dime.ps.dto.Resource resource,eu.dime.ps.semantic.model.RDFReactorThing card)
			throws InfosphereException {			

		List<Include> includes = buildIncludesFromMap(resource);
		if (!includes.isEmpty()){
			//TODO manage the unsharing 
			ArrayList<Include> shared = new ArrayList<Include>();
			ArrayList<Include> excludes = new ArrayList<Include>(); 					

			for(Include include: includes){
				for(String group : include.groups){	
					sharingManager.shareProfileCard(card.asURI().toString(), include.getSaidSender(),  new String[]{group});
				}
				for(String service: include.services){	
					sharingManager.shareProfileCard(card.asURI().toString(), include.getSaidSender(),  new String[]{service});						
				}
				for (HashMap<String, String> person : include.persons){
					if(person.get("saidReceiver") == null){						
						sharingManager.shareProfileCard(card.asURI().toString(), include.getSaidSender(),  new String[]{person.get("personId")});	
					}
					else{	
						sharingManager.shareProfileCard(card.asURI().toString(), include.getSaidSender(),  new String[]{person.get("saidReceiver")});	
					}					
				}

			}
		}
		return includes;
	}


	// ---------------------------------
	// ------------ JSON-LD ------------
	// ---------------------------------

	@POST
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSONLD)
	@Produces(MediaType.APPLICATION_JSONLD)
	public List<Object> createProfileJSONLD(List<Object> request, @PathParam("said") String said) {

		List<? extends Resource> resources = null;
		PersonContact profile = null;
		Model attributes = RDF2Go.getModelFactory().createModel().open();
		try {
			resources = JSONLDUtils.deserializeCollection(request);
			for (Resource resource : resources) {
				if (resource instanceof PersonContact) { // profile
					profile = (PersonContact) resource;
				} else { // profile attribute
					attributes.addAll(resource.getModel().iterator());
				}
			}

			if (profile == null) {
				logger.error("A profile object was not found in the request.");
				return null;
			}

			profile.getModel().addAll(attributes.iterator());
			profileManager.add(profileManager.getMe(), profile);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JSONLDUtils.serializeCollection(profile);
	}

	@PUT
	@Path("/{profileId}")
	@Consumes(MediaType.APPLICATION_JSONLD)
	@Produces(MediaType.APPLICATION_JSONLD)
	public List<Object> updateProfileJSONLD(List<Object> request, @PathParam("said") String said,
			@PathParam("profileId") String profileId) {

		List<? extends Resource> resources = null;
		PersonContact profile = null;
		Model attributes = RDF2Go.getModelFactory().createModel().open();
		try {
			resources = JSONLDUtils.deserializeCollection(request);
			for (Resource resource : resources) {
				if (resource instanceof PersonContact) { // profile
					profile = (PersonContact) resource;
				} else { // profile attribute
					attributes.addAll(resource.getModel().iterator());
				}
			}

			if (profile == null) {
				logger.error("A profile object was not found in the request.");
				return null;
			}

			profile.getModel().addAll(attributes.iterator());
			profileManager.update(profile);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JSONLDUtils.serializeCollection(profile);
	}

	// ---------------------------------
	// ------------ Profile Card Shared ------------
	// ---------------------------------
	/**
	 * 
	 * @deprecated Do not use this method! 
	 * 			   
	 */
	@Deprecated
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@sharedTo/{agentId}/@all")
	public Response<ProfileCard> getProfileCardsSharedWith(@PathParam("agentId") String agentId) {
		Data<ProfileCard> data = null;

		try {
			Collection<PrivacyPreference> cards = sharingManager.getSharedProfileCards(agentId);
			data = new Data<ProfileCard>(0, cards.size(), cards.size());
			for (PrivacyPreference card : cards) {
				String serviceAccountId = findSaid(card);
				data.getEntries().add(new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI()));
			}
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
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
	public Response<ProfileCard> getProfileCardsSharedByQuery(
			@QueryParam("sharedWithAgent") String agentId,
			@QueryParam("sharedWithService") String serviceId) {
		Data<ProfileCard> data = null;

		try {
			Collection<PrivacyPreference> cards = sharingManager.getSharedProfileCards(serviceId==null? agentId: serviceId);
			data = new Data<ProfileCard>(0, cards.size(), cards.size());
			for (PrivacyPreference card : cards) {
				String serviceAccountId = findSaid(card);
				data.getEntries().add(new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI()));
			}
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
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
	 * 			   
	 */
	@Deprecated
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@sharedTo/{agentId}/{profileCardId}")
	public Response<ProfileCard> getProfileCardSharedWith(@PathParam("agentId") String agentId,
			@PathParam("profileCardId") String profileCardId) {
		Data<ProfileCard> data = null;
		profileCardId = profileCardId.replaceFirst("pc_", "");
		try {
			if (sharingManager.hasAccessToProfileCard(profileCardId, agentId)) {
				PrivacyPreference card = profileCardManager.get(profileCardId);
				String serviceAccountId = findSaid(card);
				data = new Data<ProfileCard>(0, 1, new ProfileCard(card,serviceAccountId,profileCardManager.getMe().asURI()));
			} else {
				return Response.badRequest("Profile card " + profileCardId
						+ " cannot be accessed by " + agentId, null);
			}
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}
		return Response.ok(data);

	}


	// TODO why only 1 said? what if the profile card has been shared through multiple accounts??
	private String findSaid(PrivacyPreference profileCard) throws Exception {
		Model metadata = profileCard.getModel();
		for (Node accessSpace : ModelUtils.findObjects(metadata, profileCard, PPO.hasAccessSpace)) {
			Node sharedThrough = ModelUtils.findObject(metadata, accessSpace.asResource(), NSO.sharedThrough);
			if (sharedThrough != null) {
				return sharedThrough.toString();
			}
		}
		return "";
	}

	private String findSaid(PersonContact profile) throws Exception { 

		ClosableIterator<Statement> iterator = profile.getModel()
				.findStatements(profile.asResource().asURI(), NIE.dataSource, Variable.ANY);    	
		while (iterator.hasNext()) {
			Statement statement = iterator.next();    	   
			Node node = statement.getObject();
			try {
				for(Account accountId: accountManager.getAll())
					if(node.asURI().toString().equals(accountId.asURI().toString())){
						return node.asURI().toString();
					}
			} catch (ClassCastException e) {						
				throw new Exception(e.getMessage());					

			} catch (InfosphereException e) {
				throw new InfosphereException(e.getMessage(),e);

			}

		}
		return "";
	}

}