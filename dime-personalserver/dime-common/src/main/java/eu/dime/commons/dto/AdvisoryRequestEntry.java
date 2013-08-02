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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
/**
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 *
 */
@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class AdvisoryRequestEntry{

	@javax.xml.bind.annotation.XmlElement(name="agentGuids")
	@javax.xml.bind.annotation.XmlList
	public List <String> agentGuids;
	
	@javax.xml.bind.annotation.XmlElement(name="shareableItems")
	@javax.xml.bind.annotation.XmlList
	public List<String> shareableItems;
	
	@javax.xml.bind.annotation.XmlElement(name="profileGuid")
	public String profileGuid;
	
	public List<String> getAgentGuids() {
		return agentGuids;
	}
	public void setAgentGuids(List<String> agentGuids) {
		this.agentGuids = agentGuids;
	}
	public List<String> getShareableItems() {
		return shareableItems;
	}
	public void setShareableItems(List<String> shareableItems) {
		this.shareableItems = shareableItems;
	}
	public String getProfileGuid() {
		return profileGuid;
	}
	public void setProfileGuid(String profileGuid) {
		this.profileGuid = profileGuid;
	}
}
