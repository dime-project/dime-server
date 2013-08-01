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


import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSDataboxController;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.dto.Databox;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.nfo.DataContainer;



public class PSDataboxControllerTest extends PSInfoSphereControllerTest {

	private PSDataboxController controller = new PSDataboxController();
	private static final String said= "juan";
	
	public PSDataboxControllerTest() {
		DataboxManager mockedManager = buildDataboxManager();
		AccountManager mockedAccountManager = buildAccountManager();
		controller.setDataboxManager(mockedManager);	
		controller.setAccountManager(mockedAccountManager);
	}

	
	private Request<Databox> buildRequestdatabox(DataContainer dataContainer) {
		
		  Request<Databox> request = new Request<Databox>();
		  Message<Databox> message = new Message<Databox>();
		  String json = "{\"startIndex\":0,\"itemsPerPage\":1,\"totalResults\":1,\"entry\":[{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"name\":\"Business\",\"imageUrl\":\"/icons/group.png\",\"type\":\"databox\",\"items\":[\"urn:uuid:group:f47ac10b-58cc-c1\",\"urn:uuid:group:f47ac10b-58cc-c2\",\"urn:uuid:group:f47ac10b-58cc-c3\"],\"nao:includes\":[{\"senderSaid\":\"sender\",\"groups\":[],\"services\":[],\"persons\":[] }]}]}";
		Data<Databox> data = JaxbJsonSerializer.jaxbBean(json, Data.class);
		 
		 		
		  message.setData(data );
		  request.setMessage(message);
		  
		  return request;
	}

	
	private Request<Resource> buildRequestInclude() throws JsonParseException, JsonMappingException, IOException {
		
		  Request<Resource> request = new Request<Resource>();
		  Message<Resource> message = new Message<Resource>();
		  
		  String json = "{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"name\":\"Business\",\"imageUrl\":\"/icons/group.png\",\"type\":\"databox\",\"items\":[\"urn:uuid:group:f47ac10b-58cc-c1\",\"urn:uuid:group:f47ac10b-58cc-c2\",\"urn:uuid:group:f47ac10b-58cc-c3\"],\"nao:includes\":[{\"senderSaid\":\"sender\",\"groups\":[\"group1\",\"group2\"],\"services\":[\"service1\",\"service2\"],\"persons\":[{\"personId\": \"Person1\",\"saidReceiver\":\"\"}] }]}";
		  ObjectMapper mapper = new ObjectMapper();
			Resource resource = mapper.readValue(json, Resource.class);
		  Data<Resource> data =new Data<Resource>();
		  data.getEntries().add(resource);		  
		  message.setData(data );
		  request.setMessage(message);
		  
		  
		  return request;
	}

	@Test
	public void testGetAllDataboxes()  {
		
		Response<Resource> response = controller.getAllDatabox(said);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = controller.getAllDataboxesByPerson(said,"@me");
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}	
	
	
	@Test
	public void testDeleteDatabox()  {
		Response response= controller.deleteMyDataboxById(said, "juan");
		assertNotNull(response);
	}
	
	
}
