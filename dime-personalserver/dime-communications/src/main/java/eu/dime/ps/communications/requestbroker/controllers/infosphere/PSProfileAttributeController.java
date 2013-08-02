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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Collection;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.CardinalityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.exception.BadFormedException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.dto.ProfileAttribute;
import eu.dime.ps.semantic.model.geo.Point;
import eu.dime.ps.semantic.model.nco.Affiliation;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.Hobby;
import eu.dime.ps.semantic.model.nco.Name;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;
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
@Path("/dime/rest/{said}/profileattribute")
public class PSProfileAttributeController implements APIController {

	private static final Logger logger = LoggerFactory
			.getLogger(PSProfileAttributeController.class);

	@Autowired
	private ProfileAttributeManager profileAttributeManager;

	@Autowired
	private ProfileManager profileManager;

	@Autowired
	private ProfileCardManager profileCardManager;


	@Autowired
	private PersonManager personManager;

	public void setProfileCardManager(ProfileCardManager profileCardManager) {
		this.profileCardManager = profileCardManager;
	}

	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setProfileAttributeManager(
			ProfileAttributeManager profileAttributeManager) {
		this.profileAttributeManager = profileAttributeManager;
	}

	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	// /profileattribute/

	/**
	 * Return Collection of profile attributes
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{personId}/@all")
	public Response<ProfileAttribute> getAllProfileAttributes(
			@PathParam("said") String said,
			@PathParam("personId") String personId) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/profileattribute/{personId}/@all");

		Data<ProfileAttribute> data = null;

		try {

			Person person = personManager.get(personId);

			Collection<org.ontoware.rdfreactor.schema.rdfs.Resource> atts = new LinkedList<org.ontoware.rdfreactor.schema.rdfs.Resource>();


			for (PersonContact profile : profileManager.getAllByPerson(person)) {
				for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : 
					profileAttributeManager.getAllByContainer(profile.asURI().toString())) {
					
					if (!atts.contains(attribute)) {		
						atts.add(attribute);
					}
				}
			}

			for (PrivacyPreference profilecard : profileCardManager.getAllByPerson(person)) {
				for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : profileAttributeManager
						.getAllByContainer(profilecard.asURI().toString())) {

					if (!atts.contains(attribute)) {
						atts.add(attribute);
					}
				}
			}

			data = new Data<ProfileAttribute>();
			for (org.ontoware.rdfreactor.schema.rdfs.Resource att : atts) {
				try {
					data.getEntries().add(new ProfileAttribute(att,profileAttributeManager.getMe().asURI()));
				} catch (BadFormedException e) {
					logger.error("Profile attribute " + att.asResource()
							+ " not returned", e);
				}
			}

			data.setStartIndex(0);
			data.setItemsPerPage(data.getEntries().size());
			data.setTotalResults(data.getEntries().size());

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
	@Path("/@me/@all")
	public Response<ProfileAttribute> getAllProfileAttributes(
			@PathParam("said") String said) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/profileattribute/@me/@all");

		Data<ProfileAttribute> data = null;


		try {
			Person me = profileManager.getMe();
			Collection<org.ontoware.rdfreactor.schema.rdfs.Resource> atts = new LinkedList<org.ontoware.rdfreactor.schema.rdfs.Resource>();
			for (PersonContact profile : profileManager.getAllByPerson(me)) {
				for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : profileAttributeManager
						.getAllByContainer(profile.asURI().toString())) {
					if (!atts.contains(attribute)){
						atts.add(attribute);
						}
				}
			}

			for (PrivacyPreference profilecard : profileCardManager.getAllByPerson(me)) {
				for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : profileAttributeManager
						.getAllByContainer(profilecard.asURI().toString())) {
					if (!atts.contains(attribute)){
						atts.add(attribute);
						}
				}
			}

			data = new Data<ProfileAttribute>();
			for (org.ontoware.rdfreactor.schema.rdfs.Resource att : atts) {
				try {
					data.getEntries().add(new ProfileAttribute(att,profileAttributeManager.getMe().asURI()));
				} catch (BadFormedException e) {
					logger.error("Profile attribute " + att.asResource()
							+ " not returned", e);
				}
			}
			data.setStartIndex(0);
			data.setItemsPerPage(data.getEntries().size());
			data.setTotalResults(data.getEntries().size());
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
	@Path("/@me/{profileAttributeID}")
	public Response<ProfileAttribute> getProfileAttribute(
			@PathParam("said") String said,
			@PathParam("profileAttributeID") String profileAttributeID) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/profileattribute/@me/{profileAttributeID}");
		Data<ProfileAttribute> data = null;

		try {
			org.ontoware.rdfreactor.schema.rdfs.Resource attribute = profileAttributeManager
					.get(profileAttributeID);
			data = new Data<ProfileAttribute>(0, 1, new ProfileAttribute(attribute, profileAttributeManager.getMe().asURI()));
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
	 * Return profile attribute
	 * 
	 * @param profileID
	 * @param profileAttributeID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{profileID}/{profileAttributeID}")
	public Response<ProfileAttribute> getProfileAttribute(
			@PathParam("said") String said,
			@PathParam("profileID") String profileID,
			@PathParam("profileAttributeID") String profileAttributeID) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/profileattribute/{profileID}/{profileAttributeID}");

		Data<ProfileAttribute> data = null;

		try {
			org.ontoware.rdfreactor.schema.rdfs.Resource attribute = profileAttributeManager
					.get(profileAttributeID);
			data = new Data<ProfileAttribute>(0, 1, new ProfileAttribute(attribute, profileAttributeManager.getMe().asURI()));
		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	private void linkProfileToAttribute(PersonContact profile,
			org.ontoware.rdfreactor.schema.rdfs.Resource attribute,
			String category) {

		if (category.equals("PersonName")) {
			profile.setPersonName(attribute);
		}else if (category.equals("Name")) {
			profile.setName(attribute);
		}
		else if (category.equals("BirthDate")) {
			profile.setBirthDate(attribute);
		} else if (category.equals("EmailAddress")) {
			profile.addEmailAddress(attribute);
		} else if (category.equals("PhoneNumber") 
				||category.equals("CellPhoneNumber")
				||category.equals("FaxNumber")
				||category.equals("CarPhoneNumber")
				||category.equals("ModemNumber")
				||category.equals("PagerNumber")
				||category.equals("MessagingNumber")
				||category.equals("VideoTelephoneNumber")
				||category.equals("VoiceMail")){
			profile.addPhoneNumber(attribute);
		}
		else if (category.equals("PostalAddress")
				|| category.equals("DomesticDeliveryAddress")
				|| category.equals("InternationalDeliveryAddress")
				|| category.equals("ParcelDeliveryAddress")) {
			profile.addPostalAddress(attribute);
		} else if (category.equals("Affiliation")) {
			profile.addAffiliation(attribute);
		} else if (category.equals("Hobby")) {
			profile.addHobby(attribute);
		}		
		else if (category.equals("Location")) {			
			try {
				profile.addLocation(attribute);
			} catch (CardinalityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		else {
			throw new IllegalArgumentException("category '" + category
					+ "' is not valid.");
		}
	}

	private void linkProfileCardToAttribute(PrivacyPreference profilecard,
			org.ontoware.rdfreactor.schema.rdfs.Resource attribute
			) {

		profilecard.addAppliesToResource(attribute);

	}

	@POST
	@Path("/{profileID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<ProfileAttribute> createProfileAttribute(
			@PathParam("said") String said, Request<ProfileAttribute> request,
			@PathParam("profileID") String profileID) {

		Data<ProfileAttribute> data, returnData;

		try {
			//RequestValidator.validateRequest(request);
			PersonContact profile = null;
			PrivacyPreference profileCard = null;
			data = request.getMessage().getData();
			ProfileAttribute entry = data.getEntries().iterator().next();

			// Remove guid because is a new object
			entry.remove("guid");

			//fin the profile or profile card related to the profileId
			if ("@me".equals(profileID)) {
				profile = profileManager.getDefault();
			} else {			

				if (profileID.startsWith("pc")) {
					profileID = profileID.replaceFirst("pc_", "");
					profileCard = profileCardManager.get(profileID);

				} else if (profileID.startsWith("p_")) {
					profileID = profileID.replaceFirst("p_", "");
					profile = profileManager.get(profileID);
				}
			}

			org.ontoware.rdfreactor.schema.rdfs.Resource attribute = entry
					.asResource(org.ontoware.rdfreactor.schema.rdfs.Resource.class,profileAttributeManager.getMe().asURI());



			if (profile != null) {
				profileAttributeManager.add(attribute);
				linkProfileToAttribute(profile, attribute, entry.getCategory());
				profileManager.update(profile);
				returnData = new Data<ProfileAttribute>(0, 1,
						new ProfileAttribute(attribute,profileAttributeManager.getMe().asURI()));
			}
			else if(profileCard != null){
				profileAttributeManager.add(attribute);
				linkProfileCardToAttribute(profileCard, attribute);
				profileCardManager.update(profileCard);
				returnData = new Data<ProfileAttribute>(0, 1,
						new ProfileAttribute(attribute,profileAttributeManager.getMe().asURI()));
			}
			else {

				if ("@me".equals(profileID)) {
					throw new IllegalArgumentException("there is no default profile defined");
				} else {
					throw new IllegalArgumentException(
							"The following ProfileID does not belong to any existing profile or profile card: "
									+ profileID);
				}
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

	/*
	 * UPDATE profileAttribute
	 */

