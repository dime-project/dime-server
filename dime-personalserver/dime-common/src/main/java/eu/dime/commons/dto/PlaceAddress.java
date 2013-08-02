/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
