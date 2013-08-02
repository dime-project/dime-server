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

package eu.dime.commons.notifications.user;

import eu.dime.commons.notifications.DimeInternalNotification;

/**
 * 
 * {
         //[..]   //general fields as specified in _general user notification body_
         "unType":"message",
         "unEntry":{
              "message":"",
              "link":"urlstring" //optional link to some related information
         }
}
 * 
 * @author mplanaguma
 *
 */
public class UNMessage extends UserNotificationEntry {

	public UNMessage(){
		this.put(UNTYPE_LABEL, DimeInternalNotification.UN_TYPE_MESSAGE);
	}
	
	public String getMessage() {
		return (String) this.get("message");
	}
	public void setMessage(String message) {
		this.put("message", message);
	}
	public String getLink() {
		return (String) this.get("link");
	}
	public void setLink(String link) {
		this.put("link", link);
	}
	
	
}
