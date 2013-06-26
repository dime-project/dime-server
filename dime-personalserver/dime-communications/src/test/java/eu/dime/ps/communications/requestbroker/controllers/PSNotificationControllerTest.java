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
