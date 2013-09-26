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

import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Meta;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.Response.Status;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.NCOFactory;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Dime REST API Controller for a InfoSphere features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/person")
public class PSPersonController implements APIController {

	private static final Logger logger = LoggerFactory.getLogger(PSPersonController.class);
	
	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(PIMO.groundingOccurrence, "defProfile");
	}
	
	private PersonManager personManager;	

	private UserManager userManager;
		

	@Autowired
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * Return Collection of persons
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<Resource> getAllMyPersons(@PathParam("said") String said) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/person/@me/@all");
		Data<Resource> data = null;

		try {
			Collection<Person> people = personManager.getAll();
			data = new Data<Resource>(0, people.size(), people.size());
			for (Person person : people) {
				data.getEntries().add(new Resource(person,null,RENAMING_RULES, personManager.getMe().asURI()));
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Return person
	 * 
	 * @param personID
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{personID}")
	public Response<Resource> getPersonById(@PathParam("said") String said,
			@PathParam("personID") String personID) {

		logger.info("called API method: GET /dime/rest/" + said
				+ "/person/@me/"+personID);
		Data<Resource> data = null;

		try {
			Person person = "@self".equals(personID) ? personManager.getMe()
					: personManager.get(personID);
			// Person person= personManager.get(personID);
			data = new Data<Resource>(0, 1, 1);
			data.getEntries().add(new Resource(person,null,RENAMING_RULES,personManager.getMe().asURI()));

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	/**
	 * Creates a person
	 * 
	 * @param request
	 *            the request message
	 * @return a response message
	 */
	@POST
	@Path("/@me")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> createPerson(@PathParam("said") String said,
			Request<Resource> request) {

		logger.info("called API method: POST /dime/rest/" + said + "/person/@me");
		Data<Resource> data, returnData;

		try {

			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();

			Resource dto = data.getEntries().iterator().next();
			
			//check for int values (semantic engine will crash)
			if (dto.containsKey("nao:trustLevel")){
				try {
					double trust = ((Integer)dto.get("nao:trustLevel")).doubleValue();
					dto.put("nao:trustLevel", trust);	
				} catch (ClassCastException e){
					//value already double
				}
			}
			

			// Remove guid because is a new object
			dto.remove("guid");

			Person person = dto.asResource(Person.class,personManager.getMe().asURI());
			if(!person.hasTrustLevel()){
				person.setTrustLevel(AdvisoryConstants.DEFAULT_TRUST_VALUE);
			}
			personManager.add(person);

			returnData = new Data<Resource>(0, 1, new Resource(person,null,RENAMING_RULES,personManager.getMe().asURI()));

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
	 * Update person
	 * 
	 * @param json
	 * @param personID
	 * @return
	 */
	@POST
	@Path("/@me/{personID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> updatePerson(@PathParam("said") String said,
			Request<Resource> request, @PathParam("personID") String personID) {

		logger.info("called API method: POST /dime/rest/" + said
				+ "/person/@me/"+personID);
		Data<Resource> data, returnData;

		try {
			RequestValidator.validateRequest(request);

			data = request.getMessage().getData();
			Resource resource = data.getEntries().iterator().next();
			//check for int values (semantic engine will crash)
			if (resource.containsKey("nao:trustLevel")){
				try {
					double trust = ((Integer)resource.get("nao:trustLevel")).doubleValue();
					resource.put("nao:trustLevel", trust);	
				} catch (ClassCastException e){
					//value already double
				}
			}
			
			Person person = "@self".equals(personID) ? data.getEntries()
					.iterator().next()
					.asResource(personManager.getMe().asURI(), Person.class,personManager.getMe().asURI())
					: data.getEntries().iterator().next()
							.asResource(new URIImpl(personID), Person.class,personManager.getMe().asURI());
			personManager.update(person);
			Person returnPerson = personManager.get(personID);
			returnData = new Data<Resource>(0, 1, new Resource(returnPerson,null,RENAMING_RULES,personManager.getMe().asURI()));

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
	 * Removes person
	 * 
	 * @param personID
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{personID}")
	public Response deletePersonById(@PathParam("said") String said,
			@PathParam("personID") String personID) {

		logger.info("called API method: DELETE /dime/rest/" + said
				+ "/person/@me/"+personID);

		try {
			personManager.remove(personID);

		} catch (IllegalArgumentException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok();

	}

	@POST
	@Path("/@merge")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<Resource> mergeContacts(@PathParam("said") String said,
			Request<Resource> request) {

		logger.info("called API method: POST /dime/rest/" + said
				+ "/person/@merge");
		Data<Resource> returnData = null;
		Resource entry = request.getMessage().getData().entry.iterator().next();
		ArrayList items = (ArrayList) entry.get("items");

		try {

			Collection<URIImpl[]> collectionUris = toUriCollection(items);
			returnData = new Data<Resource>(0, collectionUris.size(),
					collectionUris.size());

			for (URIImpl[] personUri : collectionUris) {
				Person person = null;
				URI master = personUri[0];
				URI[] targets = (URI[]) ArrayUtils.remove(personUri, 0);
				person = personManager.merge(master, targets);
				returnData.getEntries().add(new Resource(person,personManager.getMe().asURI()));
			}
		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(returnData);
	}

	/**
	 * Create a serviceAccount for a Profile added from Public Registry
	 * 
	 * @param said
	 * @param request
	 *            Semantic Profile
	 * @return
	 */
	@POST
	@Path("/addcontact")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<HashMap> postAddContact(@PathParam("said") String said,
			Request<HashMap> request) {

		Data<HashMap> data, returnData;

		try {

			RequestValidator.validateRequest(request);
			data = request.getMessage().getData();

			Collection<HashMap> jsons = data.getEntries();

			returnData = new Data<HashMap>();

			for (HashMap jsonObject : jsons) {
				PersonContact personContact = toPersonContact(jsonObject);
				
				final String accountSaid = (String) jsonObject.get("said");
				final URI accountUri = new URIImpl(userManager.add(accountSaid).getAccountUri());
				userManager.addProfile(accountUri, personContact);

				returnData.addEntry(jsonObject);
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

	private PersonContact toPersonContact(HashMap json) {
		String nickname = (String) json.get("nickname");
		String fullname = json.get("name") + " " + json.get("surname");
		
		NCOFactory ncofactory = new NCOFactory();
		
		PersonContact newContact = ncofactory.createPersonContact();
		newContact.setPrefLabel(fullname);
		
		PersonName name = ncofactory.createPersonName();
		name.setNickname(nickname);
		name.setFullname(fullname);
		newContact.setPersonName(name);
		
		// adding name metadata to contact model
		newContact.getModel().addModel(name.getModel());

		return newContact;
	}

	private Collection<URIImpl[]> toUriCollection(ArrayList cosa) {
		Collection<URIImpl[]> out = new ArrayList<URIImpl[]>();
		Iterator<ArrayList> iter = cosa.iterator();

		while (iter.hasNext()) {

			URIImpl[] array = convert(iter.next());
			out.add(array);
		}

		return out;
	}

	private URIImpl[] convert(ArrayList next) {

		URIImpl[] out = new URIImpl[next.size()];

		for (Object uri : next) {
			URIImpl uriOut = new URIImpl((String) uri);
			out[next.indexOf(uri)] = uriOut;
		}
		return out;
	}

}

