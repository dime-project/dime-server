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

package eu.dime.ps.communications.requestbroker.controllers;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NSO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.semanticdesktop.aperture.vocabulary.NIE;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSResourcesController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;



public class PSResourceControllerTest extends PSInfoSphereControllerTest {

	private PSResourcesController controller = new PSResourcesController();
	private Request<Resource> request;
	protected ModelFactory modelFactory = new ModelFactory();
	
	private SharingManager SharingmockedManager ;
	private AccountManager AccountManagerMocked ;
	
	  private static final URI[] payload = new URI[] {
	    	NAO.prefSymbol, NFO.wordCount, NFO.pageNumber, NAO.privacyLevel,
	    	NIE.mimeType, NAO.created,NAO.creator, NAO.lastModified, NFO.fileOwner, NFO.fileSize,
	    	NFO.lineCount, NAO.privacyLevel, NAO.prefLabel, NAO.created, NSO.sharedBy,NSO.sharedWith };
	private List<URI> properties = new ArrayList<URI>();
	
	private static final String said= "juan";
	Person creator= buildPerson("juan");		
	FileDataObject file = buildResource("Hello sharing!", creator);
	FileManager mockedManager = buildResourceManager(payload);
	
	public PSResourceControllerTest() throws Exception {
		properties = Arrays.asList(payload);
		SharingmockedManager= buildResourceSharingManager(file,creator);
		AccountManagerMocked = buildAccountManager();
		controller.setFileManager(mockedManager);
		controller.setSharingManager(SharingmockedManager);
		controller.setAccountManager(AccountManagerMocked);
		try {
			request= buildRequest(mockedManager.get("juan",properties));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllResources()  {
		
		Response<Resource> response = controller.getAllResources(said);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}

	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = controller.getResourceFromPersonById(said,"juan", "juan");
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testUpdateResource()  {
		
	}
	
	
	@Test
	public void testCreateResource()  {
		Response<Resource> response = controller.createResourceFromPersonById(said,request);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	
	@Test
	public void testReadIncludes() throws InfosphereException {

		Request<Resource> request= buildRequestResource();
		Resource resource = request.getMessage().getData().getEntries().iterator().next();
		List<Include> includes =  controller.readIncludes(resource, file);

		assertNotNull(includes);
		assertEquals(includes.iterator().next().getSaidSender(),"urn:uuid:j000071");
		assertEquals(includes.iterator().next().getGroups().iterator().next(),"urn:uuid:j000117");
		assertEquals(includes.iterator().next().getServices().iterator().next(),"urn:uuid:j000140");

	}
	
	
	@Test
	public void testDeletePerson()  {
		
	}
	
	private Request<Resource> buildRequestResource() throws InfosphereException { 

		Request<Resource> request = new Request<Resource>();
		Message<Resource> message = new Message<Resource>();
		String json = "{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"userId\":\"@me\",\"name\":\"testResource\",\"imageUrl\":\"/icons/group.png\",\"type\":\"resource\",\"nao:privacyLevel\":0.9,\"items\":[],\"nao:includes\":[{\"saidSender\":\"urn:uuid:j000071\",\"groups\":[\"urn:uuid:j000117\"],\"services\":[\"urn:uuid:j000140\"],\"persons\":[{\"personId\":\"urn:uuid:j000070\",\"saidReceiver\":null}] }]}]}";
		Resource Entry = JaxbJsonSerializer.jaxbBean(json, Resource.class);

		Data<Resource> data = new Data<Resource>();
		data.addEntry(Entry);
		message.setData(data );
		request.setMessage(message);

		return request;
	}
	
	private FileDataObject buildResource(String text, Person creator) {
		FileDataObject livePost = modelFactory.getNFOFactory().createFileDataObject();
		livePost.setPrefLabel(text);
		livePost.setCreator(creator);		
		return livePost;
	}

	private Person buildPerson(String name) {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);		
		return person;
	}	
	
}
