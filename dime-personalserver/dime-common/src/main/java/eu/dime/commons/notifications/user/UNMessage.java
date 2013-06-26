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
