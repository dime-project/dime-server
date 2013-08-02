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

import org.junit.Test;
import org.mockito.Mockito;

import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.UserNotificationDTO;
import eu.dime.ps.communications.requestbroker.controllers.notifications.PSUserNotificationsController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.notifier.NotifierManager;

public class PSUserNotificationControllerTest extends PSInfoSphereControllerTest {

	private PSUserNotificationsController controller = new PSUserNotificationsController();

	private static final String said = "juan";
	private static final Long id = 1l;
	
	private NotifierManager mockedManager;
	private TenantManager mockedTenantManager;

	public PSUserNotificationControllerTest() {
		mockedManager = buildNotifierManager();
		mockedTenantManager = buildTenantManager();
		controller.setNotifierManager(mockedManager);
		controller.setTenantManager(mockedTenantManager);
	}
	
	@Test
	public void testGetAllMyNotifications() {

		Response<UserNotificationDTO> response = controller.getMyUserNotifications(said, null, null, "0", "0");
		assertNotNull(response.getMessage().getData().getEntries().iterator().next());
		
		Mockito.verify(mockedManager).getAllMyUserNotifications(
				mockedTenantManager.getByAccountName(said), 
				0, 1000);
		
		// Pagination
		
		response = controller.getMyUserNotifications(said, null, null, "10", "1");
		assertNotNull(response.getMessage().getData().getEntries().iterator().next());
		
		Mockito.verify(mockedManager).getAllMyUserNotifications(
				mockedTenantManager.getByAccountName(said), 
				10, 10);
		
		response = controller.getMyUserNotifications(said, null, null, "10", "2");
		assertNotNull(response.getMessage().getData().getEntries().iterator().next());
		
		Mockito.verify(mockedManager).getAllMyUserNotifications(
				mockedTenantManager.getByAccountName(said), 
				20, 10);
		
		response = controller.getMyUserNotifications(said, null, null, "5", "0");
		assertNotNull(response.getMessage().getData().getEntries().iterator().next());
		
		Mockito.verify(mockedManager).getAllMyUserNotifications(
				mockedTenantManager.getByAccountName(said), 
				0, 5);
	}

	@Test
	public void testGetById() {

		Response<UserNotificationDTO> response = controller.getUserNotificationById(said, "1");
		assertNotNull(response.getMessage().getData().getEntries().iterator().next());
		
		Mockito.verify(mockedManager).getNotificationById(1l);
	}

	@Test
	public void testMarkAsRead() {
		controller.postNotificationMarkAsRead("1");	
		Mockito.verify(mockedManager).markNotificationAsRead(1l);

	}

}
