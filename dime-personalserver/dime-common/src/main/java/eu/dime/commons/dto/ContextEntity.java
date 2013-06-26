package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class ContextEntity {
	
	@javax.xml.bind.annotation.XmlElement(name="type")
	public String type;
	
	@javax.xml.bind.annotation.XmlElement(name="id")
	public String id;
	
	public ContextEntity() {
		super();
	}
		
	public ContextEntity(String type, String id) {
		super();
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
