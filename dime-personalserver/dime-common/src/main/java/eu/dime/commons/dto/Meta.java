package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Meta {
	
	@javax.xml.bind.annotation.XmlElement(name="v")
	public String version;
	
	@javax.xml.bind.annotation.XmlElement(name="status")
	public String status;
	
	@javax.xml.bind.annotation.XmlElement(name="code")
	public Integer code;
	
	@javax.xml.bind.annotation.XmlElement(name="timeRef")
	public Long timeRef;
	
	@javax.xml.bind.annotation.XmlElement(name="method")
	public String method;
	
	@javax.xml.bind.annotation.XmlElement(name="msg")
	public String message;

	public Meta(){	
		super();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public Long getTimeRef() {
		return timeRef;
	}

	public void setTimeRef(Long timeRef) {
		this.timeRef = timeRef;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}