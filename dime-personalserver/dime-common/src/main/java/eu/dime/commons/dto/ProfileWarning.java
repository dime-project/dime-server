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
