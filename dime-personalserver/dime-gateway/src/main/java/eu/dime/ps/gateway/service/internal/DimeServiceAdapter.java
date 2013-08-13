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


package eu.dime.ps.gateway.service.internal;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.ExternalNotificationDTO;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.auth.impl.CredentialStoreImpl;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.proxy.BinaryFile;
import eu.dime.ps.gateway.proxy.HttpRestProxy;
import eu.dime.ps.gateway.proxy.ProxyFactory;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.ResourceAttributes;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;
import eu.dime.ps.gateway.util.JSONLDUtils;
import eu.dime.ps.semantic.model.NCOFactory;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nco.PersonName;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Service adapter in charge of communication between di.me personal servers (PS).
 * It is in charge of sending notifications between di.me PS's, fetching shared resources and
 * searching for contacts on user resolver/registry services.
 *  
 * @author Marc Planagumà 
 * @author Sophie Wrobel
 * @author Ismael Rivera
 */
public class DimeServiceAdapter extends ServiceAdapterBase implements InternalServiceAdapter {

	private static final Logger logger = LoggerFactory.getLogger(DimeServiceAdapter.class);

	public static final String NAME = "di.me";

	private static final String SHARED_PROFILE_PATH  = "/api/dime/rest/:target/shared/profile";
	private static final String SHARED_RESOURCE_PATH = "/api/dime/rest/:target/shared/:type/:id";
	private static final String SHARED_BINARY_FILE_PATH = "/api/dime/rest/:target/shared/resource/filemanager/:id";
	private static final String CREDENTIALS_PATH     = "/api/dime/rest/:target/user/credentials/:username";

	/**
	 * Proxy to look up di.me profiles published in the public user registry
	 */
	private HttpRestProxy publicResolverService;

	/**
	 * Resolves account identifiers to IPs using the di.me DNS
	 */
	private DimeIPResolver targetResolver;

	private ProxyFactory proxyFactory;
	private CredentialStore credentialStore;
	private PolicyManager policyManager;

	public void setProxyFactory(ProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	public void setTargetResolver(DimeIPResolver targetResolver) {
		this.targetResolver = targetResolver;
	}

	public void setPublicResolverService(HttpRestProxy publicResolverService) {
		this.publicResolverService = publicResolverService;
	}

	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}

	public void setPolicyManager(PolicyManager policyManager) {
		this.policyManager = policyManager;
	}

	@Override
	public String getAdapterName() {
		return DimeServiceAdapter.NAME;
	}

