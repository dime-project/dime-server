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
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSEventController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.EventManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.dto.Resource;

public class PSEventControllerTest extends PSInfoSphereControllerTest {

	private PSEventController controller = new PSEventController();
	private Request<Resource> request;
	private EventManager mockedManager;
	private static final String said= "juan";
	
	public PSEventControllerTest() {
		
		mockedManager = buildEventManager();
		controller.setEventManager(mockedManager);
		PersonManager mockedPersonManager = buildPersonManager();
		controller.setPersonManager(mockedPersonManager);
		
		try {
			request= buildRequest(mockedManager.get("juan"));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllEvents()  {
		
		Response<Resource> response = controller.getMyEvents(said);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = controller.getMyEvent(said,"juan");
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testCreateEvent()  {
		
		Response<Resource> response = controller.createMyEvent(said, request);
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	@Test
	public void testUpdateEvent()  {
		Response<Resource> response = null;
		try {
			response = controller.updateMyEvent(said, request,mockedManager.get(said).asURI().toString());
		} catch (ClassCastException e) {			
			e.printStackTrace();
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
		
	}
	
	
	@Test
	public void testDeleteEvent()  {
		Response response= controller.deleteMyEvent(said, "juan");
		assertNotNull(response);
		
	}
	
	@Test
	public void testAddAttendee()  {
		Response<Resource> response= controller.postMyEvent(said, request, "juan", "juan");
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	@Test
	public void testDeleteAttendee()  {
		Response<Resource> response= controller.deleteMyEvent(said, "juan", "juan");
		assertNotNull(response);
		assertEquals("juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	
}
