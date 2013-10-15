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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.UserNotificationDTO;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.communications.requestbroker.controllers.infosphere.APIController;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.util.TenantHelper;
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
@Path("/dime/rest/{said}/usernotification")
public class PSUserNotificationsController implements APIController {

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
	

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/@all")
	public Response<UserNotificationDTO> getMyUserNotifications(
			@PathParam("said") String said,
			@DefaultValue("0") @QueryParam("limit") String from, 
			@DefaultValue("0") @QueryParam("limit") String to, 
			@DefaultValue("0") @QueryParam("limit") String limit, 
			@DefaultValue("0") @QueryParam("page") String page ) {

		Tenant tenant = tenantManager.getByAccountName(said);
		
		Integer firstResult = 0;
		Integer maxResults = 1000; 
		Integer limitInt;
		Integer pageInt;
		
		try {
			limitInt = new Integer(limit);
			pageInt = new Integer(page);
		} catch (Exception e) {
			return Response.badRequest(e.getMessage(), e);
		}	
		
		if(limitInt > 0 && pageInt == 0){
			firstResult = 0;
			maxResults = limitInt;
		}else 
		if(limitInt > 0 && pageInt > 0){	
				firstResult = (limitInt * (pageInt));
				maxResults = limitInt;
		}

		List<DimeInternalNotification> list = notifierManager
                        .getAllMyUserNotifications(tenant, firstResult, maxResults);

		List<UserNotificationDTO> dtos = UserNotificationDTO.dINToUNDTOs(list);

		Data<UserNotificationDTO> data = new Data<UserNotificationDTO>(0,dtos.size(),dtos.size());
		data.setEntry(dtos);

		return Response.ok(data);

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/unread")
	public Response<UserNotificationDTO> getMyUserNotificationsUnread(
			@PathParam("said") String said,
			@DefaultValue("0") @QueryParam("limit") String limit, 
			@DefaultValue("0") @QueryParam("page") String page )
	{
		
		Tenant tenant = tenantManager.getByAccountName(said);
		
		Integer firstResult = 0;
		Integer maxResults = 1000; 
		Integer limitInt;
		Integer pageInt;
		
		try {
			limitInt = new Integer(limit);
			pageInt = new Integer(page);
		} catch (Exception e) {
			return Response.badRequest(e.getMessage(), e);
		}	
		
		if(limitInt > 0 && pageInt == 0){
			firstResult = 0;
			maxResults = limitInt;
		}else 
		if(limitInt > 0 && pageInt > 0){	
				firstResult = (limitInt * (pageInt));
				maxResults = limitInt;
		}

		List<DimeInternalNotification> list = notifierManager
				.getAllMyUserUnReadedNotifications(tenant, firstResult, maxResults);

		List<UserNotificationDTO> dtos = UserNotificationDTO.dINToUNDTOs(list);

		Data<UserNotificationDTO> data = new Data<UserNotificationDTO>(0,dtos.size(),dtos.size());
		data.setEntry(dtos);

		return Response.ok(data);
		
	}
		
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{notificationID}")
	public Response<UserNotificationDTO> getUserNotificationById(@PathParam("said") String said,
			@PathParam("notificationID") String notificationID) {

		Tenant tenant = tenantManager.getByAccountName(said);
		
		Long id = null;

		try {
			id = new Long(notificationID);

		} catch (NumberFormatException e) {
			
			return Response.badRequest("ID not valid!", e);
		}

		DimeInternalNotification dimeNotification = notifierManager
				.getNotificationById(id);
		
		if(dimeNotification == null){
			return Response.badRequest("ID Not Exist");
		}
		
		UserNotificationDTO dto = UserNotificationDTO.dINToUNDTO(dimeNotification);

		Data<UserNotificationDTO> data = new Data<UserNotificationDTO>();

		data.addEntry(dto);

		return Response.ok(data);

	}

	@POST
	@Path("/@me/{notificationID}")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response<UserNotificationDTO> postUpdateUserNotification(@PathParam("notificationID") String notificationID, Request<UserNotificationDTO> request) {

		Long id = null;
		UserNotificationDTO notificationToUpdate = request.getMessage().getData().getEntries().iterator().next();
		
		try {
			id = new Long(notificationID);
			
		} catch (NumberFormatException e) {
			
			return Response.badRequest("ID not valid!", e);
		}
		DimeInternalNotification dimeNotification=null;
		try {
			 dimeNotification = notifierManager.updateUserNotification(id,notificationToUpdate);
			
		} catch (Exception e) {
			return Response.serverError("Could not update notification: ", e);
		}		
			
		
		UserNotificationDTO dto = UserNotificationDTO.dINToUNDTO(dimeNotification);
		Data<UserNotificationDTO> data = new Data<UserNotificationDTO>();

		data.addEntry(dto);

		return Response.ok(data);

	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/@me/{notificationId}")
	public Response deleteNotification(@PathParam("notificationId") String notificationId){
		
		Long id = null;

		try {
			id = new Long(notificationId);

		} catch (NumberFormatException e) {
			
			return Response.badRequest("ID not valid!", e);
		}	
		try {
			notifierManager.remove(id);
			}catch (NotifierException e) {
				return Response.badRequest("Id not valid!", e);
			}catch (Exception e) {
			return Response.serverError(e.getMessage(), e);
		}		
		
		return Response.ok();
	}
	// test
	
	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Path("/test/{id}")
	public Response<DimeInternalNotification> addTestNotification(
			@PathParam("id") String id) throws NotifierException {

		UNRefToItem unr = new UNRefToItem();
		unr.setGuid(id);
		unr.setOperation(UNRefToItem.OPERATION_SHARED);
		unr.setType(UNRefToItem.TYPE_DATABOX);
		unr.setUserID("juan");
		
		DimeInternalNotification userNotification = new UserNotification(TenantHelper.getCurrentTenantId(),unr);
		
		notifierManager.pushInternalNotification(userNotification);

		Data<DimeInternalNotification> data = new Data<DimeInternalNotification>();
		data.addEntry(userNotification);
		return Response.ok(data);

	}


}