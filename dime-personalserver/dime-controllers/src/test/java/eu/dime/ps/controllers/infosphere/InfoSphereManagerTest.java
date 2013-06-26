package eu.dime.ps.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.semantic.model.nie.DataSource;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class InfoSphereManagerTest extends Assert {

	@Autowired
	protected TripleStore tripleStore;

	@Autowired
	protected ResourceStore resourceStore;

	@Autowired
	protected PimoService pimoService;

	@Autowired
	protected Connection connection;

	protected ModelFactory modelFactory = new ModelFactory();

	private URI file1 = new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png");
	private URI file2 = new URIImpl("file:/home/ismriv/example/dir1/D2.3.2_FAST_requirements_specification.v2.2.pdf");
	private URI file3 = new URIImpl("file:/home/ismriv/example/dir1/D2.1.2_StateOfTheArt_v1.pdf");

    @BeforeClass
    public static void setUpClass() throws Exception {
    	// disabling org.openrdf.rdf2go.RepositoryModel warnings
    	org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
    	java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
    }

	@Before
	public void setUp() throws Exception {
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));
		pimoService.clear();
		
		// add test FileDataObjet resources
		pimoService.getTripleStore().addStatement(pimoService.getPimoUri(), file1, RDF.type, NFO.FileDataObject);
		pimoService.getTripleStore().addStatement(pimoService.getPimoUri(), file2, RDF.type, NFO.FileDataObject);
		pimoService.getTripleStore().addStatement(pimoService.getPimoUri(), file3, RDF.type, NFO.FileDataObject);
	}
	
	@After
	public void tearDown() throws Exception {
		TenantContextHolder.clear();
	}

	protected void loadData(String file) throws ModelRuntimeException, IOException {
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream(file),
				Syntax.Ntriples, sinkModel);
		pimoService.getTripleStore().addAll(sinkModel.iterator());
		sinkModel.close();
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
	
	protected Account buildAccount(String name, String accountType) {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel(name);
		account.setAccountType(accountType);
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

	protected DataContainer buildDatabox() {
		PrivacyPreference databox = modelFactory.getPPOFactory().createPrivacyPreference();
		databox.getModel().addStatement(databox, RDF.type, NFO.DataContainer);
		databox.setLabel("DATABOX");
		databox.setPrefLabel("example");
		
		databox.getModel().addStatement(databox, NIE.hasPart, file1);
		databox.getModel().addStatement(databox, NIE.hasPart, file2);
		databox.getModel().addStatement(databox, NIE.hasPart, file3);
		
		databox.addAppliesToResource(file1);
		databox.addAppliesToResource(file2);
		databox.addAppliesToResource(file3);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		Person simon = modelFactory.getPIMOFactory().createPerson();
		accessSpace.setIncludes(ismael);
		accessSpace.setExcludes(simon);
		databox.setAccessSpace(accessSpace);
		
		databox.getModel().addAll(accessSpace.getModel().iterator());
		databox.getModel().addAll(ismael.getModel().iterator());
		databox.getModel().addAll(simon.getModel().iterator());
		
		return (DataContainer) databox.castTo(DataContainer.class);
	}
	
	protected DataSource buildDataSource(String label) {
		DataSource ds = modelFactory.getNIEFactory().createDataSource();
		ds.setPrefLabel(label);
		return ds;
	}

	protected LivePost buildLivePost(String textualContent) {
		LivePost livepost = modelFactory.getDLPOFactory().createLivePost();
		livepost.setTextualContent(textualContent);
		return livepost;
	}

}
