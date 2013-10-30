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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.service.external.oauth.TwitterServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;





public class PSPersonControllerTestIt extends PSInfosphereControllerTestIt {

	private static final String SAID = "juan";


	@Autowired
	private PersonManager personManager;

	@Autowired
	private AccountManager accountManager;	

	@Autowired
	private ProfileManager profileManager;


	private PSPersonController controller;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// set up PSPersonController
		controller = new PSPersonController();
		controller.setAccountManager(accountManager);
		controller.setPersonManager(personManager);
		controller.setProfileManager(profileManager);

	}

	@After
	public void tearDown() throws Exception {		
		Collection<Person> persons = personManager.getAll();
		for (Person person: persons){
			personManager.remove(person.asURI().toString());		
		}	

		Collection<PersonContact> profiles = profileManager.getAll();
		for (PersonContact profile: profiles){
			profileManager.remove(profile.asURI().toString());		
		}	
		super.tearDown();


	}	



	@Test
	public void testDefProfileDimeAccount() throws Exception {


		Account account = createAccount(pimoService.getUserUri());
		PersonContact profile = createProfile("Test user",account);
		Person person = createPerson("Test user");
		person.addGroundingOccurrence(profile);

		Response<Resource> response = controller.getAllMyPersons(SAID);

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());

		Resource resource = response.getMessage().getData().getEntries().iterator().next();
		assertTrue(resource.containsKey("defProfile"));		
		String defProfile = resource.get("defProfile").toString();
		assertEquals("p_"+profile.asURI().toString(),defProfile);


	}


	@Test
	public void testDefProfileExternalAccount() throws Exception {


		Account account = createAccount(pimoService.getUserUri(),TwitterServiceAdapter.NAME);
		PersonContact profile = createProfile("Test crawled user",account);
		Person person = createPerson("Test crawled user");
		person.addGroundingOccurrence(profile);

		Response<Resource> response = controller.getAllMyPersons(SAID);

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		Resource resource = response.getMessage().getData().getEntries().iterator().next();
		assertTrue(resource.containsKey("defProfile"));		
		String defProfile = resource.get("defProfile").toString();
		assertEquals("",defProfile);

	}


}
