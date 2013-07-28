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


import org.junit.Test;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSGroupController;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSPersonController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.ModelFactory;



public class PSGroupControllerTest extends PSInfoSphereControllerTest {

	private PSGroupController controller = new PSGroupController();
	
	protected ModelFactory modelFactory = new ModelFactory();
	private Request<Resource> request;
	private PersonGroupManager mockedManager = buildGroupManager();
	
	private static final String said= "juan";
	
	public PSGroupControllerTest() {
		
		controller.setPersonGroupManager(mockedManager);
		try {
			request= buildRequest(mockedManager.get("group1"));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllGroups()  {
		
		Response<Resource> response = controller.getAllPersonGroups(said);
		assertNotNull(response);
		assertEquals("group1",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = controller.getPersonGroupById(said,"group1");
		assertNotNull(response);
		assertEquals("group1",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testCreateGroup()  {
		Response<Resource> response = controller.createPersonGroup(said,request);
		assertNotNull(response);
		assertEquals("group1",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	@Test
	public void testUpdateGroup()  {
		Response<Resource> response = null;
		try {
			response = controller.updatePersonGroup(said, request,mockedManager.get("group1").asURI().toString());
		} catch (ClassCastException e) {			
			e.printStackTrace();
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals("group1",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	
	@Test
	public void testDeleteGroup()  {
		Response response= controller.deletePersonGroupById(said, "group1");
		assertNotNull(response);
	}
	
}
