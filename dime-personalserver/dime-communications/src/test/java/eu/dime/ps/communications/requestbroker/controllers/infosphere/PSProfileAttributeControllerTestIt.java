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
import ie.deri.smile.vocabulary.GEO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.dto.ProfileAttribute;
import eu.dime.ps.semantic.model.geo.Point;
import eu.dime.ps.semantic.model.nco.Affiliation;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.Hobby;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nco.PostalAddress;
import eu.dime.ps.semantic.model.nco.VoicePhoneNumber;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;


public class PSProfileAttributeControllerTestIt extends PSInfosphereControllerTestIt {

	private static final String SAID = "juan";

	@Autowired
	private ProfileAttributeManager profileAttributeManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private ProfileManager profileManager;

	@Autowired
	private ProfileCardManager profileCardManager;

	private PSProfileAttributeController controller;



	@Before
	public void setUp() throws Exception {
		super.setUp();

		// set up PSProfileController
		controller = new PSProfileAttributeController();		
		controller.setPersonManager(personManager);		
		controller.setProfileManager(profileManager);
		controller.setProfileAttributeManager(profileAttributeManager);
		controller.setProfileCardManager(profileCardManager);
	}

	@After
	public void tearDown() throws Exception {
		Collection<org.ontoware.rdfreactor.schema.rdfs.Resource> profileAttributes = profileAttributeManager.getAll();
		for (org.ontoware.rdfreactor.schema.rdfs.Resource profileAttribute: profileAttributes){
			profileAttributeManager.remove(profileAttribute.asURI().toString());		
		}
		Collection<PersonContact> profiles = profileManager.getAll();
		for (PersonContact profile: profiles){
			profileManager.remove(profile.asURI().toString());		
		}

		Collection<PrivacyPreference> profilecards = profileCardManager.getAll();
		for (PrivacyPreference profilecard: profilecards){
			profileCardManager.remove(profilecard.asURI().toString());		
		}

		super.tearDown();

	}


	
	@Test
	public void testPersonNameBadFormed() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPersonName,Variable.ANY));


		//create PersonName

		ProfileAttribute personName = new ProfileAttribute();
		personName.put("guid", "garbage"); // on create, even if passed, this should be ignored
		personName.put("type", "profileattribute");
		personName.put("category", "PersonName");
		personName.put("created", 1338824999);
		personName.put("lastModified", 1338824999);
		//	personName.put("name", "FriendPersonName");	
		personName.put("userId", "@me");

		HashMap<String,Object> personNameValues = new HashMap<String,Object>();
		

		personName.put("value",personNameValues);
		
			//create the attributes
		Request<ProfileAttribute> personNameRequest = buildAttributeRequest(personName);
		Response<ProfileAttribute> personNameResponse = controller.createProfileAttribute(SAID, personNameRequest, "p_"+profile.asURI().toString());

		assertFalse(pimo.contains(profile, NCO.hasPersonName,Variable.ANY));
		
		assertNotNull(personNameResponse);
		assertNull( personNameResponse.getMessage().getData());		

		

	}

	@Test
	public void testPersonNameWellFormedRDF() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPersonName,Variable.ANY));


		//create PersonName

		ProfileAttribute personName = new ProfileAttribute();
		personName.put("guid", "garbage"); // on create, even if passed, this should be ignored
		personName.put("type", "profileattribute");
		personName.put("category", "PersonName");
		personName.put("created", 1338824999);
		personName.put("lastModified", 1338824999);
		//	personName.put("name", "FriendPersonName");	
		personName.put("userId", "@me");

		HashMap<String,Object> personNameValues = new HashMap<String,Object>();
		personNameValues.put("nickname", "juanito");
		personNameValues.put("fullname", "juan gomez");
		personNameValues.put("nameHonorificSuffix", "");
		personNameValues.put("nameFamily", "gomez");
		personNameValues.put("nameHonorificPrefix", "Sr");
		personNameValues.put("nameAdditional", "additional");
		personNameValues.put("nameGiven", "Juan");

		personName.put("value",personNameValues);	

		//create the attributes
		Request<ProfileAttribute> personNameRequest = buildAttributeRequest(personName);
		Response<ProfileAttribute> personNameResponse = controller.createProfileAttribute(SAID, personNameRequest, "p_"+profile.asURI().toString());


		assertNotNull(personNameResponse);
		assertEquals(1, personNameResponse.getMessage().getData().getEntries().size());		

		//verify the personName has been created
		String guid = personNameResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);

		assertTrue(pimo.contains(uri, RDF.type,NCO.PersonName));		
		assertTrue(pimo.contains(uri, NAO.prefLabel,""));
		//assertTrue(pimo.contains(uri, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));	
		assertTrue(pimo.contains(uri, NCO.nameHonorificSuffix, ""));
		assertTrue(pimo.contains(uri, NCO.nameFamily,"gomez"));
		assertTrue(pimo.contains(uri, NCO.nameHonorificPrefix,"Sr"));
		assertTrue(pimo.contains(uri, NCO.nameAdditional,"additional"));
		assertTrue(pimo.contains(uri, NCO.nameGiven,"Juan"));
		assertTrue(pimo.contains(uri, NCO.fullname,"juan gomez"));
		assertTrue(pimo.contains(uri, NCO.nickname,"juanito"));
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPersonName,uri));	

	}
	
	@Test
	public void testPersonNameWithNouserIdWellFormedRDF() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPersonName,Variable.ANY));


		//create PersonName

		ProfileAttribute personName = new ProfileAttribute();
		personName.put("guid", "garbage"); // on create, even if passed, this should be ignored
		personName.put("type", "profileattribute");
		personName.put("category", "PersonName");
		personName.put("created", 1338824999);
		personName.put("lastModified", 1338824999);
		//	personName.put("name", "FriendPersonName");	
	

		HashMap<String,Object> personNameValues = new HashMap<String,Object>();
		personNameValues.put("nickname", "juanito");
		personNameValues.put("fullname", "juan gomez");
		personNameValues.put("nameHonorificSuffix", "");
		personNameValues.put("nameFamily", "gomez");
		personNameValues.put("nameHonorificPrefix", "Sr");
		personNameValues.put("nameAdditional", "additional");
		personNameValues.put("nameGiven", "Juan");

		personName.put("value",personNameValues);	

		//create the attributes
		Request<ProfileAttribute> personNameRequest = buildAttributeRequest(personName);
		Response<ProfileAttribute> personNameResponse = controller.createProfileAttribute(SAID, personNameRequest, "p_"+profile.asURI().toString());


		assertNotNull(personNameResponse);
		assertEquals(1, personNameResponse.getMessage().getData().getEntries().size());		

		//verify the personName has been created
		String guid = personNameResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);

		assertTrue(pimo.contains(uri, RDF.type,NCO.PersonName));
		assertTrue(pimo.contains(uri, NAO.creator,"urn:chuck:ChuckNorris"));
		assertTrue(pimo.contains(uri, NAO.prefLabel,""));
		//assertTrue(pimo.contains(uri, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));	
		assertTrue(pimo.contains(uri, NCO.nameHonorificSuffix, ""));
		assertTrue(pimo.contains(uri, NCO.nameFamily,"gomez"));
		assertTrue(pimo.contains(uri, NCO.nameHonorificPrefix,"Sr"));
		assertTrue(pimo.contains(uri, NCO.nameAdditional,"additional"));
		assertTrue(pimo.contains(uri, NCO.nameGiven,"Juan"));
		assertTrue(pimo.contains(uri, NCO.fullname,"juan gomez"));
		assertTrue(pimo.contains(uri, NCO.nickname,"juanito"));
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPersonName,uri));	

	}

	@Test
	public void testBirthDateWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);		

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasBirthDate,Variable.ANY));


		//create BirthDate

		ProfileAttribute birthDate = new ProfileAttribute();
		birthDate.put("guid", "garbage"); // on create, even if passed, this should be ignored
		birthDate.put("type", "profileattribute");
		birthDate.put("category", "BirthDate");
		birthDate.put("created", 1338824999);
		birthDate.put("lastModified", 1338824999);
		birthDate.put("name", "FriendBirthdate");
		birthDate.put("userId", "@me");

		HashMap<String,Object> birthDateValues = new HashMap<String,Object>();
		birthDateValues.put("birthDate", "13-04-1989");	
		birthDateValues.put("age", 40);	
		birthDate.put("value",birthDateValues);	

		Request<ProfileAttribute> birthDateRequest = buildAttributeRequest(birthDate);
		Response<ProfileAttribute> birthDateResponse = controller.createProfileAttribute(SAID, birthDateRequest, "p_"+profile.asURI().toString());


		assertNotNull(birthDateResponse);
		assertEquals(1, birthDateResponse.getMessage().getData().getEntries().size());

		//verify the birthDate has been created
		String guid = birthDateResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);

		assertTrue(pimo.contains(uri, RDF.type,NCO.BirthDate));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "FriendBirthdate"));		
		assertTrue(pimo.contains(uri, NCO.birthDate,new DatatypeLiteralImpl("13-04-1989", XSD._dateTime)));	
		assertTrue(pimo.contains(uri, NCO.age,"40"));

		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasBirthDate,uri));	


	}


	@Test
	public void testBirthDateProfileErase() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);		


		//set profile as default
		Person me = profileManager.getMe();
		me.addGroundingOccurrence(profile);
		pimoService.update(me);
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasBirthDate,Variable.ANY));


		//create BirthDate

		ProfileAttribute birthDate = new ProfileAttribute();
		birthDate.put("guid", "garbage"); // on create, even if passed, this should be ignored
		birthDate.put("type", "profileattribute");
		birthDate.put("category", "BirthDate");
		birthDate.put("created", 1338824999);
		birthDate.put("lastModified", 1338824999);
		birthDate.put("name", "FriendBirthdate");
		birthDate.put("userId", "@me");

		HashMap<String,Object> birthDateValues = new HashMap<String,Object>();
		birthDateValues.put("birthDate", "13-04-1989");	
		birthDateValues.put("age", 40);	
		birthDate.put("value",birthDateValues);	

		Request<ProfileAttribute> birthDateRequest = buildAttributeRequest(birthDate);
		Response<ProfileAttribute> birthDateResponse = controller.createProfileAttribute(SAID, birthDateRequest, "@me");


		assertNotNull(birthDateResponse);
		assertEquals(1, birthDateResponse.getMessage().getData().getEntries().size());

		//verify the birthDate has been created
		String guid = birthDateResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);

		assertTrue(pimo.contains(uri, RDF.type,NCO.BirthDate));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "FriendBirthdate"));		
		assertTrue(pimo.contains(uri, NCO.birthDate,new DatatypeLiteralImpl("13-04-1989", XSD._dateTime)));	
		assertTrue(pimo.contains(uri, NCO.age,"40"));

		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasBirthDate,uri));	

		//verify the profile is still set as defProfile
		assertTrue(pimo.contains(me,PIMO.groundingOccurrence,profile));

		//create PhoneNumber

		ProfileAttribute phoneNumber = new ProfileAttribute();
		phoneNumber.put("guid", "garbage"); // on create, even if passed, this should be ignored
		phoneNumber.put("type", "profileattribute");
		phoneNumber.put("category", "PhoneNumber");
		phoneNumber.put("created", 1338824999);
		phoneNumber.put("lastModified", 1338824999);
		phoneNumber.put("name", "WorkphoneNumber");
		phoneNumber.put("userId", "@me");

		HashMap<String,Object> phoneNumberValues = new HashMap<String,Object>();
		phoneNumberValues.put("phoneNumber", "4567868");

		phoneNumber.put("value",phoneNumberValues);	


		Request<ProfileAttribute> phoneNumberRequest = buildAttributeRequest(phoneNumber);
		Response<ProfileAttribute> phoneNumberResponse = controller.createProfileAttribute(SAID, phoneNumberRequest, "@me");


		//verify the phoneNumber has been created
		String phoneGuid = phoneNumberResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(phoneGuid);				
		URI phoneUri = new URIImpl(phoneGuid);
		assertTrue(pimo.contains(phoneUri, RDF.type,NCO.PhoneNumber));		
		assertTrue(pimo.contains(phoneUri, NAO.prefLabel, "WorkphoneNumber"));		
		assertTrue(pimo.contains(phoneUri, NCO.phoneNumber, "4567868"));					
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPhoneNumber,phoneUri));	

		//verify the profile is still set as defProfile
		assertTrue(pimo.contains(me,PIMO.groundingOccurrence,profile));		


	}

	@Test
	public void testPhoneNumberWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPhoneNumber,Variable.ANY));

		//create PhoneNumber

		ProfileAttribute phoneNumber = new ProfileAttribute();
		phoneNumber.put("guid", "garbage"); // on create, even if passed, this should be ignored
		phoneNumber.put("type", "profileattribute");
		phoneNumber.put("category", "PhoneNumber");
		phoneNumber.put("created", 1338824999);
		phoneNumber.put("lastModified", 1338824999);
		phoneNumber.put("name", "WorkphoneNumber");
		phoneNumber.put("userId", "@me");

		HashMap<String,Object> phoneNumberValues = new HashMap<String,Object>();
		phoneNumberValues.put("phoneNumber", "4567868");

		phoneNumber.put("value",phoneNumberValues);	


		Request<ProfileAttribute> phoneNumberRequest = buildAttributeRequest(phoneNumber);
		Response<ProfileAttribute> phoneNumberResponse = controller.createProfileAttribute(SAID, phoneNumberRequest, "p_"+profile.asURI().toString());


		assertEquals(1, phoneNumberResponse.getMessage().getData().getEntries().size());

		//verify the phoneNumber has been created
		String guid = phoneNumberResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.PhoneNumber));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "WorkphoneNumber"));		
		assertTrue(pimo.contains(uri, NCO.phoneNumber, "4567868"));					
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPhoneNumber,uri));		
	}

	@Test
	public void testVoiceMailWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPhoneNumber,Variable.ANY));

		//create VoiceMail

		ProfileAttribute voiceMail = new ProfileAttribute();
		voiceMail.put("guid", "garbage"); // on create, even if passed, this should be ignored
		voiceMail.put("type", "profileattribute");
		voiceMail.put("category", "VoiceMail");
		voiceMail.put("created", 1338824999);
		voiceMail.put("lastModified", 1338824999);
		voiceMail.put("name", "voiceMailnumber");
		voiceMail.put("userId", "@me");

		HashMap<String,Object> voiceMailValues = new HashMap<String,Object>();
		voiceMailValues.put("phoneNumber", "4567868");
		voiceMailValues.put("voiceMail", true);

		voiceMail.put("value",voiceMailValues);	


		Request<ProfileAttribute> voiceMailRequest = buildAttributeRequest(voiceMail);
		Response<ProfileAttribute> voiceMailrResponse = controller.createProfileAttribute(SAID, voiceMailRequest, "p_"+profile.asURI().toString());


		assertEquals(1, voiceMailrResponse.getMessage().getData().getEntries().size());

		//verify the voiceMail has been created
		String guid = voiceMailrResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.VoicePhoneNumber));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "voiceMailnumber"));		
		assertTrue(pimo.contains(uri, NCO.phoneNumber, "4567868"));
		assertTrue(pimo.contains(uri, NCO.voiceMail, Variable.ANY));
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPhoneNumber,uri));		
	}

	@Test
	public void testEmailWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		//set profile as default
		Person me = profileManager.getMe();
		me.addGroundingOccurrence(profile);
		pimoService.update(me);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasEmailAddress,Variable.ANY));

		//create Email

		ProfileAttribute email = new ProfileAttribute();
		email.put("guid", "garbage"); // on create, even if passed, this should be ignored
		email.put("type", "profileattribute");
		email.put("category", "EmailAddress");
		email.put("created", 1338824999);
		email.put("lastModified", 1338824999);
		email.put("name", "Email");
		email.put("imageUrl", "");
		email.put("items",new ArrayList<String>());
		email.put("userId", "");

		HashMap<String,Object> emailValues = new HashMap<String,Object>();
		emailValues.put("emailAddress","test@test.de");	

		email.put("value",emailValues);	

		Request<ProfileAttribute> emailRequest = buildAttributeRequest(email);
		Response<ProfileAttribute> emailResponse = controller.createProfileAttribute(SAID, emailRequest,"@me");

		assertEquals(1, emailResponse.getMessage().getData().getEntries().size());

		//verify the Email has been created
		String guid = emailResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.EmailAddress));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "Email"));		
		assertTrue(pimo.contains(uri, NCO.emailAddress, "test@test.de"));					
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasEmailAddress,uri));

	}

	@Test
	public void testProfileCardEmailWellFormedRDF() throws Exception {
		//CREATE THE PROFILECARD
		PrivacyPreference profileCard = buildProfileCard("John profile");
		profileCardManager.add(profileCard);


		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profileCard, PPO.appliesToResource,Variable.ANY));

		//create Email

		ProfileAttribute email = new ProfileAttribute();
		email.put("guid", "garbage"); // on create, even if passed, this should be ignored
		email.put("type", "profileattribute");
		email.put("category", "EmailAddress");
		email.put("created", 1338824999);
		email.put("lastModified", 1338824999);
		email.put("name", "Email");
		email.put("imageUrl", "");
		email.put("items",new ArrayList<String>());
		email.put("userId", "");

		HashMap<String,Object> emailValues = new HashMap<String,Object>();
		emailValues.put("emailAddress","test@test.de");	

		email.put("value",emailValues);	

		Request<ProfileAttribute> emailRequest = buildAttributeRequest(email);
		Response<ProfileAttribute> emailResponse = controller.createProfileAttribute(SAID, emailRequest,"pc_"+profileCard.asURI().toString());

		assertEquals(1, emailResponse.getMessage().getData().getEntries().size());

		//verify the Email has been created
		String guid = emailResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.EmailAddress));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "Email"));		
		assertTrue(pimo.contains(uri, NCO.emailAddress, "test@test.de"));					
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,uri));

	}

	@Test
	public void testAffiliationWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasAffiliation,Variable.ANY));

		//create Affiliation

		ProfileAttribute affiliation = new ProfileAttribute();
		affiliation.put("guid", "garbage"); // on create, even if passed, this should be ignored
		affiliation.put("type", "profileattribute");
		affiliation.put("category", "Affiliation");
		affiliation.put("created", 1338824999);
		affiliation.put("lastModified", 1338824999);
		affiliation.put("name", "OrganizationAffiliation");
		affiliation.put("userId", "@me");		

		HashMap<String,Object> affiliationValues =  new HashMap<String,Object>();
		affiliationValues.put("department","depart test");
		affiliationValues.put("title","title test");
		affiliationValues.put("role","role test");
		affiliationValues.put("org","organitzation test");

		affiliation.put("value",affiliationValues);

		Request<ProfileAttribute> affiliationRequest = buildAttributeRequest(affiliation);
		Response<ProfileAttribute> affiliationResponse = controller.createProfileAttribute(SAID, affiliationRequest,"p_"+profile.asURI().toString());

		assertEquals(1, affiliationResponse.getMessage().getData().getEntries().size());

		//verify the Email has been created
		String guid = affiliationResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.Affiliation));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "OrganizationAffiliation"));		
		assertTrue(pimo.contains(uri, NCO.department, "depart test"));
		assertTrue(pimo.contains(uri, NCO.title, "title test"));
		assertTrue(pimo.contains(uri, NCO.role, "role test"));
		assertTrue(pimo.contains(uri, NCO.org, "organitzation test"));

		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasAffiliation,uri));

	}

	@Test
	public void testPostalAddressWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("isma profile");
		profileManager.add(profile);


		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasPostalAddress,Variable.ANY));

		//create PostalAddress

		ProfileAttribute postalAddress = new ProfileAttribute();
		postalAddress.put("guid", "garbage"); // on create, even if passed, this should be ignored
		postalAddress.put("type", "profileattribute");
		postalAddress.put("category", "PostalAddress");
		postalAddress.put("created", 1338824999);
		postalAddress.put("lastModified", 1338824999);
		postalAddress.put("name", "FriendPostalAddress");
		postalAddress.put("userId", "@me");

		HashMap<String,Object> postalAddressValues = new HashMap<String,Object>();
		postalAddressValues.put("region", "testintong");
		postalAddressValues.put("country", "testlandia");
		postalAddressValues.put("extendedAddress", "test st. 2");
		postalAddressValues.put("addressLocation", "testonia");
		postalAddressValues.put("streetAddress", "test st.");

		postalAddress.put("value",postalAddressValues);		

		Request<ProfileAttribute> postalAddressRequest = buildAttributeRequest(postalAddress);
		Response<ProfileAttribute> postalAddressResponse = controller.createProfileAttribute(SAID, postalAddressRequest, "p_"+profile.asURI().toString());

		assertEquals(1, postalAddressResponse.getMessage().getData().getEntries().size());

		//verify the PostalAddress has been created
		String guid = postalAddressResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.PostalAddress));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "FriendPostalAddress"));		
		assertTrue(pimo.contains(uri, NCO.region, "testintong"));
		assertTrue(pimo.contains(uri, NCO.country, "testlandia"));
		assertTrue(pimo.contains(uri, NCO.extendedAddress, "test st. 2"));
		assertTrue(pimo.contains(uri, NCO.addressLocation, "testonia"));
		assertTrue(pimo.contains(uri, NCO.streetAddress, "test st."));
		//verify the profile contains the attribute
		assertTrue(pimo.contains(profile, NCO.hasPostalAddress,uri));
	}

	@Test
	public void testHobbyWellFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hobby,Variable.ANY));

		//create Hobby

		ProfileAttribute hobby = new ProfileAttribute();
		hobby.put("guid", "garbage"); // on create, even if passed, this should be ignored
		hobby.put("type", "profileattribute");
		hobby.put("category", "Hobby");
		hobby.put("created", 1338824999);
		hobby.put("lastModified", 1338824999);
		hobby.put("name", "Hobby test");
		hobby.put("imageUrl", "");
		hobby.put("items",new ArrayList<String>());
		hobby.put("userId", "@me");

		HashMap<String,Object> hobbyValues = new HashMap<String,Object>();
		hobbyValues.put("hobby","tennis");	

		hobby.put("value",hobbyValues);	

		Request<ProfileAttribute> hobbyRequest = buildAttributeRequest(hobby);
		Response<ProfileAttribute> hobbyResponse = controller.createProfileAttribute(SAID, hobbyRequest,"p_"+profile.asURI().toString());

		assertEquals(1, hobbyResponse.getMessage().getData().getEntries().size());

		//verify the Hobby has been created
		String guid = hobbyResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,NCO.Hobby));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "tennis"));
		assertFalse(pimo.contains(uri, NAO.prefLabel, "Hobby test"));
		assertTrue(pimo.contains(profile, NCO.hobby,uri));

	}

	@Test	
	public void testLocationFormedRDF() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasLocation,Variable.ANY));

		//create Point

		ProfileAttribute point = new ProfileAttribute();
		point.put("guid", "garbage"); // on create, even if passed, this should be ignored
		point.put("type", "profileattribute");
		point.put("category", "Location");
		point.put("created", 1338824999);
		point.put("lastModified", 1338824999);
		point.put("name", "location test");
		point.put("imageUrl", "");
		point.put("items",new ArrayList<String>());
		point.put("userId", "@me");

		HashMap<String,Object> pointValues = new HashMap<String,Object>();
		pointValues.put("lat",41.089f);	
		pointValues.put("lon",54.112f);	
		point.put("value",pointValues);	

		Request<ProfileAttribute> pointRequest = buildAttributeRequest(point);
		Response<ProfileAttribute> pointResponse = controller.createProfileAttribute(SAID, pointRequest,"p_"+profile.asURI().toString());

		assertEquals(1, pointResponse.getMessage().getData().getEntries().size());

		//verify the Email has been created
		String guid = pointResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);				
		URI uri = new URIImpl(guid);
		assertTrue(pimo.contains(uri, RDF.type,GEO.Point));		
		assertTrue(pimo.contains(uri, NAO.prefLabel, "location test"));		
		assertTrue(pimo.contains(uri, GEO.lat, new DatatypeLiteralImpl("41.089",XSD._float)));
		assertTrue(pimo.contains(uri, GEO.lon, new DatatypeLiteralImpl("54.112",XSD._float)));
		assertTrue(pimo.contains(profile, NCO.hasLocation,uri));

	}

	@Test	
	public void testAllAttributesInSameProfile() throws Exception {
		//CREATE THE PROFILE
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		//set profile as default
		Person me = profileManager.getMe();
		me.addGroundingOccurrence(profile);
		pimoService.update(me);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profile, NCO.hasLocation,Variable.ANY));

		//create PersonName
		ProfileAttribute personName = new ProfileAttribute();
		personName.put("guid", "garbage"); // on create, even if passed, this should be ignored
		personName.put("type", "profileattribute");
		personName.put("category", "PersonName");
		personName.put("created", 1338824999);
		personName.put("lastModified", 1338824999);
		//	personName.put("name", "FriendPersonName");	
		personName.put("userId", "@me");

		HashMap<String,Object> personNameValues = new HashMap<String,Object>();
		personNameValues.put("nickname", "juanito");
		personNameValues.put("fullname", "juan gomez");
		personNameValues.put("nameHonorificSuffix", "");
		personNameValues.put("nameFamily", "gomez");
		personNameValues.put("nameHonorificPrefix", "Sr");
		personNameValues.put("nameAdditional", "additional");
		personNameValues.put("nameGiven", "Juan");

		personName.put("value",personNameValues);	

		//create the attributes
		Request<ProfileAttribute> personNameRequest = buildAttributeRequest(personName);
		Response<ProfileAttribute> personNameResponse = controller.createProfileAttribute(SAID, personNameRequest, "@me");


		//verify the personName has been created
		String nameguid = personNameResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(nameguid);

		URI nameuri = new URIImpl(nameguid);

		//create BirthDate
		ProfileAttribute birthDate = new ProfileAttribute();
		birthDate.put("guid", "garbage"); // on create, even if passed, this should be ignored
		birthDate.put("type", "profileattribute");
		birthDate.put("category", "BirthDate");
		birthDate.put("created", 1338824999);
		birthDate.put("lastModified", 1338824999);
		birthDate.put("name", "FriendBirthdate");
		birthDate.put("userId", "@me");

		HashMap<String,Object> birthDateValues = new HashMap<String,Object>();
		birthDateValues.put("birthDate", "13-04-1989");	
		birthDateValues.put("age", 40);	
		birthDate.put("value",birthDateValues);	

		Request<ProfileAttribute> birthDateRequest = buildAttributeRequest(birthDate);
		Response<ProfileAttribute> birthDateResponse = controller.createProfileAttribute(SAID, birthDateRequest, "@me");


		//verify the birthDate has been created
		String birthguid = birthDateResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(birthguid);

		URI birthuri = new URIImpl(birthguid);


		//create PhoneNumber

		ProfileAttribute phoneNumber = new ProfileAttribute();
		phoneNumber.put("guid", "garbage"); // on create, even if passed, this should be ignored
		phoneNumber.put("type", "profileattribute");
		phoneNumber.put("category", "PhoneNumber");
		phoneNumber.put("created", 1338824999);
		phoneNumber.put("lastModified", 1338824999);
		phoneNumber.put("name", "WorkphoneNumber");
		phoneNumber.put("userId", "@me");

		HashMap<String,Object> phoneNumberValues = new HashMap<String,Object>();
		phoneNumberValues.put("phoneNumber", "4567868");

		phoneNumber.put("value",phoneNumberValues);	


		Request<ProfileAttribute> phoneNumberRequest = buildAttributeRequest(phoneNumber);
		Response<ProfileAttribute> phoneNumberResponse = controller.createProfileAttribute(SAID, phoneNumberRequest, "@me");

		//verify the phoneNumber has been created
		String phoneguid = phoneNumberResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(phoneguid);				
		URI phoneuri = new URIImpl(phoneguid);

		//create VoiceMail

		ProfileAttribute voiceMail = new ProfileAttribute();
		voiceMail.put("guid", "garbage"); // on create, even if passed, this should be ignored
		voiceMail.put("type", "profileattribute");
		voiceMail.put("category", "VoiceMail");
		voiceMail.put("created", 1338824999);
		voiceMail.put("lastModified", 1338824999);
		voiceMail.put("name", "voiceMailnumber");
		voiceMail.put("userId", "@me");

		HashMap<String,Object> voiceMailValues = new HashMap<String,Object>();
		voiceMailValues.put("phoneNumber", "4567868");
		voiceMailValues.put("voiceMail", true);

		voiceMail.put("value",voiceMailValues);	


		Request<ProfileAttribute> voiceMailRequest = buildAttributeRequest(voiceMail);
		Response<ProfileAttribute> voiceMailrResponse = controller.createProfileAttribute(SAID, voiceMailRequest, "@me");

		//verify the voiceMail has been created
		String voiceguid = voiceMailrResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(voiceguid);				
		URI voiceuri = new URIImpl(voiceguid);

		//create Email

		ProfileAttribute email = new ProfileAttribute();
		email.put("guid", "garbage"); // on create, even if passed, this should be ignored
		email.put("type", "profileattribute");
		email.put("category", "EmailAddress");
		email.put("created", 1338824999);
		email.put("lastModified", 1338824999);
		email.put("name", "Email");
		email.put("imageUrl", "");
		email.put("items",new ArrayList<String>());
		email.put("userId", "");

		HashMap<String,Object> emailValues = new HashMap<String,Object>();
		emailValues.put("emailAddress","test@test.de");	

		email.put("value",emailValues);	

		Request<ProfileAttribute> emailRequest = buildAttributeRequest(email);
		Response<ProfileAttribute> emailResponse = controller.createProfileAttribute(SAID, emailRequest,"@me");

		//verify the Email has been created
		String mailguid = emailResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(mailguid);				
		URI mailuri = new URIImpl(mailguid);


		//create Affiliation

		ProfileAttribute affiliation = new ProfileAttribute();
		affiliation.put("guid", "garbage"); // on create, even if passed, this should be ignored
		affiliation.put("type", "profileattribute");
		affiliation.put("category", "Affiliation");
		affiliation.put("created", 1338824999);
		affiliation.put("lastModified", 1338824999);
		affiliation.put("name", "OrganizationAffiliation");
		affiliation.put("userId", "@me");		

		HashMap<String,Object> affiliationValues =  new HashMap<String,Object>();
		affiliationValues.put("department","depart test");
		affiliationValues.put("title","title test");
		affiliationValues.put("role","role test");
		affiliationValues.put("org","organitzation test");

		affiliation.put("value",affiliationValues);

		Request<ProfileAttribute> affiliationRequest = buildAttributeRequest(affiliation);
		Response<ProfileAttribute> affiliationResponse = controller.createProfileAttribute(SAID, affiliationRequest,"@me");


		//verify the Affiliation has been created
		String afilguid = affiliationResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(afilguid);				
		URI afiluri = new URIImpl(afilguid);

		//create PostalAddress

		ProfileAttribute postalAddress = new ProfileAttribute();
		postalAddress.put("guid", "garbage"); // on create, even if passed, this should be ignored
		postalAddress.put("type", "profileattribute");
		postalAddress.put("category", "PostalAddress");
		postalAddress.put("created", 1338824999);
		postalAddress.put("lastModified", 1338824999);
		postalAddress.put("name", "FriendPostalAddress");
		postalAddress.put("userId", "@me");

		HashMap<String,Object> postalAddressValues = new HashMap<String,Object>();
		postalAddressValues.put("region", "testintong");
		postalAddressValues.put("country", "testlandia");
		postalAddressValues.put("extendedAddress", "test st. 2");
		postalAddressValues.put("addressLocation", "testonia");
		postalAddressValues.put("streetAddress", "test st.");

		postalAddress.put("value",postalAddressValues);		

		Request<ProfileAttribute> postalAddressRequest = buildAttributeRequest(postalAddress);
		Response<ProfileAttribute> postalAddressResponse = controller.createProfileAttribute(SAID, postalAddressRequest, "@me");

		//verify the PostalAddress has been created
		String addressguid = postalAddressResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(addressguid);				
		URI addressuri = new URIImpl(addressguid);

		//create Hobby

		ProfileAttribute hobby = new ProfileAttribute();
		hobby.put("guid", "garbage"); // on create, even if passed, this should be ignored
		hobby.put("type", "profileattribute");
		hobby.put("category", "Hobby");
		hobby.put("created", 1338824999);
		hobby.put("lastModified", 1338824999);
		hobby.put("name", "Hobby test");
		hobby.put("imageUrl", "");
		hobby.put("items",new ArrayList<String>());
		hobby.put("userId", "@me");

		HashMap<String,Object> hobbyValues = new HashMap<String,Object>();
		hobbyValues.put("hobby","tennis");	

		hobby.put("value",hobbyValues);	

		Request<ProfileAttribute> hobbyRequest = buildAttributeRequest(hobby);
		Response<ProfileAttribute> hobbyResponse = controller.createProfileAttribute(SAID, hobbyRequest,"@me");

		//verify the Email has been created
		String hobbyguid = hobbyResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(hobbyguid);				
		URI hobbyuri = new URIImpl(hobbyguid);


		//verify the profile contains all the profileattributes	
		assertTrue(pimo.contains(profile, NCO.hasPersonName,nameuri));
		assertTrue(pimo.contains(profile, NCO.hasBirthDate,birthuri));
		assertTrue(pimo.contains(profile, NCO.hasPhoneNumber,phoneuri));
		assertTrue(pimo.contains(profile, NCO.hasPhoneNumber,voiceuri));
		assertTrue(pimo.contains(profile, NCO.hasEmailAddress,mailuri));
		assertTrue(pimo.contains(profile, NCO.hasAffiliation,afiluri));
		assertTrue(pimo.contains(profile, NCO.hasPostalAddress,addressuri));
		assertTrue(pimo.contains(profile, NCO.hobby,hobbyuri));


		//verify you can get the attributes
		Response<ProfileAttribute> responsePersonName = controller.getProfileAttribute(SAID,nameguid);
		ProfileAttribute personNameJSON = responsePersonName.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),nameguid);

		Response<ProfileAttribute> responseBirthDate = controller.getProfileAttribute(SAID,birthguid);
		ProfileAttribute birthdateJSON = responseBirthDate.getMessage().getData().getEntries().iterator().next();
		assertEquals(birthdateJSON.get("guid"),birthguid);

		Response<ProfileAttribute> responsePhonenumber = controller.getProfileAttribute(SAID,phoneguid);
		ProfileAttribute phoneJSON = responsePhonenumber.getMessage().getData().getEntries().iterator().next();
		assertEquals(phoneJSON.get("guid"),phoneguid);

		Response<ProfileAttribute> responseVoiceMail = controller.getProfileAttribute(SAID,voiceguid);
		ProfileAttribute voiceJSON = responseVoiceMail.getMessage().getData().getEntries().iterator().next();
		assertEquals(voiceJSON.get("guid"),voiceguid);

		Response<ProfileAttribute> responseEmail = controller.getProfileAttribute(SAID,mailguid);
		ProfileAttribute mailJSON = responseEmail.getMessage().getData().getEntries().iterator().next();
		assertEquals(mailJSON.get("guid"),mailguid);

		Response<ProfileAttribute> responseAffiliation = controller.getProfileAttribute(SAID,afilguid);
		ProfileAttribute afilJSON = responseAffiliation.getMessage().getData().getEntries().iterator().next();
		assertEquals(afilJSON.get("guid"),afilguid);

		Response<ProfileAttribute> responsePostalAddress= controller.getProfileAttribute(SAID,addressguid);
		ProfileAttribute addressJSON = responsePostalAddress.getMessage().getData().getEntries().iterator().next();
		assertEquals(addressJSON.get("guid"),addressguid);

		Response<ProfileAttribute> responseHobby= controller.getProfileAttribute(SAID,hobbyguid);
		ProfileAttribute hobbyJSON = responseHobby.getMessage().getData().getEntries().iterator().next();
		assertEquals(hobbyJSON.get("guid"),hobbyguid);

	}

	@Test	
	public void testAllAttributesInSameProfileCard() throws Exception {
		//CREATE THE PROFILECARD
		PrivacyPreference profileCard = buildProfileCard("John profile");
		profileCardManager.add(profileCard);


		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertFalse(pimo.contains(profileCard, PPO.appliesToResource,Variable.ANY));


		//create PersonName
		ProfileAttribute personName = new ProfileAttribute();
		personName.put("guid", "garbage"); // on create, even if passed, this should be ignored
		personName.put("type", "profileattribute");
		personName.put("category", "PersonName");
		personName.put("created", 1338824999);
		personName.put("lastModified", 1338824999);
		//	personName.put("name", "FriendPersonName");	
		personName.put("userId", "@me");

		HashMap<String,Object> personNameValues = new HashMap<String,Object>();
		personNameValues.put("nickname", "juanito");
		personNameValues.put("fullname", "juan gomez");
		personNameValues.put("nameHonorificSuffix", "");
		personNameValues.put("nameFamily", "gomez");
		personNameValues.put("nameHonorificPrefix", "Sr");
		personNameValues.put("nameAdditional", "additional");
		personNameValues.put("nameGiven", "Juan");

		personName.put("value",personNameValues);	

		//create the attributes
		Request<ProfileAttribute> personNameRequest = buildAttributeRequest(personName);
		Response<ProfileAttribute> personNameResponse = controller.createProfileAttribute(SAID, personNameRequest, "pc_"+profileCard.asURI().toString());


		//verify the personName has been created
		String nameguid = personNameResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(nameguid);

		URI nameuri = new URIImpl(nameguid);

		//create BirthDate
		ProfileAttribute birthDate = new ProfileAttribute();
		birthDate.put("guid", "garbage"); // on create, even if passed, this should be ignored
		birthDate.put("type", "profileattribute");
		birthDate.put("category", "BirthDate");
		birthDate.put("created", 1338824999);
		birthDate.put("lastModified", 1338824999);
		birthDate.put("name", "FriendBirthdate");
		birthDate.put("userId", "@me");

		HashMap<String,Object> birthDateValues = new HashMap<String,Object>();
		birthDateValues.put("birthDate", "13-04-1989");	
		birthDateValues.put("age", 40);	
		birthDate.put("value",birthDateValues);	

		Request<ProfileAttribute> birthDateRequest = buildAttributeRequest(birthDate);
		Response<ProfileAttribute> birthDateResponse = controller.createProfileAttribute(SAID, birthDateRequest, "pc_"+profileCard.asURI().toString());


		//verify the birthDate has been created
		String birthguid = birthDateResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(birthguid);

		URI birthuri = new URIImpl(birthguid);


		//create PhoneNumber

		ProfileAttribute phoneNumber = new ProfileAttribute();
		phoneNumber.put("guid", "garbage"); // on create, even if passed, this should be ignored
		phoneNumber.put("type", "profileattribute");
		phoneNumber.put("category", "PhoneNumber");
		phoneNumber.put("created", 1338824999);
		phoneNumber.put("lastModified", 1338824999);
		phoneNumber.put("name", "WorkphoneNumber");
		phoneNumber.put("userId", "@me");

		HashMap<String,Object> phoneNumberValues = new HashMap<String,Object>();
		phoneNumberValues.put("phoneNumber", "4567868");

		phoneNumber.put("value",phoneNumberValues);	


		Request<ProfileAttribute> phoneNumberRequest = buildAttributeRequest(phoneNumber);
		Response<ProfileAttribute> phoneNumberResponse = controller.createProfileAttribute(SAID, phoneNumberRequest, "pc_"+profileCard.asURI().toString());

		//verify the phoneNumber has been created
		String phoneguid = phoneNumberResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(phoneguid);				
		URI phoneuri = new URIImpl(phoneguid);

		//create VoiceMail

		ProfileAttribute voiceMail = new ProfileAttribute();
		voiceMail.put("guid", "garbage"); // on create, even if passed, this should be ignored
		voiceMail.put("type", "profileattribute");
		voiceMail.put("category", "VoiceMail");
		voiceMail.put("created", 1338824999);
		voiceMail.put("lastModified", 1338824999);
		voiceMail.put("name", "voiceMailnumber");
		voiceMail.put("userId", "@me");

		HashMap<String,Object> voiceMailValues = new HashMap<String,Object>();
		voiceMailValues.put("phoneNumber", "4567868");
		voiceMailValues.put("voiceMail", true);

		voiceMail.put("value",voiceMailValues);	


		Request<ProfileAttribute> voiceMailRequest = buildAttributeRequest(voiceMail);
		Response<ProfileAttribute> voiceMailrResponse = controller.createProfileAttribute(SAID, voiceMailRequest, "pc_"+profileCard.asURI().toString());

		//verify the voiceMail has been created
		String voiceguid = voiceMailrResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(voiceguid);				
		URI voiceuri = new URIImpl(voiceguid);

		//create Email

		ProfileAttribute email = new ProfileAttribute();
		email.put("guid", "garbage"); // on create, even if passed, this should be ignored
		email.put("type", "profileattribute");
		email.put("category", "EmailAddress");
		email.put("created", 1338824999);
		email.put("lastModified", 1338824999);
		email.put("name", "Email");
		email.put("imageUrl", "");
		email.put("items",new ArrayList<String>());
		email.put("userId", "");

		HashMap<String,Object> emailValues = new HashMap<String,Object>();
		emailValues.put("emailAddress","test@test.de");	

		email.put("value",emailValues);	

		Request<ProfileAttribute> emailRequest = buildAttributeRequest(email);
		Response<ProfileAttribute> emailResponse = controller.createProfileAttribute(SAID, emailRequest,"pc_"+profileCard.asURI().toString());

		//verify the Email has been created
		String mailguid = emailResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(mailguid);				
		URI mailuri = new URIImpl(mailguid);


		//create Affiliation

		ProfileAttribute affiliation = new ProfileAttribute();
		affiliation.put("guid", "garbage"); // on create, even if passed, this should be ignored
		affiliation.put("type", "profileattribute");
		affiliation.put("category", "Affiliation");
		affiliation.put("created", 1338824999);
		affiliation.put("lastModified", 1338824999);
		affiliation.put("name", "OrganizationAffiliation");
		affiliation.put("userId", "@me");		

		HashMap<String,Object> affiliationValues =  new HashMap<String,Object>();
		affiliationValues.put("department","depart test");
		affiliationValues.put("title","title test");
		affiliationValues.put("role","role test");
		affiliationValues.put("org","organitzation test");

		affiliation.put("value",affiliationValues);

		Request<ProfileAttribute> affiliationRequest = buildAttributeRequest(affiliation);
		Response<ProfileAttribute> affiliationResponse = controller.createProfileAttribute(SAID, affiliationRequest,"pc_"+profileCard.asURI().toString());


		//verify the Affiliation has been created
		String afilguid = affiliationResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(afilguid);				
		URI afiluri = new URIImpl(afilguid);

		//create PostalAddress

		ProfileAttribute postalAddress = new ProfileAttribute();
		postalAddress.put("guid", "garbage"); // on create, even if passed, this should be ignored
		postalAddress.put("type", "profileattribute");
		postalAddress.put("category", "PostalAddress");
		postalAddress.put("created", 1338824999);
		postalAddress.put("lastModified", 1338824999);
		postalAddress.put("name", "FriendPostalAddress");
		postalAddress.put("userId", "@me");

		HashMap<String,Object> postalAddressValues = new HashMap<String,Object>();
		postalAddressValues.put("region", "testintong");
		postalAddressValues.put("country", "testlandia");
		postalAddressValues.put("extendedAddress", "test st. 2");
		postalAddressValues.put("addressLocation", "testonia");
		postalAddressValues.put("streetAddress", "test st.");

		postalAddress.put("value",postalAddressValues);		

		Request<ProfileAttribute> postalAddressRequest = buildAttributeRequest(postalAddress);
		Response<ProfileAttribute> postalAddressResponse = controller.createProfileAttribute(SAID, postalAddressRequest, "pc_"+profileCard.asURI().toString());

		//verify the PostalAddress has been created
		String addressguid = postalAddressResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(addressguid);				
		URI addressuri = new URIImpl(addressguid);

		//create Hobby

		ProfileAttribute hobby = new ProfileAttribute();
		hobby.put("guid", "garbage"); // on create, even if passed, this should be ignored
		hobby.put("type", "profileattribute");
		hobby.put("category", "Hobby");
		hobby.put("created", 1338824999);
		hobby.put("lastModified", 1338824999);
		hobby.put("name", "Hobby test");
		hobby.put("imageUrl", "");
		hobby.put("items",new ArrayList<String>());
		hobby.put("userId", "@me");

		HashMap<String,Object> hobbyValues = new HashMap<String,Object>();
		hobbyValues.put("hobby","tennis");	

		hobby.put("value",hobbyValues);	

		Request<ProfileAttribute> hobbyRequest = buildAttributeRequest(hobby);
		Response<ProfileAttribute> hobbyResponse = controller.createProfileAttribute(SAID, hobbyRequest,"pc_"+profileCard.asURI().toString());

		//verify the Email has been created
		String hobbyguid = hobbyResponse.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(hobbyguid);				
		URI hobbyuri = new URIImpl(hobbyguid);


		//verify the profile contains all the profileattributes	
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,nameuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,birthuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,phoneuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,voiceuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,mailuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,afiluri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,addressuri));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,hobbyuri));


		//verify you can get the attributes
		Response<ProfileAttribute> responsePersonName = controller.getProfileAttribute(SAID,nameguid);
		ProfileAttribute personNameJSON = responsePersonName.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),nameguid);

		Response<ProfileAttribute> responseBirthDate = controller.getProfileAttribute(SAID,birthguid);
		ProfileAttribute birthdateJSON = responseBirthDate.getMessage().getData().getEntries().iterator().next();
		assertEquals(birthdateJSON.get("guid"),birthguid);

		Response<ProfileAttribute> responsePhonenumber = controller.getProfileAttribute(SAID,phoneguid);
		ProfileAttribute phoneJSON = responsePhonenumber.getMessage().getData().getEntries().iterator().next();
		assertEquals(phoneJSON.get("guid"),phoneguid);

		Response<ProfileAttribute> responseVoiceMail = controller.getProfileAttribute(SAID,voiceguid);
		ProfileAttribute voiceJSON = responseVoiceMail.getMessage().getData().getEntries().iterator().next();
		assertEquals(voiceJSON.get("guid"),voiceguid);

		Response<ProfileAttribute> responseEmail = controller.getProfileAttribute(SAID,mailguid);
		ProfileAttribute mailJSON = responseEmail.getMessage().getData().getEntries().iterator().next();
		assertEquals(mailJSON.get("guid"),mailguid);

		Response<ProfileAttribute> responseAffiliation = controller.getProfileAttribute(SAID,afilguid);
		ProfileAttribute afilJSON = responseAffiliation.getMessage().getData().getEntries().iterator().next();
		assertEquals(afilJSON.get("guid"),afilguid);

		Response<ProfileAttribute> responsePostalAddress= controller.getProfileAttribute(SAID,addressguid);
		ProfileAttribute addressJSON = responsePostalAddress.getMessage().getData().getEntries().iterator().next();
		assertEquals(addressJSON.get("guid"),addressguid);

		Response<ProfileAttribute> responseHobby= controller.getProfileAttribute(SAID,hobbyguid);
		ProfileAttribute hobbyJSON = responseHobby.getMessage().getData().getEntries().iterator().next();
		assertEquals(hobbyJSON.get("guid"),hobbyguid);

	}

	@Test
	public void testGetPersonNameWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the PersonName
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname("John Doe");
		personName.setNameHonorificPrefix("Sr.");
		personName.setNameHonorificSuffix("");
		personName.setNameFamily("gomez");
		personName.setNameGiven("juan");
		personName.setNameAdditional("juanito");
		pimoService.create(personName);

		profile.addPersonName(personName);
		profileManager.update(profile);

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,personName.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),personName.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"PersonName");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("nameHonorificPrefix", "Sr.");	
		values.put("nameAdditional", "juanito");
		values.put("nameFamily", "gomez");
		values.put("nameGiven", "juan");
		values.put("nameHonorificSuffix", "");		
		values.put("fullname", "John Doe");
		assertEquals(personNameJSON.get("value"),values);

	}

	@Test
	public void testBirthDateNameWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the birthDate
		BirthDate birthDate = modelFactory.getNCOFactory().createBirthDate();
		birthDate.setAge(78);
		birthDate.setBirthDate(new DatatypeLiteralImpl("13-02-89", XSD._dateTime));			
		pimoService.create(birthDate);

		profile.addBirthDate(birthDate);
		profileManager.update(profile);

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,birthDate.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),birthDate.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"BirthDate");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("birthDate", "13-02-89");	
		values.put("age",78);

		assertEquals(personNameJSON.get("value"),values);

	}


	@Test
	public void testGetPhoneNumberWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the PhoneNumber

		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber("555-55-55-55");
		pimoService.create(phoneNumber);
		profile.addPhoneNumber(phoneNumber);
		profileManager.update(profile);

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,phoneNumber.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),phoneNumber.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"PhoneNumber");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("phoneNumber", "555-55-55-55");				
		assertEquals(personNameJSON.get("value"),values);

	}

	@Test
	public void testGetVoiceMailWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the PhoneNumber

		VoicePhoneNumber phoneNumber = modelFactory.getNCOFactory().createVoicePhoneNumber();
		phoneNumber.setPhoneNumber("555-55-55-55");
		phoneNumber.setVoiceMail(true);
		pimoService.create(phoneNumber);
		profile.addPhoneNumber(phoneNumber);
		profileManager.update(profile);

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,phoneNumber.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),phoneNumber.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"VoiceMail");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("phoneNumber", "555-55-55-55");
		values.put("voiceMail", true);
		assertEquals(personNameJSON.get("value"),values);

	}


	@Test
	public void testGetEmailWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the PersonName
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress("test@mail.com");

		pimoService.create(emailAddress);

		profile.addEmailAddress(emailAddress);
		profileManager.update(profile);

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,emailAddress.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),emailAddress.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"EmailAddress");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("emailAddress", "test@mail.com");				
		assertEquals(personNameJSON.get("value"),values);

	}

	@Test
	public void testGetAffiliationWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the Affiliation
		Affiliation affiliation = modelFactory.getNCOFactory().createAffiliation();
		affiliation.setDepartment("test department");
		affiliation.setTitle("test title");
		affiliation.setRole("test role");
		//affiliation.setOrg("org ");
		pimoService.create(affiliation);

		profile.addAffiliation(affiliation);
		profileManager.update(profile);		

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,affiliation.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),affiliation.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"Affiliation");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("department", "test department");	
		values.put("title", "test title");
		values.put("role", "test role");

		assertEquals(personNameJSON.get("value"),values);


	}


	@Test
	public void testGetPostalAddressWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the postalAddress
		PostalAddress postalAddress = modelFactory.getNCOFactory().createPostalAddress();
		postalAddress.addRegion("region");
		postalAddress.addCountry("country");
		postalAddress.addExtendedAddress("ex address");
		//postalAddress.addAddressLocation(new URIImpl("cositaLinda"));
		postalAddress.addStreetAddress("st address");
		pimoService.create(postalAddress);

		profile.addPostalAddress(postalAddress);
		profileManager.update(profile);


		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,postalAddress.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),postalAddress.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"PostalAddress");

		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("region", "region");	
		values.put("country", "country");
		values.put("extendedAddress", "ex address");
		values.put("streetAddress", "st address");
		assertEquals(personNameJSON.get("value"),values);
	}

	@Test
	public void testGetHobbyWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the postalAddress
		Hobby hobby = modelFactory.getNCOFactory().createHobby();
		hobby.setPrefLabel("tennis");


		pimoService.create(hobby);

		profile.addHobby(hobby);
		profileManager.update(profile);


		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,hobby.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),hobby.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"Hobby");
		assertEquals(personNameJSON.get("name"),"tennis");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("hobby","tennis");			
		assertEquals(personNameJSON.get("value"),values);
	}

	@Test
	@Ignore
	public void testGetLocationWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);
		//create the postalAddress
		Point location = modelFactory.getGEOFactory().createPoint();
		location.setPrefLabel("test point");
		location.setLat(41.089F);
		location.setLong(54.112F);

		pimoService.create(location);

		profile.addLocation(location);
		profileManager.update(profile);

		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());		

		assertTrue(pimo.contains(location, GEO.lat,Variable.ANY));

		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,location.asURI().toString());

		//verify JSON is correct
		ProfileAttribute personNameJSON = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(personNameJSON.get("guid"),location.asURI().toString());
		assertEquals(personNameJSON.get("type"),"profileattribute");
		assertEquals(personNameJSON.get("category"),"Location");

		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("lat", 41.089F);	
		values.put("long", 54.112F);
		assertEquals(personNameJSON.get("value"),values);
	}

	@Test
	public void testGetAllWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		//create the PersonName
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname("John Doe");
		personName.setNameHonorificPrefix("Sr.");
		personName.setNameHonorificSuffix("");
		personName.setNameFamily("gomez");
		personName.setNameGiven("juan");
		personName.setNameAdditional("juanito");
		pimoService.create(personName);
		profile.addPersonName(personName);

		//create the birthDate
		BirthDate birthDate = modelFactory.getNCOFactory().createBirthDate();
		birthDate.setAge(78);
		birthDate.setBirthDate(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));			
		pimoService.create(birthDate);
		profile.addBirthDate(birthDate);

		//create the PhoneNumber
		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber("555-55-55-55");
		pimoService.create(phoneNumber);
		profile.addPhoneNumber(phoneNumber);

		//create the Email
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress("test@mail.com");
		pimoService.create(emailAddress);
		profile.addEmailAddress(emailAddress);

		//create the Affiliation
		Affiliation affiliation = modelFactory.getNCOFactory().createAffiliation();
		affiliation.setDepartment("test department");
		affiliation.setTitle("test title");
		affiliation.setRole("test role");
		//affiliation.setOrg("org ");
		pimoService.create(affiliation);
		profile.addAffiliation(affiliation);

		//create the postalAddress
		PostalAddress postalAddress = modelFactory.getNCOFactory().createPostalAddress();
		postalAddress.addRegion("region");
		postalAddress.addCountry("country");
		postalAddress.addExtendedAddress("ex address");
		//postalAddress.addAddressLocation(new URIImpl("cositaLinda"));
		postalAddress.addStreetAddress("st address");
		pimoService.create(postalAddress);
		profile.addPostalAddress(postalAddress);

		profileManager.update(profile);


		Response<ProfileAttribute> response = controller.getAllProfileAttributes(SAID);

		assertNotNull(response);
		assertEquals(6, response.getMessage().getData().getEntries().size());
	}

	@Test
	public void testGetAllFromDifferentProfileWellFormedJSON() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		//set profilecard		
		PrivacyPreference profileCard = buildProfileCard("John profile");
		profileCardManager.add(profileCard);
		Person me = profileManager.getMe();
		me.addPrivacyPreference(profileCard);
		//create the PersonName
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname("John Doe");
		personName.setNameHonorificPrefix("Sr.");
		personName.setNameHonorificSuffix("");
		personName.setNameFamily("gomez");
		personName.setNameGiven("juan");
		personName.setNameAdditional("juanito");
		pimoService.create(personName);
		profile.addPersonName(personName);

		//create the birthDate
		BirthDate birthDate = modelFactory.getNCOFactory().createBirthDate();
		birthDate.setAge(78);
		birthDate.setBirthDate(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));			
		pimoService.create(birthDate);
		profile.addBirthDate(birthDate);

		profileManager.update(profile);

		//create the Email and add it to the profilecard
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress("test@mail.com");
		pimoService.create(emailAddress);
		profileCard.addAppliesToResource(emailAddress);

		//add the personName to the profilecard too
		profileCard.addAppliesToResource(personName);

		profileCardManager.update(profileCard);


		Response<ProfileAttribute> response = controller.getAllProfileAttributes(SAID);

		assertNotNull(response);
		assertEquals(3, response.getMessage().getData().getEntries().size());
	}


	private  Request<ProfileAttribute> buildAttributeRequest(ProfileAttribute attribute) {
		Request<ProfileAttribute> request = new Request<ProfileAttribute>();
		Message<ProfileAttribute> message = new Message<ProfileAttribute>();

		Data<ProfileAttribute> data = new Data<ProfileAttribute>();
		data.getEntries().add(attribute);
		message.setData(data);
		request.setMessage(message);

		return request;
	}


	@Test	
	public void testUpdateDefaultProfileEmailWellFormedRDF() throws Exception {
		//set the profile 
		PersonContact profile = createProfile("John profile");
		profileManager.add(profile);

		//set profile as default
		Person me = profileManager.getMe();
		me.addGroundingOccurrence(profile);
		pimoService.update(me);


		//create the Email
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress("test@mail.com");

		pimoService.create(emailAddress);

		profile.addEmailAddress(emailAddress);
		profileManager.update(profile);

		//verify the email has been created with no name
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertTrue(pimo.contains(emailAddress, NCO.emailAddress,"test@mail.com"));
		assertFalse(pimo.contains(emailAddress, NAO.prefLabel,Variable.ANY));
		assertTrue(pimo.contains(profile, NCO.hasEmailAddress,emailAddress));


		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,emailAddress.asURI().toString());

		// SET A NAME AND CHANGE THE EMAIL ADDRESS

		ProfileAttribute emailJSON = response.getMessage().getData().getEntries().iterator().next();
		emailJSON.put("name", "test name");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("emailAddress", "secondtest@mail.com");		
		emailJSON.put("value", values);

		Request<ProfileAttribute> request = buildAttributeRequest(emailJSON);

		Response<ProfileAttribute> updatedResponse = controller.updateProfileAttribute(SAID, request, emailAddress.asURI().toString());


		//verify JSON is correct
		ProfileAttribute updatedJSON = updatedResponse.getMessage().getData().getEntries().iterator().next();
		assertEquals(updatedJSON.get("guid"),emailAddress.asURI().toString());
		assertEquals(updatedJSON.get("type"),"profileattribute");
		assertEquals(updatedJSON.get("name"),"test name");
		assertEquals(updatedJSON.get("category"),"EmailAddress");
		Map<String, Object> udpatedvalues = new HashMap<String,Object>();		
		udpatedvalues.put("emailAddress", "secondtest@mail.com");				
		assertEquals(updatedJSON.get("value"),values);

		Response<ProfileAttribute> response3 = controller.getProfileAttribute(SAID,emailAddress.asURI().toString());


		//verify JSON is correct
		ProfileAttribute JSON3 = response3.getMessage().getData().getEntries().iterator().next();
		assertEquals(JSON3.get("guid"),emailAddress.asURI().toString());
		assertEquals(JSON3.get("type"),"profileattribute");
		assertEquals(JSON3.get("name"),"test name");
		assertEquals(JSON3.get("category"),"EmailAddress");
		Map<String, Object> values3 = new HashMap<String,Object>();		
		values3.put("emailAddress", "secondtest@mail.com");				
		assertEquals(JSON3.get("value"),values3);				

	}

	@Test	
	public void testUpdateProfileCardEmailWellFormedRDF() throws Exception {
		//CREATE THE PROFILECARD
		PrivacyPreference profileCard = buildProfileCard("John profile");
		profileCardManager.add(profileCard);


		//create the Email
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress("test@mail.com");

		pimoService.create(emailAddress);

		profileCard.addAppliesToResource(emailAddress);
		profileCardManager.update(profileCard);

		//verify the email has been created with no name
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		assertTrue(pimo.contains(emailAddress, NCO.emailAddress,"test@mail.com"));
		assertFalse(pimo.contains(emailAddress, NAO.prefLabel,Variable.ANY));
		assertTrue(pimo.contains(profileCard, PPO.appliesToResource,emailAddress));


		Response<ProfileAttribute> response = controller.getProfileAttribute(SAID,"pc_"+profileCard.asURI().toString() , emailAddress.asURI().toString());

		// SET A NAME AND CHANGE THE EMAIL ADDRESS

		ProfileAttribute emailJSON = response.getMessage().getData().getEntries().iterator().next();
		emailJSON.put("name", "test name");
		Map<String, Object> values = new HashMap<String,Object>();		
		values.put("emailAddress", "secondtest@mail.com");		
		emailJSON.put("value", values);

		Request<ProfileAttribute> request = buildAttributeRequest(emailJSON);

		Response<ProfileAttribute> updatedResponse = controller.updateProfileAttribute(SAID, request,"pc_"+profileCard.asURI().toString() , emailAddress.asURI().toString());


		//verify JSON is correct
		ProfileAttribute updatedJSON = updatedResponse.getMessage().getData().getEntries().iterator().next();
		assertEquals(updatedJSON.get("guid"),emailAddress.asURI().toString());
		assertEquals(updatedJSON.get("type"),"profileattribute");
		assertEquals(updatedJSON.get("name"),"test name");
		assertEquals(updatedJSON.get("category"),"EmailAddress");
		Map<String, Object> udpatedvalues = new HashMap<String,Object>();		
		udpatedvalues.put("emailAddress", "secondtest@mail.com");				
		assertEquals(updatedJSON.get("value"),values);


		Response<ProfileAttribute> response3 = controller.getProfileAttribute(SAID,"pc_"+profileCard.asURI().toString() , emailAddress.asURI().toString());

		//verify JSON is correct
		ProfileAttribute JSON3 = response3.getMessage().getData().getEntries().iterator().next();
		assertEquals(JSON3.get("guid"),emailAddress.asURI().toString());
		assertEquals(JSON3.get("type"),"profileattribute");
		assertEquals(JSON3.get("name"),"test name");
		assertEquals(JSON3.get("category"),"EmailAddress");
		Map<String, Object> values3 = new HashMap<String,Object>();		
		values3.put("emailAddress", "secondtest@mail.com");				
		assertEquals(JSON3.get("value"),values3);
	}

}
