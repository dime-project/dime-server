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

import org.junit.Test;

import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SystemNotificationDTO;
import eu.dime.ps.communications.requestbroker.controllers.notifications.PSNotificationsController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.notifier.NotifierManager;

public class PSNotificationControllerTest extends PSInfoSphereControllerTest {

	private PSNotificationsController controller = new PSNotificationsController();

	private static final String said = "juan";
	private static final Long id = 1l;

	public PSNotificationControllerTest() {
		NotifierManager mockedManager = buildNotifierManager();
		TenantManager mockedTenantManager = buildTenantManager();
		controller.setNotifierManager(mockedManager);
		controller.setTenantManager(mockedTenantManager);
	}
	
	@Test
	public void testGetAllMyNotifications() {

		Response<SystemNotificationDTO> response = controller
				.getAllMyNotifications(said);
		assertNotNull(response);
	}

	@Test
	public void testGetByDate() {
		Response response = controller.getAllMyNotificationsByDate(1l, 2l);
		assertNotNull(response);

	}

}
