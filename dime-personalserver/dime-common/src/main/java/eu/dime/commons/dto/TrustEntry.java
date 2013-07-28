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

/**
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 */
@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class TrustEntry extends Entry {

	@javax.xml.bind.annotation.XmlElement(name="agent_guid")
	public String agent_guid;
	@javax.xml.bind.annotation.XmlElement(name="thing_guid")
	public String thing_guid;
	@javax.xml.bind.annotation.XmlElement(name="message")
	public String message;

	public TrustEntry(){
		super();
		
	}
	
	public String getAgent_guid() {
		return agent_guid;
	}

	public String getThing_guid() {
		return thing_guid;
	}

	public String getMessage() {
		return message;
	}

	public void setAgent_guid(String agent_guid) {
		this.agent_guid = agent_guid;
	}

	public void setThing_guid(String thing_guid) {
		this.thing_guid = thing_guid;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
