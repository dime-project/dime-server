package eu.dime.ps.semantic.poc;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.TripleStoreImpl;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.ncal.Event;
import eu.dime.ps.semantic.model.nco.BirthDate;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.privacy.impl.PrivacyPreferenceServiceImpl;
import eu.dime.ps.semantic.query.impl.PimoQuery;
import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.rdf.impl.SesameMemoryRepositoryFactory;
import eu.dime.ps.semantic.service.impl.PimoService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/semantic-poc-tests-context.xml")
public class PocJuanTest extends TestCase {

	private static final Logger logger = LoggerFactory.getLogger(PocJuanTest.class);
	
	private TripleStore tripleStore;
	private ResourceStore resourceStore;
	private PimoService pimoService;
	private PrivacyPreferenceService privacyPrefService;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setUp() throws Exception {

		// config data
		Model configModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-pimo.trig"),
				Syntax.Trig, configModel);

		// rest of data
		ModelSet sinkModel = RDF2Go.getModelFactory().createModelSet();
		sinkModel.open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-profile.trig"),
				Syntax.Trig, sinkModel);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-resources.ttl"),
				Syntax.Turtle, sinkModel);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-databoxes.ttl"),
				Syntax.Turtle, sinkModel);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-liveposts.ttl"),
				Syntax.Turtle, sinkModel);
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("poc-data/juan/juan-situations.trig"),
				Syntax.Trig, sinkModel);
		
		RepositoryFactory factory = new SesameMemoryRepositoryFactory();
		tripleStore = new TripleStoreImpl("test", factory.get("test"));
		resourceStore = new ResourceStoreImpl(tripleStore);
		tripleStore.addAll(configModel.iterator());
		pimoService = new PimoService(tripleStore);
		tripleStore.addAll(sinkModel.iterator());
		privacyPrefService = new PrivacyPreferenceServiceImpl(pimoService);
		
		configModel.close();
		sinkModel.close();
	}
	
	@Test
	public void testGetAllPeople() throws Exception {
		Collection<Person> people =
				pimoService.find(Person.class)
					.distinct()
					.select(NAO.prefLabel, NAO.prefSymbol, PIMO.groundingOccurrence, PIMO.occurrence)
					.where(pimoService.getUserUri(), FOAF.knows, PimoQuery.THIS)
					.results();
			
		logger.info("people found: "+Arrays.toString(people.toArray()));
		assertEquals(13, people.size());
	}

	@Test
	public void testGetAllDataboxes() throws Exception {
		Collection<DataContainer> databoxes =
			resourceStore.find(DataContainer.class)
				.distinct()
				.where(RDFS.label).is(PrivacyPreferenceType.DATABOX)
				.results();

		logger.info("databoxes found: "+Arrays.toString(databoxes.toArray()));
		
		assertEquals(4, databoxes.size());
	}
	
	@Test
	public void testGetAllEvents() throws Exception {
		Collection<Event> events =
			resourceStore.find(Event.class)
				.distinct()
				.results();

		logger.info("events found: "+Arrays.toString(events.toArray()));
		assertEquals(3, events.size());
	}
	
	@Test
	public void testGetAllAccounts() throws Exception {
		Collection<Account> accounts =
			pimoService.find(Account.class)
				.distinct()
				.select(NAO.prefLabel, NAO.prefSymbol, RDFS.label, DAO.hasCredentials)
				.results();
		
		logger.info("accounts found: "+Arrays.toString(accounts.toArray()));
		
		assertEquals(4, accounts.size());
	}
	
	@Test
	public void testGetAllLivePosts() throws Exception {
		Collection<LivePost> livePosts =
			resourceStore.find(LivePost.class)
				.distinct()
				.where(NAO.creator).is(pimoService.getUserUri())
				.results();
	
		logger.info("live posts found: "+Arrays.toString(livePosts.toArray()));
		
		assertEquals(7, livePosts.size());
	}
	
	@Test
	public void testGetBirthDate() throws Exception {
		BirthDate dob = pimoService.get(new URIImpl("urn:uuid:j000077"), BirthDate.class);
		assertEquals(5, dob.getBirthDate().get(Calendar.DATE));
		assertEquals(7, dob.getBirthDate().get(Calendar.MONTH)); // 0 to 11
		assertEquals(1966, dob.getBirthDate().get(Calendar.YEAR));
	}
	
	@Test
	public void testGetPrivacyLevelForResource() throws Exception {
		Resource resource = pimoService.get(new URIImpl("urn:uuid:j000077"));
		ClosableIterator<Statement> it = resource.getModel().findStatements(resource.asResource(), NAO.privacyLevel, Variable.ANY);
		if (it.hasNext()) {
			Statement statement = it.next();
			assertEquals(0.3d, Double.parseDouble(statement.getObject().asDatatypeLiteral().getValue()));
		} else {
			fail("No nao:privacyLevel associated with "+resource.asResource()+" was found.");
		}
		it.close();
	}
	
	@Test
	public void testGetPrivacyLevelForDataObjects() throws Exception {
		for (DataObject dataObject : pimoService.find(DataObject.class).distinct().results()) {
			double privacyLevel;
			if (dataObject.hasPrivacyLevel()){
				privacyLevel = dataObject.getAllPrivacyLevel().next().doubleValue();
				assertTrue("privacy level must be in between 0 and 1", privacyLevel > 0 && privacyLevel < 1);
			}
		}
	}
	
	@Test
	public void testGetAgentsWithAccessTo() throws Exception {
		assertEquals(3, privacyPrefService.getAgentsWithAccessTo(new URIImpl("urn:uuid:j000525")).size());
		assertEquals(4, privacyPrefService.getAgentsWithAccessTo(new URIImpl("urn:uuid:j000526")).size());
		assertEquals(3, privacyPrefService.getAgentsWithAccessTo(new URIImpl("urn:uuid:j000527")).size());
		assertEquals(4, privacyPrefService.getAgentsWithAccessTo(new URIImpl("urn:uuid:j000528")).size());
	}
	
	@Test
	public void getGetPrivacyPreference() throws Exception {
		PrivacyPreference preference = pimoService.get(new URIImpl("urn:uuid:j000409"), PrivacyPreference.class);
		assertTrue(preference.getAllAppliesToResource().hasNext());
		
		FileDataObject file = pimoService.get(preference.getAllAppliesToResource().next(), FileDataObject.class);
		assertEquals("urn:uuid:j000526", file.toString());
		assertEquals("construction_plan_bridge_Brown.jpg", file.getFileName());
	}

}
