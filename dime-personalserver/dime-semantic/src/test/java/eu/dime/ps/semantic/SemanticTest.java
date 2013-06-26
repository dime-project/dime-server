package eu.dime.ps.semantic;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.TripleStoreImpl;
import ie.deri.smile.rdf.util.ModelUtils;

import java.io.InputStream;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.SesameMemoryRepositoryFactory;

/**
 * Abstract test case which must be extended by the test cases in
 * the semantic module.
 * 
 * @author Ismael Rivera
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/semantic-tests-context.xml")
public abstract class SemanticTest extends Assert {

	@Autowired
	protected RepositoryFactory repositoryFactory;

	@Autowired
	protected Repository repository;
	
	@Autowired
	protected TripleStore tripleStore;

	@Autowired
	protected ResourceStore resourceStore;

	@Autowired
	protected ModelFactory modelFactory;

	@BeforeClass
	public static void setUpClass() throws Exception {
		//disable warnings from RepositoryModel
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);		
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
		
		org.apache.log4j.Logger.getLogger("org.semanticdesktop.aperture").setLevel(org.apache.log4j.Level.OFF);		
		java.util.logging.Logger.getLogger("org.semanticdesktop.aperture").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setUp() throws Exception {
//		tripleStore.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	/*
	 * creates an empty triple store
	 */
	protected TripleStore createTripleStore() {
		TripleStore store = null;
		String name = UUID.randomUUID().toString();
		try {
			store = new TripleStoreImpl(name, new SesameMemoryRepositoryFactory().get(name));
		} catch (Exception e) {
			fail("cannot create the triple store: " + e);
		}
		return store;
	}
	
	/*
	 * creates and initializes a test triple store
	 */
	protected TripleStore createTripleStore(InputStream is, Syntax syntax) {
		TripleStore store = null;
		Model model = null;
		String name = UUID.randomUUID().toString();
		try {
			store = new TripleStoreImpl(name, new SesameMemoryRepositoryFactory().get(name));
			model = RDF2Go.getModelFactory().createModel().open();
			ModelUtils.loadFromInputStream(is, syntax, model);
			store.addAll(model.iterator());
		} catch (Exception e) {
			fail("cannot create and initialize the triple store: " + e);
		} finally {
			if (model != null) {
				model.close();
			}
		}
		return store;
	}
	
	protected Person buildPerson(String name) {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);
		return person;
	}
	
	protected PersonGroup buildPersonGroup(String label, Person... people) {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setPrefLabel(label);
		for (Person person : people) {
			group.addMember(person);
		}
		return group;
	}
	
	protected PersonContact buildProfile(String name, String email, String phone) {
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		PersonName personName = modelFactory.getNCOFactory().createPersonName();
		personName.setFullname(name);
		profile.setPersonName(personName);
		profile.getModel().addAll(personName.getModel().iterator());
		EmailAddress emailAddress = modelFactory.getNCOFactory().createEmailAddress();
		emailAddress.setEmailAddress(email);
		profile.setEmailAddress(emailAddress);
		profile.getModel().addAll(emailAddress.getModel().iterator());
		PhoneNumber phoneNumber = modelFactory.getNCOFactory().createPhoneNumber();
		phoneNumber.setPhoneNumber(phone);
		profile.setPhoneNumber(phoneNumber);
		profile.getModel().addAll(phoneNumber.getModel().iterator());
		return profile;
	}

	protected Device buildDevice(String name) {
		Device device = modelFactory.getDDOFactory().createDevice();
		device.setPrefLabel(name);
		device.setPrefSymbol(new URIImpl("http://www.dime-project.eu/resources/html/3088/dimeheader.png"));
		return device;
	}
	
	protected SocialEvent buildSocialEvent(String description) {
		SocialEvent event = modelFactory.getPIMOFactory().createSocialEvent();
		event.setDescription(description);
//		event.setDtstart(Calendar.getInstance());
//		event.setDtend(Calendar.g)
		return event;
	}
	
	protected Account buildAccount(String name) {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel(name);
		account.setLabel("Account");
		return account;
	}

	protected Location buildLocation(String name) {
		Location location = modelFactory.getPIMOFactory().createLocation();
		location.setPrefLabel(name);
		return location;
	}

	protected Placemark buildPlacemark(String name) {
		Placemark placemark = modelFactory.getNFOFactory().createPlacemark();
		placemark.setPrefLabel(name);
		return placemark;
	}

	protected PrivacyPreference buildDatabox() {
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.setLabel("DATABOX");
		
		URI f1 = new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png");
		URI f2 = new URIImpl("file:/home/ismriv/example/dir1/D2.3.2_FAST_requirements_specification.v2.2.pdf");
		URI f3 = new URIImpl("file:/home/ismriv/example/dir1/D2.1.2_StateOfTheArt_v1.pdf");
		databox.setPrefLabel("example");
		databox.addAppliesToResource(f1);
		databox.addAppliesToResource(f2);
		databox.addAppliesToResource(f3);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		Person simon = modelFactory.getPIMOFactory().createPerson();
		accessSpace.setIncludes(ismael);
		accessSpace.setExcludes(simon);
		databox.setAccessSpace(accessSpace);
		
		databox.getModel().addAll(accessSpace.getModel().iterator());
		databox.getModel().addAll(ismael.getModel().iterator());
		databox.getModel().addAll(simon.getModel().iterator());
		
		return databox;
	}

}
