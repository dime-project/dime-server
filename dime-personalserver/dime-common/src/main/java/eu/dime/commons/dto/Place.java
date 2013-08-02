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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;


@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
public class Place extends Entry {

	@javax.xml.bind.annotation.XmlElement(name = "type")
	public String type = "place";
	
	@javax.xml.bind.annotation.XmlElement(name = "userId")
	public String userId;
	
	@javax.xml.bind.annotation.XmlElement(name = "position")
	public String position;
	
	//distance in m from point used for search (from the live context)
	@javax.xml.bind.annotation.XmlElement(name = "distance")
	public double distance;
	
	@javax.xml.bind.annotation.XmlElement(name = "address")
	public PlaceAddress address;
	
	@javax.xml.bind.annotation.XmlElement(name = "tags")
	public List<String> tags;
	
	@javax.xml.bind.annotation.XmlElement(name = "phone")
	public String phone;
	
	@javax.xml.bind.annotation.XmlElement(name = "url")
	public String url;
	
	@javax.xml.bind.annotation.XmlElement(name = "information")
	public String information;
	
	@javax.xml.bind.annotation.XmlElement(name = "YMRating")
	public double YMRating;
	
	@javax.xml.bind.annotation.XmlElement(name = "socialRecRating")
	public double socialRecRating;
	
	@javax.xml.bind.annotation.XmlElement(name = "userRating")
	public double userRating;
	
	@javax.xml.bind.annotation.XmlElement(name = "favorite")
	public boolean favorite;

	public PlaceAddress getAddress() {
		return address;
	}

	public void setAddress(PlaceAddress address) {
		this.address = address;
	}

	public double getSocialRecRating() {
		return socialRecRating;
	}

	public void setSocialRecRating(double socialRecRating) {
		this.socialRecRating = socialRecRating;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public double getYMRating() {
		return YMRating;
	}

	public void setYMRating(double yMRating) {
		YMRating = yMRating;
	}

	public double getUserRating() {
		return userRating;
	}

	public void setUserRating(double userRating) {
		this.userRating = userRating;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
}
