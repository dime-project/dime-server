/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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