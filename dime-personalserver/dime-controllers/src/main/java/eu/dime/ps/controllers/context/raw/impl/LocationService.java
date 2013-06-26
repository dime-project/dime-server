package eu.dime.ps.controllers.context.raw.impl;

import java.util.Collection;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.Util;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.context.raw.data.CivilAddress;
import eu.dime.ps.controllers.context.raw.ifc.ILocationService;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.noauth.LocationServiceAdapter;
import eu.dime.ps.gateway.service.noauth.SocialRecommenderAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.Tenant;

public class LocationService implements ILocationService {
	
	private Logger logger = Logger.getLogger(LocationService.class);
	private IContextProcessor contextProcessor;
	private ServiceGateway serviceGateway;
	private AccountManager accountManager;
	private LocationServiceAdapter locationService;
	
	//private CloudServiceHelper helper = new CloudServiceHelper();
	
	public LocationService(IContextProcessor contextProcessor, ServiceGateway serviceGateway, AccountManager accountManager) {
		this.contextProcessor = contextProcessor;
		this.serviceGateway = serviceGateway;
		this.accountManager = accountManager;
	}
	
	private Account retrieveAccount(Tenant t, String serviceName) {
		TenantContextHolder.setTenant(t.getId());
		if (this.accountManager != null && this.serviceGateway != null) {
			if (serviceName.equalsIgnoreCase(LocationServiceAdapter.adapterName)) {
				Collection<Account> locAccts = null;
				try {
					locAccts = this.accountManager.getAllByType(serviceName);
				} catch (InfosphereException e) {
					logger.error(e.getMessage(),e);
					TenantContextHolder.unset();
					return null;
				}
				if (locAccts != null && locAccts.size() > 0) {
					Iterator<Account> it = locAccts.iterator();
					TenantContextHolder.unset();
					return it.next();
				}
			}
		} 
		TenantContextHolder.unset();
		return null;
	}
	
	private void setServiceReference(Account account, String serviceName) {
		if (serviceName.equalsIgnoreCase(LocationServiceAdapter.adapterName)) {
			try {
				this.locationService = (LocationServiceAdapter)this.serviceGateway.getServiceAdapter(account.asURI().toString());
			} catch (ServiceNotAvailableException e) {
				logger.error(e.getMessage(),e);
				this.locationService = null;
			} catch (ServiceAdapterNotSupportedException e) {
				logger.error(e.getMessage(),e);
				this.locationService = null;
			} catch (ClassCastException e) {
				logger.error(e.getMessage(),e);
				this.locationService = null;
			} 
		} 
	}
	
	@Override
	public IContextDataset getCivilAddress(Tenant t, IContextDataset dataset) {
		
		Account locationAccount = retrieveAccount(t,LocationServiceAdapter.adapterName);
		if (locationAccount != null) {
			setServiceReference(locationAccount, LocationServiceAdapter.adapterName);
		}
		
		if (this.locationService != null) {
			
			IEntity entity = null;
			
			IContextElement[] wfs = dataset.getContextElements(Factory.createScope(Constants.SCOPE_WF)); 
			IContextElement[] currentPos = dataset.getContextElements(Factory.createScope(Constants.SCOPE_LOCATION_POSITION)); 
			IContextElement[] currentPlace = dataset.getContextElements(Factory.createScope(Constants.SCOPE_CURRENT_PLACE)); 
			
			IContextElement wf = null;
			IContextElement place = null;
			IContextElement position = null;
			
			// consider only entry [0] of each scope
			if ((wfs != null) && (wfs.length > 0)) {
				wf = wfs[0];
				if (entity == null) entity = wf.getEntity();
			} 
			if ((currentPlace != null) && (currentPlace.length > 0)) {
				place = currentPlace[0];
				if (entity == null) entity = place.getEntity();
			}
			if ((currentPos != null) && (currentPos.length > 0)) {
				position = currentPos[0];
				if (entity == null) entity = position.getEntity();
			}
			
			CivilAddress civilAddress = getLocation(t,position,wf,place);
			if (civilAddress == null) {
				logger.debug("No civil address available");
				return IContextDataset.EMPTY_CONTEXT_DATASET;
			} else {
				IContextDataset ca = createCivilAddressDataset(entity,civilAddress);
				return ca;			
			}
		} else {
			logger.warn("LocationServiceAdapter not found for Tenant " + t.getName());
			return IContextDataset.EMPTY_CONTEXT_DATASET;
		}
		
	}
	
