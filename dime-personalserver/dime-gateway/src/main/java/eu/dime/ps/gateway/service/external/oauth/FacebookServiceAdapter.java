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

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.RateLimitException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ResourceAttributes;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.storage.entities.Tenant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.scribe.builder.api.FacebookApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Response;
import org.scribe.model.Verb;

/**
 * @author Sophie.Wrobel
 */
public class FacebookServiceAdapter extends OAuthServiceAdapter implements ExternalServiceAdapter {

	public static final String NAME = "Facebook";
	private static final String RESOURCE_URL = "https://graph.facebook.com";
	
	public FacebookServiceAdapter(Tenant tenant) throws ServiceNotAvailableException {
		super(FacebookApi.class, tenant);
	}

	@Override
	public String getAdapterName() {
		return FacebookServiceAdapter.NAME;
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		String fields = "";
		ResourceAttributes parts = new ResourceAttributes(attribute);

		// Profiles
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_PROFILE)) {
			// /profileattribute/@me/@all
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/me/profile?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
		}
			
		// Profiles / Profile attributes - handled same for twitter
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_PROFILE) || parts.getResourceType().equals(
				ResourceAttributes.ATTR_PROFILEATTRIBUTE)) {
			// /profileattribute/@me/@all
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) { 
				fields = "/me?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
			// /profileattribute/{pid}/@all
			else if (parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/"+ parts.getPerson() +"?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
		}

		// Connections
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_PERSON)) {

			// /person/@me/@all
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/me/friends?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
			// /person/@me/{pid}
			else if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)) {
				fields = "/" + parts.getQueryObject() + "/friends?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
		}

		// Status updates
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_LIVEPOST)) {

			// /livepost/@me/@all
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/me/statuses?access_token=" + getAccessToken().getToken();
			}

			// /livepost/{pid}/@all
			else if (!parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/" + parts.getPerson() + "/statuses?access_token=" + getAccessToken().getToken();
			}

			// /livepost/@all
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/me/home?access_token=" + getAccessToken().getToken();
			}
		}
		
		// Events
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_EVENT)) {

			// /events/@me/{eid}/{pid}
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& !parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/"+parts.getQueryObject() +"?fields=id,name,first_name,middle_name,last_name,email,gender,username,bio,birthday,hometown,location,work,picture,link,website,interests&access_token=" + getAccessToken().getToken();
			}
			
			// /events/@me/{eid}/@all
			else if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& !parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/"+parts.getQueryObject()+"/attending?access_token=" + getAccessToken().getToken();
			}
			
			// /events/@me/{eid}
			else if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& !parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/"+parts.getQueryObject()+"?access_token=" + getAccessToken().getToken();
			}
			
			// /events/@me/@all
			else if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				fields = "/me/events?access_token=" + getAccessToken().getToken();
			}
		}

		// Still no attribute... so it's not supported.
		if (fields.equals("")) {
			throw new AttributeNotSupportedException(attribute, this);
		}

		// Open connection and request data
		try {
			String responseBody = sendRequest(Verb.GET, FacebookServiceAdapter.RESOURCE_URL+fields).getBody();
			ServiceResponse response = new ServiceResponse(ServiceResponse.JSON, attribute, fields, responseBody);
			return new ServiceResponse[] { response };
		} catch (OAuthException e) {
			throw new ServiceNotAvailableException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.gateway.service.IPublicServiceAdapter#_set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidDataException {

		String fields = "";
		String[] params = new String[2];
		ResourceAttributes parts = new ResourceAttributes(attribute);

		// Profiles - handled same as profile attfributes
		if (parts.getResourceType().equals(ResourceAttributes.ATTR_LIVEPOST)) {
			// /livepost/@me
			if (parts.getPerson().equals(ResourceAttributes.ABBR_ME)
					&& parts.getQueryObject().equals(ResourceAttributes.ABBR_ALL)) {
				
				// Currently supports only status update
				fields = "/me/statuses";
				Status livepost = (Status) value;

				Collection<Status> resources = new ArrayList<Status>();
				resources.add(livepost);
				String text = getTransformer().serialize(resources, this.getIdentifier(), fields);
				if (text == null) {
					throw new InvalidDataException("The livepost text is empty, nothing to say?");
				}
				params[0] = text;

				// TODO: Support sharing to specific group
				
				// TODO: Support location
			}
		}
		
		// Still no attribute... so it's not supported.
		if (fields.equals("")) {
			throw new AttributeNotSupportedException(attribute, this);
		}

		// Open connection and send data
		try {
			Map<String, String> parameters = new HashMap<String, String>();
			if (params[0] != null)
				parameters.put("message", params[0]);
			
			Response response = sendRequest(Verb.POST, FacebookServiceAdapter.RESOURCE_URL+fields, parameters);
	
			// Check for 403 (too many status posts simultaneously - e.g. double-posting!)
			if (response.getCode() == 403) {
				throw new RateLimitException("You are trying to post too quickly. Wait a few seconds and try again.");
			}
		} catch (OAuthException e) {
			throw new ServiceNotAvailableException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.gateway.service.IPublicServiceAdapter#_delete(java.lang.String)
	 */
	@Override
	public void _delete(String attribute)
			throws AttributeNotSupportedException, ServiceNotAvailableException {
		// Not supported at this time
		throw new AttributeNotSupportedException(attribute, this);
	}

}
