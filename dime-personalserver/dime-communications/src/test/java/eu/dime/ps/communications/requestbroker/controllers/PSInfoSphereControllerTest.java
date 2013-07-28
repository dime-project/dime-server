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

package eu.dime.ps.communications.requestbroker.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyListOf;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Assert;
import org.mockito.Mockito;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.vocabulary.NIE;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.SAccount;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.commons.notifications.user.UserNotificationEntry;
import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.DataboxManager;
import eu.dime.ps.controllers.infosphere.manager.EventManager;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.LivePostManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileAttributeManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.dto.Resource;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.SocialEvent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.storage.entities.Tenant;


/**
 * Tests {@link PSInfoSphereController}.
 */

public class PSInfoSphereControllerTest extends Assert {

	protected ModelFactory modelFactory = new ModelFactory();

	protected static Tenant tenant;


	private static final URI[] payload = new URI[] { NAO.prefSymbol,
		NAO.privacyLevel, DLPO.timestamp, NIE.mimeType, NAO.created,
		NAO.lastModified, NAO.privacyLevel, NAO.prefLabel, NSO.sharedBy,
		NSO.sharedWith, DLPO.textualContent };


	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
		org.apache.log4j.Logger.getLogger("org.semanticdesktop.aperture").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.semanticdesktop.aperture").setLevel(java.util.logging.Level.OFF);

