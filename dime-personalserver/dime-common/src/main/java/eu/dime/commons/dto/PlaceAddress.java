package eu.dime.commons.dto;

public class PlaceAddress {

	@javax.xml.bind.annotation.XmlElement(name = "formatted")
	public String formatted;
	
	@javax.xml.bind.annotation.XmlElement(name = "streetAddress")
	public String streetAddress;
	
	@javax.xml.bind.annotation.XmlElement(name = "locality")
	public String locality;
	
	@javax.xml.bind.annotation.XmlElement(name = "region")
	public String region;
	
	@javax.xml.bind.annotation.XmlElement(name = "postalCode")
	public String postalCode;
	
	@javax.xml.bind.annotation.XmlElement(name = "country")
	public String country;
	
	
	public String getFormatted() {
		return formatted;
	}
	
	public void setFormatted(String formatted) {
		this.formatted = formatted;
	}
	
	public String getStreetAddress() {
		return streetAddress;
	}
	
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	public String getRegion() {
		return region;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getPostalCode() {
		return postalCode;
	}
	
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}
	
}
