package eu.dime.ps.controllers.context.raw.data;

public class CivilAddress {
	
	private Double latitude;
	private Double longitude;
	
	private String country = "";
	private String city = "";
	private String street = "";
	private String building = "";
	private String floor = "";
	private String corridor = "";
	private String room = "";
	private String placeName = "";
	private String placeType = "";
	
	public CivilAddress() {}
	
	public CivilAddress(String propertyLine) {
		String[] tokens = propertyLine.split(";");
		if (tokens.length >= 11) {
			try {
				String lat = tokens[0];
				if (!lat.equalsIgnoreCase("")) this.latitude = Double.parseDouble(lat);
				String lon = tokens[1];
				if (!lon.equalsIgnoreCase("")) this.longitude = Double.parseDouble(lon);
				this.country = tokens[2];
				this.city = tokens[3];
				this.street = tokens[4];
				this.building = tokens[5];
				this.floor = tokens[6];
				this.corridor = tokens[7];
				this.room = tokens[8];
				this.placeType = tokens[9];
				this.placeName = tokens[10];
			} catch (Exception ex) {
				
			}
		}
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getCorridor() {
		return corridor;
	}
	public void setCorridor(String corridor) {
		this.corridor = corridor;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPlaceType() {
		return placeType;
	}
	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

}
