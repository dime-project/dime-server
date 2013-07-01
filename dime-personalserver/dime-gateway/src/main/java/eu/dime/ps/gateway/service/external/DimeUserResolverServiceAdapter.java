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
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.proxy.HttpRestProxy;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.MediaType;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.userresolver.client.IdemixClient;
import eu.dime.userresolver.client.ResolverClient;

/**
 * @author Sophie.Wrobel
 * 
 */
public class DimeUserResolverServiceAdapter extends ServiceAdapterBase implements ExternalServiceAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(DimeUserResolverServiceAdapter.class);

	public final static String NAME = "PublicDimeUserDirectory";
	
	private String identifier;
	private Token accessToken;
	private String masterSecret;
	private String idemixCredential;

	@Autowired
	private PolicyManager policyManager;

	private HttpRestProxy proxy;
	private HashMap<String, String> headers;

	/**
	 * Creates a new Dime URS Adapter with a random identifier
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter() throws ServiceNotAvailableException {
		this("account:urn:" + UUID.randomUUID(), null, null);
	}

	/**
	 * @param identifier
	 *            The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter(String identifier)
			throws ServiceNotAvailableException {
		this(identifier, null, null);
	}

	/**
	 * @param identifier
	 *            The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter(String identifier, String username,
			String password) throws ServiceNotAvailableException {
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
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public void setIdentifer(String identifier) {
		this.identifier = identifier;
	}

	public Token getUserToken() {
		return this.accessToken;
	}

	@Override
	public void setPolicyManager(PolicyManager policyManager) {
		this.policyManager = policyManager;
	}

	@Override
	public void set(String attribute, Object value)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidDataException {

		// Profiles
		// Attribute: /profile/@me
		if (AttributeMap.PROFILE_ME.equals(attribute)) {

			// Prepare contact to register
			// FIXME what if value is not a PersonContact object?? (now an ugly ClassCastException) 
			// FIXME what if the contact has no PersonName?? (now a NullPointerException, not nice!)
			PersonContact contact = (PersonContact) value;
			String firstname = contact.getAllPersonName_as().firstValue()
					.getNameGiven();
			String surname = contact.getAllPersonName_as().firstValue()
					.getNameFamily();
			String nickname = contact.getAllPersonName_as().firstValue()
					.getAllNickname().next().toString();

			try {
				// Retrieve master secret
				IdemixClient idemixClient;
				String URS = policyManager.getPolicyString("URS", "DIME");
				idemixClient = new IdemixClient(policyManager.getPolicyString("ISSUER", "DIME"));

				StringBuilder resolverEndpoint = new StringBuilder();
				resolverEndpoint.append(URS);
				resolverEndpoint.append(policyManager.getPolicyString("RESOLVER_ENDPOINT", "DIME"));

				StringBuilder authEndpoint = new StringBuilder();
				authEndpoint.append(URS);
				authEndpoint.append(policyManager.getPolicyString("AUTH_ENDPOINT", "DIME"));
				
				ResolverClient resolverClient = new ResolverClient(
						resolverEndpoint.toString(), authEndpoint.toString(), idemixClient);
				this.masterSecret = idemixClient.generateMasterSecret();
				// FIXME: Save idemix master secret in credential store

				// Create idemix credential
				Map<String, String> values = new HashMap<String, String>();
				values.put("name", firstname);
				values.put("surname", surname);
				values.put("nickname", nickname);
				this.idemixCredential = idemixClient.getCredential(
						masterSecret, "dime-credential", values);
				// FIXME: Save idemix credential in credential store

				// Register with user service
				String scope = "register";
				String token = resolverClient.getToken(scope, masterSecret,
						idemixCredential);
				resolverClient.register(token, firstname, surname, nickname,
						this.getIdentifier());
			} catch (IOException e) {
				logger.error(e.toString(),e);
				throw new ServiceNotAvailableException(
						"Error retrieving idemix token. Registering to "
								+ attribute + " failed.");
			}

		} else {
			throw new ServiceNotAvailableException("Registering to "
					+ attribute + " is not supported.");
		}
	}

	public JSONArray search(String value) throws ServiceNotAvailableException {

		// Generate query
		String query = "noauth/users/search?string=" + value;

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
	public void delete(String attribute) throws ServiceNotAvailableException,
			AttributeNotSupportedException {

		// Remove not supported
		throw new ServiceNotAvailableException("Delete not supported.");
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

	@Override
	public void setSetting(String name, String value) {
		// Do nothing, since there are no settings for the Dime URS
	}

}
