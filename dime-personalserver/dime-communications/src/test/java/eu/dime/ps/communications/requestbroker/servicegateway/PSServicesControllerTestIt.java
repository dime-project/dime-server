package eu.dime.ps.communications.requestbroker.servicegateway;

import static org.mockito.Mockito.when;
import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.ExternalNotificationDTO;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.communications.requestbroker.controllers.servicegateway.PSServicesController;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableFileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableLivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;
import org.junit.Ignore;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config/serviceController-tests-context.xml")
public class PSServicesControllerTestIt extends Assert {

	private PSServicesController servicesController = new PSServicesController();

	protected ModelFactory modelFactory = new ModelFactory();

	private Tenant tenant;
	private LivePost livepost;
	private Account senderAccount;
	private Person senderPerson;
	private PersonContact senderProfile;
	private Account receiverAccount;

	// Test Ids
	private final static String SENDER = "456saidSender456";
	private final static String RECEIVER = "123saidReceiver123";
	private String SENDER_URI = "";
	private final static String RECEIVER_URI = "urn:uri:receiver";
	private final static String GUID ="dummyGUID";
	private final static String PASS ="dummyPASS";

	// Integration test over PSServicesController class injecting real dependencies

	@Autowired
	protected ResourceStore resourceStore;

	@Autowired
	protected Connection connection;

	@Autowired
	protected PimoService pimoService;

	@Autowired
	protected ConnectionProvider connectionProvider;

	@Mock //FIXME
	private ServiceGateway serviceGateway;

	@Mock //FIXME
	private TenantManager tenantManager;

	@Mock //FIXME
	private CredentialStore credentialStore;

	@Mock
	private DimeServiceAdapter dimeServiceAdapter;	

	@Autowired
	private ShareableDataboxManager shareableDataboxManager;

	@Autowired
	private ShareableFileManager shareableFileManager;

	@Autowired
	private ShareableLivePostManager shareableLivePostManager;

	@Autowired
	private ShareableProfileManager shareableProfileManager;

	@Autowired
	private AccountManager accountManager;

	@Mock //FIXME
	private UserManager userManager;

