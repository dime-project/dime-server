package eu.dime.ps.semantic.privacy;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.ArchiveItem;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Tests {@link PrivacyPreferenceService}.
 */
public class PrivacyPreferenceServiceTest extends SemanticTest {

	@Autowired
	private PrivacyPreferenceService manager;
	
	@Autowired
	private ModelFactory modelFactory;
	
	@Autowired
	private PimoService pimoService;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		pimoService.clear();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	private boolean comparePrivacyPreferences(Collection<PrivacyPreference> privacyPrefs, String[] labels) {
		for (PrivacyPreference privacyPref : privacyPrefs) {
			String prefLabel = privacyPref.getPrefLabel();
			if (!ArrayUtils.contains(labels, prefLabel)) {
				return false;
			}
		}
		return true;
	}
	
	private Account buildAccount() throws NotFoundException {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType("di.me");
		account.setCreator(pimoService.getUser());
		pimoService.createOrUpdate(account);
		return account;
	}
	
	@Test
	public void testGrantAccessToAgents() throws Exception {
		Person pierre = modelFactory.getPIMOFactory().createPerson(); 
		Person simon = modelFactory.getPIMOFactory().createPerson(); 
		Person mohamed = modelFactory.getPIMOFactory().createPerson(); 
		pimoService.create(pierre);
		pimoService.create(simon);
		pimoService.create(mohamed);
		
		// creates 'sharedThrough' di.me account 
		Account account = buildAccount();

		PrivacyPreference businessDatabox = manager.getOrCreate("business", PrivacyPreferenceType.DATABOX);
		manager.grantAccess(businessDatabox, account.asURI(), simon);
		
		PrivacyPreference privateDatabox = manager.getOrCreate("private", PrivacyPreferenceType.DATABOX);
		manager.grantAccess(privateDatabox, account.asURI(), simon, mohamed);
		
		Collection<PrivacyPreference> databoxes = manager.getByType(PrivacyPreferenceType.DATABOX);
		assertEquals(2, databoxes.size());
		assertTrue(comparePrivacyPreferences(databoxes, new String[]{"business", "private"}));
		
		// check for the agents in 'business' privacy preference
		Collection<Agent> agents = pimoService.find(Agent.class)
			.distinct()
			.where(businessDatabox, PPO.hasAccessSpace, BasicQuery.X)
			.where(BasicQuery.X, NSO.includes, BasicQuery.THIS)
			.results();
		assertEquals(1, agents.size());
		assertTrue(agents.contains(simon));
		
		// check for the agents in 'private' privacy pref
		agents = resourceStore.find(Agent.class)
			.distinct()
			.where(privateDatabox, PPO.hasAccessSpace, BasicQuery.X)
			.where(BasicQuery.X, NSO.includes, BasicQuery.THIS)
			.results();
		assertEquals(2, agents.size());
		assertTrue(agents.contains(simon));
		assertTrue(agents.contains(mohamed));
	}

	// TODO deprecated: adapt test for saving the privacy preference definition using the save method
//	@Test
//	public void testGrantAccessForDataboxes() throws Exception {
//		FileDataObject file1 = modelFactory.getNFOFactory().createFileDataObject();
//		FileDataObject file2 = modelFactory.getNFOFactory().createFileDataObject();
//		FileDataObject file3 = modelFactory.getNFOFactory().createFileDataObject();
//		
//		PrivacyPreference publicDatabox = manager.getOrCreate("public", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(publicDatabox, new FileDataObject[]{file1});
//		
//		PrivacyPreference familyDatabox = manager.getOrCreate("family", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(familyDatabox, new FileDataObject[]{file1, file2});
//		
//		PrivacyPreference privateDatabox = manager.getOrCreate("private", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(privateDatabox, new FileDataObject[]{file3});
//
//		Collection<PrivacyPreference> databoxes = manager.getByType(PrivacyPreferenceType.DATABOX);
//		assertEquals(3, databoxes.size());
//		assertTrue(comparePrivacyPreferences(databoxes, new String[]{"public", "family", "private"}));
//
//		PrivacyPreference publicDb = resourceStore.find(PrivacyPreference.class)
//			.where(RDFS.label).eq(PrivacyPreferenceType.DATABOX)
//			.where(NAO.prefLabel).eq("public")
//			.first();
//		assertTrue(publicDb != null);
//		assertEquals(1, publicDb.getAllAppliesToResource_as().count());
//		
//		PrivacyPreference familyDb = resourceStore.find(PrivacyPreference.class)
//			.where(RDFS.label).eq(PrivacyPreferenceType.DATABOX)
//			.where(NAO.prefLabel).eq("family")
//			.first();
//		assertTrue(familyDb != null);
//		assertEquals(2, familyDb.getAllAppliesToResource_as().count());
//
//		PrivacyPreference privateDb = resourceStore.find(PrivacyPreference.class)
//			.where(RDFS.label).eq(PrivacyPreferenceType.DATABOX)
//			.where(NAO.prefLabel).eq("private")
//			.first();
//		assertTrue(privateDb != null);
//		assertEquals(1, privateDb.getAllAppliesToResource_as().count());
//	}
	
