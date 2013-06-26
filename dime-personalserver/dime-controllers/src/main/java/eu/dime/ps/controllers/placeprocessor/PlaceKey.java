package eu.dime.ps.controllers.placeprocessor;

public class PlaceKey {
	
	private Long tenant;
	private String guid;
	
	public PlaceKey(Long tenantId, String guid) {
		this.tenant = tenantId;
		this.guid = guid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PlaceKey) {
			PlaceKey p = (PlaceKey)o;
			return p.guid.equalsIgnoreCase(this.guid) && p.tenant.doubleValue() == this.tenant.doubleValue();
		} else return false;
	}
	
	@Override
	public int hashCode() {
		return tenant.intValue() + guid.hashCode();
	}

}
