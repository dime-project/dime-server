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

package eu.dime.ps.communications.requestbroker.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.UserNotificationDTO;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.user.UNAdhocGroupRecommendation;
import eu.dime.commons.notifications.user.UNMergeRecommendation;
import eu.dime.commons.notifications.user.UNMessage;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UNSituationRecommendation;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.commons.notifications.user.UserNotificationEntry;
import eu.dime.ps.communications.requestbroker.controllers.notifications.PSUserNotificationsController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.NotifyHistory;
import eu.dime.ps.storage.entities.Notification;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config/notificationControllers-tests-context.xml")
@TransactionConfiguration(defaultRollback = true)
public class PSUserNotificationControllerTestIt {

	private PSUserNotificationsController controller = new PSUserNotificationsController();

	private static final String said = "juantestnotifier-" + UUID.randomUUID().toString();
	private static final Long id = 1234567812345678l;
	
	@Autowired NotifierManager notifierManager;
	@Autowired NotifyHistory notifyHistory;
	@Autowired EntityFactory entityFactory;
	
	private Tenant tenant;
	
	public PSUserNotificationControllerTestIt() {

	}
	
	@Before
	@Transactional
	public void before(){
		controller.setNotifierManager(notifierManager);
		
		tenant = entityFactory.buildTenant();
		tenant.setName(said);
		tenant.persist();
		tenant.flush();
		
		TenantManager mockedManager = mock(TenantManager.class);
		when(mockedManager.getByAccountName(said)).thenReturn(tenant);
		controller.setTenantManager(mockedManager);
		
		// adding notiofications
		List<UserNotificationEntry> uns = new Vector<UserNotificationEntry>();
		
		UNRefToItem refToItem = new UNRefToItem();
		refToItem.setGuid(UUID.randomUUID().toString());
		refToItem.setOperation(UNRefToItem.OPERATION_SHARED);
		refToItem.setType(UNRefToItem.TYPE_DATABOX);
		refToItem.setUserID(UUID.randomUUID().toString());
		
		uns.add(refToItem);
		
		UNRefToItem refToItem2 = new UNRefToItem();
		refToItem2.setGuid(UUID.randomUUID().toString());
		refToItem2.setOperation(UNRefToItem.OPERATION_UNSHARED);
		refToItem2.setType(UNRefToItem.TYPE_DATABOX);
		refToItem2.setUserID(UUID.randomUUID().toString());
		
		uns.add(refToItem2);
		
		UNAdhocGroupRecommendation adhocGroupRecommendation= new UNAdhocGroupRecommendation();
		adhocGroupRecommendation.setGuid(UUID.randomUUID().toString());
		adhocGroupRecommendation.setName("name");
		adhocGroupRecommendation.setNao_creator("nao_creator");
		
		uns.add(adhocGroupRecommendation);
		
		UNMessage message = new UNMessage();
		message.setMessage("message");
		message.setLink("link");
		
		uns.add(message);
		
		UNSituationRecommendation situationRecommendation = new UNSituationRecommendation();
		situationRecommendation.setGuid(UUID.randomUUID().toString());
		
		uns.add(situationRecommendation);
		
		UNMergeRecommendation mergeRecommendation = new UNMergeRecommendation();
		mergeRecommendation.setSourceId("sourceId");
		mergeRecommendation.setSourceName("sourceName");
		mergeRecommendation.setTargetId("targetId");
		mergeRecommendation.setTargetName("targetName");
		mergeRecommendation.setSimilarity(1d);
		mergeRecommendation.setStatus("status");
		
		uns.add(mergeRecommendation);
		
		Random r = new Random();
		
		// add 100 diferent notifications
		for (int i = 0; i < 100; i++) {
			DimeInternalNotification n = new UserNotification(tenant.getId(), uns.get(r.nextInt(uns.size()-1)));
			notifyHistory.addNotificationOnHistory(n);
		}
	}
	
