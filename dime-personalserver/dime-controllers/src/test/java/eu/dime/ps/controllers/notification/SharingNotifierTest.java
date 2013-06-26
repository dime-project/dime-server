package eu.dime.ps.controllers.notification;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.ps.controllers.SingleConnectionProviderMock;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.ObjectFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.dto.Type;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class SharingNotifierTest extends TestCase {

	private ModelFactory modelFactory = new ModelFactory();
	
	@Autowired
	private Connection connection;
	
	private SingleConnectionProviderMock connectionProvider = new SingleConnectionProviderMock();
	private NotifierManagerMock notifierManager = new NotifierManagerMock();
	
	private SharingNotifier notifier;
	private TripleStore tripleStore;
	private PimoService pimoService;

	private Account accountSender;
	private Person personR1;
	private Account accountR1;
	private PersonContact profileR1;
	private Person personR2;
	private Account accountR2;
	private PersonContact profileR2;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// disable warnings from RepositoryModel
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);		
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setUp() throws Exception {
		connectionProvider.setConnection(connection);
		notifierManager.external.clear();
		
		notifier = new SharingNotifier();
		notifier.setConnectionProvider(connectionProvider);
		notifier.setNotifierManager(notifierManager);

		tripleStore = connection.getTripleStore();
		pimoService = connection.getPimoService();
		
		// create some common test data for all tests
		accountSender = createAccount("me@di.me", pimoService.getUser());
		personR1 = createPerson("Manuela Cordin");
		accountR1 = createAccount("manuela@di.me", personR1);
		profileR1 = createPersonContact("Manuela Cordin", personR1, accountR1);
		personR2 = createPerson("Fabrizio Lepardi");
		accountR2 = createAccount("fabb@di.me", personR2);
		profileR2 = createPersonContact("Fabrizio Lepardi", personR2, accountR2);
	}
	
	@Test
	public void testShareWithAccount() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		Account accountSender = createAccount("me@di.me", pimoService.getUser());
		accessSpace.setSharedThrough(accountSender);
		Account accountR1B = createAccount("mancor@di.me", personR1);
		accessSpace.addIncludes(accountR1B);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		
		Set<URI> recipients = new HashSet<URI>();
		recipients.add(accountR1B.asURI());

		assertEquals(1, notifierManager.external.size());
		DimeExternalNotification notification = notifierManager.external.get(0);
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR1B.toString(), notification.getTarget());
		assertEquals(DimeExternalNotification.OP_SHARE, notification.getOperation());
		assertEquals(Type.get(livePost).toString(), notification.getItemType());
	}
	
	@Test
	public void testShareWithPerson() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(personR1);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		
		Set<URI> recipients = new HashSet<URI>();
		recipients.add(accountR1.asURI());

		assertEquals(1, notifierManager.external.size());
		DimeExternalNotification notification = notifierManager.external.get(0);
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR1.toString(), notification.getTarget());
		assertEquals(DimeExternalNotification.OP_SHARE, notification.getOperation());
		assertEquals(Type.get(livePost).toString(), notification.getItemType());
	}
	
	@Test
	public void testShareWithGroup() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		PersonGroup group = createPersonGroup("Friends", personR1, personR2);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(group);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		
		assertEquals(2, notifierManager.external.size());
	}

	@Test
	public void testShareWithGroupExcludeAccount() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		PersonGroup group = createPersonGroup("Friends", personR1, personR2);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(group);
		accessSpace.addExcludes(accountR2);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		
		assertEquals(1, notifierManager.external.size());

		DimeExternalNotification notification = notifierManager.external.get(0);
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR1.toString(), notification.getTarget());
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(DimeExternalNotification.OP_SHARE, notification.getOperation());
		assertEquals(Type.get(livePost).toString(), notification.getItemType());
	}
	
	@Test
	public void testShareWithGroupExcludePerson() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		PersonGroup group = createPersonGroup("Friends", personR1, personR2);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(group);
		accessSpace.addExcludes(personR1);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		
		assertEquals(1, notifierManager.external.size());

		DimeExternalNotification notification = notifierManager.external.get(0);
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR2.toString(), notification.getTarget());
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(DimeExternalNotification.OP_SHARE, notification.getOperation());
		assertEquals(Type.get(livePost).toString(), notification.getItemType());
	}
	
	@Test
	public void testShareWithGroupAndAddPerson() throws Exception {
		LivePost livePost = createLivePost("Hello sharing!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		// create group with only one person
		PersonGroup group = createPersonGroup("Friends", personR1);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(group);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		DimeExternalNotification notification = null;
		Event event = null;
		
		// verify one notification is created
		event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, preference);
		notifier.onReceive(event);
		assertEquals(1, notifierManager.external.size());
		
		// verify contents of the notification
		notification = notifierManager.external.get(0);
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(Type.LIVEPOST.toString(), notification.getItemType());
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR1.toString(), notification.getTarget());
		
		// when personR1 receives the notification, the item (livepost) is fetched and flagged
		// as shared with personR1
		tripleStore.addStatement(pimoService.getPimoUri(), livePost, NSO.sharedWith, personR1);
		
		// add a second person to the group
		group.addMember(personR2);
		tripleStore.addStatement(pimoService.getPimoUri(), group, PIMO.hasMember, personR2);

		// send the resource modified event and verify the notification is created
		notifierManager.external.clear();
		event = new Event(connection.getName(), Event.ACTION_RESOURCE_MODIFY, group);
		notifier.onReceive(event);
		assertEquals(1, notifierManager.external.size());

		// verify contents of the notification
		notification = notifierManager.external.get(0);
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(Type.LIVEPOST.toString(), notification.getItemType());
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR2.toString(), notification.getTarget());
	}
	
	@Test
	public void testShareAndUpdateLivePost() throws Exception {
		LivePost livePost = createLivePost("Text updated!", pimoService.getUser());
		
		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(pimoService.getUser());
		preference.setAppliesToResource(livePost);
		
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.setSharedThrough(accountSender);
		accessSpace.addIncludes(personR1);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());
		
		tripleStore.addAll(pimoService.getPimoUri(), preference.getModel().iterator());
		
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_MODIFY, livePost);
		notifier.onReceive(event);

		assertEquals(1, notifierManager.external.size());
		
		DimeExternalNotification notification = notifierManager.external.get(0);
		assertEquals(accountSender.toString(), notification.getSender());
		assertEquals(accountR1.toString(), notification.getTarget());
		assertEquals(livePost.toString(), notification.getItemID());
		assertEquals(DimeExternalNotification.OP_SHARE, notification.getOperation());
		assertEquals(Type.get(livePost).toString(), notification.getItemType());
	}

	private Person createPerson(String name) throws Exception {
		Person person = ObjectFactory.buildPerson(name);
		pimoService.create(person);
		return person;
	}
	
	private PersonContact createPersonContact(String name, Person person, Account dataSource) throws Exception {
		PersonName personName = ObjectFactory.buildPersonName(name);
		PersonContact profile = ObjectFactory.buildPersonContact(personName);
		profile.getModel().addStatement(profile, NIE.dataSource, dataSource);
		pimoService.create(profile);
		
		person.setGroundingOccurrence(profile);
		pimoService.update(person, true);
		
		return profile;
	}
	
	private PersonGroup createPersonGroup(String name, Person...members) throws Exception {
		PersonGroup group = ObjectFactory.buildPersonGroup(name, members);
		pimoService.create(group);
		return group;
	}
	
	private Account createAccount(String name, Person creator) throws Exception {
		Account account = ObjectFactory.buildAccount(name, DimeServiceAdapter.NAME, creator.asURI());
		pimoService.create(account);
		return account;
	}
	
	private LivePost createLivePost(String text, Person creator) throws Exception {
		LivePost livePost = ObjectFactory.buildLivePost(text, creator.asURI());
		pimoService.create(livePost);
		return livePost;
	}
	
}
