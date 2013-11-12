/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

package eu.dime.ps.controllers;

import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DDO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.controllers.infosphere.manager.DeviceManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dcon.Connectivity;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.semantic.model.ddo.LocalAreaNetwork;
import eu.dime.ps.semantic.model.ddo.WiFi;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dpo.Activity;
import eu.dime.ps.semantic.model.dpo.Place;
import eu.dime.ps.semantic.model.dpo.TimePeriod;
import eu.dime.ps.semantic.model.nco.EmailAddress;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * This creates and configures, by default, tenants for a pre-defined list of
 * personas, loading pre-defined data for each of them.
 * 
 * @author Ismael Rivera
 */
public class DefaultDataSetup implements BroadcastReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultDataSetup.class);

	public static final String DEFAULT_PUBLIC_PROFILE_CARD_NAME = "MyPublicCard";
	
	private static final String TEST_USER_1_ID = "c17d653e-43cb-45bf-a1c3-7f3304762ece@team.dime.wiwi.uni-siegen.de:8443";
	private static final String TEST_USER_2_ID = "5f88f366-c537-4ca4-9af1-56db84f3adb3@team.dime.wiwi.uni-siegen.de:8443";
	private static final String TEST_USER_3_ID = "fe068bf0-ed0e-4611-89da-3a4f40ace745@team.dime.wiwi.uni-siegen.de:8443";
	
	private static final Map<String, String[]> CONTACTS = new HashMap<String, String[]>();
	static {	  
	  CONTACTS.put("eea9c9ac-514b-4854-ad9e-1906cb8ccc33@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Christian Knecht", "christian.knecht@iao.fraunhofer.de", "Chris"});
	  CONTACTS.put("8d745f1e-4e7f-43c8-b85c-904514393779@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Cristina Fra", "cristina.fra@telecomitalia.it", "cristina"});
	  CONTACTS.put("12a18c1e-eaef-45a8-8ec0-a51f4f4c4b7f@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Fabian Hermann", "fabian.hermann@iao.fraunhofer.de", "FHermann"});
	  CONTACTS.put("078f9fda-7c0b-41d4-98a3-b5c0f7f5f8c2@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Ismael Rivera", "ismael.rivera@email.com", "ismriv"});
	  CONTACTS.put("f139d98d-1fb8-4b7b-8e1a-50d3e5050bb8@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Marc Planaguma", "marc.planaguma@email.com", "marc"});
	  CONTACTS.put("dad4c707-fd9e-4b8d-bf49-b8100a75fe84@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Marcel Heupel", "heupel@wiwi.uni-siegen.de", "mhpl"});
	  CONTACTS.put("c8029310-7089-40f2-a7ab-e113d705240c@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Massimo Valla", "massi.tilab@gmail.com", "massitilab"});
	  CONTACTS.put("3afbad0c-5e49-40f2-b2a4-587babd5dda3@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Rafael Gimenez", "rgimenez@bdigital.org", "rgimenez"});
	  CONTACTS.put("e73b3dce-9309-42c3-a1d6-e20617cd6a55@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Simon Scerri", "simon.scerri@deri.org", "irrecs"});	  
	  CONTACTS.put("6f243a9e-e214-435c-8fda-926bf853dcd9@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Simon Thiel", "simon.thiel@iao.fraunhofer.de", "Simon"});
	  CONTACTS.put("e3004fb5-8bb1-425f-b98e-59e22175e5de@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Sophie Wrobel", "sophie.wrobel@cas.de", "webmage"});
	  CONTACTS.put("d11a5986-c5ac-4959-98d8-5733bb22dcf8@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Richard Wacker", "richard.wacker@yellowmap.de", "riwa"});
	  CONTACTS.put("c42422fe-f9da-4290-97e6-19a120fabdc6@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Borja Gorriz", "bgorriz@bdigital.org", "bgorriz"});
	  CONTACTS.put("69f88843-ae7d-4eb9-9629-aecc23c41dc7@team.dime.wiwi.uni-siegen.de:8443", new String[] { "Javier Vendrell", "jvendrell@ametic.es", "jvendrell"});
	  CONTACTS.put("c41370d1-0aba-4566-8542-cd84516c7553@team.dime.wiwi.uni-siegen.de:8443", new String[] { "David Alonso", "dalonso@ametic.es", "dalonso"});
	  //new contacts - testusers
	  CONTACTS.put(TEST_USER_1_ID, new String[] { "Test User1", "test.user1@email.com", "testuser1"});
	  CONTACTS.put(TEST_USER_2_ID, new String[] { "Test User2", "test.user2@email.com", "testuser2"});
	  CONTACTS.put(TEST_USER_3_ID, new String[] { "Test User3", "test.user3@email.com", "testuser3"});
	}
	
	private static final URI FILE_FLYER = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI FILE_LOGO = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI FILE_WELCOME = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI FILE_PHOTO = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI DATABOX_DIME = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI DATABOX_FRIENDS = new URIImpl("urn:uuid:" + UUID.randomUUID());
	private static final URI LIVEPOST_WELCOME = new URIImpl("urn:uuid:" + UUID.randomUUID());
	
	private final ModelFactory modelFactory = new ModelFactory();

	private ConnectionProvider connectionProvider;
	private AccountManager accountManager;
	private PersonManager personManager;
	private ProfileManager profileManager;
	private PersonGroupManager personGroupManager;
	private ProfileCardManager profileCardManager;
	private DataboxManager databoxManager;
	private DeviceManager deviceManager;
	private SituationManager situationManager;
	private LivePostManager livePostManager;
	private FileManager fileManager;
	private UserManager userManager;

	@Autowired
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Autowired
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	@Autowired
	public void setProfileManager(ProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	@Autowired
	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	@Autowired
	public void setProfileCardManager(ProfileCardManager profileCardManager) {
		this.profileCardManager = profileCardManager;
	}

	@Autowired
	public void setDataboxManager(DataboxManager databoxManager) {
		this.databoxManager = databoxManager;
	}

	@Autowired
	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	@Autowired
	public void setSituationManager(SituationManager situationManager) {
		this.situationManager = situationManager;
	}

	@Autowired
	public void setLivePostManager(LivePostManager livePostManager) {
		this.livePostManager = livePostManager;
	}

	@Autowired
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	@Autowired
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public DefaultDataSetup() {
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	@Override
	public void onReceive(Event event) {
		
		// only interested in 'user registered' events
		if (!UserManager.ACTION_USER_REGISTERED.equals(event.getAction())) {
			return;
		}

		Long tenant = Long.parseLong(event.getTenant());
		TenantContextHolder.setTenant(tenant);
		logger.info("Creating default (dummy) data for new PIM/user [tenant = " + tenant + "]");
		
		PimoService pimoService;
		try {
			pimoService = connectionProvider.getConnection(Long.toString(TenantContextHolder.getTenant())).getPimoService();
		} catch (RepositoryException e) {
			logger.error("Can't find PIMO Service: default data won't be generated for " + event.getIdentifier(), e);
			return;
		}
		
		// fetch owner of the PIM
		Person me;
		try {
			me = personManager.getMe();
		} catch (InfosphereException e) {
			logger.error("Can't find PIM's owner: default data won't be generated for " + event.getIdentifier(), e);
			return;
		}
		
		// creating pre-defined devices (ddo:Laptop and ddo:Mobile)
		Device laptop = modelFactory.getDDOFactory().createDevice("http://www.semanticdesktop.org/ontologies/2011/10/05/ddo#Laptop");
		laptop.setPrefLabel("My laptop");
		laptop.setCreator(me);
		laptop.getModel().addStatement(me, DDO.owns, laptop);
		Device mobile = modelFactory.getDDOFactory().createDevice("http://www.semanticdesktop.org/ontologies/2011/10/05/ddo#Mobile");
		mobile.setPrefLabel("My mobile phone");
		mobile.setCreator(me);
		mobile.getModel().addStatement(me, DDO.owns, mobile);
		try {
			deviceManager.add(laptop);
			deviceManager.add(mobile);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined devices: " + e.getMessage(), e);
		}

		// creating pre-defined contacts & contacts of the di.me consortium
		Person testuser1 = null, testuser2 = null, testuser3 = null;
		List<Person> dimePeople = new ArrayList<Person>();
		List<Account> contactsAccounts = new ArrayList<Account>();
		for (String said : CONTACTS.keySet()) {
			
			// skip contacts which do not have a real said
			if (said == null || said.equals("") || said.equals("todo")) {
				continue;
			}
			
			String[] data = CONTACTS.get(said);
			logger.info("Adding '" + data[0] + "' as a contact [said=" + said + "]");
			
			PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
			profile.setPrefLabel(data[0]);
			
			PersonName name = modelFactory.getNCOFactory().createPersonName();
			name.setFullname(data[0]);
			name.setNickname(data[2]);
			profile.setPersonName(name);
			profile.getModel().addAll(name.getModel().iterator());
			
			EmailAddress email = modelFactory.getNCOFactory().createEmailAddress();
			email.setEmailAddress(data[1]);
			profile.setEmailAddress(email);
			profile.getModel().addAll(email.getModel().iterator());
			
			try {
				final String accountUri = userManager.add(said).getAccountUri();
				userManager.addProfile(new URIImpl(accountUri), profile);
				
				Account account = accountManager.get(accountUri);
				contactsAccounts.add(account);
				
				if (account.hasCreator()) {
					Person person = personManager.get(account.getCreator().toString());
					
					if (said.equals(TEST_USER_1_ID)) {
						person.setTrustLevel(0.5); 
						personManager.update(person);
						testuser1 = person;
					} else if (said.equals(TEST_USER_2_ID)) {
						person.setTrustLevel(0.0);
						personManager.update(person);
						testuser2 = person;
					} else if (said.equals(TEST_USER_3_ID)) {
						person.setTrustLevel(1.0);
						personManager.update(person);
						testuser3 = person;
					} else {
						// if not a test user, then it's a di.me contact
						dimePeople.add(person);
					}
				} else {
					logger.error("Contact account " + accountUri + " does not have a pimo:Person as creator.");
				}
			} catch (InfosphereException e) {
				logger.error("Contact '" + data[0] + "' couldn't be added as a contact: " + e.getMessage(), e);
			}
		}
		
		// creating pre-defined groups
		PersonGroup businessGroup = createPersonGroup("Business Contacts", me, testuser1, testuser2);
		PersonGroup colleaguesGroup = createPersonGroup("Colleagues", me, testuser1);
		PersonGroup familyGroup = createPersonGroup("Family", me);
		PersonGroup friendsGroup = createPersonGroup("Friends", me, testuser1, testuser3);
		PersonGroup acquaintancesGroup = createPersonGroup("Acquaintances", me);

		// creating pre-defined profile cards
		PrivacyPreference publicCard = null, anonymousCard = null, businessCard = null, privateCard = null;
		Account publicAccount = null;
		Resource[] attributes = null;
		try {
			publicCard = profileCardManager.getByLabel(DEFAULT_PUBLIC_PROFILE_CARD_NAME);
			AccessSpace accessSpace = publicCard.getAllAccessSpace().next();
			publicAccount = accessSpace.getSharedThrough();

			attributes = publicCard.getAllAppliesToResource_as().asArray();
			
			shareProfileCard(publicCard, businessGroup, colleaguesGroup, familyGroup, friendsGroup, acquaintancesGroup, testuser1);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating the pre-defined profile cards.", e);
		}
		
		PersonName anonymousName = modelFactory.getNCOFactory().createPersonName();
		anonymousName.setNickname("dimeUser");
		anonymousCard = createProfileCard("MyAnonymousCard");
		
		businessCard = createProfileCard("MyBusinessCard", attributes);
		shareProfileCard(businessCard, businessGroup, colleaguesGroup);
		
		privateCard = createProfileCard("MyPrivateCard", attributes);
		
		// set the public card as shared with all contacts' accounts
		try {
			for (Account account : contactsAccounts) {
				publicCard.addSharedWith(account);
			}
			profileCardManager.update(publicCard);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when flagging public card as shared with all contacts.", e);
		}

		// creating welcome livepost
		Account testuser3Account = null;
		if (testuser3 != null) {
			try {
				testuser3Account = accountManager.getAllByCreator(testuser3).iterator().next();
				
				String text = "Welcome to the di.me Test Trial 2013! Please try out the prototype and give us feedback! Visit the trial page: http://trial.dime-project.eu or our project page: http://www.dime-project.eu";
				createLivePost(LIVEPOST_WELCOME, text, testuser3, testuser3Account.asURI(), publicAccount.asURI());
			} catch (Exception e) {
				logger.error("An error ocurred when creating the pre-defined liveposts.", e);
			}
		}
		
		// creating pre-defined files
		FileDataObject flyer = createFile(FILE_FLYER, "digital.me_project_flyer.pdf", DefaultDataSetup.class.getClassLoader().getResourceAsStream("default/digital.me_project_flyer.pdf"), 0.5, me);
		FileDataObject logo = createFile(FILE_LOGO, "digital.me_logo.jpg", DefaultDataSetup.class.getClassLoader().getResourceAsStream("default/digital.me_logo.jpg"), 0, me);
		FileDataObject trial = createFile(FILE_WELCOME, "welcome_to_di.me_test.txt", DefaultDataSetup.class.getClassLoader().getResourceAsStream("default/welcome_to_di.me_test.txt"), 0, me);
		FileDataObject photo = createFile(FILE_PHOTO, "hiking.jpg", DefaultDataSetup.class.getClassLoader().getResourceAsStream("default/hiking.jpg"), 1, me);

		DataContainer databoxDime = createDatabox(DATABOX_DIME, "di.me info", flyer, logo, trial);
		DataContainer databoxFriends = createDatabox(DATABOX_FRIENDS, "FriendsBox", photo);
		shareDatabox(databoxDime, publicAccount, testuser1);


		URI userNamespace = pimoService.getUserNamespace();

		
		//creating pre-defined wireless connections - REMOVED BECAUSE THESE CANNOT BE GENERIC FOR ALL USERS
		//LocalAreaNetwork worklan = modelFactory.getDDOFactory().createLocalAreaNetwork();
		//worklan.setPrefLabel("lan.local");
		//WiFi homewifi = modelFactory.getDDOFactory().createWiFi();
		//homewifi.setPrefLabel("Martinez");
		//WiFi airportwifi = modelFactory.getDDOFactory().createWiFi();
		//airportwifi.setPrefLabel("Terminal2_Open");
		//WiFi conferencewifi = modelFactory.getDDOFactory().createWiFi();
		//conferencewifi.setPrefLabel("Guest007");
		
		// creating pre-defined situations
		try{
			// retrieve pre-defined places, time periods, activities
			Place workplace = pimoService.get(new URIImpl(userNamespace + "#Workplace"), Place.class);
			Place residence = pimoService.get(new URIImpl(userNamespace + "#Residence"), Place.class);
			Place airport = pimoService.get(new URIImpl(userNamespace + "#Airport"), Place.class);
			Place conventioncentre = pimoService.get(new URIImpl(userNamespace + "#ConventionCenter"), Place.class);
			Place restaurant = pimoService.get(new URIImpl(userNamespace + "#Restaurant"), Place.class);
			Place bar = pimoService.get(new URIImpl(userNamespace + "#Bar"), Place.class);
			Place club = pimoService.get(new URIImpl(userNamespace + "#Club"), Place.class);
			Place hotel = pimoService.get(new URIImpl(userNamespace + "#Hotel"), Place.class);
			Place station = pimoService.get(new URIImpl(userNamespace + "#Station"), Place.class);
			
			TimePeriod earlymorning = pimoService.get(new URIImpl(userNamespace + "#EarlyMorning"), TimePeriod.class);
			TimePeriod latemorning = pimoService.get(new URIImpl(userNamespace + "#LateMorning"), TimePeriod.class);
			TimePeriod earlyafternoon = pimoService.get(new URIImpl(userNamespace + "#EarlyAfternoon"), TimePeriod.class);
			TimePeriod lateafternoon = pimoService.get(new URIImpl(userNamespace + "#LateAfternoon"), TimePeriod.class);
			TimePeriod earlyevening = pimoService.get(new URIImpl(userNamespace + "#EarlyEvening"), TimePeriod.class);
			TimePeriod lateevening = pimoService.get(new URIImpl(userNamespace + "#LateEvening"), TimePeriod.class);
			TimePeriod earlynight = pimoService.get(new URIImpl(userNamespace + "#EarlyNight"), TimePeriod.class);
			TimePeriod latenight = pimoService.get(new URIImpl(userNamespace + "#LateNight"), TimePeriod.class);
			
			Activity working = pimoService.get(new URIImpl(userNamespace + "#Working"), Activity.class);
			Activity meeting = pimoService.get(new URIImpl(userNamespace + "#Meeting"), Activity.class);
			Activity performance = pimoService.get(new URIImpl(userNamespace + "#Performance"), Activity.class);
			Activity recreation = pimoService.get(new URIImpl(userNamespace + "#Recreation"), Activity.class);
			Activity eating = pimoService.get(new URIImpl(userNamespace + "#Eating"), Activity.class);
			Activity party = pimoService.get(new URIImpl(userNamespace + "#Party"), Activity.class);
			Activity driving = pimoService.get(new URIImpl(userNamespace + "#Driving"), Activity.class);
			Activity travelling = pimoService.get(new URIImpl(userNamespace + "#Travelling"), Activity.class);
	
			// create aspects
			SpaTem spatem = modelFactory.getDCONFactory().createSpaTem();
			State state = modelFactory.getDCONFactory().createState();
			
			//working@office
			//timeperiod = latemorning, earlyafternoon, lateafternoon, earlyevening
			latemorning.getModel().addStatement(latemorning, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlyafternoon.getModel().addStatement(earlyafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			lateafternoon.getModel().addStatement(lateafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			earlyevening.getModel().addStatement(earlyevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(latemorning);
			spatem.getModel().addModel(latemorning.getModel());
			spatem.addCurrentTime(earlyafternoon);
			spatem.getModel().addModel(earlyafternoon.getModel());
			spatem.addCurrentTime(lateafternoon);
			spatem.getModel().addModel(lateafternoon.getModel());
			spatem.addCurrentTime(earlyevening);
			spatem.getModel().addModel(earlyevening.getModel());
			//place = office
			workplace.getModel().addStatement(workplace, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			spatem.addCurrentPlace(workplace);
			spatem.getModel().addModel(workplace.getModel());

			//state = working
			working.getModel().addStatement(working, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(working);
			state.getModel().addModel(working.getModel());
			//CREATE SITUATION
			createSituation("Working@Office", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
			
			//working@home
			//timeperiod = latemorning, earlyafternoon, lateafternoon, earlyevening
			latemorning.getModel().addStatement(latemorning, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlyafternoon.getModel().addStatement(earlyafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			lateafternoon.getModel().addStatement(lateafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			earlyevening.getModel().addStatement(earlyevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(latemorning);
			spatem.getModel().addModel(latemorning.getModel());
			spatem.addCurrentTime(earlyafternoon);
			spatem.getModel().addModel(earlyafternoon.getModel());
			spatem.addCurrentTime(lateafternoon);
			spatem.getModel().addModel(lateafternoon.getModel());
			spatem.addCurrentTime(earlyevening);
			spatem.getModel().addModel(earlyevening.getModel());
			//place = residence
			residence.getModel().addStatement(residence, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			spatem.addCurrentPlace(residence);
			spatem.getModel().addModel(residence.getModel());
			//state = working
			working.getModel().addStatement(working, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(working);
			state.getModel().addModel(working.getModel());
			//CREATE SITUATION
			createSituation("Working@Home", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
			
			//@conference
			//timeperiod = latemorning, earlyafternoon, lateafternoon
			latemorning.getModel().addStatement(latemorning, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlyafternoon.getModel().addStatement(earlyafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			lateafternoon.getModel().addStatement(lateafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(latemorning);
			spatem.getModel().addModel(latemorning.getModel());
			spatem.addCurrentTime(earlyafternoon);
			spatem.getModel().addModel(earlyafternoon.getModel());
			spatem.addCurrentTime(lateafternoon);
			spatem.getModel().addModel(lateafternoon.getModel());
			//place = conference venue
			conventioncentre.getModel().addStatement(conventioncentre, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			spatem.addCurrentPlace(conventioncentre);
			spatem.getModel().addModel(conventioncentre.getModel());
			//state = working, meeting, performance
			working.getModel().addStatement(working, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(working);
			state.getModel().addModel(working.getModel());
			meeting.getModel().addStatement(meeting, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(meeting);
			state.getModel().addModel(meeting.getModel());
			performance.getModel().addStatement(performance, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(performance);
			state.getModel().addModel(performance.getModel());
			//CREATE SITUATION
			createSituation("@Conference", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
			
			//relaxing@home
			//timeperiod = lateevening, earlynight
			lateevening.getModel().addStatement(lateevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlynight.getModel().addStatement(earlynight, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(lateevening);
			spatem.getModel().addModel(lateevening.getModel());
			spatem.addCurrentTime(earlynight);
			spatem.getModel().addModel(earlynight.getModel());
			//place = residence
			residence.getModel().addStatement(residence, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			spatem.addCurrentPlace(residence);
			spatem.getModel().addModel(residence.getModel());
			//state = recreation
			recreation.getModel().addStatement(recreation, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(recreation);
			state.getModel().addModel(recreation.getModel());
			//CREATE SITUATION
			createSituation("Relaxing@Home", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
			
			//socialevent
			//timeperiod = lateevening, earlynight
			lateevening.getModel().addStatement(lateevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlynight.getModel().addStatement(earlynight, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(lateevening);
			spatem.getModel().addModel(lateevening.getModel());
			spatem.addCurrentTime(earlynight);
			spatem.getModel().addModel(earlynight.getModel());
			//place = residence, restaruant, bar, club, hotel
			residence.getModel().addStatement(residence, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			restaurant.getModel().addStatement(restaurant, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			bar.getModel().addStatement(bar, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			club.getModel().addStatement(club, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			hotel.getModel().addStatement(hotel, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentPlace(residence);
			spatem.getModel().addModel(residence.getModel());
			spatem.addCurrentPlace(restaurant);
			spatem.getModel().addModel(restaurant.getModel());
			spatem.addCurrentPlace(bar);
			spatem.getModel().addModel(bar.getModel());
			spatem.addCurrentPlace(club);
			spatem.getModel().addModel(club.getModel());
			spatem.addCurrentPlace(hotel);
			spatem.getModel().addModel(hotel.getModel());
			//state = recreation
			recreation.getModel().addStatement(recreation, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(recreation);
			state.getModel().addModel(recreation.getModel());
			state.addCurrentActivity(eating);
			state.getModel().addModel(eating.getModel());
			state.addCurrentActivity(party);
			state.getModel().addModel(party.getModel());
			//CREATE SITUATION
			createSituation("Social Event", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
			
			//travelling
			//timeperiod = earlymorning, latemorning, earlyafternoon, lateafternoon, earlyevening, lateevening, earlynight, latenight
			earlymorning.getModel().addStatement(earlymorning, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			latemorning.getModel().addStatement(latemorning, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlyafternoon.getModel().addStatement(earlyafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			lateafternoon.getModel().addStatement(lateafternoon, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			earlyevening.getModel().addStatement(earlyevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			lateevening.getModel().addStatement(lateevening, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float)); 
			earlynight.getModel().addStatement(earlynight, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			latenight.getModel().addStatement(latenight, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentTime(earlymorning);
			spatem.getModel().addModel(earlymorning.getModel());
			spatem.addCurrentTime(latemorning);
			spatem.getModel().addModel(latemorning.getModel());
			spatem.addCurrentTime(earlyafternoon);
			spatem.getModel().addModel(earlyafternoon.getModel());
			spatem.addCurrentTime(lateafternoon);
			spatem.getModel().addModel(lateafternoon.getModel());
			spatem.addCurrentTime(earlyevening);
			spatem.getModel().addModel(earlyevening.getModel());
			spatem.addCurrentTime(lateevening);
			spatem.getModel().addModel(lateevening.getModel());
			spatem.addCurrentTime(earlynight);
			spatem.getModel().addModel(earlynight.getModel());
			spatem.addCurrentTime(latenight);
			spatem.getModel().addModel(latenight.getModel());
			//place = airport, station
			airport.getModel().addStatement(airport, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			station.getModel().addStatement(station, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			spatem.addCurrentPlace(airport);
			spatem.getModel().addModel(airport.getModel());
			spatem.addCurrentPlace(station);
			spatem.getModel().addModel(station.getModel());
			//state = driving, travelling
			recreation.getModel().addStatement(recreation, DCON.weight, new DatatypeLiteralImpl("0.5", XSD._float));
			state.addCurrentActivity(travelling);
			state.getModel().addModel(travelling.getModel());
			state.addCurrentActivity(driving);
			state.getModel().addModel(driving.getModel());
			//CREATE SITUATION		
			createSituation("Travelling", me, spatem, state);
			//reset used aspects
			spatem.getModel().removeAll();
			state.getModel().removeAll();
				
		}
		catch(NotFoundException e){
			logger.error("Could not create situations: " + e.getMessage(), e);
		}
		
		// add all dime people in a group
		createPersonGroup("di.me Project", me, dimePeople.toArray(new Person[dimePeople.size()]));
		
		// set photo and group "friends" related (nao:isRelated) to one another
		photo.getModel().addStatement(photo, NAO.isRelated, friendsGroup);
		friendsGroup.getModel().addStatement(friendsGroup, NAO.isRelated, photo);
		try {
			personGroupManager.update(friendsGroup);
			fileManager.update(photo);
		} catch (InfosphereException e) {
			logger.error("Could not set nao:isRelatedTo between photo and group 'friends': " + e.getMessage(), e);
		}
		
		TenantContextHolder.clear();
	}
	
	private PersonGroup createPersonGroup(String label, Person creator, Person... members) {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setPrefLabel(label);
		for (Person member : members) {
			if (member != null) {
				group.getModel().addStatement(group, PIMO.hasMember, member);
			}
		}
		
		try {
			personGroupManager.add(group);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined situation '" + label + "'", e);
		}

		return group;
	}

	private LivePost createLivePost(URI uri, String text, Person creator, URI sharedBy, URI sharedWith) {
		if (creator == null || sharedBy == null || sharedWith == null) {
			return null;
		}
		
		LivePost livePost = modelFactory.getDLPOFactory().createLivePost(uri);
		livePost.setTextualContent(text);
		livePost.setCreator(creator);
		livePost.getModel().addStatement(livePost, NIE.dataSource, sharedBy);
		livePost.setSharedBy(sharedBy);
		livePost.setSharedWith(sharedWith);

		try {
			livePostManager.add(livePost);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined livepost '" + text + "'", e);
		}

		return livePost;
	}

	private Situation createSituation(String label, Person creator, SpaTem spatem, State state) {
		Situation situation = modelFactory.getDCONFactory().createSituation();
		
		situation.setPrefLabel(label);
		situation.setCreator(creator);
		situation.addContextAspect(spatem);
		situation.getModel().addModel(spatem.getModel());
		//		situation.addContextAspect(connectivity);
		//		situation.getModel().addModel(connectivity.getModel());
		situation.addContextAspect(state);
		situation.getModel().addModel(state.getModel());
		
		try {
			situationManager.add(situation);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined situation '" + label + "'", e);
		}

		return situation;
	}
	
	private PrivacyPreference createProfileCard(String label, Resource... attributes) {
		// create the di.me account
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setPrefLabel(label);
		
		// create the profile card for the di.me account
		PrivacyPreference profileCard = modelFactory.getPPOFactory().createPrivacyPreference();
		profileCard.setLabel(PrivacyPreferenceType.PROFILECARD.name());
		profileCard.setPrefLabel(label);
		
		// set di.me account as sharedThrough in the profile card's access space
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(account);
		profileCard.getModel().addAll(accessSpace.getModel().iterator());
		profileCard.setAccessSpace(accessSpace);
	
		// add profile attributes to profile card
		for (Resource attribute : attributes) {
			profileCard.addAppliesToResource(attribute);
		}
	
		try {
			accountManager.add(account);
			profileCardManager.add(profileCard);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined profile card '" + 
					label + "': "+ e.getMessage(), e);
		}
		
		return profileCard;
	}

	private DataContainer createDatabox(URI uri, String label, FileDataObject... files) {
		// create the databox
		DataContainer databox = modelFactory.getNFOFactory().createDataContainer(uri);
		databox.setPrefLabel(label);
		
		// add files to databox
		for (FileDataObject file : files) {
			databox.addPart(file);
		}
		
		try {
			databoxManager.add(databox);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined databox '" + 
					label + "': "+ e.getMessage(), e);
		}

		return databox;
	}
	
	private void shareProfileCard(PrivacyPreference profileCard, Resource... agents) {
		// find first access space
		AccessSpace accessSpace = profileCard.getAllAccessSpace().next();
		
		// include agents in access space
		for (Resource agent : agents) {
			if (agent != null) {
				accessSpace.getModel().addStatement(accessSpace, NSO.includes, agent);
			}
		}
		
		try {
			profileCardManager.update(profileCard);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when adding agents to pre-defined profile card '" +
					profileCard.getPrefLabel() + "': " + e.getMessage(), e);
		}
	}
	
	private void shareDatabox(DataContainer databox, Account sharedThrough, Resource... agents) {
		PrivacyPreference ppDatabox = (PrivacyPreference) databox.castTo(PrivacyPreference.class);
		
		// find access space sharedThrough given account
		AccessSpace accessSpace = null;
		ClosableIterator<AccessSpace> asIt = ppDatabox.getAllAccessSpace();
		while (asIt.hasNext()) {
			accessSpace = asIt.next();
			if (!sharedThrough.equals(accessSpace.getSharedThrough())) {
				accessSpace = null;
			}
		}
		asIt.close();
		
		// create a new access space if it didn't exist yet for given sharedThrough
		if (accessSpace == null) {
			accessSpace = modelFactory.getNSOFactory().createAccessSpace();
			accessSpace.setSharedThrough(sharedThrough);
			ppDatabox.addAccessSpace(accessSpace);
		}
		
		// include agents in access space
		for (Resource agent : agents) {
			if (agent != null) {
				accessSpace.getModel().addStatement(accessSpace, NSO.includes, agent);
			}
		}

		// add access space metadata to databox
		databox.getModel().addAll(accessSpace.getModel().iterator());

		try {
			databoxManager.update(databox);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when adding agents to pre-defined databox '" +
					databox.getPrefLabel() + "': " + e.getMessage(), e);
		}
	}

	private FileDataObject createFile(URI uri, String fileName, InputStream inputStream, double privacyLevel, Person creator) {
		FileDataObject file = modelFactory.getNFOFactory().createFileDataObject(uri);
		file.setFileName(fileName);
		file.setPrivacyLevel(privacyLevel);
		file.setCreator(creator);
		
		try {
			fileManager.add(file, inputStream);
		} catch (IOException e) {
			logger.error("An error ocurred when creating pre-defined file '" + fileName + "': " + e.getMessage(), e);
		} catch (InfosphereException e) {
			logger.error("An error ocurred when creating pre-defined file '" + fileName + "': " + e.getMessage(), e);
                } catch (Exception e) {
			logger.error("An error ocurred when creating pre-defined file '" + fileName + "': " + e.getMessage(), e);
		}
		
		return file;
	}

}
