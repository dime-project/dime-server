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

import java.util.Map;

import org.junit.Test;

import com.github.jsonldjava.utils.JSONUtils;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSProfileController;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.dto.Resource;


public class PSProfileControllerTest extends PSInfoSphereControllerTest {

	private PSProfileController controller = new PSProfileController();	
	private static final String said= "juan";
	private PersonManager mockedPersonManager = buildPersonManager();
	private ProfileManager mockedManager;
	private ProfileCardManager cardMockedManager = buildProfileCardManager();
	

	
	private  Request<Resource> request;
	public PSProfileControllerTest() {
				try {
			mockedManager = buildProfileManager(mockedPersonManager.get(said));
			request= buildRequest(mockedManager.get("juan"));			
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
				controller.setProfileManager(mockedManager);
				controller.setProfileCardManager(cardMockedManager);
				controller.setPersonManager(mockedPersonManager);
	}

	@Test
	public void testGetAllProfiles()  {
		
		Response<Resource> response = controller.getAllProfiles(said);
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testGetById()  {
		
		Response<Resource> response = null;
		try {
			response = controller.getProfileFromPersonById(said, "@me","p_"+mockedManager.get("juan").asURI().toString());
		} catch (ClassCastException e) {			
			e.printStackTrace();
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		assertNotNull(response);
		assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	

	@Test
	public void testUpdateProfile() throws ClassCastException, InfosphereException  {
		Response<Resource> response= controller.updateProfile(said,request,"@self","p_"+mockedManager.get("juan").asURI().toString());
				assertNotNull(response);
	//	assertEquals("Juan",response.getMessage().getData().entry.iterator().next().get("name").toString());
	}
	
	@Test
	public void testDeleteProfile()  {
		Response response= controller.removeProfile(said, "Juan");
		assertNotNull(response);
	}
	
	@Test
	public void testGetProfileJSONLD() throws Exception {
		// TODO implement test!
	}
	
	@Test
	public void testCreateProfileJSONLD() throws Exception {
		Object json = JSONUtils.fromInputStream(this.getClass().getClassLoader().getResourceAsStream("controllers/profile/profile.jsonld"));
		Object response = controller.createProfileJSONLD(json, "123");
		assertNotNull(response);
		assertTrue(response instanceof Map);
	}
	
	@Test
	public void testUpdateProfileJSONLD() throws Exception {
		Object json = JSONUtils.fromInputStream(this.getClass().getClassLoader().getResourceAsStream("controllers/profile/profile.jsonld"));
		Object response = controller.updateProfileJSONLD(json, "123", "content://com.android.contacts/contacts/lookup/1");
		assertNotNull(response);
		assertTrue(response instanceof Map);
	}
	
}
