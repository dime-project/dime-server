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

package eu.dime.commons.dto;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;

import eu.dime.commons.notifications.DimeInternalNotification;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SystemNotificationDTO extends Entry{
    
    public static final String OPERATION_CREATE = "create";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";

	@javax.xml.bind.annotation.XmlElement(name = "created")
	public Long created;
	
	@javax.xml.bind.annotation.XmlElement(name = "operation")
	public String operation = null;
	
	@javax.xml.bind.annotation.XmlElement(name = "element")
	public Element element;

	public SystemNotificationDTO(){
		super.setType("notification"); 
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public Element getElement() {
	    return element;
	}

	public void setElement(Element element) {
	    this.element = element;
	}

	
	// Utils
	
	public static List<SystemNotificationDTO> dINTONDTOs(List<DimeInternalNotification> dimeNotifications){
		
		Vector<SystemNotificationDTO> dtos = new Vector<SystemNotificationDTO>(0);
		
		for (DimeInternalNotification dimeNotification : dimeNotifications) {
			dtos.add(dINTONDTO(dimeNotification));
		}
		
		return dtos;
	}

	public static SystemNotificationDTO dINTONDTO(DimeInternalNotification dimeNotification){
		
		SystemNotificationDTO jsonNotification = new SystemNotificationDTO();
	
		jsonNotification.setGuid(dimeNotification.getId());
		jsonNotification.setOperation(dimeNotification.getOperation());
		jsonNotification.setCreated(dimeNotification.getCreateTS());
		
		jsonNotification.setName(dimeNotification.getTenant().toString());
	
		Element element = new Element();
			element.setGuid(dimeNotification.getItemID());
			element.setType(dimeNotification.getItemType());
			element.setUserID(dimeNotification.getUserID());
			
		jsonNotification.setElement(element);
		
		return jsonNotification;
		
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

}