	@Before
	public void init() throws Exception {

		// All dependencies Mocked
		MockitoAnnotations.initMocks(this);

		// set up tenant data in the thread local holders
		TenantContextHolder.setTenant(Long.parseLong(connection.getName()));

		//FIXME mocking tenant manager
		tenant = EntityFactory.getInstance().buildTenant();
		tenant.setId(Long.parseLong(connection.getName()));
		when(tenantManager.getByAccountName(RECEIVER)).thenReturn(tenant);

		//set up sender person, account and profile		
		senderPerson = modelFactory.getPIMOFactory().createPerson();
		senderAccount = modelFactory.getDAOFactory().createAccount();
		senderAccount.setPrefLabel("anna's account");
		senderAccount.setCreator(senderPerson);
		senderProfile = modelFactory.getNCOFactory().createPersonContact();
		senderPerson.setGroundingOccurrence(senderProfile);
		senderProfile.getModel().addStatement(senderProfile, NIE.dataSource, senderAccount.asResource());
		resourceStore.createOrUpdate(pimoService.getPimoUri(), senderAccount);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), senderProfile);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), senderPerson);

		//set up receiver account		
		receiverAccount = modelFactory.getDAOFactory().createAccount(RECEIVER_URI);
		receiverAccount.setPrefLabel("receivers's account");
		resourceStore.createOrUpdate(pimoService.getPimoUri(), receiverAccount);

		//add livepost to sender account
		livepost =  modelFactory.getDLPOFactory().createLivePost();	
		livepost.addPrefLabel("livepost Name");
		livepost.addTextualContent("test livepost");
		livepost.setCreated(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		livepost.setLastModified(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));
		livepost.setTextualContent("test livepost text");
		livepost.setTimestamp(new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime));

		SENDER_URI= senderAccount.asURI().toString();		


		// mocking connection provider
		when(connectionProvider.getConnection(connection.getName())).thenReturn(connection);	

		//FIXME mocking credential store
		when(credentialStore.getUriForName(RECEIVER)).thenReturn(RECEIVER_URI);	
		when(credentialStore.getUriForAccountName(RECEIVER, SENDER)).thenReturn(SENDER_URI);
		when(credentialStore.getPassword(RECEIVER_URI, SENDER_URI)).thenReturn(PASS);

		//mocking DimeServiceAdapter
		Vector<LivePost> liveposts = new Vector<LivePost>();						
		liveposts.add(livepost);		

		when(dimeServiceAdapter.get(Mockito.anyString(),Mockito.anyString(),Mockito.anyString(), Mockito.any(Class.class))).thenReturn(liveposts);

		//FIXME mocking servicegateway
		when(serviceGateway.getDimeServiceAdapter(SENDER)).thenReturn(dimeServiceAdapter);

		//config serviceController		
		servicesController.setCredentialStore(credentialStore);
		servicesController.setServiceGateway(serviceGateway);		
		servicesController.setShareableProfileManager(shareableProfileManager);
		servicesController.setShareableDataboxManager(shareableDataboxManager);
		servicesController.setShareableFileManager(shareableFileManager);
		servicesController.setShareableLivePostManager(shareableLivePostManager);
		servicesController.setTenantManager(tenantManager);
		servicesController.setUserManager(userManager);
		servicesController.setAccountManager(accountManager);


	}

	@Test
	public void testSetNotification() throws UnsupportedEncodingException, InfosphereException, RepositoryStorageException, ClassCastException, NotFoundException {

		Request <ExternalNotificationDTO> NotificationRequest = buildMockNotification();
		Response response = servicesController.setNotification(NotificationRequest, RECEIVER);

		//verify response is 200 OK
		assertEquals("OK", response.getMessage().getMeta().getStatus());

		// verify livepost is stored correctly
		TripleStore tripleStore = pimoService.getTripleStore();			
		assertTrue(tripleStore.containsStatements(Variable.ANY, livepost, NAO.creator, senderPerson));
		assertTrue(tripleStore.containsStatements(Variable.ANY, livepost, NSO.sharedBy, senderAccount));
		assertTrue(tripleStore.containsStatements(Variable.ANY, livepost, NSO.sharedWith, receiverAccount));
		assertTrue(tripleStore.containsStatements(Variable.ANY, livepost, NAO.created, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
		assertTrue(tripleStore.containsStatements(Variable.ANY, livepost, NAO.lastModified, new DatatypeLiteralImpl("1970-01-16T11:53:44.999Z", XSD._dateTime)));
	
	}

	private Request <ExternalNotificationDTO> buildMockNotification(){	
		Request <ExternalNotificationDTO> mockRequest = new Request<ExternalNotificationDTO>();
		ExternalNotificationDTO jsonNotification = new ExternalNotificationDTO();
		Message<ExternalNotificationDTO> message = new Message<ExternalNotificationDTO>();
		Data<ExternalNotificationDTO> data  = new Data<ExternalNotificationDTO>();

		jsonNotification.setGuid(GUID);
		jsonNotification.setName("");
		jsonNotification.setOperation(ExternalNotificationDTO.OPERATION_SHARE);
		jsonNotification.setSaidReciever(RECEIVER);
		jsonNotification.setSaidSender(SENDER);
		jsonNotification.setType(DimeInternalNotification.ITEM_TYPE_LIVEPOST);
		jsonNotification.setDate(new Long(1));

		Entry element = new Entry();
		element.setGuid(GUID);
		element.setType(DimeInternalNotification.ITEM_TYPE_LIVEPOST);
		element.setName("");

		jsonNotification.setElement(element);
		data.addEntry(jsonNotification);
		message.setData(data);
		mockRequest.setMessage(message);
		return mockRequest;
	}


}
