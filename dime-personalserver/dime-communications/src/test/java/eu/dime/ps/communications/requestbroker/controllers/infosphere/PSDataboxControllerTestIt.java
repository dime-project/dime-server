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
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
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
import eu.dime.jfix.util.Arrays;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Databox;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import org.junit.Ignore;

public class PSDataboxControllerTestIt extends PSInfosphereControllerTestIt {

	private static final String SAID = "juan";

	@Autowired
	private DataboxManager databoxManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private SharingManager sharingManager;
	
	@Autowired
	private PersonGroupManager personGroupManager;

	private PSDataboxController controller;

	
	
	@Before
	public void setUp() throws Exception {
		super.setUp();

		// set up PSDataboxController
		controller = new PSDataboxController();
		controller.setAccountManager(accountManager);
		controller.setPersonManager(personManager);
		controller.setDataboxManager(databoxManager);
		controller.setSharingManager(sharingManager);
		controller.setPersonGroupManager(personGroupManager);

	}
	
	@After
	public void tearDown() throws Exception {
		Collection<DataContainer> databoxs = databoxManager.getAll();
		for (DataContainer databox: databoxs){
			databoxManager.remove(databox.asURI().toString());			
		}
		super.tearDown();
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

	private Databox createDataboxJSON(Account sender, Person person, DataObject f1) throws ResourceExistsException {		

		Databox databox = new Databox();	

		databox.put("guid", "garbage"); // on create, even if passed, this should be ignored
		databox.put("type", "databox");
		databox.put("created", 1338824999);
		databox.put("lastModified", 1338824999);
		databox.put("name", "Test databox");
		databox.put("userId", "@me");
		
		databox.put("nao:includes", buildIncludes(sender, person));		
		databox.put("nao:privacyLevel", 0.5);		
		databox.put("items", Arrays.asList(new String[]{f1.toString()}));
		return databox;
	}


	@Test
	public void testCreateDataboxWellFormedRDF() throws Exception {
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));
		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");		
		//create a resource to add to the databox
		DataObject f1 = createDataObject();

		Databox databox =  createDataboxJSON(sender,person,f1);
		Request<Databox> request = buildDataboxRequest(databox);
		Response<Databox> response = controller.postCreateMyDatabox(SAID, request);

		assertNotNull(response);
		if(response.getMessage().getMeta().getStatus().equalsIgnoreCase("ERROR")){
			fail("Response was ERROR. "+response.getMessage().getMeta().getMessage());
		}
		assertNotNull(response.getMessage());
		assertNotNull(response.getMessage().getData());
		assertNotNull(response.getMessage().getData().getEntries());
		assertEquals(1, response.getMessage().getData().getEntries().size());

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);


		URI uri = new URIImpl(guid);
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		//verify Datacontainer is correct
		assertTrue(pimo.contains(uri, RDF.type, NFO.DataContainer));

		// verify PrivacyPreference metadata is correct
		assertTrue(pimo.contains(uri, RDF.type, PPO.PrivacyPreference));
		assertTrue(pimo.contains(uri, RDFS.label, PrivacyPreferenceType.DATABOX.toString()));
		assertTrue(pimo.contains(uri, NIE.hasPart, f1));
		assertTrue(pimo.contains(uri, NAO.prefLabel, "Test databox"));	
		assertTrue(pimo.contains(uri, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(pimo.contains(uri, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));


		// verify AccessSpace metadata (of PrivacyPreference) is correct
		assertTrue(pimo.contains(uri, PPO.hasAccessSpace, Variable.ANY));
		URI accessSpace = ModelUtils.findObject(pimo, uri, PPO.hasAccessSpace).asURI();
		assertTrue(pimo.contains(accessSpace, NSO.sharedThrough, sender.asURI()));
		assertTrue(pimo.contains(accessSpace, NSO.includes, person.asURI()));
	}


   
	@Test
	public void testGetDataboxWellFormedJSON() throws Exception {

		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");
		DataObject f1 = createDataObject();
		Databox databox =  createDataboxJSON(sender,person,f1);
		Request<Databox> request = buildDataboxRequest(databox);
		Response<Databox> resp = controller.postCreateMyDatabox(SAID, request);
	
		Response<Resource> response = controller.getAllDatabox(SAID);

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());		 

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		ClosableIterator<Statement> hasPart = pimo.findStatements(uri, NIE.hasPart ,Variable.ANY);
		assertNotNull(hasPart);
		String dataObjectID= hasPart.next().getObject().asURI().toString();
		Resource resource =  response.getMessage().getData().getEntries().iterator().next();

		assertNotNull(resource);		
		assertEquals(resource.get("name"),"Test databox" );
		assertEquals(resource.get("type"),"databox");
		assertEquals(resource.get("userId"),"@me");
		assertEquals(resource.get("created"), 1338824999L);
		assertEquals(resource.get("lastModified"), 1338824999L);
		assertEquals(resource.get("nao:privacyLevel"), 0.5);
		assertEquals(resource.get("items"),  Arrays.asList(new String[]{dataObjectID}));		

	}
	
	@Test
	public void testGetSharedDataboxWellFormedJSON() throws Exception {

		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");
		DataObject f1 = createDataObject();
		Databox databox =  createDataboxJSON(sender,person,f1);
		databox.put("userId", person.asURI().toString());
		databox.put("nso:sharedBy", person.asURI().toString());
		Request<Databox> request = buildDataboxRequest(databox);
		Response<Databox> resp = controller.postCreateMyDatabox(SAID, request);
	
		Response<Resource> response = controller.getAllDataboxesByPerson(SAID, person.asURI().toString());

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());		 

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);

		URI uri = new URIImpl(guid);
		TripleStore tripleStore = pimoService.getTripleStore();
		Model pimo = tripleStore.getModel(pimoService.getPimoUri());

		ClosableIterator<Statement> hasPart = pimo.findStatements(uri, NIE.hasPart ,Variable.ANY);
		assertNotNull(hasPart);
		String dataObjectID= hasPart.next().getObject().asURI().toString();
		Resource resource =  response.getMessage().getData().getEntries().iterator().next();

		assertNotNull(resource);		
		assertEquals(resource.get("name"),"Test databox" );
		assertEquals(resource.get("type"),"databox");
		assertEquals(resource.get("userId"),person.asURI().toString());
		assertEquals(resource.get("created"), 1338824999L);
		assertEquals(resource.get("lastModified"), 1338824999L);
		assertEquals(resource.get("nao:privacyLevel"), 0.5);
		assertEquals(resource.get("items"),  Arrays.asList(new String[]{dataObjectID}));		

	}	

	@Test
	public void testGetEmptySharedDataboxWellFormedJSON() throws Exception {
		
		Person person = createPerson("Ismael Rivera");			
		Response<Resource> response = controller.getAllDataboxesByPerson(SAID, person.asURI().toString());
		assertNotNull(response);
		assertEquals(0, response.getMessage().getData().getEntries().size());
		assertEquals(Integer.toString(200), response.getMessage().getMeta().getCode().toString());

	}	

}
