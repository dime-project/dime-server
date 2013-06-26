package eu.dime.ps.communications.requestbroker.controllers;


import org.junit.Test;

import eu.dime.commons.dto.Response;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSProfileAttributeController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManagerImpl;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.dto.ProfileAttribute;



public class PSProfileAttributeControllerTest extends PSInfoSphereControllerTest {

	private PSProfileAttributeController controller = new PSProfileAttributeController();
			
	private static final String said= "juan";
	private ProfileManager profileMockedManager;
	private ProfileAttributeManager mockedManager;
	private ProfileCardManager profileCardMockedManager;
	private PersonManager mockedPersonManager = buildPersonManager();
	
	
	
	public PSProfileAttributeControllerTest() {				
	
		try {
			profileMockedManager = buildProfileManager(mockedPersonManager.get(said));			
			mockedManager = buildProfileAttributeManager(profileMockedManager.get("juan").asURI().toString());
			profileCardMockedManager = buildProfileCardManager();
		} catch (ClassCastException e) {			
			e.printStackTrace();
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		controller.setProfileAttributeManager(mockedManager);
		controller.setProfileManager(profileMockedManager);
		controller.setProfileCardManager(profileCardMockedManager);
		
	}

	
	@Test
	public void testGetAllProfileAttributes()  {
		
		Response<ProfileAttribute> response = controller.getAllProfileAttributes(said);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
		
		Response<ProfileAttribute> response = controller.getProfileAttribute(said,"juan");
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	
	@Test
	public void testUpdateProfileAttribute()  {
		
		
	}
	
	@Test
	public void testUpdateProfileAttributeByProfile()  {
		
	}
	
	@Test
	public void testDeleteProfileAttribute()  {
		
	}
	
	@Test
	public void testDeleteProfileAttributeByProfile()  {
		
	}
	
	
	
}
