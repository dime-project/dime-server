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

package eu.dime.ps.communications.requestbroker.servicegateway;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scribe.model.Token;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.ExternalNotificationDTO;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.communications.requestbroker.controllers.servicegateway.PSServicesController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableFileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableLivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PSServicesController.class, DimeServiceAdapter.class})
public class PSServicesControllerTest {

	private PSServicesController servicesController = new PSServicesController();
	private ModelFactory modelFactory = new ModelFactory();
	
	private static Tenant tenant;	
	private LivePost livepost; 
	
	// Test Ids
	private final static String SENDER = "456saidSender456";
	private final static String RECEIVER = "123saidReceiver123";
	private final static String SENDER_URI = "urn:uri:sender";
	private final static String SENDER_URI_ON_DEMAND = "urn:uri:sender:ondemand";
	private final static String RECEIVER_URI = "urn:uri:receiver";
	private final static String GUID ="dummyGUID";
	private final static String PASSWORD = "pass";
	
	// Unit test over PSServicesController class MOCKING all the dependencies:
	@Mock private ServiceGateway serviceGateway;	
	@Mock private TenantManager tenantManager;
	@Mock private AccountManager accountManager;
	@Mock private CredentialStore credentialStore;
	@Mock private ShareableDataboxManager shareableDataboxManager;
	@Mock private ShareableFileManager shareableFileManager;
	@Mock private ShareableLivePostManager shareableLivePostManager;
	@Mock private ShareableProfileManager shareableProfileManager;
	@Mock private UserManager userManager;
	
	@Test
	public void testSetNotificationExistProfile() throws Exception {
		Request <ExternalNotificationDTO> mockRequest = buildMockNotification();
		this.buildMockResponsesExistingProfile();
		
		PSServicesController servicesControllerSpied = PowerMockito.spy(servicesController);
		
		Response response = servicesControllerSpied.setNotification(mockRequest, RECEIVER);
		
		assertNotNull(response);	
		
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("obtainSharedObjecte", Mockito.anyObject());
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("getAndSaveSharedObject", Mockito.anyObject(), Mockito.anyString(), Mockito.anyString());
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("requestCredentialsAndProfile", SENDER, RECEIVER, RECEIVER_URI);
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("requestProfile", Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}
	
	@Test
	public void testSetNotificationNewProfile() throws Exception {
		Request <ExternalNotificationDTO> mockRequest = buildMockNotification();
		this.buildMockResponsesNewProfile();
		
		PSServicesController servicesControllerSpied = PowerMockito.spy(servicesController);
		
		Response response = servicesControllerSpied.setNotification(mockRequest, RECEIVER);
		
		assertNotNull(response);	
		
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("obtainSharedObjecte", Mockito.anyObject());
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("getAndSaveSharedObject", Mockito.anyObject(), Mockito.anyString(), Mockito.anyString());
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("requestCredentialsAndProfile", SENDER, RECEIVER, RECEIVER_URI);
		PowerMockito.verifyPrivate(servicesControllerSpied).invoke("requestProfile", Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
	}

	/* Mock dependencies */
	@Before
	public void init() throws Exception {
		
		// All dependencies Mocked
		MockitoAnnotations.initMocks(this);
		
		servicesController.setServiceGateway(serviceGateway);		
		servicesController.setTenantManager(tenantManager);
		servicesController.setAccountManager(accountManager);
		servicesController.setCredentialStore(credentialStore);
		servicesController.setShareableDataboxManager(shareableDataboxManager);
		servicesController.setShareableFileManager(shareableFileManager);
		servicesController.setShareableLivePostManager(shareableLivePostManager);
		servicesController.setShareableProfileManager(shareableProfileManager);
		servicesController.setUserManager(userManager);
	}
	
	private void buildMockResponsesExistingProfile() throws Exception{
		
		// Loading responses for mocks
		
		// Tenant
		setupTenant();
		when(tenantManager.getByAccountName(RECEIVER)).thenReturn(tenant);
		
		// CredentialStore
		when(credentialStore.getUriForName(RECEIVER)).thenReturn(RECEIVER_URI);
		when(credentialStore.getUriForAccountName(RECEIVER, SENDER)).thenReturn(SENDER_URI);
		when(credentialStore.getPassword(RECEIVER, SENDER, tenant)).thenReturn(PASSWORD);
		
		// Mocking: DimeServiceAdapter
		Token token = new Token("token", "secret");	
		DimeServiceAdapter mockDimeServiceAdapter = mock(DimeServiceAdapter.class);
		when(mockDimeServiceAdapter.getUserToken(RECEIVER)).thenReturn(token);
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		when(mockDimeServiceAdapter.getProfile(Mockito.anyString(), Mockito.eq(token))).thenReturn(profile);
	
		//PowerMockito.whenNew(DimeServiceAdapter.class).withAnyArguments().thenReturn(mockDimeServiceAdapter);
		when(serviceGateway.getDimeServiceAdapter(SENDER)).thenReturn(mockDimeServiceAdapter);

		User user = new User();
		Account account = modelFactory.getDAOFactory().createAccount(SENDER_URI_ON_DEMAND);
		String name = "dummy";
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setPrefLabel(name+"@"+DimeServiceAdapter.NAME);
		
		user.setAccountUri(SENDER_URI_ON_DEMAND);
		when(userManager.add(Mockito.eq(SENDER), Mockito.any(URI.class))).thenReturn(user);
		when(userManager.addProfile(Mockito.eq(new URIImpl(SENDER_URI_ON_DEMAND)), Mockito.eq(profile), tenant)).thenReturn(account);

		//ServiceGateway			
		Vector<LivePost> liveposts = new Vector<LivePost>();
		livepost =  modelFactory.getDLPOFactory().createLivePost();	
		liveposts.add(livepost);
		when(mockDimeServiceAdapter.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Class.class), tenant)).thenReturn(liveposts);
		when(serviceGateway.getDimeServiceAdapter(RECEIVER_URI)).thenReturn(mockDimeServiceAdapter); 
		
	}
	