	/**
	 * @param identifier The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeServiceAdapter(String identifier) throws ServiceNotAvailableException {
		super(identifier);

		this.proxyFactory = new ProxyFactory();
		this.policyManager = PolicyManagerImpl.getInstance();
		this.targetResolver = new DimeIPResolver();
		this.transformer = new XSparqlTransformer();
		this.credentialStore = CredentialStoreImpl.getInstance();

		try {
			this.publicResolverService = new HttpRestProxy(new URL(
					this.policyManager.getPolicyString("DIME_URS", null)));
		} catch (MalformedURLException e) {
			throw new ServiceNotAvailableException("di.me URS couldn't be initialized: " + e.getMessage(), e);
		}
	}

	/**
	 * get Method for di.me service adapter with additional SAID parameter
	 * 
     * @param <T>
     * @param receiverSAID GET request should go to this SAID
	 * @param senderSAID Generated GET request is being sent from this SAID using their credentials to access information
	 * @param attribute
	 * @param returnType
     * @param localTenant
     * @return
	 * @throws AttributeNotSupportedException
	 * @throws ServiceNotAvailableException
     * @throws InvalidLoginException
     * @throws ServiceException 
	 */
	public <T extends Resource> Collection<T> get(String receiverSAID, String senderSAID,
			String attribute, Class<T> returnType, Tenant localTenant) throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException, ServiceException {

		// Retrieve username & password to establish the connection 
		String username = null;
		String secret = null;
		try {
			username = credentialStore.getUsername(senderSAID, receiverSAID, localTenant);
			secret = credentialStore.getPassword(senderSAID, receiverSAID, localTenant);
		} catch (NoResultException e) {
			throw new ServiceNotAvailableException("Could not find username & password from the credentials store", e);
		}

		// Resolve IP for the receiver said and create proxy
		HttpRestProxy proxy = null;
		String targetSaidName = credentialStore.getNameSaid(receiverSAID, localTenant);
		try {
			proxy = prepareProxy(targetResolver.resolve(targetSaidName), username, secret);
			proxy.authenticate(new UsernamePasswordAuthenticationToken(username, secret));
		} catch (DimeDNSCannotConnectException e) {
			throw new ServiceNotAvailableException("Could not connect to dns for " + receiverSAID, e);
		} catch (DimeDNSCannotResolveException e) {
			throw new ServiceNotAvailableException("said not registered at dns: " + receiverSAID, e);
		} catch (DimeDNSException e) {
			throw new ServiceNotAvailableException("Unknown error occurred when trying to connect to " + 
					receiverSAID + ": " + e.getMessage(), e);
		}

		// Prepare URL path to request/fetch the shared item
		ResourceAttributes parts = new ResourceAttributes(attribute);
		String resourceType = parts.getResourceType();
		String path = null;

		if (ResourceAttributes.ATTR_DATABOX.equals(resourceType)
				|| ResourceAttributes.ATTR_LIVEPOST.equals(resourceType)
				|| ResourceAttributes.ATTR_RESOURCE.equals(resourceType)) {
			path = SHARED_RESOURCE_PATH.replace(":type", resourceType).replace(":id", parts.getQueryObject());
		}
		// TODO [Isma says] this should replace the method getProfile -- but I need to confirm what credentials
		// I need to use (why getProfile only requires sender said and uses this.username/this.password
		// as credentials???)
		else if (parts.getResourceType().equals(ResourceAttributes.ATTR_PROFILE)) {
			path = SHARED_PROFILE_PATH;
		}
		// Resource type is not supported...
		else {
			throw new AttributeNotSupportedException(attribute, this);
		}

		// Insert the target said in the path
		path = path.replace(":target", targetSaidName);

		Collection<T> results = new ArrayList<T>();
		try {
			// Perform GET request
			String response = proxy.get(path, headers("Accept", MediaType.APPLICATION_JSONLD));
			logger.info("GET " + path + " returned: " + response);
			
			// Transform json+ld response to a Java object or collection
			Object object = JSONLDUtils.deserialize(response, returnType);
			if (object instanceof Resource) {
				Resource resource = (Resource) object;
				results.add((T) resource.castTo(returnType));
			} else {
				Collection<Resource> resources = (Collection<Resource>) object;
				for (Resource resource : resources) {
					results.add((T) resource.castTo(returnType));
				}
			}

		} catch (JsonParseException e) {
			throw new ServiceNotAvailableException(
					"Could not process response: Invalid JSON returned - Parse exception.",
					"SERV-100");
		} catch (JsonMappingException e) {
			throw new ServiceNotAvailableException(
					"Could not process response: Invalid JSON returned - Parse exception.",
					"SERV-100");
		} finally {
			if (proxy != null) {
				proxy.close();
			}
		}

		return results;
	}

	/**
	 * get Method for di.me service adapter retrieve a Binary file from a ResourceId
	 * 
	 * @param receiverSAID
	 * @param senderSAID
	 * @param resourceId
     * @param localTenant
     * @return BinaryFile
	 * @throws ServiceNotAvailableException	
	 */
	public  BinaryFile getBinary(String receiverSAID, String senderSAID,
			String resourceId, Tenant localTenant) throws 	ServiceNotAvailableException {
			
		// Retrieve username & password to establish the connection 
		String username = null;
		String secret = null;
		try {
			username = credentialStore.getUsername(senderSAID, receiverSAID, localTenant);
			secret = credentialStore.getPassword(senderSAID, receiverSAID, localTenant);
		} catch (NoResultException e) {
			throw new ServiceNotAvailableException("Could not find username & password from the credentials store", e);
		}
		logger.info("username: " + username + " secret: " + secret);
		// Resolve IP for the receiver said and create proxy
		HttpRestProxy proxy = null;
		String targetSaidName = credentialStore.getNameSaid(receiverSAID, localTenant);
		try {
			proxy = prepareProxy(targetResolver.resolve(targetSaidName), username, secret);
			proxy.authenticate(new UsernamePasswordAuthenticationToken(username, secret));

		} catch (DimeDNSCannotConnectException e) {
			throw new ServiceNotAvailableException("Could not connect to dns for " + receiverSAID, e);
		} catch (DimeDNSCannotResolveException e) {
			throw new ServiceNotAvailableException("said not registered at dns: " + receiverSAID, e);
		} catch (DimeDNSException e) {
			throw new ServiceNotAvailableException("Unknown error occurred when trying to connect to " +
					receiverSAID + ": " + e.getMessage(), e);
		}

		// Prepare URL path to request/fetch the binary item	
		String path = null;

		path = SHARED_BINARY_FILE_PATH.replace(":id", resourceId);

		// Insert the target said in the path
		path = path.replace(":target", targetSaidName);

		// Perform GET request
		BinaryFile response = proxy.getBinary(path, headers("Accept",MediaType.APPLICATION_OCTET_STREAM));
		logger.info("GET " + path + " returned: " + response.getType());	
		
		if (proxy != null) {
			proxy.close();	
		}
		
		return response;
	}

