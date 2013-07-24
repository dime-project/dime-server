package eu.dime.ps.gateway.service.noauth;

import eu.dime.commons.dto.Place;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;
import java.io.UnsupportedEncodingException;

public interface PlaceServiceAdapter extends ExternalServiceAdapter {
	
	public static String adapterName = "YellowMapPlaceService";

	public void setCredentials(String tenant);

	public void updatePlace(Place place) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException;
	
	public String getPlacesParameters(double longitude, double latitude, int radius) throws UnsupportedEncodingException;
	
	public String getPlaceDetailsParameters(String poiID) throws UnsupportedEncodingException;

}
