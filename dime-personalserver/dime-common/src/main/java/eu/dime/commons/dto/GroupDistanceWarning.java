package eu.dime.commons.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;


/**
 * 
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 */
@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class GroupDistanceWarning extends Warning {

	public static final String TYPE = "disjunct_groups";
	
	@javax.xml.bind.annotation.XmlElement(name="previousSharedGroups")
	@javax.xml.bind.annotation.XmlList
	private List<String> previousSharedGroups;
	
	@javax.xml.bind.annotation.XmlElement(name="concernedPersons")
	@javax.xml.bind.annotation.XmlList
	private List<String> concernedPersons;
	
	@javax.xml.bind.annotation.XmlElement(name="concernedResources")
	@javax.xml.bind.annotation.XmlList
	private List<String> concernedResources;

	public GroupDistanceWarning(){
		this.type = TYPE;
		previousSharedGroups = new LinkedList<String>();
		concernedPersons = new LinkedList<String>();
		concernedResources = new LinkedList<String>();
	}

	public List<String> getPreviousSharedGroups() {
		return previousSharedGroups;
	}

	public void setPreviousSharedGroups(List<String> previousSharedGroups) {
		this.previousSharedGroups = previousSharedGroups;
	}

	public List<String> getConcernedPersons() {
		return concernedPersons;
	}

	public void setConcernedPersons(List<String> concernedPersons) {
		this.concernedPersons = concernedPersons;
	}

	public List<String> getConcernedResources() {
		return concernedResources;
	}

	public void setConcernedResources(List<String> concernedResources) {
		this.concernedResources = concernedResources;
	}
	
	public void addPerson(String concernedPerson){
		if (this.concernedPersons ==  null){
			this.concernedPersons = new LinkedList<String>();
		}
		this.concernedPersons.add(concernedPerson);
	}
	
	public void addGroup(String group){
		if (previousSharedGroups == null){
			this.previousSharedGroups = new LinkedList<String>();
		}
		this.previousSharedGroups.add(group);
	}
	
	public void addResource(String resource){
		if (this.concernedResources == null){
			this.concernedResources = new LinkedList<String>();
		}
		this.concernedResources.add(resource);
	}
	
	@Override
	public String toString() {
		String string = super.toString();
		string += "\nRecipients:";
		for (String person : concernedPersons){
			string += "\n"+person;
		}
		string += "\nConcerned Ressources";
		for (String ressource : concernedResources){
			string += "\n"+ressource;
		}
		string += "\nFiles belong to Groups:";
		for (String group : previousSharedGroups){
			string += "\n" + group;
		}
		return string;
	}
}