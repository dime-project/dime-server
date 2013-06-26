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

