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

package eu.dime.ps.storage.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;

import eu.dime.ps.storage.entities.PersonMatch;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class PersonMatchEntry {

	
	@javax.xml.bind.annotation.XmlElement(name="guid")
	public String guid;
	
	@javax.xml.bind.annotation.XmlElement(name = "type")
	private String type = "personmatch";
	
	 @javax.xml.bind.annotation.XmlElement(name="sourceGUID")
	 private String source; 
	
	 @javax.xml.bind.annotation.XmlElement(name="matches")
	private List<PersonMatching> matches=null;
		

	public PersonMatchEntry(PersonMatch person) {
		this.setGuid(UUID.randomUUID().toString());
		matches = new ArrayList<PersonMatching> ();		
		this.setSource(person.getSource());
		PersonMatching newEntry = new PersonMatching();
		newEntry.setTarget(person.getTarget());			
		newEntry.setSimilarity(person.getSimilarityScore());
		newEntry.setStatus(person.getStatus());
		matches.add(newEntry);
			}		
		
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public List<PersonMatching> getMatches() {
		return matches;
	}
	public void setMatches(List<PersonMatching> matches) {
		this.matches = matches;
	}
	public String getGuid() {
		return guid;
	}
	
	public void addMatch(PersonMatch person){
		
		if(matches.isEmpty()) matches = new ArrayList<PersonMatching> ();	
		PersonMatching newEntry = new PersonMatching();
		newEntry.setTarget(person.getTarget());			
		newEntry.setSimilarity(person.getSimilarityScore());
		newEntry.setStatus(person.getStatus());
		matches.add(newEntry);
	}
	
	
	
	
	

	
	
	
	
}
