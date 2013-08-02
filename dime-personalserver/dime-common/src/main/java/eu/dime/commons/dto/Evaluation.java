/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)

public class Evaluation  {

		
	@javax.xml.bind.annotation.XmlElement(name = "tenantId")
	private String tenantId;	
	
	@javax.xml.bind.annotation.XmlElement(name = "created")
	private long created;
		
	@javax.xml.bind.annotation.XmlElement(name = "clientId")
	private String clientId;
	
	@javax.xml.bind.annotation.XmlElement(name = "viewStack")
	private String[] viewStack;
	
	@javax.xml.bind.annotation.XmlElement(name = "action")
	private String action;
	
	@javax.xml.bind.annotation.XmlElement(name = "currPlace")
	private String currPlace;
	
	@javax.xml.bind.annotation.XmlElement(name = "currSituationId")
	private String currSituationId;
	
	@javax.xml.bind.annotation.XmlElement(name = "involvedItems")
	private Map<String,Object> involvedItems;

	@javax.xml.bind.annotation.XmlElement(name = "guid")
	private String guid;
	
	@javax.xml.bind.annotation.XmlElement(name = "type")
	private String type = "evaluation";
	
	public Map<String, Object> getInvolvedItems() {
		return involvedItems;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public String getGuid() {
		return guid;
	}

	public void setInvolvedItems(Map<String, Object> involvedItems) {
		this.involvedItems = involvedItems;
	}

		
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}	

	public String[] getViewStack() {
		return viewStack;
	}

	public void setViewStack(String[] viewStack) {
		this.viewStack = viewStack;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCurrPlace() {
		return currPlace;
	}

	public void setCurrPlace(String currPlace) {
		this.currPlace = currPlace;
	}

	public String getCurrSituationId() {
		return currSituationId;
	}

	public void setCurrSituationId(String currSituationId) {
		this.currSituationId = currSituationId;
	}

}