	@Override
	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException, ServiceNotAvailableException {

		HttpRestProxy proxy = null;

		try {
			String target = "";
			Data<ExternalNotificationDTO> data = new Data<ExternalNotificationDTO>();
			Request<ExternalNotificationDTO> request = new Request<ExternalNotificationDTO>();

			if (attribute.equals("/notification")) {
				DimeExternalNotification notification = (DimeExternalNotification) value;

				Entry element = new Entry();
				element.setGuid(notification.getItemID());
				element.setType(notification.getItemType());
				element.setName(notification.getName());

				ExternalNotificationDTO payload = new ExternalNotificationDTO();
				payload.setElement(element);
				payload.setGuid(notification.getItemID());
				payload.setOperation(notification.getOperation());
				payload.setSender(notification.getSender());
				payload.setSaidReciever(notification.getTarget());
				payload.setSaidSender(notification.getSender());

				// Resolve IP via proxy
				proxy = prepareProxy(targetResolver.resolve(notification.getTarget()), null, null);
				List<ExternalNotificationDTO> collection = new ArrayList<ExternalNotificationDTO>(1);
				collection.add(payload);
				data.setEntry(collection);
				target = "/"+notification.getTarget();
			}

			Message<ExternalNotificationDTO> message = new Message<ExternalNotificationDTO>();
			message.setData(data);
			request.setMessage(message);

			String json = JaxbJsonSerializer.jsonValue(request);
			int response = proxy.post("/api/services"+target+"/set" + attribute, json);

			if (response != 200) {
				throw new ServiceNotAvailableException(response);
			}
		} catch (DimeDNSCannotConnectException e) {
			throw new ServiceNotAvailableException("Could not connect to dns server" , e);
		} catch (DimeDNSCannotResolveException e) {
			throw new ServiceNotAvailableException("said not registered at dns: ", e);
		} catch (DimeDNSException e) {
			throw new ServiceNotAvailableException(e.getMessage(), e);
		} finally {
			if (proxy != null) {
				proxy.close();
			}
		}
	}

	@Override
	public <T extends Resource> Collection<T> search(String attribute,
			Resource values, Class<T> returnType)
					throws ServiceNotAvailableException, ServiceException {

		// Transform parameter 'values' into a PersonContact object
		PersonContact contact = (PersonContact) values.castTo(PersonContact.class);

		// Constructing the path...
		String path = policyManager.getPolicyString("NOAUTH_RESOLVER_ENDPOINT", DimeServiceAdapter.NAME);
		if ("firstname".equals(attribute)) {
			String firstname = contact.getAllPersonName_as().firstValue().getNameGiven();
			if (firstname == null || firstname.isEmpty()) {
				throw new IllegalArgumentException("Parameter 'values' should contain a valid 'firstname'.");
			}
			path = path.concat("/registeredusers/getByName?name=" + firstname);
		} else if ("surname".equals(attribute)) {
			String surname = contact.getAllPersonName_as().firstValue().getNameFamily();
			if (surname == null || surname.isEmpty()) {
				throw new IllegalArgumentException("Parameter 'values' should contain a valid 'surname'.");
			}
			path = path.concat("/registeredusers/getBySurname?surname=" + surname);
		} else if ("nickname".equals(attribute)) {
			String nickname = contact.getAllPersonName_as().firstValue().getAllNickname().next().toString();
			if (nickname == null || nickname.isEmpty()) {
				throw new IllegalArgumentException("Parameter 'values' should contain a valid 'nickname'.");
			}
			path = path.concat("/registeredusers/getByNickname?nickname=" + nickname);
		} else {
			throw new IllegalArgumentException("Attribute '"+attribute+"' is not supported, please try" +
					" one of the following: 'firstname', 'surname' or 'nickname'");
		}

		// Send GET request to user registry service
		String response = this.publicResolverService.get(path);

		NCOFactory ncofactory = new NCOFactory();
		Collection<PersonContact> contacts = new ArrayList<PersonContact>();

		// Parse results into objects
		JSONArray jsonContacts = (JSONArray) JSONSerializer.toJSON(response);
		Iterator<JSONObject> contactIt = jsonContacts.iterator();
		while (contactIt.hasNext()) {
			JSONObject current = contactIt.next();
			PersonName name = ncofactory.createPersonName();
			name.setNickname(current.getString("nickname"));
			name.setFullname(current.getString("name") + " " + current.getString("surname"));
			name.setNameGiven(current.getString("name"));
			name.setNameFamily(current.getString("surname"));

			// FIXME [Isma] why are we using serviceAccountID as the URI for the profile? when I'm sure of the consequences
			// I'll instead use a new unique UUID-based URI
			// and actually, shouldn't we be creating an account for this person as well? I mean, that's the truly (and only)
			// way for communicating with him/her
			PersonContact newContact = ncofactory.createPersonContact(current.getString("serviceAccountID"));
			newContact.setPersonName(name);
			newContact.getModel().addAll(name.getModel().iterator()); // important to add PersonName's metadata to PersonContact

			contacts.add(newContact);
		}

		return (Collection<T>) contacts;
	}

