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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
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
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.runtime.CardinalityException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;





public class PSLivePostControllerTestIt extends PSInfosphereControllerTestIt {


	@Autowired
	private LivePostManager livePostManager;

	@Autowired
	private PersonManager personManager;

	@Autowired
	private AccountManager accountManager;

	@Autowired
	private SharingManager sharingManager;
	
	@Autowired
	private PersonGroupManager personGroupManager;

	private PSLivePostController controller;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// set up PSLivePostController
		controller = new PSLivePostController();
		controller.setAccountManager(accountManager);
		controller.setPersonManager(personManager);
		controller.setLivePostManager(livePostManager);
		controller.setSharingManager(sharingManager);
		controller.setPersonGroupManager(personGroupManager);
	}
	
	@After
	public void tearDown() throws Exception {
		Collection<LivePost> livePosts = livePostManager.getAll();
		for (LivePost livePost: livePosts){
			livePostManager.remove(livePost.asURI().toString());		
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
	
	


	@Test
	public void testCreateLivePostWellFormedRDF() throws Exception {
		Account sender = createAccount(pimoService.getUserUri());
		Person person = createPerson("Ismael Rivera");


		Resource livepost = new Resource();	

		livepost.put("guid", "garbage"); // on create, even if passed, this should be ignored
		livepost.put("type", "livepost");
		livepost.put("created", 1338824999);
		livepost.put("lastModified", 1338824999);
		livepost.put("name", "Test livepost");
		livepost.put("userId", "@me");
		livepost.put("text", "this is a test");
		livepost.put("nao:includes", buildIncludes(sender, person));		


		Request<Resource> request = buildRequest(livepost);
		Response<Resource> response = controller.createLivePost(request, "@me");

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);


		URI uri = new URIImpl(guid);
		org.ontoware.rdfreactor.schema.rdfs.Resource resource = pimoService.get(uri);


		assertNotNull(resource);
		//verify rdf Type is correct
		assertTrue(resource.getModel().contains(resource,RDF.type, DLPO.LivePost));
		assertTrue(resource.getModel().contains(resource, NAO.prefLabel, "Test livepost"));
		assertTrue(resource.getModel().contains(resource, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(resource.getModel().contains(resource, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(resource.getModel().contains(resource, DLPO.textualContent, "this is a test"));
		


		// verify PrivacyPreference is created and metadata isis correct

		PrivacyPreference pp = sharingManager.findPrivacyPreference(guid,  PrivacyPreferenceType.LIVEPOST);
		assertNotNull(pp);
		assertTrue(pp.getModel().contains(pp, RDFS.label, PrivacyPreferenceType.LIVEPOST.toString()));	


		// verify AccessSpace metadata (of PrivacyPreference) is correct
		assertTrue(pp.getModel().contains(pp, PPO.hasAccessSpace, Variable.ANY));
		URI accessSpace = ModelUtils.findObject(pp.getModel(), pp, PPO.hasAccessSpace).asURI();
		assertTrue(pp.getModel().contains(accessSpace, NSO.sharedThrough, sender.asURI()));
		assertTrue(pp.getModel().contains(accessSpace, NSO.includes, person.asURI()));
	}
	
	
	
	@Test
	public void testGetLivePostWellFormedJSON() throws Exception {

		Account sender = createAccount(pimoService.getUserUri());		
		
		LivePost livepost =  createLivePost(pimoService.getUserUri());
		livePostManager.add(livepost);
	
		Response<Resource> response = controller.getAllLivePosts("juan");

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());		 

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);
			
		Resource resource =  response.getMessage().getData().getEntries().iterator().next();

		assertNotNull(resource);
		assertEquals(resource.get("guid"),livepost.asURI().toString());
		assertEquals(resource.get("name"),"Test livepost" );		
		assertEquals(resource.get("userId"),"@me" );
		assertEquals(resource.get("type"),"livepost" );
		assertEquals(resource.get("created"),1338824999L);
		assertEquals(resource.get("lastModified"),1338824999L);
		assertEquals(resource.get("text"),"test livepost text");
		assertEquals(resource.get("dlpo:timestamp"),1338824999L);

	}
	
	@Test
	public void testGetSharedLivePostWellFormedJSON() throws Exception {

		Account sender = createAccount(new URIImpl("urn:uuid:test"));		
		
		LivePost livepost =  createLivePost(sender.asURI());
		livepost.setSharedBy(sender);
		livePostManager.add(livepost);
	
		Response<Resource> response = controller.getAllLivePosts("juan");

		assertNotNull(response);
		assertEquals(1, response.getMessage().getData().getEntries().size());		 

		String guid = response.getMessage().getData().getEntries().iterator().next().get("guid").toString();
		assertNotNull(guid);
			
		Resource resource =  response.getMessage().getData().getEntries().iterator().next();

		assertNotNull(resource);
		assertEquals(resource.get("guid"),livepost.asURI().toString());
		assertEquals(resource.get("name"),"Test livepost" );		
		assertEquals(resource.get("userId"),"urn:uuid:test");
		assertEquals(resource.get("type"),"livepost" );
		assertEquals(resource.get("created"),1338824999L);
		assertEquals(resource.get("lastModified"),1338824999L);
		assertEquals(resource.get("text"),"test livepost text");
		assertEquals(resource.get("dlpo:timestamp"),1338824999L);
		
	}
	
	
	
	private LivePost createLivePost(URI	 creator) throws CardinalityException, ResourceExistsException {
		LivePost livepost = modelFactory.getDLPOFactory().createLivePost();
		livepost.setPrefLabel("Test livepost");		
		livepost.setCreator(creator);		
		livepost.setCreated(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		livepost.setLastModified(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		livepost.setTextualContent("test livepost text");
		livepost.setTimestamp(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		
		
		return livepost;
	}

}
