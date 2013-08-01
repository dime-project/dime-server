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

package eu.dime.ps.communications.requestbroker.controllers;



import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
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
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSLivePostController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Include;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.pimo.Person;




public class PSLivepostControllerTest extends PSInfoSphereControllerTest {
	
	private static final URI[] payload = new URI[] { NAO.prefSymbol,
		NAO.privacyLevel, DLPO.timestamp, NIE.mimeType, NAO.created,
		NAO.lastModified, NAO.privacyLevel, NAO.prefLabel, NSO.sharedBy,
		NSO.sharedWith,DLPO.textualContent,NAO.creator  };
	private List<URI> properties = new ArrayList<URI>();
	Person creator= buildPerson("juan");		
	LivePost livePost = buildLivePost("Hello sharing!", creator);
	
	private SharingManager SharingmockedManager ;
	private AccountManager mockedAccountManager ;
	
	private PSLivePostController controller = new PSLivePostController();
	private LivePostManager mockedManager = buildLivepostManager(payload);
	private Request<Resource> request;
	
	
	private static final String said= "juan";
	
	
	public PSLivepostControllerTest() throws Exception {	
		SharingmockedManager= buildSharingManager(livePost,creator);
		mockedAccountManager = buildAccountManager();
		properties = Arrays.asList(payload);
		controller.setLivePostManager(mockedManager);
		controller.setSharingManager(SharingmockedManager);
		controller.setAccountManager(mockedAccountManager);
		try {
			request= buildRequest(mockedManager.get("juan",properties));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllLiveposts()  {
		
		Response<Resource> response = controller.getAllLivePosts(said);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetAllLivepostsByMe()  {
		
		Response<Resource> response = controller.getAllLivePostsByPerson("@me", said);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = controller.getLivePosts(said,"juan", "juan");
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testCreateLivepost()  {
		Response<Resource> response = controller.createLivePost(request,"@me");
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testUpdateLivepost()  {
		Response<Resource> response = null;
		
			try {
				response = controller.updateLivePost(request,said,"juan",mockedManager.get("juan",properties).asURI().toString());
			} catch (ClassCastException e) {				
				e.printStackTrace();
			} catch (InfosphereException e) {				
				e.printStackTrace();
			}
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
		
	}
	
	@Test
	public void testDeleteLivepost()  {
		Response response= controller.deleteLivePost(said,"juan","group1");
		assertNotNull(response);
	}
	
	
	@Test
	public void testReadIncludes() throws InfosphereException {

		Request<Resource> request= buildRequestLivePost();
		Resource resource = request.getMessage().getData().getEntries().iterator().next();
		List<Include> includes =  controller.readIncludes(resource, livePost);

		assertNotNull(includes);
		assertEquals(includes.iterator().next().getSaidSender(),"urn:uuid:j000071");
		assertEquals(includes.iterator().next().getGroups().iterator().next(),"urn:uuid:j000117");
		assertEquals(includes.iterator().next().getServices().iterator().next(),"urn:uuid:j000140");

	}
	
	@Test
	public void testWriteIncludes(){
		//TODO test for reading the privacyPreference and injecting them on the payload
		
	}
	
	
	
	
	private Request<Resource> buildRequestLivePost() throws InfosphereException { 

		Request<Resource> request = new Request<Resource>();
		Message<Resource> message = new Message<Resource>();
		String json = "{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"userId\":\"@me\",\"name\":\"testLivepost\",\"imageUrl\":\"/icons/group.png\",\"type\":\"livepost\",\"text\":\"test text\",\"nao:privacyLevel\":0.9,\"items\":[],\"nao:includes\":[{\"saidSender\":\"urn:uuid:j000071\",\"groups\":[\"urn:uuid:j000117\"],\"services\":[\"urn:uuid:j000140\"],\"persons\":[{\"personId\":\"urn:uuid:j000070\",\"saidReceiver\":null}] }]}]}";
		Resource Entry = JaxbJsonSerializer.jaxbBean(json, Resource.class);

		Data<Resource> data = new Data<Resource>();
		data.addEntry(Entry);
		message.setData(data );
		request.setMessage(message);

		return request;
	}
	
	private LivePost buildLivePost(String text, Person creator) {
		LivePost livePost = modelFactory.getDLPOFactory().createLivePost();
		livePost.setTextualContent(text);
		livePost.setCreator(creator);		
		return livePost;
	}

	private Person buildPerson(String name) {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);		
		return person;
	}	

	
	
}
