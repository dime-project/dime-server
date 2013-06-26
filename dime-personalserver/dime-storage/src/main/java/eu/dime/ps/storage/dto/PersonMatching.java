package eu.dime.ps.storage.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

import eu.dime.ps.storage.entities.PersonMatch;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class PersonMatching  {
	 @javax.xml.bind.annotation.XmlElement(name="target")   
	 private String target;	    
	 @javax.xml.bind.annotation.XmlElement(name="similarity")  
	 private Double similarity;	    
	 @javax.xml.bind.annotation.XmlElement(name="status")   
	 private String status; 	 
	 
	  
		public String getTarget() {
			return target;
		}
		public void setTarget(String target) {
			this.target = target;
		}
		public Double getSimilarity() {
			return similarity;
		}
		public void setSimilarity(Double similarity) {
			this.similarity = similarity;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	   
}
