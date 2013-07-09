package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
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
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;





public class PSResourceControllerTestIt extends PSInfosphereControllerTestIt {

	private static final String SAID = "juan";


	@Autowired
	private FileManager fileManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private SharingManager sharingManager;

	private PSResourcesController controller;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// set up PSResourcesController
		controller = new PSResourcesController();
		controller.setAccountManager(accountManager);
		controller.setPersonManager(personManager);
		controller.setFileManager(fileManager);
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
	public void testCreateResourceWellFormedRDF() throws Exception {
		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");


		Resource file = new Resource();	

		file.put("guid", "garbage"); // on create, even if passed, this should be ignored
		file.put("type", "resource");
		file.put("created", 1338824999);
		file.put("lastModified", 1338824999);
		file.put("name", "Test resource");
		file.put("userId", "@me");			
		file.put("nao:includes", buildIncludes(sender, person));		
		file.put("nfo:fileOwner",person.asURI().toString());

		Request<Resource> request = buildRequest(file);
		Response<Resource> response = controller.createResourceFromPersonById(SAID, request);

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);


		URI uri = new URIImpl(guid);
		org.ontoware.rdfreactor.schema.rdfs.Resource resource = pimoService.get(uri);


		assertNotNull(resource);
		//verify rdf Type is correct
		assertTrue(resource.getModel().contains(resource,RDF.type, NFO.FileDataObject));
		assertTrue(resource.getModel().contains(resource, NAO.prefLabel, "Test resource"));
		assertTrue(resource.getModel().contains(resource, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(resource.getModel().contains(resource, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(resource.getModel().contains(resource, NAO.creator, fileManager.getMe()));

		// verify PrivacyPreference is created and metadata is correct
		PrivacyPreference pp = sharingManager.findPrivacyPreference(guid,  PrivacyPreferenceType.FILE);
		assertNotNull(pp);
		assertTrue(pp.getModel().contains(pp, RDFS.label, PrivacyPreferenceType.FILE.toString()));	



		// verify AccessSpace metadata (of PrivacyPreference) is correct
		assertTrue(pp.getModel().contains(pp, PPO.hasAccessSpace, Variable.ANY));
		URI accessSpace = ModelUtils.findObject(pp.getModel(), pp, PPO.hasAccessSpace).asURI();
		assertTrue(pp.getModel().contains(accessSpace, NSO.sharedThrough, sender.asURI()));
		assertTrue(pp.getModel().contains(accessSpace, NSO.includes, person.asURI()));


	}

}
