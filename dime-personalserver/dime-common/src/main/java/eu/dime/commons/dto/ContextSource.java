package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class ContextSource {

	@javax.xml.bind.annotation.XmlElement(name="id")
	public String id;
	@javax.xml.bind.annotation.XmlElement(name="v")
	public String version;
	
	public ContextSource() {
		super();
	}
	
	public ContextSource(String id, String version) {
		super();
		this.id = id;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
