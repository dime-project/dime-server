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
public abstract class Warning {
	
	@javax.xml.bind.annotation.XmlElement(name="warningLevel")
	protected double warningLevel;
	@javax.xml.bind.annotation.XmlElement(name="type")
	protected String type;
	
	public double getWarningLevel() {
		return warningLevel;
	}
	public void setWarningLevel(double warningLevel) {
		this.warningLevel = warningLevel;
	}
	public String getType() {
		return type;
	}
//	public void setType(String type) {
//		this.type = type;		
//	}

	@Override
	public String toString(){
		String string = "Warning: "+type+"\n"
			+ "Level: "+warningLevel;
		
		return string;	
	}


}

