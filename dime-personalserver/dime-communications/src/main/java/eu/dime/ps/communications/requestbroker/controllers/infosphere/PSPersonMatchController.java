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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonMatchManager;
import eu.dime.ps.storage.dto.PersonMatchEntry;
import eu.dime.ps.storage.entities.PersonMatch;

@Controller
@Path("/dime/rest/{said}/personmatch/")
public class PSPersonMatchController implements APIController {

	private static final Logger logger = Logger
			.getLogger(PSPersonMatchController.class);

	private PersonMatchManager personMatchManager;

	public void setPersonMatchManager(PersonMatchManager personMatchManager) {
		this.personMatchManager = personMatchManager;
	}

	/**
	 * Return all the person matchings
	 * 
	 * @return Collection containing all the person matchings for the user 
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/@all")
	public Response<List<PersonMatchEntry>> getPersonMatches(
			@PathParam("said") String said, @QueryParam("threshold") Double threshold) {

		logger.info("called API method: GET /dime/rest" + said
				+ "/personmatch/@me/@all");
		Data<List<PersonMatchEntry>> data = null;

		try {
			List<PersonMatch> matches = threshold == null ? personMatchManager.getAll() : personMatchManager.getAllByThreshold(threshold);
			data = new Data<List<PersonMatchEntry>>(0, matches.size(), matches.size());
			
			List<PersonMatchEntry> entries= new ArrayList<PersonMatchEntry>();
			for(PersonMatch person: matches){
				boolean found= false;
				for(PersonMatchEntry entry : entries)
				{
					if(person.getSource().equals(entry.getSource())){
						entry.addMatch(person);
						found = true;
						break;
						}
					
				}
				if(found==false){
					PersonMatchEntry newEntry = new PersonMatchEntry(person);
					entries.add(newEntry);
				}
				
			}
			data.getEntries().add(entries);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}
	
	
	/**
	 * Return all the person matchings in a provided status
	 * 
	 * @param status
	 * @return Collection containing all the person matchings in the provided status 
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/bystatus/{status}") 
	public Response<List<PersonMatchEntry>> getPersonMatchesbyStatus(@PathParam("said") String said,
			@PathParam("status") String status) {

		logger.info("called API method: GET /dime/rest" + said
				+ "/personmatch/@me/" + status);
		Data<List<PersonMatchEntry>> data = null;

		try {
			List<PersonMatch> matches = personMatchManager.getAllByStatus(status);
			data = new Data<List<PersonMatchEntry>>(0, matches.size(), matches.size());
			
			List<PersonMatchEntry> entries= new ArrayList<PersonMatchEntry>();
			for(PersonMatch person: matches){
				boolean found= false;
				for(PersonMatchEntry entry : entries)
				{
					if(person.getSource().equals(entry.getSource())){
						entry.addMatch(person);
						found = true;
						break;
						}
					
				}
				if(found==false){
					PersonMatchEntry newEntry = new PersonMatchEntry(person);
					entries.add(newEntry);
				}
				
			}
			data.getEntries().add(entries);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}
	
	
	/**
	 * Return all the person matchings for a provided person
	 *
	 * @param personID
	 * @return Collection containing all the person matchings in the provided status 
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/byperson/{personID}")  
	public Response<List<PersonMatchEntry>> getPersonMatchesbyPerson(@PathParam("said") String said,
			@PathParam("personID") String personID ) {

		logger.info("called API method: GET /dime/rest" + said
				+ "/personmatch/@me/{personID}" + personID);
		Data<List<PersonMatchEntry>> data = null;

		try {
			URI personURI = new URIImpl(personID);
			List<PersonMatch> matches = personMatchManager.getAllByPerson(personURI);
			data = new Data<List<PersonMatchEntry>>(0, matches.size(), matches.size());
			
			List<PersonMatchEntry> entries= new ArrayList<PersonMatchEntry>();
			for(PersonMatch person: matches){
				boolean found= false;
				for(PersonMatchEntry entry : entries)
				{
					if(person.getSource().equals(entry.getSource())){
						entry.addMatch(person);
						found = true;
						break;
						}
					
				}
				if(found==false){
					PersonMatchEntry newEntry = new PersonMatchEntry(person);
					entries.add(newEntry);
				}
				
			}
			data.getEntries().add(entries);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}

	
	
	/**
	 * Return all the person matchings for a provided person in the provided status
	 * 
	 * @param personID, status
	 * @return Collection containing all the person matchings for a provided person in the provided status 
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("@me/bystatusandperson/{status}/{personID}")  
	public Response<List<PersonMatchEntry>> getPersonMatchesbyStatusAndPerson(@PathParam("said") String said,
			@PathParam("status") String status, @PathParam("personID") String personID) {

		logger.info("called API method: GET /dime/rest" + said
				+ "/personmatch/@me/{status}/" + status + "/{personID}" + personID);
		Data<List<PersonMatchEntry>> data = null;

		try {
			URI personURI = new URIImpl(personID);
			List<PersonMatch> matches = personMatchManager.getAllByPersonAndByStatus(personURI, status);
			data = new Data<List<PersonMatchEntry>>(0, matches.size(), matches.size());
			
			List<PersonMatchEntry> entries= new ArrayList<PersonMatchEntry>();
			for(PersonMatch person: matches){
				boolean found= false;
				for(PersonMatchEntry entry : entries)
				{
					if(person.getSource().equals(entry.getSource())){
						entry.addMatch(person);
						found = true;
						break;
						}
					
				}
				if(found==false){
					PersonMatchEntry newEntry = new PersonMatchEntry(person);
					entries.add(newEntry);
				}
				
			}
			data.getEntries().add(entries);

		} catch (InfosphereException e) {
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}

		return Response.ok(data);
	}
	
	
	
}
