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
