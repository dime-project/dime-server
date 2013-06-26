package eu.dime.ps.communications.requestbroker.controllers;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.communications.requestbroker.controllers.infosphere.PSSharedController;
import eu.dime.ps.communications.utils.Base64encoding;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableFileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableLivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.ObjectFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nco.PhoneNumber;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config/controllers-tests-context.xml")
public class PSSharedControllerTestIt extends Assert {

	private PSSharedController controller;
	
	@Autowired
	private ModelFactory modelFactory;

	@Autowired
	private Connection connection;

	@Autowired
	private ConnectionProvider connectionProvider;

	@Autowired
	private PimoService pimoService;

	@Autowired
	private EntityFactory entityFactory;
	
	@Autowired
	private DataboxManager databoxManager;
	
	@Autowired
	private FileManager fileManager;

	@Autowired
	private LivePostManager livePostManager;

	@Autowired
	private ProfileCardManager profileCardManager;

	@Autowired
	private SharingManager sharingManager;

	@Autowired
	private ShareableDataboxManager shareableDataboxManager;
	
	@Autowired
	private ShareableFileManager shareableFileManager;

	@Autowired
	private ShareableLivePostManager shareableLivePostManager;

	@Autowired
	private ShareableProfileManager shareableProfileManager;

	private static final String USERNAME = "adsasdasd";
	private static final String PASSWORD = "81729837128";
	private static final String SAID = "8738-ads345a-34dasda353-787g";
	
	private Tenant dbTenant;
	private User dbUser;
	private ServiceAccount dbAccount;
	
	private Person friend;
	private Account sender;
	private Account recipient;
	private Account otherRecipient;

    @BeforeClass
    public static void setUpClass() throws Exception {
    	// disabling org.openrdf.rdf2go.RepositoryModel warnings
    	org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
    	java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
    }

