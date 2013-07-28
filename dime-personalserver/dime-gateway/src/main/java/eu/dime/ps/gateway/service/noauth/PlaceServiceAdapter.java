/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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
