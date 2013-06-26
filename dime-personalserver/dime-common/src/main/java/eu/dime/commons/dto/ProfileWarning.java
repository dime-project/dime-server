package eu.dime.commons.dto;

import java.util.LinkedList;
import java.util.List;


/**
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 *
 */
public class ProfileWarning extends Warning {
	
	public static final String TYPE = "unshared_profile";
	
	@javax.xml.bind.annotation.XmlElement(name="personGuids")
	@javax.xml.bind.annotation.XmlList
	private List<String> personGuids;
	
	public ProfileWarning(){
		this.type = TYPE;
	}

	public List<String> getPersonGuids() {
		return personGuids;
	}

	public void setPersonGuids(List<String> personGuids) {
		this.personGuids = personGuids;
	}
	
	public String getType(){
		return TYPE;
	}
	
	public void addProfile(String profile){
		if (this.personGuids == null){
			this.personGuids = new LinkedList<String>();
		}
		this.personGuids.add(profile);
	}
}
