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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.scribe.builder.api.TwitterApi;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.RateLimitException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ResourceAttributes;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;
import eu.dime.ps.gateway.transformer.FormatUtils;
import eu.dime.ps.gateway.transformer.TransformerException;
import eu.dime.ps.semantic.model.dlpo.Status;
import eu.dime.ps.storage.entities.Tenant;

/**
 * @author Sophie.Wrobel
 *
 */
public class TwitterServiceAdapter extends OAuthServiceAdapter implements ExternalServiceAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(TwitterServiceAdapter.class);

 	public static final String NAME = "Twitter";
	private final String apiUrl;

	private static final int MAX_LENGTH = 140; // Twitter does not allow messages longer than 140 characters
	private static final int TWITTER_MAX_RATE_LIMIT = 160;

	private String currUserId;
	
	/**
	 * @throws ServiceNotAvailableException
	 */
	public TwitterServiceAdapter(Tenant localTenant)
			throws ServiceNotAvailableException {
		super(TwitterApi.class, localTenant);
		this.apiUrl = this.policyManager.getPolicyString("resourceUrl", TwitterServiceAdapter.NAME);
		this.MAX_RATE_LIMIT = TWITTER_MAX_RATE_LIMIT;
		this.rateLimit = MAX_RATE_LIMIT;
	}
	
	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.ServiceAdapter#getAdapterName()
	 */
	@Override
	public String getAdapterName() {
		return TwitterServiceAdapter.NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.ServiceAdapter#set(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void set(String attribute, Object value)
			throws AttributeNotSupportedException, ServiceNotAvailableException,
			InvalidDataException {

		String fields = "";
		String[] params = new String[2];
		ResourceAttributes parts = new ResourceAttributes(attribute);

		
		// Profiles - handled same as profile attributes
		if (ResourceAttributes.ATTR_LIVEPOST.equals(parts.getResourceType())) {
			// /livepost/@me
			if (ResourceAttributes.ABBR_ME.equals(parts.getPerson())
					&& ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
				
				// Currently supports only tweet update
				fields = "/statuses/update.json";
				Status livepost = (Status) value;

				Collection<Status> resources = new ArrayList<Status>();
				resources.add(livepost);
				String text = getTransformer().serialize(resources, this.getIdentifier(), fields);
				if (text == null) {
					throw new InvalidDataException("The livepost text is empty, nothing to say?");
				} else if (text.length() > MAX_LENGTH) {
					throw new InvalidDataException("The length of the livepost text is too long, no more than "+MAX_LENGTH+" characters are permitted.");					
				}
				params[0] = text;

				// TODO: support reply to tweet
				/*Comment comment = (Comment) value;
				payload = "status=" + comment.getAllComment_as().asList().get(0);
				payload += "in_reply_to_status_id" + ...*/
			
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
				parameters.put("status", params[0]);
			if (params[1] != null)
				parameters.put("in_reply_to_status_id", params[1]);
			
			Response response = sendRequest(Verb.POST, this.apiUrl+fields, parameters);
	
			// Check for 403 (too many tweet posts simultaneously - e.g. double-posting!)
			if (response.getCode() == 403) {
				throw new RateLimitException("You are trying to post too quickly. Wait a few seconds and try again.");
			}
		} catch (OAuthException e) {
			throw new ServiceNotAvailableException(e);
		}
	}
	
	/**
	 * Extract the ids from a json response
	 * 
	 * @param jsonInput
	 * @return Array with comma-separated lists of ids with at most 100 ids per array element
	 */
	private List<String> extractIds(String jsonInput) {
		JSONArray ids = (JSONArray) JSONSerializer.toJSON(jsonInput);
		JSONArray idList = ids;
		List<String> ret = new ArrayList<String>();
		while (ids.size() > 0) {
			while (ids.size() > 100) {
				ids.remove(ids.size()-1);
			}
			while (idList.size() > ids.size()) {
				idList.remove(0);
			}
			ret.add(ids.join(","));
			ids = idList;
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.opensocial.OpenSocialServiceAdapter#getRaw(java.lang.String)
	 */
	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		try {
	
			String fields = "";
			ResourceAttributes parts = new ResourceAttributes(attribute);
			
			// Make sure we know who the current user is
			if (this.currUserId == null) {
				ServiceResponse[] serviceResponse = new ServiceResponse[2];    
				
				// Open connection and request data
				fields = "/account/verify_credentials.json";
				Response response = sendRequest(Verb.GET, this.apiUrl+fields);
				serviceResponse[0] = new ServiceResponse(ServiceResponse.JSON, attribute,
						this.apiUrl+fields, response.getBody());
				
				// Extract current User ID
				JSONObject json = (JSONObject) JSONSerializer.toJSON(response.getBody());
				this.currUserId  = json.getString("id");
			}
	
			// Profiles / Profile attributes - handled same for twitter
			if (ResourceAttributes.ATTR_PROFILE.equals(parts.getResourceType())
					|| ResourceAttributes.ATTR_PROFILEATTRIBUTE.equals(parts.getResourceType())) {
				
				// /profileattribute/@me/@all
				if (ResourceAttributes.ABBR_ME.equals(parts.getPerson())
						&& ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
					
					ServiceResponse[] serviceResponse = new ServiceResponse[1];
	                
					// Open connection and request data
					fields = "/account/verify_credentials.json";
					Response response = sendRequest(Verb.GET, this.apiUrl+fields);
					serviceResponse[0] = new ServiceResponse(ServiceResponse.JSON, attribute,
							this.apiUrl+fields, response.getBody());

					// FIXME now response is JSON, so the merge (mergeProfileWithSuggestions) XML method
					// cannot be used anymore (_get(attribute, returnType)).
					// for now no suggestions are retrieved until fixed.
//					// Open connection and request data	
//					fields = "/users/suggestions.json";
//					response = sendRequest(Verb.GET, this.apiUrl+fields);
//					serviceResponse[1] = new ServiceResponse(ServiceResponse.JSON, attribute,
//							this.apiUrl+fields, response.getBody());
					
					return serviceResponse;
					
				}
				// /profileattribute/{pid}/@all
				else if (ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
					fields = "/users/lookup.json?cursor=-1&user_id="+parts.getPerson();
				}
			}
	
			// Connections
			if (ResourceAttributes.ATTR_PERSON.equals(parts.getResourceType())) {
	
				// /person/@me/@all
				if (ResourceAttributes.ABBR_ME.equals(parts.getPerson())
						&& ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
					fields = "/friends/list.json?skip_status=true";
				}
				
				// /person/@me/{pid}
				else if (ResourceAttributes.ABBR_ME.equals(parts.getPerson())) {
					fields = "/users/lookup.json?cursor=-1&user_id="+parts.getQueryObject();
				}
			}
	
			// Status updates
			if (ResourceAttributes.ATTR_LIVEPOST.equals(parts.getResourceType())) {
	
				// /livepost/@me/@all
				if (ResourceAttributes.ABBR_ME.equals(parts.getPerson())
						&& ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
					fields = "/statuses/user_timeline.json?count=200&include_entities=true&include_rts=true";
				}
	
				// /livepost/{pid}/@all
				else if (!ResourceAttributes.ABBR_ME.equals(parts.getPerson())
						&& ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
					fields = "/users/lookup.json?cursor=-1&user_id=" + parts.getPerson();
				}
	
				// /livepost/@all
				if (ResourceAttributes.ABBR_ALL.equals(parts.getPerson())) {
					fields = "/statuses/home_timeline.json?count=200&include_entities=true";
				}
			}
	
			// Still no attribute... so it's not supported.
			if (fields.equals("")) {
				throw new AttributeNotSupportedException(attribute, this);
			}
	
			// Open connection and request data
			String responseBody = sendRequest(Verb.GET, this.apiUrl+fields).getBody();
			
			// Connections - to retrieve more than 20 contacts, up to 300 contacts in a 15 minute time frame
			if (ResourceAttributes.ATTR_PERSON.equals(parts.getResourceType()) 
				    && ResourceAttributes.ABBR_ME.equals(parts.getPerson())
				    && ResourceAttributes.ABBR_ALL.equals(parts.getQueryObject())) {
				JSONObject json = null;
				JSONArray contactsArray = new JSONArray();
				String cursor = null;
				long nextCursor = 0;
			    
				try {
		    		// only a few users are returned on each call, several calls using
		    		// the next_cursor are needed to fetch all contacts
			    	do {
			    		json = (JSONObject) JSONSerializer.toJSON(responseBody);
		    			
			    		// add users to array from the response
			    		if (json.containsKey("errors")) {
			    			// TODO if would be nice to check the code of the error, but most of the times
			    			// it will be a "Rate limit exceeded", thus we log it and at least return the
			    			// contacts that were already fetched
			    			// Note: not throwing exception leads to an inconsistent state of the account integration
//			    			logger.error("Couldn't fetch all friends from service: "+json);
//			    			break;
		    				throw new ServiceNotAvailableException("Couldn't fetch all friends from service: "+json);
		    			} else if (json.containsKey("users")) {
		    				addContacts(json, contactsArray);
		    			} else {
		    				throw new ServiceNotAvailableException("Get all friends call failed: "+json);
		    			}
			    		
			    		cursor = accessJsonField(responseBody, "next_cursor");
			    		nextCursor = Long.parseLong(cursor);
			    		
			    		if (nextCursor != 0) {
			    			fields = "/friends/list.json?skip_status=true&cursor="+String.valueOf(nextCursor);
			    			responseBody = sendRequest(Verb.GET, this.apiUrl+fields).getBody();
			    		}						
					} while (nextCursor != 0);
					
				} catch (JSONException e) {
					logger.error("error in retrieving all contacts:"+json, e);
			    } catch (IOException e) {
					logger.error("error in retrieving value of Json Field - "+e, e);
			    }
				
				JSONObject contactsObject = new JSONObject();
		    	contactsObject.put("users", contactsArray);
		    	responseBody = contactsObject.toString();
			}
			
			ServiceResponse response = new ServiceResponse(ServiceResponse.JSON, attribute, fields, responseBody);
			return new ServiceResponse[] { response };
		} catch (OAuthException e) {
			throw new ServiceNotAvailableException(e);
		}
	}

	@Override
	protected <T extends Resource> Collection<T> _get(String attribute, Class<T> returnType)
			throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException {
		try {
			ResourceAttributes parts = new ResourceAttributes(attribute);
			ServiceResponse[] responses = this.getRaw(attribute);
			String json = null;
			
			// FIXME now response is JSON, so the merge (mergeProfileWithSuggestions) XML method
			// cannot be used anymore; for now no suggestions are retrieved until fixed.
//			if (ResourceAttributes.ATTR_PROFILE.equals(parts.getResourceType())) {
//				// Profiles are 2 calls: /users/lookup and /users/suggestions (interests)
//				// Both responses (xml) are merged for the transformer to apply the
//				// profile.xsparql query.
//				String lookupJson = null, suggestionsJson = null;
//				for (ServiceResponse response : responses) {
//					if (response.getPath().contains("/account/verify_credentials")) {
//						lookupJson = response.getResponse();
//					} else if (response.getPath().contains("/users/suggestions")) {
//						suggestionsJson = response.getResponse();
//					}
//				}
//				json = mergeProfileWithSuggestions(lookupJson, suggestionsJson);
//			} else {
				json = responses[0].getResponse();
//			}
			
			String xml = FormatUtils.convertJSONToXML(json, null);
			return getTransformer().deserialize(xml, this.getAdapterName(), attribute, returnType);
		} catch (TransformerException e) {
			throw new ServiceNotAvailableException(new Exception(
					"Error parsing XML response: " + e.getMessage()));
		}
	}
	
	/**
	 * It merges the XML documents for profile information (lookup.xml) and suggestions
	 * or interests (suggestions.xml) into a single XML document, grouped at the
	 * <user> XML element.
	 * 
	 * @param lookupJson JSON response of lookup.json
	 * @param suggestionsJson JSON response of suggestions.json
	 * @return
	 */
	private String mergeProfileWithSuggestions(String lookupJson, String suggestionsJson) {
		if (suggestionsJson == null) {
			logger.warn("suggestions.xml is null: cannot merge lookup.json with suggestions.json");
			return lookupJson;
		}
		
		StringWriter outputString = new StringWriter();
		try {			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document docA = docBuilder.parse(new InputSource(new StringReader(lookupJson)));
			Document docB = docBuilder.parse(new InputSource(new StringReader(suggestionsJson)));
			org.w3c.dom.Element rootA = docA.getDocumentElement();
			org.w3c.dom.Element rootB = docB.getDocumentElement();
			rootA.appendChild(docA.importNode(rootB, true));
			TransformerFactory tranFactory = TransformerFactory.newInstance(); 
			Transformer transformer = tranFactory.newTransformer(); 
			Source src = new DOMSource(docA); 
			Result dest = new StreamResult(outputString); //jsonFile
			transformer.transform(src, dest); 
		} catch (Exception e) {
			logger.error("error merging lookup.json with suggestions.json", e);
			return lookupJson;
		}
		return outputString.getBuffer().toString();
	}
	
	/**
	 * Retrieves value of next_cursor to get more contacts (20 records at a time)
	 * 
	 * @param jsonTxt json text to search in
	 * @param jsonFieldName json field name for retrieving it's value
	 * @return
	 */
	private String accessJsonField(String jsonTxt, String jsonFieldName) throws IOException {    
	    JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTxt);        
	    String jsonFieldValue = json.getString(jsonFieldName);
	    
	    return jsonFieldValue;	  
	}
	
	/**
	 * Retrieves value of the json response from the current call and adds each JSON Object in a separate
	 * array which will contain all the connections retrieved for a given user
	 * 
	 * @param jsonResponse json response received from call
	 * @param contactsArray JSON Array to which each contact will be added to
	 */
	private void addContacts(JSONObject jsonResponse, JSONArray contactsArray) {
	    JSONArray usersArray  = jsonResponse.getJSONArray("users");				
		for (int j=0; j<usersArray.size(); j++) {			
			JSONObject contact = usersArray.getJSONObject(j);
			contactsArray.add(contact);
		}
	}

}