	private CivilAddress getLocation(Tenant t, IContextElement position, IContextElement wf, IContextElement place) {
		
		IEntity entity = null;
		
		String[] wfNames = null;
		int[] wfSignals = null;
		
		if (wf != null) {
			entity = wf.getEntity();
			IValue wfAddressValues = wf.getContextData().getValue(Factory.createScope(Constants.SCOPE_WF_LIST));
			IValue wfSignalsValues = wf.getContextData().getValue(Factory.createScope(Constants.SCOPE_WF_SIGNALS));
			if ((wfAddressValues != null) && (wfSignalsValues != null)) {
				wfNames = (String[])wfAddressValues.getValue();
				wfSignals = (int[])wfSignalsValues.getValue();
			}
		}
		
		String placeName = null;
		String placeId = null;
		
		if (place != null) {
			if (entity != null) entity = place.getEntity();
			IValue placeIdValue = place.getContextData().getValue(Factory.createScope(Constants.SCOPE_CURRENT_PLACE_ID));
			IValue placeNameValue = place.getContextData().getValue(Factory.createScope(Constants.SCOPE_CURRENT_PLACE_NAME));
			if ((placeIdValue != null) && (placeNameValue != null)) {
				placeId = (String)placeIdValue.getValue();
				placeName = (String)placeNameValue.getValue();
			}
		}
		
		Double latitude = null;
		Double longitude = null;
		
		if (position != null) {
			if (entity != null) entity = position.getEntity();
			IValue latitudeValue = position.getContextData().getValue(Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE));
			IValue longitudeValue = position.getContextData().getValue(Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE));
			if ((latitudeValue != null) && (longitudeValue != null)) {
				latitude = (Double)latitudeValue.getValue();
				longitude = (Double)longitudeValue.getValue();
			}
		}
		
		if (latitude == null && longitude == null && wfNames == null && wfSignals == null && placeId == null && placeName == null) return null;
		
		String body = createLocationPostBody(latitude,longitude,wfNames,wfSignals,placeId,placeName);
		
		String response = null;
		try {
			response = this.locationService.postRaw(body);
		} catch (Exception ex) {
			logger.error(ex.toString(),ex);
		}
		//String response = ""; //helper.post(this.serviceUrl,"",body);
		
		if (response != null) {
			return parseLocationServiceResponse(t,entity,response);
		}
		else return null;
		
	}

	public String createLocationPostBody(Double latitude, Double longitude,
			String[] wfAddresses, int[] wfSignals, String placeId, String placeName) {
		
		JSONObject w3cData = new JSONObject();
		
		try {
			
			if (latitude != null && longitude != null) {
				JSONObject position = new JSONObject();
				position.put("latitude",latitude.doubleValue());
				position.put("longitude",longitude.doubleValue());
				w3cData.put("location",position);
			}
			
			if (wfAddresses != null && wfSignals != null) {
				JSONArray wifi_towers = new JSONArray();
				for (int i=0; i<wfAddresses.length; i++) {
					JSONObject tower = new JSONObject();
					//tower.put("mac_address",convertWFAddress(wfAddresses[i]));
					tower.put("mac_address",wfAddresses[i].replaceAll(":","-"));
					tower.put("signal_strength",wfSignals[i]);
					wifi_towers.put(tower);
				}
				w3cData.put("wifi_towers",wifi_towers);
			}
			
			if (placeId != null && placeName != null) {
				JSONObject place = new JSONObject();
				place.put("placeId",placeId);
				place.put("placeName",placeName);
				w3cData.put("place",place);
			}
			
			return w3cData.toString();
			
		} catch (JSONException e) {
			logger.error(e.toString());
			return "";
		}
	}
	
	private CivilAddress parseLocationServiceResponse(Tenant t, IEntity entity, String response) {
		
		try {
			JSONObject jsonResp = new JSONObject(response);
			CivilAddress addr = new CivilAddress();
			JSONObject location = jsonResp.getJSONObject("location");
			JSONObject address = location.optJSONObject("address");
			addr.setCountry(address.optString("country"));
			addr.setCity(address.optString("city"));
			addr.setStreet(address.optString("street"));
			addr.setBuilding(address.optString("building"));
			addr.setFloor(address.optString("floor"));
			addr.setCorridor(address.optString("corridor"));
			addr.setRoom(address.optString("room"));
			JSONObject place = location.optJSONObject("place");
			if (place != null) {
				// For compatibility with PoC version of Situation Detector
				addr.setPlaceName(place.optString("placeName"));
				//
				// currentPlace updated ONLY IF it contains both id and name
				String placeName = place.optString("placeName");
				String placeId = place.optString("placeId");
				if (placeName != null && placeId != null && !placeId.equalsIgnoreCase("") && !placeId.equalsIgnoreCase("unknown") && !placeName.equalsIgnoreCase("")) {
					String[] params = {Constants.SCOPE_CURRENT_PLACE_ID, Constants.SCOPE_CURRENT_PLACE_NAME};
					String values[] = {placeId, placeName};
					try {
						// FIXME automatically detected currentPlace validity???
						this.contextProcessor.contextUpdate(t,Util.createComplexContextDataset(
								entity,Factory.createScope(Constants.SCOPE_CURRENT_PLACE), "Location Service", params, values, 600));
					} catch (ContextException e) {
						logger.error(e.toString());
					}
				}
				}
			JSONObject event = location.optJSONObject("event");
			if (event != null) {
				String eventName = event.optString("eventName");
				String eventId = event.optString("eventId");
				// currentEvent updated ONLY IF it contains both id and name
				if (eventName != null && eventId != null && !eventId.equalsIgnoreCase("") && !eventName.equalsIgnoreCase("")) {
					String[] params = {Constants.SCOPE_CURRENT_EVENT_ID, Constants.SCOPE_CURRENT_EVENT_NAME};
					String values[] = {eventId, eventName};
					try {
						// Note [TI]: if needed, currentEvent validity should be derived from currentPlace validity
						this.contextProcessor.contextUpdate(t,Util.createComplexContextDataset(
								entity, Factory.createScope(Constants.SCOPE_CURRENT_EVENT), "Location Service", params, values, 600));
					} catch (ContextException e) {
						logger.error(e.toString());
					}
				}
			}
			return addr;
		} catch (JSONException e) {
			logger.error(e.toString(),e);
		}
		return null;
	}
	
	private IContextDataset createCivilAddressDataset(IEntity entity, CivilAddress civilAddress) {
		String[] params = new String[9];
		String[] values = new String[9];
		params[0] = Constants.SCOPE_LOCATION_CIVILADDRESS_COUNTRY;
		values[0] = civilAddress.getCountry();
		params[1] = Constants.SCOPE_LOCATION_CIVILADDRESS_CITY;
		values[1] = civilAddress.getCity();
		params[2] = Constants.SCOPE_LOCATION_CIVILADDRESS_STREET;
		values[2] = civilAddress.getStreet();
		params[3] = Constants.SCOPE_LOCATION_CIVILADDRESS_BUILDING;
		values[3] = civilAddress.getBuilding();
		params[4] = Constants.SCOPE_LOCATION_CIVILADDRESS_FLOOR;
		values[4] = civilAddress.getFloor();
		params[5] = Constants.SCOPE_LOCATION_CIVILADDRESS_CORRIDOR;
		values[5] = civilAddress.getCorridor();
		params[6] = Constants.SCOPE_LOCATION_CIVILADDRESS_ROOM;
		values[6] = civilAddress.getRoom();
		params[7] = Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_TYPE;
		values[7] = civilAddress.getPlaceType();
		params[8] = Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME;
		values[8] = civilAddress.getPlaceName();
		return Util.createComplexContextDataset(entity, Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS),
				"Location Service", params, values, 1);
	}

}