		tenant = new Tenant();
		tenant.setName("juan");
		tenant.setId(1l);

	}

	protected Request<Resource> buildRequest(org.ontoware.rdfreactor.schema.rdfs.Resource value){

		Request<Resource> request = new Request<Resource>();
		Message<Resource> message = new Message<Resource>();
		Data<Resource> data = new Data<Resource>();
		Resource resource =   new Resource(value,new URIImpl("urn:juan"));			
		data.getEntries().add(resource);		  
		message.setData(data );
		request.setMessage(message);

		return request;	  

	}	

	protected Request<SAccount> buildSARequest(Account account){

		Request<SAccount> request = new Request<SAccount>();
		Message<SAccount> message = new Message<SAccount>();
		Data<SAccount> data = new Data<SAccount>();
		SAccount myAccount = new SAccount();
                myAccount.setGuid(account.asURI().toString());
		myAccount.setServiceadapterguid("di.me");
		data.getEntries().add(myAccount);
		message.setData(data);
		request.setMessage(message);

		return request;	  

	}		


	protected PersonManager buildPersonManager() {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");
		Collection<Person> persons = new ArrayList<Person>();
		persons.add(juan);
		PersonManager mockedManager = mock(PersonManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll()).thenReturn(persons);
			when(mockedManager.get(anyString())).thenReturn(juan);
		} catch (InfosphereException e) {				
			e.printStackTrace();
		}
		return mockedManager;
	}



	protected PersonGroupManager buildGroupManager() {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setPrefLabel("group1");
		Collection<PersonGroup> groups = new ArrayList<PersonGroup>();
		groups.add(group);
		PersonGroupManager mockedManager = mock(PersonGroupManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll()).thenReturn(groups);
			when(mockedManager.get("group1")).thenReturn(group);
			when(mockedManager.get(anyString())).thenReturn(group);  
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		return mockedManager;			

	}

	protected AccountManager buildAccountManager() {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");		  
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel("juan");
		Collection<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		AccountManager mockedManager = mock(AccountManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAllByCreator(juan)).thenReturn(accounts);
			when(mockedManager.get("juan")).thenReturn(account);					

		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mockedManager;			

	}

	protected DataboxManager buildDataboxManager() {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");		  
		DataContainer databox = modelFactory.getNFOFactory().createDataContainer();
		databox.setPrefLabel("juan");
		Collection<DataContainer> databoxes = new ArrayList<DataContainer>();
		databoxes.add(databox);
		DataboxManager mockedManager = mock(DataboxManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAllByCreator(juan.asURI())).thenReturn(databoxes);
			when(mockedManager.get(anyString())).thenReturn(databox);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mockedManager;			

	}

	protected EventManager buildEventManager() {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");		  
		SocialEvent databox = modelFactory.getPIMOFactory().createSocialEvent();
		databox.setPrefLabel("juan");
		Collection<SocialEvent> databoxes = new ArrayList<SocialEvent>();
		databoxes.add(databox);
		EventManager mockedManager = mock(EventManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll()).thenReturn(databoxes);
			when(mockedManager.get("juan")).thenReturn(databox);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mockedManager;			

	}

	protected LivePostManager buildLivepostManager(URI[] payload) {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);
		LivePost livepost = modelFactory.getDLPOFactory().createLivePost();
		livepost.setPrefLabel("Juan");
		Collection<LivePost> liveposts = new ArrayList<LivePost>();
		liveposts.add(livepost);
		Status status = modelFactory.getDLPOFactory().createStatus();
		status.setPrefLabel("Juan");
		Collection<Status> statuses = new ArrayList<Status>();
		statuses.add(status);
		LivePostManager mockedManager = mock(LivePostManager.class);
		//Ç
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAllByType(Status.class,properties)).thenReturn(statuses);				
			when(mockedManager.getAllByCreator(juan.asURI().toString(),properties)).thenReturn(liveposts);
			when(mockedManager.get("juan",properties)).thenReturn(livepost);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mockedManager;			

	}

	protected NotifierManager buildNotifierManager() {		 
		UserNotificationEntry unEntry = new UserNotificationEntry();
		UserNotification notification = new UserNotification(1l, unEntry);
		notification.setId("1");
		
		List<DimeInternalNotification> notifications = new ArrayList<DimeInternalNotification>();
		notifications.add(notification);
		
		NotifierManager mockedManager = mock(NotifierManager.class);
		when(mockedManager.getAllNotifications(tenant, null, null)).thenReturn(notifications);
		when(mockedManager.getNotificationById(1l)).thenReturn(notification);
		when(mockedManager.getNotificationsByDate(new Date(1l),new Date(2l))).thenReturn(notifications);
		when(mockedManager.getAllMyUserUnReadedNotifications(tenant, 0, 1000)).thenReturn(notifications);
		when(mockedManager.getAllMyUserNotifications((Tenant)Mockito.anyObject(), (Integer)Mockito.anyObject(), (Integer)Mockito.anyObject())).thenReturn(notifications);
		
		return mockedManager;			

	}

	protected TenantManager buildTenantManager(){

		TenantManager mockedManager = mock(TenantManager.class);
		when(mockedManager.getByAccountName("juan")).thenReturn(tenant);

		return mockedManager;
	}

	protected ProfileManager buildProfileManager(Person person) { 
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.setPrefLabel("Juan");
		Collection<PersonContact> profiles = 	new ArrayList<PersonContact>();
		profiles.add(profile);
		ProfileManager mockedManager = mock(ProfileManager.class);
		try {
			when(mockedManager.getMe()).thenReturn(person);
			when(mockedManager.getAll()).thenReturn(profiles);
			when(mockedManager.getAllByPerson(person)).thenReturn(profiles);
			when(mockedManager.get("juan")).thenReturn(profile);
		} catch (InfosphereException e) {				
			e.printStackTrace();
		}


		return mockedManager;			

	}

	protected ProfileCardManager buildProfileCardManager(){
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		profile.setPrefLabel("Juan");
		ProfileCardManager mockedManager = mock(ProfileCardManager.class);
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll()).thenReturn(new ArrayList<PrivacyPreference>());

		} catch (InfosphereException e) {				
			e.printStackTrace();
		}
		return mockedManager;  
	}

	protected ProfileAttributeManager buildProfileAttributeManager(String profileId) {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");		  
		PersonName juanAttribute = modelFactory.getNCOFactory().createPersonName();		  
		juanAttribute.setPrefLabel("Juan");
		juanAttribute.setNameAdditional("Juan");
		Collection<org.ontoware.rdfreactor.schema.rdfs.Resource> attributes = new ArrayList<org.ontoware.rdfreactor.schema.rdfs.Resource>();
		attributes.add(juanAttribute);
		ProfileAttributeManager mockedManager = mock(ProfileAttributeManager.class);
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll()).thenReturn(attributes);
			when(mockedManager.getAllByContainer(profileId)).thenReturn(attributes);
			when(mockedManager.get("juan")).thenReturn(juanAttribute);
		} catch (InfosphereException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return mockedManager;			

	}

	protected FileManager buildResourceManager(URI[] payload) {
		Person juan = modelFactory.getPIMOFactory().createPerson();
		juan.setPrefLabel("Juan");		  
		FileDataObject fileDataObject= modelFactory.getNFOFactory().createFileDataObject();
		fileDataObject.setPrefLabel("juan");
		List<URI> properties = new ArrayList<URI>();
		properties = Arrays.asList(payload);
		Collection<FileDataObject> fileDataObjects = new ArrayList<FileDataObject>();
		fileDataObjects.add(fileDataObject);
		FileManager mockedManager = mock(FileManager.class);
		//
		try {
			when(mockedManager.getMe()).thenReturn(juan);
			when(mockedManager.getAll(anyListOf(URI.class))).thenReturn(fileDataObjects);
			when(mockedManager.get("juan",properties)).thenReturn(fileDataObject);
		} catch (InfosphereException e) {			
			e.printStackTrace();
		}
		return mockedManager;			

	}


	protected SharingManager buildSharingManager(LivePost livePost,Person creator) throws Exception{

		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.LIVEPOST.name());
		preference.setCreator(creator);
		preference.setAppliesToResource(livePost);

		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		Account accountSender = buildAccount("me@di.me",creator);
		accessSpace.setSharedThrough(accountSender);
		Person personR1 = buildPerson("Manuela Cordin");		
		Account accountR1B = buildAccount("mancor@di.me", personR1);
		accessSpace.addIncludes(accountR1B);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());

		SharingManager mockedManager = mock(SharingManager.class);

		when(mockedManager.findPrivacyPreference(livePost.asURI().toString(),PrivacyPreferenceType.LIVEPOST)).thenReturn(preference);

		return mockedManager;	
	}

	protected SharingManager buildResourceSharingManager(FileDataObject file,Person creator) throws Exception{

		PrivacyPreference preference = modelFactory.getPPOFactory().createPrivacyPreference();
		preference.setLabel(PrivacyPreferenceType.FILE.name());
		preference.setCreator(creator);
		preference.setAppliesToResource(file);

		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		Account accountSender = buildAccount("me@di.me",creator);
		accessSpace.setSharedThrough(accountSender);
		Person personR1 = buildPerson("Manuela Cordin");		
		Account accountR1B = buildAccount("mancor@di.me", personR1);
		accessSpace.addIncludes(accountR1B);
		preference.addAccessSpace(accessSpace);
		preference.getModel().addAll(accessSpace.getModel().iterator());

		SharingManager mockedManager = mock(SharingManager.class);

		when(mockedManager.findPrivacyPreference(file.asURI().toString(),PrivacyPreferenceType.FILE)).thenReturn(preference);

		return mockedManager;



	}

	protected ServiceGateway buildServiceGateway() throws ServiceNotAvailableException, ServiceAdapterNotSupportedException{
		ServiceGateway mockedServiceGateway = mock(ServiceGateway.class);
		DimeServiceAdapter sa = mock(DimeServiceAdapter.class);
		when(sa.getAdapterName()).thenReturn("di.me");
		ServiceMetadata serviceMeta = new ServiceMetadata("guid", "dimeAdapter", "description", "authURL", "status", null, "settings");		
		when(mockedServiceGateway.getServiceAdapter(anyString())).thenReturn(sa);
		when(mockedServiceGateway.getServiceMetadata(anyString(),anyString())).thenReturn(serviceMeta);

		return mockedServiceGateway;		

	}


	private Account buildAccount(String name, Person creator) throws Exception {
		Account account = modelFactory.getDAOFactory().createAccount();
		account.setPrefLabel(name);
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setCreator(creator);		
		return account;
	}

	private Person buildPerson(String name) throws Exception {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel(name);		
		return person;
	}	


}
