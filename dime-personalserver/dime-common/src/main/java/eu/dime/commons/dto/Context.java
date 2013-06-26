package eu.dime.commons.dto;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Context extends Entry {
	
	@javax.xml.bind.annotation.XmlElement(name = "entity")
	public ContextEntity entity;
	
	@javax.xml.bind.annotation.XmlElement(name = "scope")
	public String scope;
	
	@javax.xml.bind.annotation.XmlElement(name = "source")
	public ContextSource source;
	
	@javax.xml.bind.annotation.XmlElement(name = "timestamp")
	public String timestamp;

	@javax.xml.bind.annotation.XmlElement(name = "expires")
	public String expires;
	
	@javax.xml.bind.annotation.XmlElement(name = "dataPart")
	public Map<String, Object> dataPart;
	
	/*@javax.xml.bind.annotation.XmlElement(name = "entity")
	public ContextEntity entity;
	
	@javax.xml.bind.annotation.XmlElement(name = "scope")
	public String scope;
	
	@javax.xml.bind.annotation.XmlElement(name = "contextSource")
	public ContextSource source;
	
	@javax.xml.bind.annotation.XmlElement(name = "timestamp")
	public String timestamp;

	@javax.xml.bind.annotation.XmlElement(name = "expires")
	public String expires;
	
	@javax.xml.bind.annotation.XmlElement(name = "dataPart")
	public Map<String, Object> dataPart;*/

	public Context() {
		super();
	}
	
	public ContextEntity getEntity() {
		return entity;
	}

	public void setEntity(ContextEntity entity) {
		this.entity = entity;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public ContextSource getSource() {
		return source;
	}

	public void setSource(ContextSource source) {
		this.source = source;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Map<String, Object> getDataPart() {
		return dataPart;
	}

	public void setDataPart(Map<String, Object> dataPart) {
		this.dataPart = dataPart;
	}

}
