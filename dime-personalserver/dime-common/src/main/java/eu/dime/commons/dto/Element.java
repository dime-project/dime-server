package eu.dime.commons.dto;

import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Element extends Entry {
	
	@javax.xml.bind.annotation.XmlElement(name="userId")
	public String userId;

	public String getUserID() {
		return userId;
	}

	public void setUserID(String userID) {
		this.userId = userID;
	}


}
