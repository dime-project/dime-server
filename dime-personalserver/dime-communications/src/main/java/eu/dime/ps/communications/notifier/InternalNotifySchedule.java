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

package eu.dime.ps.communications.notifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.atmosphere.cpr.Broadcaster;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.SystemNotificationDTO;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.communications.requestbroker.pubsub.PSNotificationDispacher;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.TenantManagerException;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.gateway.auth.CredentialStore;

/**
 * Cron of tasks for the Notifications delivery to the UI
 * 
 * @author mplanaguma
 * 
 */
public class InternalNotifySchedule {

	private static final Logger logger = LoggerFactory
			.getLogger(InternalNotifySchedule.class);
	
	private NotifierManager notifierManager;
	private CredentialStore credentialStore;
	private TenantManager tenantManager;

	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}

	@Autowired
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	private  ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> pendingNotifications = new  ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>();
	
	public void pushNotAtendedNotification(JSONObject json){
		
		try {
			String tenant = json.getJSONObject("response").getJSONObject("data").getJSONArray("entry").getJSONObject(0).getString("name");
		
			ConcurrentLinkedQueue<String> list = pendingNotifications.get(tenant);
			if(list == null){
				list = new ConcurrentLinkedQueue<String>();
			}
			
			list.add(json.toString());
			
			pendingNotifications.put(tenant, list);
		
		} catch (JSONException e) {
			logger.warn("Is not possible recover this notification: " + json.toString());
		}
		
	
	}
	
	public String popNotAtendedNotification(String tenant){

		ConcurrentLinkedQueue<String> list = pendingNotifications.get(tenant);
		
		if(list != null){
			String o = list.poll();
			return o;
		}
		
		return null;
		
	}
	
	// Collection of active broadcaters
	// {said: {broadcaster: dispacher}}
	private ConcurrentHashMap<String, ConcurrentHashMap<Broadcaster, PSNotificationDispacher>> broadcasters;

	public void addBroadcaster(PSNotificationDispacher notificationDispacher,
			Broadcaster broadcaster, String said) {

		if (broadcasters.containsKey(said)) {
			broadcasters.get(said).put(broadcaster, notificationDispacher);
		} else {
			ConcurrentHashMap<Broadcaster, PSNotificationDispacher> mapBroadcaster = new ConcurrentHashMap<Broadcaster, PSNotificationDispacher>(
					1);
			mapBroadcaster.put(broadcaster, notificationDispacher);
			this.broadcasters.put(said, mapBroadcaster);
		}

	}
	
	public void removeBroadcaster(Broadcaster broadcaster){
		
		Set<String> saids = broadcasters.keySet();
		for (String said : saids) {
			ConcurrentHashMap<Broadcaster, PSNotificationDispacher> bMap = broadcasters.get(said);
			
			if(bMap.containsKey(broadcaster)){
				bMap.remove(broadcaster);
			}
			
			if(bMap.isEmpty()){
				broadcasters.remove(said);
			}
		}
		
	}

	public InternalNotifySchedule() {
		broadcasters = new ConcurrentHashMap<String, ConcurrentHashMap<Broadcaster, PSNotificationDispacher>>(
				0);

	}

	// Notifications Between PS - UI
	public void dealNotifications() {

		// Getting pending Broadcasters
		Set<String> saidList = broadcasters.keySet();
		logger.debug("Job executed for: " + saidList);

		Map<Broadcaster, PSNotificationDispacher> broadcasterMap;

		for (String key : saidList) {

			broadcasterMap = broadcasters.get(key);
			logger.debug("Broadcasters actived for: " + saidList
					+ " with devices: " + broadcasterMap.size());

			Long tenant;
			try {
				tenant = tenantManager.getByAccountName(key).getId();

			} catch (TenantManagerException e) {
				logger.warn("User not exist: " + e.getMessage());
				
				// Send Notification Error - User not exist
				Response response = Response.badRequest(e.getMessage(), e);
				String json = JaxbJsonSerializer.jsonValue(response);

				Set<Broadcaster> listKeys = broadcasterMap.keySet();
				for (Broadcaster broadcaster : listKeys) {
					PSNotificationDispacher notificationDispacher = broadcasterMap
							.get(broadcaster);
					logger.warn("JSON ERROR message: " + json);
				
					notificationDispacher.publishIntern(json, broadcaster);
				}

				logger.debug("Removing broadcasters for: " + key);
				broadcasters.remove(key);
				break;
			}

			Set<Broadcaster> listKeys = broadcasterMap.keySet();
			logger.debug("Boradcaster devices: " + listKeys);
			
			String pendingNotification = this.popNotAtendedNotification(tenant.toString());
			
			if(pendingNotification == null){
				
				List<DimeInternalNotification> internalNotififcations = notifierManager
						.popInternalNotifications(tenant, 10);

				if (internalNotififcations.isEmpty()) {
					logger.debug("No Internal Notifications for user: " + key);
					break;
				}
				
				for (Broadcaster broadcaster : listKeys) {

					PSNotificationDispacher notificationDispacher = broadcasterMap
							.get(broadcaster);

					Data<SystemNotificationDTO> data = new Data<SystemNotificationDTO>();

					for (DimeInternalNotification internalNotififcation : internalNotififcations) {

						logger.debug("Internal Notifications: "
								+ internalNotififcation);

						SystemNotificationDTO jsonNotification = SystemNotificationDTO.dINTONDTO(internalNotififcation);
						
						data.addEntry(jsonNotification);
					}

					Response response = Response.ok(data);

					String json = JaxbJsonSerializer.jsonValue(response);
					logger.debug("JSON Notification created: " + json);

					logger.info("Dealing Internal Notifications for user: " + key
							+ " and device: " + broadcaster.getID());

					// dealing the notification and closing connection
					notificationDispacher.publishIntern(json, broadcaster);
					logger.debug("Notification dealed to UI");

				}
				
			}else{
				
				for (Broadcaster broadcaster : listKeys) {

					PSNotificationDispacher notificationDispacher = broadcasterMap
							.get(broadcaster);

					logger.debug("JSON Notification recovered: " + pendingNotification);

					logger.info("Dealing Internal Notifications for user: " + key
							+ " and device: " + broadcaster.getID());

					// dealing the notification and closing connection
					notificationDispacher.publishIntern(pendingNotification, broadcaster);
					logger.debug("Notification dealed to UI");

				}
				
			}

			// removing broadcaster from the pending list
			logger.debug("Removing broadcasters dealed for: " + key);
			broadcasters.remove(key);
		}

	}

}
