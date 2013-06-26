package eu.dime.commons.dto;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;


/**
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 */
@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class TrustWarning extends Warning{

	public static final String TYPE = "untrusted";
	
	@javax.xml.bind.annotation.XmlElement(name="untrustedAgents")
	@javax.xml.bind.annotation.XmlList
	private List<String> untrustedAgents;
	
	@javax.xml.bind.annotation.XmlElement(name="privateResources")
	@javax.xml.bind.annotation.XmlList
	private List<String> privateResources;
	
	@javax.xml.bind.annotation.XmlElement(name="privacyValue")
	private double privacyValue;
	
	@javax.xml.bind.annotation.XmlElement(name="trustValue")
	private double trustValue;



	
	public TrustWarning(){
		this.type = TYPE;
	}
	
	public List<String> getUntrustedAgents() {
		return untrustedAgents;
	}

	public void setUntrustedAgents(List<String> untrustedAgents) {
		this.untrustedAgents = untrustedAgents;
	}

	public List<String> getPrivateResources() {
		return privateResources;
	}

	public void setPrivateResources(List<String> privateResources) {
		this.privateResources = privateResources;
	}	
	
	public String getType(){
		return TYPE;
	}
	
	public double getPrivacyValue() {
		return privacyValue;
	}

	public void setPrivacyValue(double privacyValue) {
		this.privacyValue = privacyValue;
	}

	public double getTrustValue() {
		return trustValue;
	}

	public void setTrustValue(double trustValue) {
		this.trustValue = trustValue;
	}
	
	public void addAgent(String agent){
		if (this.untrustedAgents == null){
			this.untrustedAgents = new LinkedList<String>();
		}
		this.untrustedAgents.add(agent);
	}
	
	public void addAllAgents(Collection<String> agents) {
		if (this.untrustedAgents == null){
			this.untrustedAgents = new LinkedList<String>();
		}
		this.untrustedAgents.addAll(agents);		
	}
	
	public void addResource(String resource){
		if(this.privateResources == null){
			this.privateResources = new LinkedList<String>();
		}
		this.privateResources.add(resource);
	}
	
	public void addAllResources(Collection<String> ressources){
		if(this.privateResources == null){
			this.privateResources = new LinkedList<String>();
		}
		this.privateResources.addAll(ressources);
	}
	
	@Override
	public String toString() {
		String string = super.toString();
		string += "\nUntrusted Persons:";
		for (String agent: untrustedAgents){
			string += "\n"+agent;
		}
		for (String ressource: privateResources){
			string += "\n"+ressource;
		}
		return string;
	}
}

