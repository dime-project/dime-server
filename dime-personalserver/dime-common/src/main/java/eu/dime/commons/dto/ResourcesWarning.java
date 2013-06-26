/**
 * 
 */
package eu.dime.commons.dto;


/**
 * @author Marcel Heupel (heupel@wiwi.uni-siegen.de)
 *
 */
public class ResourcesWarning extends Warning {

	public static final String TYPE = "too_many_resources";
	
	@javax.xml.bind.annotation.XmlElement(name="numberOfResources")
	private int numberOfResources;
	
	public ResourcesWarning(){
		this.type = TYPE;
	}

	public int getNumberOfResources() {
		return numberOfResources;
	}

	public void setNumberOfResources(int numberOfResources) {
		this.numberOfResources = numberOfResources;
	}
	
}
