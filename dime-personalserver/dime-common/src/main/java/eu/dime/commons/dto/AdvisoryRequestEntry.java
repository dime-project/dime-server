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
