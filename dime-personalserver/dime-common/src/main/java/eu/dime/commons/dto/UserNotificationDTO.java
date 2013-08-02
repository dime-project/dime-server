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
import eu.dime.commons.notifications.user.UserNotificationEntry;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class UserNotificationDTO {

	@javax.xml.bind.annotation.XmlElement(name="guid")
	public String guid;
	
	@javax.xml.bind.annotation.XmlElement(name="type")
	public String type;

        @javax.xml.bind.annotation.XmlElement(name="userId")
	private String userId="@me";


	@javax.xml.bind.annotation.XmlElement(name="created")
	public Long created;
	
	@javax.xml.bind.annotation.XmlElement(name="lastUpdated")
	public Long lastUpdated;
	
	@javax.xml.bind.annotation.XmlElement(name="name")
	public String name;
	
	@javax.xml.bind.annotation.XmlElement(name="read")
	public Boolean read;
	
	@javax.xml.bind.annotation.XmlElement(name="unType")
	public String unType;
	
	@javax.xml.bind.annotation.XmlElement(name="unEntry")
	public UserNotificationEntry unEntry;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public String getUnType() {
		return unType;
	}

	public void setUnType(String unType) {
		this.unType = unType;
	}


	public UserNotificationEntry getUnEntry() {
		return unEntry;
	}

	public void setUnEntry(UserNotificationEntry unEntry) {
		this.unEntry = unEntry;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


        /**
            * @return the userId
            */
        public String getUserId() {
            return userId;
        }

        /**
            * @param userId the userId to set
            */
        public void setUserId(String userId) {
            this.userId = userId;
        }

	// Utils
	
	public static List<UserNotificationDTO> dINToUNDTOs(
			List<DimeInternalNotification> dims) {
	
		Vector<UserNotificationDTO> result = new Vector<UserNotificationDTO>();
		for (DimeInternalNotification dimeInternalNotification : dims) {
	
			result.add(dINToUNDTO(dimeInternalNotification));
		}
	
		return result;
	}

	public static UserNotificationDTO dINToUNDTO(DimeInternalNotification dim) {
	
		UserNotificationDTO nDTO = new UserNotificationDTO();
	
		if(dim.getId()!= null){
			nDTO.setGuid(dim.getId().toString());
		}else{
			nDTO.setGuid(UUID.randomUUID().toString());
		}		
		
		nDTO.setType(dim.getNotificationType());
		nDTO.setCreated(dim.getCreateTS());
		nDTO.setLastUpdated(dim.getUpdateTS());
		nDTO.setName(dim.getName());
		nDTO.setUnType((String) dim.getUnEntry().get(
				UserNotificationEntry.UNTYPE_LABEL));
		nDTO.setUnEntry(dim.getUnEntry());
	
		return nDTO;
	
	}


}
