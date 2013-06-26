package eu.dime.ps.communications.requestbroker.controllers;



import org.junit.Test;

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
		
		Response<Resource> response = controller.getPersonById(said,"juan");
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
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