	@Override
	public <T extends Resource> Collection<T> search(Resource values,
			Class<T> returnType) throws ServiceNotAvailableException, ServiceException {

		// Search not supported.
		throw new ServiceNotAvailableException("Unsupported search query."); 
	}

	/**
	 * retrieves profile (unauthenticated)
	 * 
	 * @param targetSaidName
	 * @return a PersonContact instance with the profile information metadata
	 * @throws AttributeNotSupportedException
	 * @throws ServiceNotAvailableException
	 */
	public PersonContact getProfile(String targetSaidName, Token token)
			throws AttributeNotSupportedException, ServiceNotAvailableException {

		HttpRestProxy proxy = null;
		PersonContact profile = null;
		String path = SHARED_PROFILE_PATH.replace(":target", targetSaidName);
		
		try {
			String baseURL = targetResolver.resolve(targetSaidName);
			proxy = prepareProxy(baseURL, token.getToken(), token.getSecret());

			// encode username and password
			String authorization = Base64.encodeBase64String((token.getToken()+":"+token.getSecret()).getBytes()).replace("\r\n", "");

			Map<String, String> headers = headers(
					"Accept", MediaType.APPLICATION_JSONLD,
					"Authorization", "Basic " + authorization);

			String response = proxy.get(path, headers);
			logger.info("GET "+path+" [username="+token.getToken()+", password="+token.getSecret()+"] responded with: "+response);

			profile = JSONLDUtils.deserialize(response, PersonContact.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Profile metadata for "+path+" response is:\n"+profile.getModel().serialize(Syntax.Turtle));
			}
		} catch (DimeDNSCannotConnectException e) {
			throw new ServiceNotAvailableException("Could not connect to dns for " + targetSaidName, e);
		} catch (DimeDNSCannotResolveException e) {
			throw new ServiceNotAvailableException("said not registered at dns: " + targetSaidName, e);
		} catch (DimeDNSException e) {
			throw new ServiceNotAvailableException("Unknown error occurred when trying to connect to " + 
					targetSaidName + ": " + e.getMessage(), e);
		} catch (JsonParseException e) {
			throw new ServiceNotAvailableException(
					"Could not process response from "+path+", invalid JSON returned: "+e.getMessage(),
					"SERV-100");
		} catch (JsonMappingException e) {
			throw new ServiceNotAvailableException(
					"Could not process response, invalid JSON returned: "+e.getMessage(),
					"SERV-100");
		} catch (Exception e) {
			throw new ServiceNotAvailableException("Unknown error occurred: "+e.getMessage(), e);
		} finally {
			if (proxy != null) {
				proxy.close();
			}
		}

		return profile;
	}

