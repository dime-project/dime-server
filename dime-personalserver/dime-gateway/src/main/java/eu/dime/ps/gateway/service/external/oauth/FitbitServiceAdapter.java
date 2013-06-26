package eu.dime.ps.gateway.service.external.oauth;

import java.util.Map;

import org.scribe.builder.api.FitbitApi;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;

/**
 * Service adapter enabling the access to the Fitbit API.
 * 
 * @author Ismael Rivera
 * @author Keith Cortis
 */
public class FitbitServiceAdapter extends OAuthServiceAdapter implements ExternalServiceAdapter {

	public static final String NAME = "Fitbit";
	private final String RESOURCE_URL = "http://api.fitbit.com/1/user";

	/**
	 * @throws ServiceNotAvailableException
	 */
	public FitbitServiceAdapter() throws ServiceNotAvailableException {
		super(FitbitApi.class);
	}
	
	@Override
	public String getAdapterName() {
		return FitbitServiceAdapter.NAME;
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		AttributeMap attributeMap = new AttributeMap();
		String genericAttribute = attributeMap.getAttribute(attribute);
		Map<String, String> ids = attributeMap.extractIds(genericAttribute, attribute);
		String path = "";
		
		if (getAccessToken() == null) {
			throw new InvalidLoginException();
		}
		
		// Attribute: /profile/@me/@all
		if (genericAttribute.equals(AttributeMap.PROFILE_MYDETAILS)) {
			path = "/-/profile.xml";
		}
		// Attribute: /profile/{pid}/@all
		else if (genericAttribute.equals(AttributeMap.PROFILE_DETAILS)) {
			path = "/"+ids.get(AttributeMap.USER_ID)+"/profile.xml";
		}
		
		// Attribute: /person/@me/@all
		else if (genericAttribute.equals(AttributeMap.FRIEND_ALL)) {
			path = "/-/friends.xml";
		}
		// Attribute: /person/@me/{pid}
		else if (genericAttribute.equals(AttributeMap.FRIEND_DETAILS)) {
			path = "/"+ids.get(AttributeMap.USER_ID)+"/friends.xml";
		}
		
		// Attribute: /activity/@me/@all
		else if (genericAttribute.equals(AttributeMap.ACTIVITY_ALL)) {
			path = "/-/activities/recent.xml";
		}
		// Attribute: /activity/@me/{pid}
		else if (genericAttribute.equals(AttributeMap.ACTIVITY_DETAILS)) {
			path = "/???"; //according to API this call is not available, will be certain once I test it
		}
		
		// No path declared... so this attribute is not supported.
		if (path.equals("")) {
			throw new AttributeNotSupportedException(attribute, this);
		}

		// Open connection and request data
		Response response = sendRequest(Verb.GET, this.RESOURCE_URL + path);
		ServiceResponse[] serviceResponse = new ServiceResponse[1];
		serviceResponse[0] = new ServiceResponse(ServiceResponse.XML, attribute, path, response.getBody());
		return serviceResponse;
	}

	@Override
	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidDataException {
		throw new AttributeNotSupportedException("Setting " + attribute + " is not supported.", this);
	}

	@Override
	public void _delete(String attribute)
			throws AttributeNotSupportedException, ServiceNotAvailableException {
		throw new AttributeNotSupportedException("Deleting " + attribute + " is not supported.", this);
	}

}
