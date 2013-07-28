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
