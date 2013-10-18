/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

package eu.dime.ps.gateway.service.external;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.auth.impl.CredentialStoreImpl;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.proxy.HttpRestProxy;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.userresolver.client.DimeResolver;
import eu.dime.ps.gateway.userresolver.client.noauth.ResolverClient;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.storage.entities.Tenant;

/**
 * @author Sophie.Wrobel
 * @author mheupel
 * 
 */
public class DimeUserResolverServiceAdapter extends ServiceAdapterBase implements ExternalServiceAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(DimeUserResolverServiceAdapter.class);

	public final static String NAME = "PublicDimeUserDirectory";
	
	private CredentialStore credentialStore;

	private HttpRestProxy proxy;
	private HashMap<String, String> headers;
	
	private Tenant tenant; //need to know the current tenant for storing/loading credentials (on register, update)
	
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	
	public void setCredentialStore(CredentialStore credentialStore){
		this.credentialStore = credentialStore;
	}

	/**
	 * Creates a new Dime URS Adapter with a random identifier
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter() throws ServiceNotAvailableException {
		this("account:urn:" + UUID.randomUUID());
		if (credentialStore == null){
			this.credentialStore = CredentialStoreImpl.getInstance();
		}
	}

	/**
	 * @param identifier
	 *            The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter(String identifier)
			throws ServiceNotAvailableException {	
		super();
		this.identifier = identifier;
		// this.policyManager = PolicyManagerImpl.getInstance();
		this.policyManager = new PolicyManagerImpl();
		this.headers = new HashMap<String, String>();
		this.headers.put("Accept", MediaType.APPLICATION_JSON);

		// Initialize public resolver service
		try {
			this.proxy = new HttpRestProxy(new URL(this.policyManager.getPolicyString("DIME_URS", null)));
		} catch (MalformedURLException e) {
			throw new ServiceNotAvailableException(e);
		}
	}

	




	@Override
	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidDataException {

		if (tenant == null){
			throw new ServiceNotAvailableException("Tenant was not specified. Could not register to the Public Resolver ");
		}
		// Profiles
		// Attribute: /profile/@me
		if (AttributeMap.PROFILE_ME.equals(attribute)) {
            try {
                // Prepare contact to registerAtURS
                // FIXME what if value is not a PersonContact object?? (now an ugly ClassCastException)
                // FIXME what if the contact has no PersonName?? (now a NullPointerException, not nice!)
                PersonContact contact = (PersonContact) value;
                String firstname = contact.getAllPersonName_as().firstValue()
                        .getNameGiven();
                String surname = contact.getAllPersonName_as().firstValue()
                        .getNameFamily();
                String nickname = contact.getAllPersonName_as().firstValue()
                        .getAllNickname().next().toString();
                String secret = registerAtURS(getIdentifier(), nickname, firstname, surname);
            } catch (IOException e) {
				logger.error(e.toString(),e);
				throw new ServiceNotAvailableException(
						"Registering to "+ attribute + " failed.");
			}

		} else {
			throw new ServiceNotAvailableException("Registering to "
					+ attribute + " is not supported.");
		}
	}

	public JSONArray search(String value) throws ServiceNotAvailableException, ServiceException {

		// Generate query
		String query;
		try {
			query = "noauth/users/search?string=" + URIUtil.encodeWithinQuery(value);

			// Execute query
			String responseString = this.proxy.get(query, headers);
			JSON json = JSONSerializer.toJSON(responseString);
	
			JSONArray jsonContacts = null;
			
			if (json instanceof JSONObject) {
				jsonContacts = ((JSONObject) json).getJSONArray("result");
			} else if (json instanceof JSONArray) {
				jsonContacts = (JSONArray) json;
			} else {
				// TODO what happens in this case???
			}
			
			return jsonContacts;
		} catch (URIException e) {
			e.printStackTrace();
			throw new ServiceException("010", e.getMessage(), e);
		}
	}

	@Override
	public <T extends Resource> Collection<T> search(String attribute,
			Resource values, Class<T> returnType)
			throws ServiceNotAvailableException {

		// Search not supported.
		throw new ServiceNotAvailableException("Unsupported search query.");
	}

	@Override
	public <T extends Resource> Collection<T> search(Resource values,
			Class<T> returnType) throws ServiceNotAvailableException {

		// Search not supported.
		throw new ServiceNotAvailableException("Unsupported search query.");
	}

	@Override
	public <T extends Resource> Collection<T> get(String attribute,
			Class<T> returnType) throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		// Reverse lookup not supported.
		throw new ServiceNotAvailableException(
				"Reverse lookup to the Dime URS is not supported. Use DimeServiceAdapter.search(...) instead.");
	}

	@Override
	public void response(String attribute, Resource value)
			throws ServiceNotAvailableException {

		// not supported
		throw new ServiceNotAvailableException("Response not supported.");
	}

	@Override
	public void _delete(String attribute) throws ServiceNotAvailableException,
			AttributeNotSupportedException {

        String resolverEndpoint = resolveEndpoint();

		DimeResolver resolverClient = new ResolverClient(resolverEndpoint);

		String token = credentialStore.getAccessSecret("dime:urs:" + attribute.replace("urn:uuid:", ""), tenant);
		if (token == null ||token.isEmpty()){
			throw new ServiceNotAvailableException("Deleting of this Public Resolver account is not supported.");
		}
		String response = resolverClient.delete(token, attribute);
		//TODO: check response
		credentialStore.deleteOAuthCredentials(attribute, tenant);
	}

	@Override
	public ServiceResponse[] getRaw(String fields)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {

		// Lookup not supported
		throw new ServiceNotAvailableException("Unsupported get query.");
	}

	@Override
	public String getAdapterName() {
		return NAME;
	}

	@Override
	public Boolean isConnected() {
		return this.proxy != null;
	}

	
    public String registerAtURS(String id, String nickname, String firstname, String surname) throws IOException  {

    	String resolverEndpoint = policyManager.getPolicyString("NOAUTH_RESOLVER_ENDPOINT", "DIME");
    	String host = policyManager.getPolicyString("URS", "DIME");
    	
        DimeResolver resolverClient = new ResolverClient(host + resolverEndpoint);

        Map<String, String> values = new HashMap<String, String>();
        values.put("name", URIUtil.encodeWithinQuery(firstname));
        values.put("surname", URIUtil.encodeWithinQuery(surname));
        values.put("nickname", URIUtil.encodeWithinQuery(nickname));
        
//		String query = resolverEndpoint + "/register"  
//					+ "?name=" 		+ URIUtil.encodeWithinQuery(firstname)
//					+ "&surname=" 	+ URIUtil.encodeWithinQuery(surname)
//					+ "&nickname="	+ URIUtil.encodeWithinQuery(nickname)
//					+ "&said=" 		+ URIUtil.encodeWithinQuery(id);

        // Register with user service //token currently not supported without idemix
        String jsonResponse =  resolverClient.register(null, firstname, surname, nickname, id);

		JSON json = JSONSerializer.toJSON(jsonResponse);
		String secret = null;
		
		if (json instanceof JSONObject) {
			try {
				secret = ((JSONObject) json).getString("key");
			} catch (JSONException e) {
				throw new IOException("Registration failed. Could not parse server response.");
			}
		} else {
			throw new IOException("Registration failed. Could not parse server response.");
		}
		
        storeURScredentials("dime:urs:" + identifier.replace("urn:uuid:", ""), id, secret);
        return secret;
    }

	private String resolveEndpoint() {
        
        String URS = policyManager.getPolicyString("URS", "DIME");
        String endpoint = policyManager.getPolicyString("RESOLVER_ENDPOINT", "DIME");
        
        // Retrieve auth token
//        String token = loadURStoken();
//        if (token == null || token.equals("")){ //if no token available, try noauth API
//        	endpoint = policyManager.getPolicyString("NOAUTH_RESOLVER_ENDPOINT", "DIME");
//        }

        StringBuilder resolverEndpoint = new StringBuilder();
        resolverEndpoint.append(URS);
        resolverEndpoint.append(endpoint);
        
        return resolverEndpoint.toString();
	}
	
	private void storeURScredentials(String token, String id, String secret) {	
		credentialStore.storeOAuthCredentials(NAME, token, id, secret, tenant);
	}


	public void update(String attribute, Object value) throws ServiceNotAvailableException{
		//TODO: activate update
		if (false || AttributeMap.PROFILE_ME.equals(attribute)) {
            // Prepare contact to registerAtURS
			// FIXME what if value is not a PersonContact object?? (now an ugly ClassCastException)
			// FIXME what if the contact has no PersonName?? (now a NullPointerException, not nice!)
			PersonContact contact = (PersonContact) value;
			String firstname = contact.getAllPersonName_as().firstValue()
			        .getNameGiven();
			String surname = contact.getAllPersonName_as().firstValue()
			        .getNameFamily();
			String nickname = contact.getAllPersonName_as().firstValue()
			        .getAllNickname().next().toString();
			//String secret = registerAtURS(getIdentifier(), nickname, firstname, surname);

		} else {
			throw new ServiceNotAvailableException("Updating at "
					+ attribute + " is not supported.");
		}
	}



}
