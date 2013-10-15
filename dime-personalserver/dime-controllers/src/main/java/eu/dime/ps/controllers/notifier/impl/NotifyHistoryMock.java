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

package eu.dime.ps.controllers.notifier.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.dime.commons.dto.UserNotificationDTO;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.controllers.notifier.NotifyHistory;
import eu.dime.ps.storage.entities.Tenant;

public class NotifyHistoryMock  implements NotifyHistory  {
    
    
    public Long addNotificationOnHistory(DimeInternalNotification notification){
	
    	return 1l;
    }
    
    public List<DimeInternalNotification> getNotificationHistory(){
	
	List<DimeInternalNotification> result = new LinkedList<DimeInternalNotification>();
	return result;
	
    }

	@Override
	public DimeInternalNotification getNotificationById(Long id) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> getNotificationsByDate(Date from, Date to) {
		return null;
	}

	@Override
	public List<DimeInternalNotification> getUnreadedUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults) {		
		return null;
	}

	@Override
	public List<DimeInternalNotification> getUserNotificationHistory(Tenant tenant, Integer firstResult, Integer maxResults) {
		return null;
	}

	@Override
	public void markAsRead(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DimeInternalNotification updateNotification(long id,
			UserNotificationDTO userNotification) {
		// TODO Auto-generated method stub
		return null;
	}
	


}
