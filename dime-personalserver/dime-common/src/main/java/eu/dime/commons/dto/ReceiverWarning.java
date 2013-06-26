package eu.dime.commons.dto;


/**
 * Warning if information is shared too many (unusually) receivers
 * @author marcel
 *
 */
public class ReceiverWarning extends Warning{

	public static final String TYPE = "too_many_receivers";
	
	@javax.xml.bind.annotation.XmlElement(name="numberOfReceivers")
	private int numberOfReceivers;
	
	public ReceiverWarning(){
		this.type = TYPE;
	}

	public int getNumberOfReceivers() {
		return numberOfReceivers;
	}

	public void setNumberOfReceivers(int numberOfReceivers) {
		this.numberOfReceivers = numberOfReceivers;
	}
	
}