	public Token getUserToken(String username) throws ServiceNotAvailableException, ServiceException {
		Token token = null;
		HttpRestProxy proxy = null;

		try {
			// request username/password to access the target di.me PS
			// since the password is yet unknown, it can be set to anything including empty string
			proxy = prepareProxy(targetResolver.resolve(this.identifier), username, "");
			
			String path = CREDENTIALS_PATH.replace(":target", this.identifier).replace(":username", username);
			String response = proxy.get(path, headers("Accept", MediaType.APPLICATION_JSON));
			logger.info("GET "+path+" returned: " + response);

			JSONObject jsonObject = JSONObject.fromObject(response); 
			JSONObject res = jsonObject.getJSONObject("response");
			JSONObject meta = res.getJSONObject("meta");
			String status = meta.getString("status");

			String password = null;
			if (status.equalsIgnoreCase("OK")) {
				// Parse out username and password to reconstruct token
				JSONObject data = res.getJSONObject("data");
				JSONArray entry = data.getJSONArray("entry");
				JSONObject accountEntry = entry.getJSONObject(0);
				username = accountEntry.getString("username");
				password = accountEntry.getString("password");
			} else {
				// error means, password already received
				// TODO is this an error then? so why just logging it as info? and if its not an error,
				// why displaying ERROR in the message???
				logger.info("ERROR in Response: " + status + res.toString());
			}

			token = new Token(username, password);

		} catch (JSONException e) {
			logger.info("ERROR in Response: it was not JSON.");
		} catch (DimeDNSCannotConnectException e) {
			throw new ServiceNotAvailableException(e.getMessage(), e);
		} catch (DimeDNSCannotResolveException e) {
			throw new ServiceNotAvailableException(e.getMessage(), e);
		} catch (DimeDNSException e) {
			throw new ServiceNotAvailableException(e.getMessage(), e);
		} catch (ServiceNotAvailableException e) {
			throw new ServiceNotAvailableException(e);
		} finally {
			if (proxy != null) {
				proxy.close();
			}
		}

		return token;
	}

	public boolean confirmToken(Token token){
		int status;
		HttpRestProxy proxy = null;
		String path = null;
		
		try {
			path = CREDENTIALS_PATH.replace(":target", this.identifier).replace(":username", token.getToken());
			proxy = prepareProxy(targetResolver.resolve(this.identifier), token.getToken(), token.getSecret());
			status = proxy.post(path, "");
		} catch (ServiceNotAvailableException e) {
			logger.error("Could not confirm token. System may be in a undefined state: "+e.getMessage(), e);
			return false;
		} catch (DimeDNSCannotConnectException e) {
			logger.error("Could not confirm token. System may be in a undefined state: "+e.getMessage(), e);
			return false;
		} catch (DimeDNSCannotResolveException e) {
			logger.error("Could not confirm token. System may be in a undefined state: "+e.getMessage(), e);
			return false;
		} catch (DimeDNSException e) {
			logger.error("Could not confirm token. System may be in a undefined state: "+e.getMessage(), e);
			return false;
		} finally {
			if (proxy != null) {
				proxy.close();
			}
		}

		if (status == HttpStatus.SC_OK){
			logger.info("POST "+path+" returned "+status+". Token confirmed!");
			return true;
		} else {
			logger.warn("POST "+path+" returned "+status+". Could not confirm token. System may be in a undefined state.");
			return false;
		}
	}

	@Override
	public Boolean isConnected() {
		return true;
	}

	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException {
		throw new AttributeNotSupportedException("getRaw is not supported for di.meServiceAdapter!", this);
	}

	// *********************
	// Not supported Methods
	// *********************

	@Override
	public <T extends Resource> Collection<T> get(String attribute, Class<T> returnType)
			throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException {
		throw new AttributeNotSupportedException("You forgot the SAID parameter when calling the dimeServiceAdapter!", this);
	}

	@Override
	public void response(String attribute, Resource value) throws ServiceNotAvailableException {
		throw new ServiceNotAvailableException("di.me service adapter does not support 'response'.");
	}

	@Override
	public void delete(String attribute) throws ServiceNotAvailableException,
	AttributeNotSupportedException {
		throw new AttributeNotSupportedException("delete is currently not supported for di.me service adapter.", this);
	}

	// Prepares a HTTP proxy to connect to external web services
	private HttpRestProxy prepareProxy(String serviceURL, String username, String password) throws ServiceNotAvailableException {
		try {
			String realm = policyManager.getPolicyString("DIME_REALM", null);
			int port = policyManager.getPolicyInteger("DIME_PORT", null);
			return proxyFactory.createProxy(new URL(serviceURL), port, realm, username, password);
		} catch (MalformedURLException e) {
			throw new ServiceNotAvailableException(e);
		}
	}

	// Helper method to ease the creation of the Map containing the headers
	private Map<String, String> headers(String...headers) {
		if (headers.length % 2 != 0) {
			throw new IllegalArgumentException("The 'headers' parameter needs to contain key-value pairs. " +
					"An odd number of arguments looks like you may have forgotten one value.");
		}
		Map<String, String> headersMap = new HashMap<String, String>();
		for (int idx = 0; idx < headers.length; idx++) {
			headersMap.put(headers[idx], headers[++idx]);
		}
		return headersMap;
	}

}