	@After
	@Transactional
	public void after(){
		List<Notification> ns = Notification.findAllNotificationsByTenant(tenant);
		for (Notification notification : ns) {
			notification.remove();
		}
		
		tenant.remove();
	}
	
	@Test
	public void testGetAllUN() {
		
		Response<UserNotificationDTO> response = controller.getMyUserNotifications(said, null, null, "0", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());
		
		//Test Pagination	
		response = controller.getMyUserNotifications(said, null, null, "10", "1"); // pag 2
		assertEquals(10, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "10", "2"); // pag 3
		assertEquals(10, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "50", "0"); // pag 1
		assertEquals(50, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "50", "1"); // pag 2
		assertEquals(50, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "60", "1"); // pag 2
		assertEquals(40, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "60", "2"); // pag 3
		assertEquals(0, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotifications(said, null, null, "100000", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());
	}
	
	@Test
	public void testGetByIdNotExist() {

		// not exist
		Response<UserNotificationDTO> response = controller.getUserNotificationById(said, "6666666666");
		assertNotNull(response);
		assertEquals(400, response.getMessage().getMeta().getCode().intValue());
		
	}
	@Test
	public void testGetByIdExist() {

		List<Notification> ns = Notification.findAllNotificationses();
		
		// not exist
		Response<UserNotificationDTO> response = controller.getUserNotificationById(said, ns.iterator().next().getId().toString());
		assertNotNull(response);
		assertEquals(200, response.getMessage().getMeta().getCode().intValue());
		assertFalse(response.getMessage().getData().getEntries().isEmpty());
		
	}
	
	@Test
	public void testGetAllUNUnread() {
		
		Response<UserNotificationDTO> response = controller.getMyUserNotificationsUnread(said, "0", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());
		
		// Test Pagination
		response = controller.getMyUserNotificationsUnread(said, "10", "0");
		assertEquals(10, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "10", "2"); // pag 3
		assertEquals(10, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "50", "0"); // pag 1
		assertEquals(50, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "50", "1"); // pag 2
		assertEquals(50, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "60", "1"); // pag 2
		assertEquals(40, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "60", "2"); // pag 3
		assertEquals(0, response.getMessage().getData().getEntries().size());
		response = controller.getMyUserNotificationsUnread(said, "100000", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());
	}

	@Test
	public void testReadeandUnread() {
		
		Response<UserNotificationDTO> response = controller.getMyUserNotificationsUnread(said, "0", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());
		
		String idToread = response.getMessage().getData().getEntries().iterator().next().getGuid();
		
		response = controller.getUserNotificationById(said, idToread);
		UserNotificationDTO uNotification = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(200, response.getMessage().getMeta().getCode().intValue());
		assertEquals(false,uNotification.getRead());
		
		UserNotificationEntry unEntry= uNotification.getUnEntry();
		assertNull(unEntry.get("testentry"));
		
		// update
		unEntry.put("testentry", "test");
		
		uNotification.setUnEntry(unEntry);
		Data<UserNotificationDTO> data = new Data<UserNotificationDTO>(0,0,uNotification);
		Message<UserNotificationDTO> message = new Message<UserNotificationDTO>();
		message.setData(data);
		Request<UserNotificationDTO> request = new Request<UserNotificationDTO>();
		request.setMessage(message);
		
		controller.postUpdateUserNotification(idToread, request);
		
		response = controller.getUserNotificationById(said, idToread);
		assertEquals(200, response.getMessage().getMeta().getCode().intValue());
		uNotification = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(true,uNotification.getRead());
		assertTrue(uNotification.getUnEntry().containsKey("testentry"));
		
		response = controller.getMyUserNotificationsUnread(said, "0", "0");
		assertEquals(99, response.getMessage().getData().getEntries().size());
		
		response = controller.getMyUserNotifications(said, null, null, "0", "0");
		assertEquals(100, response.getMessage().getData().getEntries().size());		
		
	}

}
