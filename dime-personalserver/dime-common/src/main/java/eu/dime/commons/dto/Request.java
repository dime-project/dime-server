package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Request<T> {
	
	@javax.xml.bind.annotation.XmlElement(name="request")
	private Message<T> message;

	public Request() {}

	public Request(Message<T> message) {
		this.message = message;
	}

	public Message<T> getMessage() {
		return message;
	}

	public void setMessage(Message<T> message) {
		this.message = message;
	}
	
}