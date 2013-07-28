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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.dto.Place;
import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;

public class YMServiceAdapter extends ServiceAdapterBase implements /*ExternalServiceAdapter*/ PlaceServiceAdapter {

	public static String adapterName = "YellowMapPlaceService";
	private static final Logger logger = LoggerFactory.getLogger(YMServiceAdapter.class);
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String AGREEDTC = "agreedTC";
	
	private YMServiceWrapper ymServiceWrapper;
	
	public YMServiceAdapter() throws ServiceNotAvailableException {
		super();
		
		this.sadapter.addSetting(new SAdapterSetting(YMServiceAdapter.AGREEDTC, true, SAdapterSetting.BOOLEAN, "false"));
		this.sadapter.addSetting(new SAdapterSetting(YMServiceAdapter.USERNAME, false, SAdapterSetting.STRING, ""));
		this.sadapter.addSetting(new SAdapterSetting(YMServiceAdapter.PASSWORD, false, SAdapterSetting.PASSWORD, ""));
		
		try {
			this.ymServiceWrapper = YMServiceWrapper.getInstance(this.policyManager);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			throw new ServiceNotAvailableException(e);
		}
	}

	@Override
	public String getAdapterName() {
		return adapterName;
	}

	@Override
	public Boolean isConnected() {
		if(this.ymServiceWrapper != null)
			return new Boolean(this.ymServiceWrapper.isConnected());
		return false;
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException, ServiceException {

		String response = null;

		// place info, either details or all places according to the parameters
		if(attribute.startsWith("&QT=10&DetailInfoView=1&Ebinr=") ||
				attribute.contains("&LocX=") && attribute.contains("&LocY=") && attribute.contains("&BC=")) {
			try {
				response = this.ymServiceWrapper.getPlaces(attribute, this.sadapter.getSetting(YMServiceAdapter.USERNAME), this.sadapter.getSetting(YMServiceAdapter.PASSWORD));
			} catch (ParserConfigurationException e) {
				logger.error(e.getMessage(), e);
			} catch (TransformerException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		ServiceResponse[] sr = new ServiceResponse[1];
		sr[0] = new ServiceResponse(ServiceResponse.XML, attribute, null, null, response);
		return sr;
	}

	public void setCredentials(String user) {
		// The my yellowmap user
		this.sadapter.updateSetting(YMServiceAdapter.USERNAME, user);
		this.sadapter.updateSetting(YMServiceAdapter.PASSWORD, YMUtils.USR_PWD);
	}
	
	/**
	 * Update the personal information (favorit, rating) on the given place
	 * @param place
	 * @throws ServiceNotAvailableException 
	 * @throws UnsupportedEncodingException 
	 */
	public void updatePlace(Place place) throws UnsupportedEncodingException, ServiceNotAvailableException, ServiceException {
		this.ymServiceWrapper.updatePOI(place, this.sadapter.getSetting(YMServiceAdapter.USERNAME), this.sadapter.getSetting(YMServiceAdapter.PASSWORD));
	}
	
	public String getPlacesParameters(double longitude, double latitude, int radius) throws UnsupportedEncodingException {
		String parameter = "&BC=" + YMUtils.CATEGORY_DIME + "&LocX=" + longitude + "&LocY=" + latitude;
		
		if(radius > 0)
			parameter += "&Radius=" + radius;
		return parameter;
	}
	
	public String getPlaceDetailsParameters(String poiID) throws UnsupportedEncodingException {
		return "&QT=10&DetailInfoView=1&Ebinr=" + poiID;
	}
}
