package eu.dime.commons.dto;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SharedTo extends Entry {
	
	@javax.xml.bind.annotation.XmlElement(name="agentIdReceiver")
	public String agentId;
	@javax.xml.bind.annotation.XmlElement(name="agentType")
	public String agentType;
	@javax.xml.bind.annotation.XmlElement(name="itemType")
	public String type;
	@javax.xml.bind.annotation.XmlElement(name="itemId")
	public String itemId;
	@javax.xml.bind.annotation.XmlElement(name="saidSender")
	public String saidSender;
	
	
	public String getGuid() {
	    return guid;
	}
	public void setGuid(String guid) {
	    this.guid = guid;
	}
	public String getAgentId() {
	    return agentId;
	}
	public void setAgentId(String agentId) {
	    this.agentId = agentId;
	}
	public String getAgentType() {
	    return agentType;
	}
	public void setAgentType(String agentType) {
	    this.agentType = agentType;
	}
	public String getType() {
	    return type;
	}
	public void setType(String type) {
	    this.type = type;
	}
	public String getItemId() {
	    return itemId;
	}
	public void setItemId(String itemId) {
	    this.itemId = itemId;
	}
	public String getSaidSender() {
	    return saidSender;
	}
	public void setSaidSender(String saidSender) {
	    this.saidSender = saidSender;
	}
	
	

}
