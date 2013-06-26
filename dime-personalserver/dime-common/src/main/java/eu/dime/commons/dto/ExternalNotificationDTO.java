package eu.dime.commons.dto;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;

import eu.dime.commons.notifications.DimeInternalNotification;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class ExternalNotificationDTO extends Entry {
    
    public static final String OPERATION_CREATE = "create";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";
    public static final String OPERATION_SHARE = "share";
    public static final String OPERATION_UNSHARE = "unshare";
    public static final String OPERATION_MATCHING = "matching";

	@javax.xml.bind.annotation.XmlElement(name = "type")
	public String type = "notification";
	
	@javax.xml.bind.annotation.XmlElement(name = "sender")
	public String sender = "";
	
	@javax.xml.bind.annotation.XmlElement(name = "operation")
	public String operation = null;
	
	@javax.xml.bind.annotation.XmlElement(name = "date")
	public Long date = 0l;
	
	@javax.xml.bind.annotation.XmlElement(name = "saidSender")
	public String saidSender = null;
	
	@javax.xml.bind.annotation.XmlElement(name = "saidReciever")
	public String saidReciever = null;
	
	@javax.xml.bind.annotation.XmlElement(name = "element")
	public Entry element;

	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	

	public Long getDate() {
		return date;
	}
	
	public void setDate(Long date) {
	    this.date = date;
	}
	
	public Entry getElement() {
	    return element;
	}

	public void setElement(Entry element) {
	    this.element = element;
	}

	@Override
	@Deprecated
	public String getType() {
		return type;
	}
	@Override
	@Deprecated
	public List<String> getItems() {
		return items;
	}
	@Override
	@Deprecated
	public void setItems(List<String> items) {
	}
	@Override
	@Deprecated
	public String getLastModified() {
		return lastModified;
	}
	@Override
	@Deprecated
	public void setLastModified(String lastUpdate) {
	}

	public String getSaidSender() {
	    return saidSender;
	}

	public void setSaidSender(String saidSender) {
	    this.saidSender = saidSender;
	}

	public String getSaidReciever() {
	    return saidReciever;
	}

	public void setSaidReciever(String saidReciever) {
	    this.saidReciever = saidReciever;
	}

	// Utils
	
	public static List<ExternalNotificationDTO> dINTONDTOs(List<DimeInternalNotification> dimeNotifications){
		
		Vector<ExternalNotificationDTO> dtos = new Vector<ExternalNotificationDTO>(0);
		
		for (DimeInternalNotification dimeNotification : dimeNotifications) {
			dtos.add(dINTONDTO(dimeNotification));
		}
		
		return dtos;
	}

	public static ExternalNotificationDTO dINTONDTO(DimeInternalNotification dimeNotification){
		
		ExternalNotificationDTO jsonNotification = new ExternalNotificationDTO();
	
		jsonNotification.setGuid(UUID.randomUUID().toString());
		jsonNotification.setName(dimeNotification.getName());
		jsonNotification.setSender(dimeNotification.getSender());
		jsonNotification.setOperation(dimeNotification.getOperation());
		jsonNotification.setDate(System.currentTimeMillis());
	
		Entry element = new Entry();
			element.setGuid(dimeNotification.getItemID());
			element.setName(dimeNotification.getName());
			element.setType(dimeNotification.getItemType());
		jsonNotification.setElement(element);
		
		return jsonNotification;
		
	}

}