	@Before
	public void setUp() throws Exception {

		// create dummy data for tests
		friend = ObjectFactory.buildPerson("John Doe");
		sender = ObjectFactory.buildAccount("My account", DimeServiceAdapter.NAME, pimoService.getUserUri());
		recipient = ObjectFactory.buildAccount("Someone's account", DimeServiceAdapter.NAME, friend.asURI());
		otherRecipient = ObjectFactory.buildAccount("Other's account", DimeServiceAdapter.NAME, friend.asURI());
		pimoService.createOrUpdate(sender);
		pimoService.createOrUpdate(recipient);
		pimoService.createOrUpdate(otherRecipient);

		dbTenant = entityFactory.buildTenant();
		dbTenant.setName("test"+System.nanoTime());
		dbTenant.persist();
		dbUser = User.findByAccountUri(recipient.toString());
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
    	
		// mocking connection provider
		when(connectionProvider.getConnection(dbTenant.getId().toString())).thenReturn(connection);

		controller = new PSSharedController();
		controller.setShareableDataboxManager(shareableDataboxManager);
		controller.setShareableFileManager(shareableFileManager);
		controller.setShareableLivePostManager(shareableLivePostManager);
		controller.setShareableProfileManager(shareableProfileManager);
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
	public void testGetProfileJSONLDExplicitShared() throws Exception {
		
		// a profile card can be explicitly shared with agents
		
		PersonName personName = ObjectFactory.buildPersonName("Ismael Rivera");
		EmailAddress emailAddress = ObjectFactory.buildEmailAddress("example@email.com");
		PhoneNumber phoneNumber = ObjectFactory.buildPhoneNumber("5555555");
		PersonContact profile = ObjectFactory.buildPersonContact(personName, emailAddress, phoneNumber);
		pimoService.createOrUpdate(profile);
		
		PrivacyPreference profileCard = ObjectFactory.buildProfileCard("My profile card", new Resource[]{ personName, emailAddress, phoneNumber }, pimoService.getUserUri(), sender.asURI(), new Account[]{ recipient });
		profileCardManager.add(profileCard);
		
		Object json = controller.getProfileJSONLD(SAID);

		assertNotNull(json);
		assertTrue(json instanceof List);
		
		List response = (List) json;
		assertEquals(4, response.size()); // PersonContact + 3 attributes

		List<String> ids = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for (Object entry : response) {
			Map<String, Object> data = (Map<String, Object>) entry;
			ids.add(data.get("@id").toString());
			Object type = data.get("@type");
			if (type instanceof List) {
				// some resources may have several types
				List<String> typeList = (List<String>) type;
				for (String t : typeList) {
					types.add(t);
				}
			} else {
				types.add(type.toString());
			}
		}
		
		assertTrue(ids.contains(personName.toString()));
		assertTrue(ids.contains(emailAddress.toString()));
		assertTrue(ids.contains(phoneNumber.toString()));
		// the other id is the profile URI, but I don't know it beforehand, it's arbitrary

		assertEquals(10, types.size());
		assertTrue(types.contains("nco:PersonContact"));
		assertTrue(types.contains("nco:PersonName"));
		assertTrue(types.contains("nco:EmailAddress"));
		assertTrue(types.contains("nco:PhoneNumber"));
	}

	@Test
	public void testGetProfileJSONLDImplicitShared() throws Exception {

		// a profile card can also be accessed if any another privacy preference (but not a profile card) is shared
		// through the same di.me account with the requester agent
		
		DataContainer databox = ObjectFactory.buildDatabox("My databox", new DataObject[]{}, pimoService.getUserUri(), sender.asURI(), recipient);
		databoxManager.add(databox);
		
		PersonName personName = ObjectFactory.buildPersonName("Ismael Rivera");
		EmailAddress emailAddress = ObjectFactory.buildEmailAddress("example@email.com");
		PhoneNumber phoneNumber = ObjectFactory.buildPhoneNumber("5555555");
		PersonContact profile = ObjectFactory.buildPersonContact(personName, emailAddress, phoneNumber);
		pimoService.createOrUpdate(profile);
		
		PrivacyPreference profileCard = ObjectFactory.buildProfileCard("My profile card", new Resource[]{ personName, emailAddress, phoneNumber }, pimoService.getUserUri(), sender.asURI());
		profileCardManager.add(profileCard);
		
		Object json = controller.getProfileJSONLD(SAID);
		
		assertNotNull(json);
		assertTrue(json instanceof List);
		
		List response = (List) json;
		assertEquals(4, response.size()); // PersonContact + 3 attributes

		List<String> ids = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for (Object entry : response) {
			Map<String, Object> data = (Map<String, Object>) entry;
			ids.add(data.get("@id").toString());
			Object type = data.get("@type");
			if (type instanceof List) {
				// some resources may have several types
				List<String> typeList = (List<String>) type;
				for (String t : typeList) {
					types.add(t);
				}
			} else {
				types.add(type.toString());
			}
		}
		
		assertTrue(ids.contains(personName.toString()));
		assertTrue(ids.contains(emailAddress.toString()));
		assertTrue(ids.contains(phoneNumber.toString()));
		// the other id is the profile URI, but I don't know it beforehand, it's arbitrary

		assertEquals(10, types.size());
		assertTrue(types.contains("nco:PersonContact"));
		assertTrue(types.contains("nco:PersonName"));
		assertTrue(types.contains("nco:EmailAddress"));
		assertTrue(types.contains("nco:PhoneNumber"));

	}

	@Test
	public void testShareAndGetProfileJSONLD() throws Exception {
		
		// a profile card can be explicitly shared with agents
		
		PersonName personName = ObjectFactory.buildPersonName("Ismael Rivera");
		EmailAddress emailAddress = ObjectFactory.buildEmailAddress("example@email.com");
		PhoneNumber phoneNumber = ObjectFactory.buildPhoneNumber("5555555");
		PersonContact profile = ObjectFactory.buildPersonContact(personName, emailAddress, phoneNumber);
		pimoService.createOrUpdate(profile);
		
		PrivacyPreference profileCard = ObjectFactory.buildProfileCard("My profile card", new Resource[]{ personName, emailAddress, phoneNumber }, pimoService.getUserUri());
		profileCardManager.add(profileCard);
		sharingManager.shareProfileCard(profileCard.toString(), sender.toString(), new String[]{ recipient.toString() });

		Object json = controller.getProfileJSONLD(SAID);

		assertNotNull(json);
		assertTrue(json instanceof List);
		
		List response = (List) json;
		assertEquals(4, response.size()); // PersonContact + 3 attributes

		List<String> ids = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for (Object entry : response) {
			Map<String, Object> data = (Map<String, Object>) entry;
			ids.add(data.get("@id").toString());
			Object type = data.get("@type");
			if (type instanceof List) {
				// some resources may have several types
				List<String> typeList = (List<String>) type;
				for (String t : typeList) {
					types.add(t);
				}
			} else {
				types.add(type.toString());
			}
		}
		
		assertTrue(ids.contains(personName.toString()));
		assertTrue(ids.contains(emailAddress.toString()));
		assertTrue(ids.contains(phoneNumber.toString()));
		// the other id is the profile URI, but I don't know it beforehand, it's arbitrary

		assertEquals(10, types.size());
		assertTrue(types.contains("nco:PersonContact"));
		assertTrue(types.contains("nco:PersonName"));
		assertTrue(types.contains("nco:EmailAddress"));
		assertTrue(types.contains("nco:PhoneNumber"));
	}

	@Test
	public void testGetProfileJSONLDNotShared() throws Exception {
		PersonName personName = ObjectFactory.buildPersonName("Ismael Rivera");
		EmailAddress emailAddress = ObjectFactory.buildEmailAddress("example@email.com");
		PhoneNumber phoneNumber = ObjectFactory.buildPhoneNumber("5555555");
		PersonContact profile = ObjectFactory.buildPersonContact(personName, emailAddress, phoneNumber);
		pimoService.createOrUpdate(profile);
		
		PrivacyPreference profileCard = ObjectFactory.buildProfileCard("My profile card", new Resource[]{ personName, emailAddress, phoneNumber }, pimoService.getUserUri());
		profileCardManager.add(profileCard);
		// not calling 'share', when retrieving an error should be returned
		
		Object json = controller.getProfileJSONLD(SAID);

		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map response = (Map) json;
		assertTrue(response.containsKey("error"));
		assertTrue(response.get("error").toString().contains("No profile was shared"));
	}

	@Test
	public void testGetProfileJSONLDSharedWithWrongRecipient() throws Exception {
		PersonName personName = ObjectFactory.buildPersonName("Ismael Rivera");
		PersonContact profile = ObjectFactory.buildPersonContact(personName);
		pimoService.createOrUpdate(profile);
		
		PrivacyPreference profileCard = ObjectFactory.buildProfileCard("My profile card", new Resource[]{ personName }, pimoService.getUserUri());
		profileCardManager.add(profileCard);
		sharingManager.shareProfileCard(profileCard.toString(), sender.toString(), new String[]{ otherRecipient.toString() });
		
		Object json = controller.getProfileJSONLD(SAID);

		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map response = (Map) json;
		assertTrue(response.containsKey("error"));
		assertTrue(response.get("error").toString().contains("No profile was shared"));
	}

	@Test
	public void testGetDataboxJSONLD() throws Exception {
		DataObject dataObject1 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		DataObject dataObject2 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		pimoService.createOrUpdate(dataObject1);
		pimoService.createOrUpdate(dataObject2);
		
		DataContainer databox = ObjectFactory.buildDatabox("My databox", new DataObject[]{ dataObject1, dataObject2 }, pimoService.getUserUri(), sender.asURI(), recipient);
		databoxManager.add(databox);
		
		String encodedId = Base64encoding.encode(databox.toString());
		Object json = controller.getDataboxJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map<String, Object> response = (Map<String, Object>) json;
		assertEquals(7, response.size());
		assertTrue(response.containsKey("@context"));
		assertTrue(response.containsKey("@id"));
		assertTrue(response.containsKey("@type"));
		assertTrue(response.containsKey("nao:created"));
		assertTrue(response.containsKey("nao:lastModified"));
		assertTrue(response.containsKey("nao:prefLabel"));
		assertTrue(response.containsKey("nie:hasPart"));

		assertEquals(databox.toString(), response.get("@id"));
		assertEquals("My databox", response.get("nao:prefLabel"));
		
		List<String> types = (List<String>) response.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));

