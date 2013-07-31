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

package eu.dime.ps.gateway.service.external.oauth;

import java.util.Map;

import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Responsible for implementing the concrete LinkedIn adapter
 * 
 * @author <a href="mailto:schwarte@wiwi.uni-siegen.de"> Philipp Schwarte
 *         (pschwarte)</a>
 */
public class LinkedInServiceAdapter extends OAuthServiceAdapter implements ExternalServiceAdapter {

	public static final String NAME = "LinkedIn";
	private static final String API_URL = "http://api.linkedin.com/v1";

	/**
	 * @throws ServiceNotAvailableException
	 */
	public LinkedInServiceAdapter(Tenant localTenant) throws ServiceNotAvailableException {
		super(LinkedInApi.class, localTenant);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.dime.ps.communications.services.ServiceAdapter#getAdapterName()
	 */
	@Override
	public String getAdapterName() {
		return LinkedInServiceAdapter.NAME;
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		AttributeMap attributeMap = new AttributeMap();
		String genericAttribute = attributeMap.getAttribute(attribute);
		Map<String, String> ids = attributeMap.extractIds(genericAttribute,
				attribute);
		String fields = "";
		
		if (getAccessToken() == null) {
			throw new InvalidLoginException();
		}
		
		// Retrieve Attributes
		// Profiles
		// Attribute: /profiles/@me/@all
		if (genericAttribute.equals(AttributeMap.PROFILE_MYDETAILS)) {
			fields = "/people/~:(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}
		// Attribute: /profiles/{pid}/@all
		else if (genericAttribute.equals(AttributeMap.PROFILE_DETAILS)) {
			fields = "/people/id=" + ids.get(AttributeMap.USER_ID)
				+ ":(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}
		
		// Profile attributes (same retrieved info as profiles)
		// Attribute: /profileattribute/@me/@all
		else if (genericAttribute.equals(AttributeMap.PROFILEATTRIBUTE_MYDETAILS)) {
			fields = "/people/~:(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}
		// Attribute: /profileattribute/{pid}/@all
		else if (genericAttribute.equals(AttributeMap.PROFILEATTRIBUTE_DETAILS)) {
			fields = "/people/id=" + ids.get(AttributeMap.USER_ID)
				+ ":(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}

		// Connections
		// Attribute: /persons/@me/@all
		else if (genericAttribute.equals(AttributeMap.FRIEND_ALL)) {
			fields = "/people/~/connections"
				+ ":(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}
		// Attribute: /persons/@me/{pid}
		else if (genericAttribute.equals(AttributeMap.FRIEND_DETAILS)) {
			fields = "/people/id=" + ids.get(AttributeMap.USER_ID)
				+ ":(id,first-name,last-name,location:(name),picture-url,summary,positions,phone-numbers,im-accounts,date-of-birth,main-address,interests,email-address)";
		}

		// Status updates
		// Attribute: /livepost/@me/@all
		else if (genericAttribute.equals(AttributeMap.LIVEPOST_ALLMINE)) {
			fields = "/people/~:(current-share)";
		}
		// Attribute: /livepost/{pid}/@all
		else if (genericAttribute.equals(AttributeMap.LIVEPOST_ALLUSER)) {
			fields = "/people/id=" + ids.get(AttributeMap.USER_ID) + ":(current-share)";
		}
		// Attribute: /livepost/@all
		else if (genericAttribute.equals(AttributeMap.LIVEPOST_ALL)) {
			fields = "/people/~/network?type=STAT";
		}
		
		// Still no attribute... so it's not supported.
		if (fields.equals("")) {
			throw new AttributeNotSupportedException(attribute, this);
		}

		// Open connection and request data
		Response response = sendRequest(Verb.GET, LinkedInServiceAdapter.API_URL + fields);
		ServiceResponse[] serviceResponse = new ServiceResponse[1];
		serviceResponse[0] = new ServiceResponse(ServiceResponse.XML, attribute, fields, response.getBody());
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
