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

package eu.dime.ps.communications.requestbroker.controllers.notifications;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SystemNotificationDTO;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.APIController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Dime REST API Controller for a InfoSphere Methods GET, POST and DELETE to
 * access on Notifocations features
 * 
 * @author <a href="mailto:mplanaguma@bdigital.org"> Marc Planaguma
 *         (mplanaguma)</a>
 * 
 */
@Controller
@Path("/dime/rest/{said}/notification")
public class PSNotificationsController implements APIController {

	private NotifierManager notifierManager;
	private TenantManager tenantManager;

	@Autowired
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	@Autowired
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	// All Notifications

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response getAllMyNotifications(@PathParam("said") String said) {

		Tenant tenant = tenantManager.getByAccountName(said);

		List<DimeInternalNotification> list = notifierManager
				.getAllNotifications(tenant, null, null);

		Data<SystemNotificationDTO> data = new Data<SystemNotificationDTO>();

		data.setEntry(SystemNotificationDTO.dINTONDTOs(list));

		return Response.ok(data);

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/byDate")
	public Response getAllMyNotificationsByDate(@QueryParam("from") Long from,
			@QueryParam("to") Long to) {

		List<DimeInternalNotification> list = notifierManager
				.getNotificationsByDate(new Date(from), new Date(to));

		Data<SystemNotificationDTO> data = new Data<SystemNotificationDTO>();

		data.setEntry(SystemNotificationDTO.dINTONDTOs(list));

		return Response.ok(data);
	}

	// test methods

	//	@GET
	//	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	//	@Path("/test/{id}")
	//	public Response<DimeInternalNotification> addTestNotification(
	//			@PathParam("id") String id) throws NotifierException {
	//
	//		DimeInternalNotification notification = new DimeInternalNotification(
	//				TenantContextHolder.getTenant());
	//		
	//		notification.setItemID(id);
	//		notification.setName("name-" + id);
	//		notification.setSender("juan");
	//		notification.setTarget("juan");
	//		notification.setItemType("type");
	//		notification.setOperation(DimeInternalNotification.OP_CREATE);
	//		notification.setCreateTS(System.currentTimeMillis());
	//		notification.setUpdateTS(System.currentTimeMillis());
	//		notification.setUserID("@me");
	//
	//		notifierManager.pushInternalNotification(notification);
	//		
	//		Data<DimeInternalNotification> data = new Data<DimeInternalNotification>();
	//		data.addEntry(notification);
	//		return Response.ok(data);
	//
	//	}

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@size")
	public Response<HashMap<String,Object>> getsizeNotification(@PathParam("said") String said) {
		Tenant tenant = tenantManager.getByAccountName(said);
		HashMap<String,Object> size = notifierManager.getSize(tenant.getId());

		Data<HashMap<String,Object>> data = new Data<HashMap<String,Object>>();

		data.addEntry(size);

		return Response.ok(data);
	}	


	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@clear/internal")
	public Response clearInternalNotifications(@PathParam("said") String said) {
		Tenant tenant = tenantManager.getByAccountName(said);

		notifierManager.clearInternal(tenant.getId());

		return Response.ok();

	}	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@clear/external")
	public Response clearExternalNotifications(@PathParam("said") String said) {		

		notifierManager.clearExternal();

		return Response.ok();

	}	

}