		Object parts = response.get("nie:hasPart");
		assertTrue(parts instanceof List);
		assertEquals(2, ((List) parts).size());
	}

	@Test
	public void testShareAndGetDataboxJSONLD() throws Exception {
		DataObject dataObject1 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		DataObject dataObject2 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		pimoService.createOrUpdate(dataObject1);
		pimoService.createOrUpdate(dataObject2);
		
		DataContainer databox = ObjectFactory.buildDatabox("My databox", new DataObject[]{ dataObject1, dataObject2 }, pimoService.getUserUri());
		databoxManager.add(databox);
		sharingManager.shareDatabox(databox.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		String encodedId = Base64encoding.encode(databox.toString());
		Object json = controller.getDataboxJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map<String, Object> response = (Map<String, Object>) json;
		assertEquals(7, response.size());
		assertTrue(response.containsKey("@context"));
		assertTrue(response.containsKey("@id"));
		assertTrue(response.containsKey("@type"));
		assertTrue(response.containsKey("nao:created"));
		assertTrue(response.containsKey("nao:lastModified"));
		assertTrue(response.containsKey("nao:prefLabel"));
		assertTrue(response.containsKey("nie:hasPart"));

		assertEquals(databox.toString(), response.get("@id"));
		assertEquals("My databox", response.get("nao:prefLabel"));
		
		List<String> types = (List<String>) response.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));

		Object parts = response.get("nie:hasPart");
		assertTrue(parts instanceof List);
		assertEquals(2, ((List) parts).size());
	}

	@Test
	public void testGetDataboxJSONLDNotShared() throws Exception {
		DataContainer databox = ObjectFactory.buildDatabox("My databox", new DataObject[0], pimoService.getUserUri());
		databoxManager.add(databox);
		// not calling 'share', when retrieving an error should be returned

		String encodedId = Base64encoding.encode(databox.toString());
		Object json = controller.getDataboxJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map response = (Map) json;
		assertTrue(response.containsKey("error"));
		assertTrue(response.get("error").toString().contains("Cannot retrieve resource"));
	}

	@Test
	public void testGetAllDataboxJSONLD() throws Exception {
		DataObject dataObject1 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		DataObject dataObject2 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		pimoService.createOrUpdate(dataObject1);
		pimoService.createOrUpdate(dataObject2);

		DataContainer databoxA = ObjectFactory.buildDatabox("Databox A", new DataObject[]{ dataObject1 }, pimoService.getUserUri(), sender.asURI(), recipient);
		databoxManager.add(databoxA);
		DataContainer databoxB = ObjectFactory.buildDatabox("Databox B", new DataObject[]{ dataObject2 }, pimoService.getUserUri(), sender.asURI(), recipient);
		databoxManager.add(databoxB);
		
		Object json = controller.getAllDataboxJSONLD(SAID);
		assertNotNull(json);
		assertTrue(json instanceof List);

		List<Map<String, Object>> response = (List<Map<String, Object>>) json;
		assertEquals(2, response.size());
		
		Map<String, Object> dbAJsonld = response.get(0);
		Map<String, Object> dbBJsonld = null;
		if (databoxA.toString().equals(dbAJsonld.get("@id"))) {
			dbBJsonld = response.get(1);
		} else {
			dbAJsonld = response.get(1);
			dbBJsonld = response.get(0);
		}
		
		// verify databoxA metadata is correct
		
		assertEquals(databoxA.toString(), dbAJsonld.get("@id"));
		List<String> types = (List<String>) dbAJsonld.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));
		
		Object parts = dbAJsonld.get("nie:hasPart");
		assertTrue(parts instanceof Map);
		assertEquals(1, ((Map) parts).size());
		assertEquals(dataObject1.toString(), ((Map) parts).get("@id"));

		// verify databoxB metadata is correct
		
		assertEquals(databoxB.toString(), dbBJsonld.get("@id"));
		types = (List<String>) dbBJsonld.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));
		
		parts = dbBJsonld.get("nie:hasPart");
		assertTrue(parts instanceof Map);
		assertEquals(1, ((Map) parts).size());
		assertEquals(dataObject2.toString(), ((Map) parts).get("@id"));
	}
	
	@Test
	public void testShareAndGetAllDataboxJSONLD() throws Exception {
		DataObject dataObject1 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		DataObject dataObject2 = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		pimoService.createOrUpdate(dataObject1);
		pimoService.createOrUpdate(dataObject2);

		DataContainer databoxA = ObjectFactory.buildDatabox("Databox A", new DataObject[]{ dataObject1 }, pimoService.getUserUri());
		databoxManager.add(databoxA);
		sharingManager.shareDatabox(databoxA.toString(), sender.toString(), new String[]{ recipient.toString() });
		DataContainer databoxB = ObjectFactory.buildDatabox("Databox B", new DataObject[]{ dataObject2 }, pimoService.getUserUri());
		databoxManager.add(databoxB);
		sharingManager.shareDatabox(databoxB.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		Object json = controller.getAllDataboxJSONLD(SAID);
		assertNotNull(json);
		assertTrue(json instanceof List);

		List<Map<String, Object>> response = (List<Map<String, Object>>) json;
		assertEquals(2, response.size());
		
		Map<String, Object> dbAJsonld = response.get(0);
		Map<String, Object> dbBJsonld = null;
		if (databoxA.toString().equals(dbAJsonld.get("@id"))) {
			dbBJsonld = response.get(1);
		} else {
			dbAJsonld = response.get(1);
			dbBJsonld = response.get(0);
		}
		
		// verify databoxA metadata is correct
		
		assertEquals(databoxA.toString(), dbAJsonld.get("@id"));
		List<String> types = (List<String>) dbAJsonld.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));
		
		Object parts = dbAJsonld.get("nie:hasPart");
		assertTrue(parts instanceof Map);
		assertEquals(1, ((Map) parts).size());
		assertEquals(dataObject1.toString(), ((Map) parts).get("@id"));

		// verify databoxB metadata is correct
		
		assertEquals(databoxB.toString(), dbBJsonld.get("@id"));
		types = (List<String>) dbBJsonld.get("@type");
		assertTrue(types.contains("ppo:PrivacyPreference"));
		assertTrue(types.contains("nfo:DataContainer"));
		
		parts = dbBJsonld.get("nie:hasPart");
		assertTrue(parts instanceof Map);
		assertEquals(1, ((Map) parts).size());
		assertEquals(dataObject2.toString(), ((Map) parts).get("@id"));
	}
	
	@Test
	public void testShareAndGetLivepostJSONLD() throws Exception {
		String textualContent = "Message sent on "+System.currentTimeMillis();
		LivePost livepost = ObjectFactory.buildLivePost(textualContent, pimoService.getUserUri(), Calendar.getInstance());
		livePostManager.add(livepost);
		sharingManager.shareLivePost(livepost.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		String encodedId = Base64encoding.encode(livepost.toString());
		Object json = controller.getLivepostJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map<String, Object> response = (Map<String, Object>) json;
		assertEquals(7, response.size());
		assertTrue(response.containsKey("@context"));
		assertTrue(response.containsKey("@id"));
		assertTrue(response.containsKey("@type"));
		assertTrue(response.containsKey("nao:created"));
		assertTrue(response.containsKey("nao:lastModified"));
		assertTrue(response.containsKey("dlpo:textualContent"));
		assertTrue(response.containsKey("dlpo:timestamp"));

		assertEquals(livepost.toString(), response.get("@id"));
		assertEquals(textualContent, response.get("dlpo:textualContent"));
		
		List<String> types = (List<String>) response.get("@type");
		assertTrue(types.contains("dlpo:LivePost"));
	}

	@Test
	public void testGetLivepostJSONLDNotShared() throws Exception {
		LivePost livepost = ObjectFactory.buildLivePost("Some text...", pimoService.getUserUri());
		livePostManager.add(livepost);
		// not calling 'share', when retrieving an error should be returned

		String encodedId = Base64encoding.encode(livepost.toString());
		Object json = controller.getDataboxJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map response = (Map) json;
		assertTrue(response.containsKey("error"));
		assertTrue(response.get("error").toString().contains("Cannot retrieve resource"));
	}

	@Test
	public void testShareAndGetAllLivepostJSONLD() throws Exception {
		LivePost livepostA = ObjectFactory.buildLivePost("Message A", pimoService.getUserUri());
		livePostManager.add(livepostA);
		sharingManager.shareLivePost(livepostA.toString(), sender.toString(), new String[]{ recipient.toString() });
		LivePost livepostB = ObjectFactory.buildLivePost("Message B", pimoService.getUserUri());
		livePostManager.add(livepostB);
		sharingManager.shareLivePost(livepostB.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		Object json = controller.getAllLivepostJSONLD(SAID);
		
		assertNotNull(json);
		assertTrue(json instanceof List);
		
		List<Map<String, Object>> response = (List<Map<String, Object>>) json;
		assertEquals(2, response.size());

		Map<String, Object> lpAJsonld = response.get(0);
		Map<String, Object> lpBJsonld = null;
		if (livepostA.toString().equals(lpAJsonld.get("@id"))) {
			lpBJsonld = response.get(1);
		} else {
			lpAJsonld = response.get(1);
			lpBJsonld = response.get(0);
		}

		// verify livepostA metadata is correct

		assertEquals(livepostA.toString(), lpAJsonld.get("@id"));
		assertEquals("Message A", lpAJsonld.get("dlpo:textualContent"));
		
		List<String> types = (List<String>) lpAJsonld.get("@type");
		assertTrue(types.contains("dlpo:LivePost"));
		
		// verify livepostB metadata is correct

		assertEquals(livepostB.toString(), lpBJsonld.get("@id"));
		assertEquals("Message B", lpBJsonld.get("dlpo:textualContent"));
		
		types = (List<String>) lpBJsonld.get("@type");
		assertTrue(types.contains("dlpo:LivePost"));
	}
	
	@Test
	public void testShareAndGetResourceJSONLD() throws Exception {
		FileDataObject file = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri(), Calendar.getInstance());
		
		fileManager.add(file, this.getClass().getClassLoader().getResourceAsStream("controllers/file/file1.txt"));
		sharingManager.shareFile(file.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		String encodedId = Base64encoding.encode(file.toString());
		Object json = controller.getResourceJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map<String, Object> response = (Map<String, Object>) json;
		assertEquals(9, response.size());
		assertTrue(response.containsKey("@context"));
		assertTrue(response.containsKey("@id"));
		assertTrue(response.containsKey("@type"));
		assertTrue(response.containsKey("nao:created"));
		assertTrue(response.containsKey("nao:lastModified"));
		assertTrue(response.containsKey("nao:prefLabel"));
		assertTrue(response.containsKey("nfo:fileName"));
		assertTrue(response.containsKey("nfo:fileSize"));
		assertTrue(response.containsKey("nfo:fileLastModified"));
		
		assertEquals(file.toString(), response.get("@id"));
		assertEquals("file1.txt", response.get("nfo:fileName"));
		assertEquals("file1.txt", response.get("nao:prefLabel"));
		
		List<String> types = (List<String>) response.get("@type");
		assertTrue(types.contains("nfo:FileDataObject"));
		assertTrue(types.contains("nie:DataObject"));
	}

	@Test
	public void testGetResourceJSONLDNotShared() throws Exception {
		FileDataObject file = ObjectFactory.buildFileDataObject("file1.txt", pimoService.getUserUri());
		fileManager.add(file);
		// not calling 'share', when retrieving an error should be returned

		String encodedId = Base64encoding.encode(file.toString());
		Object json = controller.getDataboxJSONLD(SAID, encodedId);
		
		assertNotNull(json);
		assertTrue(json instanceof Map);
		
		Map response = (Map) json;
		assertTrue(response.containsKey("error"));
		assertTrue(response.get("error").toString().contains("Cannot retrieve resource"));
	}

	@Test
	public void testShareAndGetAllResourceJSONLD() throws Exception {
		FileDataObject fileA = ObjectFactory.buildFileDataObject("fileA.txt", pimoService.getUserUri());
		fileManager.add(fileA);
		sharingManager.shareFile(fileA.toString(), sender.toString(), new String[]{ recipient.toString() });
		FileDataObject fileB = ObjectFactory.buildFileDataObject("fileB.doc", pimoService.getUserUri());
		fileManager.add(fileB);
		sharingManager.shareFile(fileB.toString(), sender.toString(), new String[]{ recipient.toString() });
		
		Object json = controller.getAllResourceJSONLD(SAID);
		
		assertNotNull(json);
		assertTrue(json instanceof List);
		
		List<Map<String, Object>> response = (List<Map<String, Object>>) json;
		assertEquals(2, response.size());

		Map<String, Object> fileAJsonld = response.get(0);
		Map<String, Object> fileBJsonld = null;
		if (fileA.toString().equals(fileAJsonld.get("@id"))) {
			fileBJsonld = response.get(1);
		} else {
			fileAJsonld = response.get(1);
			fileBJsonld = response.get(0);
		}

		// verify fileA metadata is correct

		assertEquals(fileA.toString(), fileAJsonld.get("@id"));
		assertEquals("fileA.txt", fileAJsonld.get("nfo:fileName"));
		assertEquals("fileA.txt", fileAJsonld.get("nao:prefLabel"));
		
		List<String> types = (List<String>) fileAJsonld.get("@type");
		assertTrue(types.contains("nfo:FileDataObject"));
		assertTrue(types.contains("nie:DataObject"));
		
		// verify fileB metadata is correct

		assertEquals(fileB.toString(), fileBJsonld.get("@id"));
		assertEquals("fileB.doc", fileBJsonld.get("nfo:fileName"));
		assertEquals("fileB.doc", fileBJsonld.get("nao:prefLabel"));
		
		types = (List<String>) fileBJsonld.get("@type");
		assertTrue(types.contains("nfo:FileDataObject"));
		assertTrue(types.contains("nie:DataObject"));
	}

}