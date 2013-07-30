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

package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.ProfileCard;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.jfix.util.Arrays;


public class PSProfileControllerTestIt extends PSInfosphereControllerTestIt {

	private static final String SAID = "juan";

	@Autowired
	private ProfileAttributeManager profileAttributeManager;

	@Autowired
	private ProfileCardManager profileCardManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private SharingManager sharingManager;
	
	private PSProfileController controller;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();		
		
		// set up PSProfileController
		controller = new PSProfileController();
		controller.setAccountManager(accountManager);
		controller.setPersonManager(personManager);
		controller.setProfileCardManager(profileCardManager);
		controller.setSharingManager(sharingManager);
	}
	
	private Collection<Map<String, Object>> buildIncludes(Account sender, Person...persons) {
		Map<String, Object> include = new HashMap<String, Object>();
		include.put("saidSender", sender.toString());
		include.put("groups", Collections.EMPTY_LIST);
		include.put("services", Collections.EMPTY_LIST);
		
		Collection<Map<String, Object>> personsArray = new ArrayList<Map<String, Object>>(persons.length);
		for (Person person : persons) {
			Map<String, Object> personMap = new HashMap<String, Object>();
			personMap.put("personId", person.toString());
			personMap.put("saidReceiver",null);
			personsArray.add(personMap);
		}
		include.put("persons", personsArray);

		Collection<Map<String, Object>> includes = new ArrayList<Map<String, Object>>(1);
		includes.add(include);
		return includes;
	}
	
	
	@Test
	@Ignore //FIXME need to allow to persist in the database
	public void testCreateProfileWellFormedRDF() throws Exception {
		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");

		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname("Ismael Rivera");
		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber("555-55-55-55");
		profileAttributeManager.add(personName);
		profileAttributeManager.add(phoneNumber);
		
		ProfileCard profileCard = new ProfileCard();
		profileCard.put("guid", "garbage"); // on create, even if passed, this should be ignored
		profileCard.put("type", "profile");
		profileCard.put("created", 1338824999);
		profileCard.put("lastModified", 1338824999);
		profileCard.put("name", "Test profile card");
		profileCard.put("userId", "@me");
		profileCard.put("editable", true);
		profileCard.put("items", Arrays.asList(new String[]{ personName.toString(), phoneNumber.toString() }));
		profileCard.put("nao:includes", buildIncludes(sender, person));
		
		Request<Resource> request = buildRequest(profileCard);
		Response<Resource> response = controller.createProfile(SAID, request);
		
		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());
		
		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);
		assertTrue(guid.startsWith("pc_"));
		
		URI uri = new URIImpl(guid.replace("pc_urn", "urn"));
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		// verify PrivacyPreference metadata is correct
		assertTrue(pimo.contains(uri, RDF.type, PPO.PrivacyPreference));
		assertTrue(pimo.contains(uri, RDFS.label, PrivacyPreferenceType.PROFILECARD.toString()));
		assertTrue(pimo.contains(uri, NAO.prefLabel, "Test profile card"));
		assertTrue(pimo.contains(uri, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(pimo.contains(uri, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(pimo.contains(uri, PPO.appliesToResource, personName));
		assertTrue(pimo.contains(uri, PPO.appliesToResource, phoneNumber));
		
		// verify AccessSpace metadata (of PrivacyPreference) is correct
		assertTrue(pimo.contains(uri, PPO.hasAccessSpace, Variable.ANY));
		URI accessSpace = ModelUtils.findObject(pimo, uri, PPO.hasAccessSpace).asURI();
		assertTrue(pimo.contains(accessSpace, NSO.sharedThrough, sender.asURI()));
		assertTrue(pimo.contains(accessSpace, NSO.includes, person.asURI()));
	}
	
	
	@Ignore
	@Test
	public void testUpdateProfileCardFormedRDF() throws ResourceExistsException, InfosphereException  {
		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");

		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname("Ismael Rivera");
		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber("555-55-55-55");
		profileAttributeManager.add(personName);
		profileAttributeManager.add(phoneNumber);
		
		ProfileCard profileCard = new ProfileCard();
		profileCard.put("guid", "garbage"); // on create, even if passed, this should be ignored
		profileCard.put("type", "profile");
		profileCard.put("created", 1338824999);
		profileCard.put("lastModified", 1338824999);
		profileCard.put("name", "Test profile card");
		profileCard.put("userId", "@me");
		profileCard.put("editable", true);
		profileCard.put("items", Arrays.asList(new String[]{ personName.toString(), phoneNumber.toString() }));
		profileCard.put("nao:includes", buildIncludes(sender, person));
		
		Request<Resource> request = buildRequest(profileCard);
		Response<Resource> response = controller.createProfile(SAID, request);
		
		EmailAddress email = modelFactory.getNCOFactory().createEmailAddress();
		email.setEmailAddress("test@mail.com");
		profileAttributeManager.add(email);
		Resource updateProfileCard= response.getMessage().getData().getEntries().iterator().next();
		updateProfileCard.put("items", Arrays.asList(new String[]{ personName.toString(), phoneNumber.toString(),email.toString() }));
		
		
		Request<Resource> updateRequest = buildRequest(updateProfileCard);
		Response<Resource> updateResponse = controller.updateMyProfile(SAID, updateRequest,"pc_"+updateProfileCard.get("guid"));
		
		
		assertNotNull(updateResponse);
		assertEquals(1, updateResponse.getMessage().getData().getEntries().size());
		
		String guid = updateResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);
		assertTrue(guid.startsWith("pc_"));
		
		URI uri = new URIImpl(guid.replace("pc_urn", "urn"));
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		// verify PrivacyPreference metadata is correct
		assertTrue(pimo.contains(uri, RDF.type, PPO.PrivacyPreference));
		assertTrue(pimo.contains(uri, RDFS.label, PrivacyPreferenceType.PROFILECARD.toString()));
		assertTrue(pimo.contains(uri, NAO.prefLabel, "Test profile card"));
		assertTrue(pimo.contains(uri, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(pimo.contains(uri, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(pimo.contains(uri, PPO.appliesToResource, personName));
		assertTrue(pimo.contains(uri, PPO.appliesToResource, phoneNumber));
		assertTrue(pimo.contains(uri, PPO.appliesToResource, email));
		
		// verify AccessSpace metadata (of PrivacyPreference) is correct
		assertTrue(pimo.contains(uri, PPO.hasAccessSpace, Variable.ANY));
		URI accessSpace = ModelUtils.findObject(pimo, uri, PPO.hasAccessSpace).asURI();
		assertTrue(pimo.contains(accessSpace, NSO.sharedThrough, sender.asURI()));
		assertTrue(pimo.contains(accessSpace, NSO.includes, person.asURI()));
		
		
		
	}
	
}
