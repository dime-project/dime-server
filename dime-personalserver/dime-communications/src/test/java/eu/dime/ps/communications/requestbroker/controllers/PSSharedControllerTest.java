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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSSharedController;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ObjectFactory;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class PSSharedControllerTest extends Assert {

	private PSSharedController controller;
	
	@Autowired
	private Connection connection;

	@Autowired
	private PimoService pimoService;

	@Autowired
	private EntityFactory entityFactory;

	private static final String USERNAME = "adsasdasd";
	private static final String PASSWORD = "81729837128";
	private static final String SAID = "8738-ads345a-34dasda353-787g";
	
	private static final String PROFILECARD_LABEL = "My profile card";
	private static final String DATABOX_LABEL = "My databox";
	
	private Tenant dbTenant;
	private User dbUser;
	private ServiceAccount dbAccount;
	
	private final URI sender = new URIImpl("urn:account:sender");
	private final URI recipient = new URIImpl("urn:account:recipient");

	private PersonName personName;
	private EmailAddress emailAddress;
	private PhoneNumber phoneNumber;
	private PersonContact profile;
	private PrivacyPreference profileCard;
	
	private DataObject file;
	private DataContainer databox;

	@Before
	public void setUp() throws Exception {

		// create dummy data for tests
		personName = ObjectFactory.buildPersonName("Ismael Rivera");
		emailAddress = ObjectFactory.buildEmailAddress("example@email.com");
		phoneNumber = ObjectFactory.buildPhoneNumber("5555555");
		profile = ObjectFactory.buildPersonContact(personName, emailAddress, phoneNumber);
		profileCard = ObjectFactory.buildProfileCard(PROFILECARD_LABEL, new Resource[]{ personName, emailAddress, phoneNumber }, pimoService.getUserUri());
		
		file = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		databox = ObjectFactory.buildDatabox(DATABOX_LABEL, new DataObject[]{ file }, pimoService.getUserUri());
		
		dbTenant = entityFactory.buildTenant();
		dbTenant.setName("test"+System.nanoTime());
		dbTenant.persist();
		dbUser = User.findByAccountUri(recipient.toString(), dbTenant);
		if (dbUser == null) {
			dbUser = entityFactory.buildUser();
			dbUser.setTenant(dbTenant);
			dbUser.setEnabled(true);
			dbUser.setRole(Role.GUEST);
			dbUser.setUsername(USERNAME);
			dbUser.setPassword(PASSWORD);
			dbUser.setAccountUri(recipient.toString());
			dbUser.persist();
		}
		dbAccount = ServiceAccount.findByName(SAID);
		if (dbAccount == null) {
			dbAccount = entityFactory.buildServiceAccount();
			dbAccount.setTenant(dbTenant);
			dbAccount.setEnabled(true);
			dbAccount.setName(SAID);
			dbAccount.setAccountURI(sender.toString());
			dbAccount.persist();
		}

		// set up authentication and request data in the thread local holders
        SecurityContextHolder.getContext().setAuthentication(
        		new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD));
    	TenantContextHolder.setTenant(dbTenant.getId());

    	// mocking managers
    	ShareableProfileManager shareableProfileManager = mock(ShareableProfileManager.class);
		when(shareableProfileManager.get(profileCard.toString(), recipient.toString())).thenReturn(profile);
		when(shareableProfileManager.getAll(sender.toString(), recipient.toString())).thenReturn(Arrays.asList(profile));

    	ShareableDataboxManager shareableDataboxManager = mock(ShareableDataboxManager.class);
    	when(shareableDataboxManager.get(databox.toString(), recipient.toString())).thenReturn(databox);

		controller = new PSSharedController();
		controller.setShareableProfileManager(shareableProfileManager);
		controller.setShareableDataboxManager(shareableDataboxManager);
	}
	
	@After
	public void tearDown() throws Exception {
		try {
			if (dbAccount != null) dbAccount.remove();
		} catch (Exception e) {}
		try {
			if (dbUser != null) dbUser.remove();
		} catch (Exception e) {}
		try {
			if (dbTenant != null) dbTenant.remove();
		} catch (Exception e) {}
	}
	
	@Test
	public void testGetProfileJSONLD() throws Exception {
		Object response = controller.getProfileJSONLD(SAID);
		
		assertNotNull(response);
		assertTrue(response instanceof List);
		
		List jsonld = (List) response;
		assertEquals(4, jsonld.size()); // PersonContact + 3 attributes

		List<String> ids = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for (Object entry : jsonld) {
			Map<String, Object> data = (Map<String, Object>) entry;
			ids.add(data.get("@id").toString());
			types.add(data.get("@type").toString());
		}
		
		assertTrue(ids.contains(profile.toString()));
		assertTrue(ids.contains(personName.toString()));
		assertTrue(ids.contains(emailAddress.toString()));
		assertTrue(ids.contains(phoneNumber.toString()));

		assertTrue(types.contains("nco:PersonContact"));
		assertTrue(types.contains("nco:PersonName"));
		assertTrue(types.contains("nco:EmailAddress"));
		assertTrue(types.contains("nco:PhoneNumber"));
	}

}
