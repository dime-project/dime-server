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

package eu.dime.commons.notifications.system;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * Notification related with system changes.
 * This Notification extends DimeInternalNotification and can be added on NotifierManager 
 * queue to be deal on UI. 
 * 
 * @author mplanaguma
 *
 */
public class SystemNotification extends DimeInternalNotification {

	public SystemNotification(
			Long tenant, String operation,
			String itemID, 
			String itemType,
			String userID) {
		
		super(tenant);
		
		super.setNotificationType(DimeInternalNotification.SYSTEM_NOTIFICATION_TYPE);
		super.setCreateTS(System.currentTimeMillis());
		super.setUpdateTS(System.currentTimeMillis());
		
		super.setOperation(operation);
		super.setItemID(itemID);
		super.setItemType(itemType);
		
		if (userID == null || userID.equals("") || userID.endsWith(":me")) {
			super.setUserID(DimeInternalNotification.USERID_ME);
		} else {
			super.setUserID(userID);
		}
	}

}
