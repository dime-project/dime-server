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



import javax.ws.rs.core.EntityTag;

import org.junit.Test;
import org.mockito.Mockito;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSPersonController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.ModelFactory;



public class PSPersonControllerTest extends PSInfoSphereControllerTest {

	private PSPersonController controller = new PSPersonController();	
	protected ModelFactory modelFactory = new ModelFactory();
	private Request<Resource> request;
	private PersonManager mockedManager = buildPersonManager();
	
	private static final String said= "juan";
	
	public PSPersonControllerTest() {
		
		controller.setPersonManager(mockedManager);
		try {
			request= buildRequest(mockedManager.get("juan"));
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
	}

	
	@Test
	public void testGetAllPerson()  {
		
		Response<Resource> response = controller.getAllMyPersons(said);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
//		Response<Resource> response = controller.getPersonById(said, "juan", request);
//		assertNotNull(response);
//		assertEquals("Juan", response.getMessage().getData().entry.iterator().next().get("name").toString());

		javax.ws.rs.core.Request request = Mockito.mock(javax.ws.rs.core.Request.class);
		Mockito.when(request.evaluatePreconditions(Mockito.any(EntityTag.class))).thenReturn(null);
		javax.ws.rs.core.Response response = controller.getPersonById(said, "juan", request);
		assertNotNull(response);
	}
	
	@Test
	public void testCreatePerson()  {
		
		Response<Resource> response = controller.createPerson(said,request);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
		
	}
	
	@Test
	public void testUpdatePerson()  {
		Response<Resource> response= controller.updatePerson(said,request,"@self");
				assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testDeletePerson()  {
		Response response= controller.deletePersonById(said, "Juan");
		assertNotNull(response);
	}
	
	@Test
	public void testAddContact()  {
		
	}
	
	@Test
	public void testMergePersons()  {
		
	}
	
	
	
	
}