	@Test
	public void testGrantAccessToDataObjects() throws PrivacyPreferenceException, NotFoundException {
		ArchiveItem file1 = modelFactory.getNFOFactory().createArchiveItem();
		FileDataObject file2 = modelFactory.getNFOFactory().createFileDataObject();

		Agent simon = modelFactory.getPIMOFactory().createAgent(); 
		Agent brian = modelFactory.getPIMOFactory().createAgent(); 

		// creates 'sharedThrough' di.me account 
		Account account = buildAccount();
		
		// creates the privacy preferences
		PrivacyPreference file1pp = manager.grantAccess(file1, account.asURI(), simon);
		PrivacyPreference file1ppb = manager.grantAccess(file1, account.asURI(), brian);
		PrivacyPreference file2pp = manager.grantAccess(file2, account.asURI(), simon);

		// 1 file should only have 1 privacy preference
		assertEquals(file1pp, file1ppb);
		
		// load them and check if they contain the correct privacy settings
		file1pp = pimoService.get(file1pp.asResource(), PrivacyPreference.class);
		assertEquals(file1.asResource(), file1pp.getAllAppliesToResource().next().asResource());
		AccessSpace space1 = pimoService.get(file1pp.getAllAccessSpace().next().asResource(), AccessSpace.class);
		assertEquals(2, space1.getAllIncludes_as().count());
		assertEquals(account, space1.getSharedThrough().asURI());

		file2pp = pimoService.get(file2pp.asResource(), PrivacyPreference.class);
		assertEquals(file2.asResource(), file2pp.getAllAppliesToResource().next().asResource());
		AccessSpace space2 = pimoService.get(file2pp.getAllAccessSpace().next().asResource(), AccessSpace.class);
		assertEquals(1, space2.getAllIncludes_as().count());
		assertEquals(account, space2.getSharedThrough().asURI());
	}

	// TODO deprecated: adapt test for saving the privacy preference definition using the save method
//	@Test
//	public void testGrantAccessToProfileAttributes() throws PrivacyPreferenceException, NotFoundException {
//		PersonName name = modelFactory.getNCOFactory().createPersonName();
//		name.setNameFamily("Rivera");
//		PhoneNumber phone = modelFactory.getNCOFactory().createPhoneNumber();
//		phone.setPhoneNumber("555-55555");
//		
//		Agent jeremy = modelFactory.getPIMOFactory().createAgent(); 
//		Agent judie = modelFactory.getPIMOFactory().createAgent(); 
//		Agent keith = modelFactory.getPIMOFactory().createAgent(); 
//
//		PrivacyPreference cardA = manager.getOrCreate("cardA", PrivacyPreferenceType.PROFILECARD);
//		manager.grantAccess(cardA, name);
//		manager.grantAccess(cardA, jeremy, judie);
//	
//		PrivacyPreference cardB = manager.getOrCreate("cardB", PrivacyPreferenceType.PROFILECARD);
//		manager.grantAccess(cardB, phone);
//		manager.grantAccess(cardB, keith);
//		
//		cardA = pimoService.get(cardA.asResource(), PrivacyPreference.class);
//		assertEquals(name.asResource(), cardA.getAllAppliesToResource().next().asResource());
//		AccessSpace spaceA = pimoService.get(cardA.getAllAccessSpace().next().asResource(), AccessSpace.class);
//		assertEquals(2, spaceA.getAllIncludes_as().count());
//
//		cardB = pimoService.get(cardB.asResource(), PrivacyPreference.class);
//		assertEquals(phone.asResource(), cardB.getAllAppliesToResource().next().asResource());
//		AccessSpace spaceB = pimoService.get(cardB.getAllAccessSpace().next().asResource(), AccessSpace.class);
//		assertEquals(keith.asResource(), spaceB.getAllIncludes().next().asResource());
//	}
	