	@POST
	@Path("/@me/{profileAttributeID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<ProfileAttribute> updateProfileAttribute(
			@PathParam("said") String said, Request<ProfileAttribute> request,
			@PathParam("profileAttributeID") String profileAttributeID) {

		Data<ProfileAttribute> data, returnData;

		try {
			RequestValidator.validateRequest(request);
			if (belongsTo(profileAttributeID, "@me")) {
				data = request.getMessage().getData();				
				ProfileAttribute entry = data.getEntries().iterator().next();
				org.ontoware.rdfreactor.schema.rdfs.Resource attribute = entry
						.asResource(
								new URIImpl(profileAttributeID),
								org.ontoware.rdfreactor.schema.rdfs.Resource.class,profileAttributeManager.getMe().asURI());

				profileAttributeManager.update(attribute);
				org.ontoware.rdfreactor.schema.rdfs.Resource returnAttribute = profileAttributeManager.get(profileAttributeID);

				returnData = new Data<ProfileAttribute>(0, 1,
						new ProfileAttribute(returnAttribute,profileAttributeManager.getMe().asURI()));
			} else
				return Response.badRequest("the profile attribute: "
						+ profileAttributeID
						+ "does not belong to the person @me", null);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}

	/*
	 * UPDATE profileAttribute by profileID
	 */

	@POST
	@Path("/{profileID}/{profileAttributeID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<ProfileAttribute> updateProfileAttribute(
			@PathParam("said") String said,
			Request<ProfileAttribute> request,
			@PathParam("profileID") String profileID,
			@PathParam("profileAttributeID") String profileAttributeID) {

		Data<ProfileAttribute> data, returnData;

		// Remove the p_ form the UI
		if (profileID.startsWith("pc")) {
			profileID = profileID.replaceFirst("pc_", "");

		} else if (profileID.startsWith("p_")) {
			profileID = profileID.replaceFirst("p_", "");
		}

		try {
			RequestValidator.validateRequest(request);
			if (belongsTo(profileAttributeID, profileID)) {
				data = request.getMessage().getData();
				ProfileAttribute entry = data.getEntries().iterator().next();

				org.ontoware.rdfreactor.schema.rdfs.Resource attribute = entry
						.asResource(
								new URIImpl(profileAttributeID),
								org.ontoware.rdfreactor.schema.rdfs.Resource.class,profileAttributeManager.getMe().asURI());

				profileAttributeManager.update(attribute);
				org.ontoware.rdfreactor.schema.rdfs.Resource returnAttribute = profileAttributeManager.get(profileAttributeID);

				returnData = new Data<ProfileAttribute>(0, 1,
						new ProfileAttribute(returnAttribute,profileAttributeManager.getMe().asURI()));
			} else {
				return Response.badRequest("the profile attribute: "
						+ profileAttributeID
						+ "does not belong to the person @me", null);
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

	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{profileAttributeID}")
	public Response deleteMyProfileAttribute(@PathParam("said") String said,
			@PathParam("profileAttributeID") String profileAttributeID) {

		try {
			if (belongsTo(profileAttributeID, "@me")) {
				profileAttributeManager.remove(profileAttributeID);
			} else {
				return Response.badRequest("the profile attribute: "
						+ profileAttributeID
						+ "does not belong to the person @me", null);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/{profileID}/{profileAttributeID}")
	public Response deleteProfileAttribute(@PathParam("said") String said,
			@PathParam("profileID") String profileID,
			@PathParam("profileAttributeID") String profileAttributeID) {

		// Remove the p_ form the UI
		if (profileID.startsWith("pc")) {
			profileID = profileID.replaceFirst("pc_", "");

		} else if (profileID.startsWith("p_")) {
			profileID = profileID.replaceFirst("p_", "");
		}

		try {
			if (belongsTo(profileAttributeID, profileID)) {
				profileAttributeManager.remove(profileAttributeID);
			} else {
				return Response.badRequest("the profile attribute: "
						+ profileAttributeID
						+ "does not belong to the profile: " + profileID, null);
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();
	}

	private boolean belongsTo(String profileAttributeID, String ID)
			throws InfosphereException {

		if ("@me".equals(ID)) {
			PersonContact profile = profileManager.getDefault();
			for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : profileAttributeManager
					.getAllByContainer(profile.asURI().toString())) {
				if (attribute.asURI().toString().equals(profileAttributeID)) {
					return true;
				}

			}
		}
		else {
			for (org.ontoware.rdfreactor.schema.rdfs.Resource attribute : profileAttributeManager
					.getAllByContainer(ID)) {
				if (attribute.asURI().toString().equals(profileAttributeID)) {
					return true;
				}

			}

		}

		return false;
	}

}