package eu.dime.commons.dto;

import javax.xml.bind.annotation.XmlAccessType;

@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Message<T> {

	@javax.xml.bind.annotation.XmlElement(name="meta")
	private Meta meta;
	
	@javax.xml.bind.annotation.XmlElement(name="data")
	private Data<T> data;
	
	public Message() {}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public Data<T> getData() {
		return data;
	}

	public void setData(Data<T> data) {
		this.data = data;
	}
	
}