	private void buildMockResponsesNewProfile() throws Exception{
		
		// Loading responses for mocks
		
		// Tenant
		setupTenant();
		when(tenantManager.getByAccountName(RECEIVER)).thenReturn(tenant);
		
		// CredentialStore
		when(credentialStore.getUriForName(RECEIVER)).thenReturn(RECEIVER_URI);
		when(credentialStore.getUriForAccountName(RECEIVER, SENDER)).thenReturn(null);
		when(credentialStore.getPassword(RECEIVER, SENDER, tenant)).thenReturn(null);
		
		// Mocking: DimeServiceAdapter
		Token token = new Token("token", "secret");	
		DimeServiceAdapter mockDimeServiceAdapter = mock(DimeServiceAdapter.class);
		when(mockDimeServiceAdapter.getUserToken(RECEIVER)).thenReturn(token);
		PersonContact profile = modelFactory.getNCOFactory().createPersonContact();
		when(mockDimeServiceAdapter.getProfile(Mockito.anyString(), Mockito.eq(token))).thenReturn(profile);
	
		//PowerMockito.whenNew(DimeServiceAdapter.class).withAnyArguments().thenReturn(mockDimeServiceAdapter);
		when(serviceGateway.getDimeServiceAdapter(SENDER)).thenReturn(mockDimeServiceAdapter);

		User user = new User();
		Account account = modelFactory.getDAOFactory().createAccount(SENDER_URI_ON_DEMAND);
		String name = "dummy";
		account.setAccountType(DimeServiceAdapter.NAME);
		account.setPrefLabel(name+"@"+DimeServiceAdapter.NAME);
		
		user.setAccountUri(SENDER_URI_ON_DEMAND);
		when(userManager.add(Mockito.eq(SENDER), Mockito.any(URI.class))).thenReturn(user);
		when(userManager.addProfile(Mockito.eq(new URIImpl(SENDER_URI_ON_DEMAND)), Mockito.eq(profile), tenant)).thenReturn(account);

		//ServiceGateway			
		Vector<LivePost> liveposts = new Vector<LivePost>();
		livepost =  modelFactory.getDLPOFactory().createLivePost();	
		liveposts.add(livepost);
		when(mockDimeServiceAdapter.get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any(Class.class), tenant)).thenReturn(liveposts);
		when(serviceGateway.getDimeServiceAdapter(RECEIVER_URI)).thenReturn(mockDimeServiceAdapter); 
		
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
	
	private void setupTenant(){
		tenant = new Tenant();
		tenant.setId(new Long(1));
		tenant.setName("juan");
	}
}
