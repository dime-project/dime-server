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

package eu.dime.ps.controllers.notifier;

import java.util.Date;
import java.util.List;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Set and Get the notifications from persistence 
 * 
 * @author mplanaguma
 */
public interface NotifyHistory {
    
    public Long addNotificationOnHistory(DimeInternalNotification notification);
    
    public List<DimeInternalNotification> getNotificationHistory();
    
    public DimeInternalNotification getNotificationById(Long id);
    
    public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to);

	public List<DimeInternalNotification> getUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults);
	
	public List<DimeInternalNotification> getUnreadedUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults);
	
	public void markAsRead(Long id);

}