	@Test
	public void testGrantAccessToLivePosts() throws PrivacyPreferenceException, NotFoundException {
		LivePost lp1 = modelFactory.getDLPOFactory().createLivePost();
		LivePost lp2 = modelFactory.getDLPOFactory().createCheckin();
		LivePost lp3 = modelFactory.getDLPOFactory().createImagePost();
		
		Agent jeremy = modelFactory.getPIMOFactory().createAgent(); 
		Agent judie = modelFactory.getPIMOFactory().createAgent(); 
		Agent keith = modelFactory.getPIMOFactory().createAgent(); 

		// creates 'sharedThrough' di.me account 
		Account account = buildAccount();

		// creates the privacy preferences
		PrivacyPreference lp1pp = manager.grantAccess(lp1, account.asURI(), jeremy, judie, keith);
		PrivacyPreference lp2pp = manager.grantAccess(lp2, account.asURI(), keith);
		PrivacyPreference lp3pp = manager.grantAccess(lp3, account.asURI(), judie, jeremy);

		// load them and check if they contain the correct privacy settings
		lp1pp = pimoService.get(lp1pp.asResource(), PrivacyPreference.class);
		assertEquals(lp1.asResource(), lp1pp.getAllAppliesToResource().next().asResource());
		AccessSpace space1 = pimoService.get(lp1pp.getAllAccessSpace().next().asResource(), AccessSpace.class);
		assertEquals(3, space1.getAllIncludes_as().count());
		assertEquals(account, space1.getSharedThrough().asURI());

		lp2pp = pimoService.get(lp2pp.asResource(), PrivacyPreference.class);
		assertEquals(lp2.asResource(), lp2pp.getAllAppliesToResource().next().asResource());
		AccessSpace space2 = pimoService.get(lp2pp.getAllAccessSpace().next().asResource(), AccessSpace.class);
		assertEquals(keith.asResource(), space2.getAllIncludes().next().asResource());
		assertEquals(account, space2.getSharedThrough().asURI());

		lp3pp = pimoService.get(lp3pp.asResource(), PrivacyPreference.class);
		assertEquals(lp3.asResource(), lp3pp.getAllAppliesToResource().next().asResource());
		AccessSpace space3 = pimoService.get(lp3pp.getAllAccessSpace().next().asResource(), AccessSpace.class);
		assertEquals(2, space3.getAllIncludes_as().count());
		assertEquals(account, space3.getSharedThrough().asURI());
	}

	// TODO deprecated: adapt test for saving the privacy preference definition using the save method
//	@Test
//	public void testHasAccessTo() throws PrivacyPreferenceException {
//		FileDataObject file1 = modelFactory.getNFOFactory().createFileDataObject();
//		FileDataObject file2 = modelFactory.getNFOFactory().createFileDataObject();
//		FileDataObject file3 = modelFactory.getNFOFactory().createFileDataObject();
//		
//		Agent pierre = modelFactory.getPIMOFactory().createAgent(); 
//		Agent simon = modelFactory.getPIMOFactory().createAgent(); 
//		Agent mohamed = modelFactory.getPIMOFactory().createAgent(); 
//		
//		PrivacyPreference publicDatabox = manager.getOrCreate("public", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(publicDatabox, new FileDataObject[]{file1, file2});
//		manager.grantAccess(publicDatabox, new Agent[]{simon, pierre, mohamed});
//		
//		PrivacyPreference privateDatabox = manager.getOrCreate("private", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(privateDatabox, new FileDataObject[]{file3});
//		manager.grantAccess(privateDatabox, new Agent[]{simon});
//		
//		assertTrue(manager.hasAccessTo(file1, simon));
//		assertTrue(manager.hasAccessTo(file3, simon));
//		assertTrue(manager.hasAccessTo(file2, pierre));
//		assertFalse(manager.hasAccessTo(file3, pierre));
//		assertFalse(manager.hasAccessTo(file3, mohamed));
//	}
	
	@Test
	public void testHasAccessToLivePost() throws Exception {
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("privacypreference-livepost.ttl"),
				Syntax.Turtle, sinkModel);
		tripleStore.addModel(sinkModel, pimoService.getPimoUri());
		tripleStore.addStatement(pimoService.getPimoUri(), new URIImpl("urn:juan:juanPerson02"), PIMO.isDefinedBy, pimoService.getPimoUri());
		sinkModel.close();

		Resource livePost = pimoService.get(new URIImpl("urn:uuid:7c0dfda1-096f-4f2d-8403-6813e40e38b6"), LivePost.class);
		Person person = pimoService.get(new URIImpl("urn:juan:juanPerson02"), Person.class);
		assertTrue(manager.hasAccessTo(livePost, person));
	}
	
	// TODO deprecated: adapt test for saving the privacy preference definition using the save method
//	@Test
//	public void testHasAccessToFileInDatabox() throws Exception {
//		FileDataObject file1 = modelFactory.getNFOFactory().createFileDataObject();
//		Agent ismael = modelFactory.getPIMOFactory().createAgent(); 
//		
//		PrivacyPreference publicDatabox = manager.getOrCreate("public", PrivacyPreferenceType.DATABOX);
//		manager.grantAccess(publicDatabox, new FileDataObject[]{file1});
//		manager.grantAccess(publicDatabox, new Agent[]{ismael});
//
//		assertTrue(manager.hasAccessTo(file1, ismael));
//	}

	@Test
	public void testGetIncludesAccessSpaceMetadata() throws Exception {
		Agent simon = modelFactory.getPIMOFactory().createAgent();
		Account account = buildAccount();
		PrivacyPreference databox = manager.getOrCreate("business", PrivacyPreferenceType.DATABOX);
		manager.grantAccess(databox, account.asURI(), simon);
		
		// reload
		databox = manager.getOrCreate("business", PrivacyPreferenceType.DATABOX);
		assertTrue(databox.getModel().contains(databox.getAllAccessSpace().next(), RDF.type, PPO.AccessSpace));
		assertTrue(databox.getModel().contains(databox.getAllAccessSpace().next(), NSO.includes, simon.asResource()));
	}
	
}
