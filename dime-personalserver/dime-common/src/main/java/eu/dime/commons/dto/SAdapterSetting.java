package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class SAdapterSetting extends Entry {

	// Types
	public static final String BOOLEAN = "boolean";
	public static final String STRING = "string";
	public static final String PASSWORD = "password";
	public static final String ACCOUNT = "account";
	public static final String LINK = "link";

	@javax.xml.bind.annotation.XmlElement(name = "name")
	private String name;
	
	@javax.xml.bind.annotation.XmlElement(name = "fieldtype")
	private String fieldtype;

	@javax.xml.bind.annotation.XmlElement(name = "mandatory")
	private String mandatory;

	@javax.xml.bind.annotation.XmlElement(name = "value")
	private String value;

	public SAdapterSetting() {
		this ("unknown", false, "string", "");
		this.name = "unknown";
		this.fieldtype = "string";
		this.mandatory = "false";
		this.value = "";
	}

	public SAdapterSetting(String name, boolean mandatory, String type,
			String value) {
		this.name = name;
		this.fieldtype = type;
		this.setMandatory(mandatory);
		this.value = value;
	}
	
	public SAdapterSetting(String name, String mandatory, String type,
			String value) {
		this.name = name;
		this.fieldtype = type;
		this.mandatory = mandatory;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return fieldtype;
	}
	
	public void setType(String type) {
		this.fieldtype = type;
	}

	public String getMandatory() {
		return mandatory;
	}
	
	public boolean isMandatory() {
		if (this.mandatory.equals("true"))
			return true;
		else
			return false;
	}
	
	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public void setMandatory(boolean mandatory) {
		if (mandatory)
			this.mandatory = "true";
		else
			this.mandatory="false";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}