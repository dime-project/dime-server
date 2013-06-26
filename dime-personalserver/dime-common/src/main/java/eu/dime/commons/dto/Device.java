package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Device extends Entry {

	@javax.xml.bind.annotation.XmlElement(name = "type")
	private String type = "device";
	
	@javax.xml.bind.annotation.XmlElement(name = "userId")
	public String userId;
		
	@javax.xml.bind.annotation.XmlElement(name = "clientType")
	public String clientType;
	
	@javax.xml.bind.annotation.XmlElement(name = "ddo:deviceIdentifier")
	public String deviceIdentifier;
	
	@javax.xml.bind.annotation.XmlElement(name = "versionNumber")
	public String versionNumber;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
	}
	
	
}
