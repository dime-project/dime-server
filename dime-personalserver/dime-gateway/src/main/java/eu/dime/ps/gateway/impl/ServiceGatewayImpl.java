package eu.dime.ps.gateway.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ArrayUtils;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.external.AMETICDummyAdapter;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.gateway.service.external.KMLAdapter;
import eu.dime.ps.gateway.service.external.oauth.DoodleServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.FitbitServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.GooglePlusServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.LinkedInServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.OAuthServiceAdapter;
import eu.dime.ps.gateway.service.external.oauth.TwitterServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.gateway.service.noauth.LocationServiceAdapter;
import eu.dime.ps.gateway.service.noauth.ProximityServiceAdapter;
import eu.dime.ps.gateway.service.noauth.SocialRecommenderAdapter;
import eu.dime.ps.storage.entities.ServiceAccount;

/**
 * Constructs and configure the various service adapters for communicating with
 * third-party APIs.
 * 
 * @author Ismael Rivera
 * @author Sophie Wrobel
 */
public class ServiceGatewayImpl implements ServiceGateway {

	private static final Logger logger = LoggerFactory.getLogger(ServiceGatewayImpl.class);

	protected ConcurrentMap<String, ServiceAdapter> adapters;
	protected ConcurrentMap<String, ServiceMetadata> supportedAdapters;
	protected String[] hiddenAdapters;
	protected PolicyManager policyManager;
	protected ConcurrentMap<String, Class> loadedAdapters;

	private CredentialStore credentialStore;

	@Autowired
	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}

	public ServiceGatewayImpl() {
		this.loadedAdapters = new ConcurrentHashMap<String, Class> ();
		this.loadedAdapters.putIfAbsent(AMETICDummyAdapter.adapterName, AMETICDummyAdapter.class);
		this.loadedAdapters.putIfAbsent(DimeUserResolverServiceAdapter.NAME, DimeUserResolverServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(KMLAdapter.NAME, KMLAdapter.class);
		this.loadedAdapters.putIfAbsent(DoodleServiceAdapter.NAME, DoodleServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(FacebookServiceAdapter.NAME, FacebookServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(FitbitServiceAdapter.NAME, FitbitServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(GooglePlusServiceAdapter.NAME, GooglePlusServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(LinkedInServiceAdapter.NAME, LinkedInServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(TwitterServiceAdapter.NAME, TwitterServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(DimeServiceAdapter.NAME, DimeServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(LocationServiceAdapter.adapterName, LocationServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(ProximityServiceAdapter.adapterName, ProximityServiceAdapter.class);
		this.loadedAdapters.putIfAbsent(SocialRecommenderAdapter.adapterName, SocialRecommenderAdapter.class);
                //FIXME - deactivated for github
		//this.loadedAdapters.putIfAbsent(YMServiceAdapter.adapterName, YMServiceAdapter.class);
		
		this.adapters = new ConcurrentHashMap<String, ServiceAdapter>();
		this.supportedAdapters = new ConcurrentHashMap<String, ServiceMetadata>();
		this.policyManager = PolicyManagerImpl.getInstance();
		if (this.policyManager == null)
			this.policyManager = new PolicyManagerImpl();

		this.hiddenAdapters = this.policyManager.getPolicyString("HIDDEN", "").split(",");
		String policyStr = this.policyManager.getPolicyString("ENABLED", null);
		if (policyStr == null) {
			logger.error("Could not find property ENABLED");
		} else {
			String[] iadapters = policyStr.split(",");
			if (iadapters != null) {
				for (int i = 0; i < iadapters.length; i++) {
					if (iadapters[i] != null) {
						try {
							String iadapter = iadapters[i];
							// ### should be replaced by tenant name
							ServiceMetadata metadata = makeMetadata(iadapter, "###");
							this.supportedAdapters.put(iadapter, metadata);
						} catch (NullPointerException e) {
							logger.error(e.getMessage());
						}
					}
				}
			}
		}
	}


	public ServiceAdapter makeServiceAdapter(String adapterName) throws Exception {

		if (supportedAdapters.containsKey(adapterName)){
			ServiceAdapter adapter = (ServiceAdapter) this.loadedAdapters.get(adapterName).newInstance();

			return adapter;
		} else {
			return null;
		}
	}
	
	@Override
	public ServiceAdapter getServiceAdapter(String identifier) throws ServiceNotAvailableException,
			ServiceAdapterNotSupportedException {
	
		if (identifier == null)
			throw new ServiceNotAvailableException(
					"Cannot find a service adapter if identifier is not specified.");
	
		// the list of adapters is lazily initialized when the adapters are
		// requested
		if (!this.adapters.containsKey(identifier)) {
			ServiceAccount account = ServiceAccount.findAllByAccountUri(identifier);
			ServiceAdapter adapter = buildAdapter(account);
	
			if (adapter == null)
				throw new ServiceNotAvailableException(
						"Cannot find a service adapter for the account " + identifier);
	
			this.adapters.put(identifier, adapter);
		}
	
		return this.adapters.get(identifier);
	}

	public DimeServiceAdapter getDimeServiceAdapter(String identifier) throws ServiceNotAvailableException {

		DimeServiceAdapter adapter = new DimeServiceAdapter(identifier);

		return adapter;
	}

	public DimeUserResolverServiceAdapter getDimeUserResolverServiceAdapter()
			throws ServiceNotAvailableException {
	
		String name = DimeUserResolverServiceAdapter.NAME;
	
		if (!this.adapters.containsKey(name)) {
			DimeUserResolverServiceAdapter adapter = new DimeUserResolverServiceAdapter();
			
	
			this.adapters.put(name, adapter);
		}
	
		return (DimeUserResolverServiceAdapter) this.adapters.get(name);
	
	}

	@Override
	public Map<String, ServiceMetadata> listSupportedAdapters(String accountName) {
		HashMap<String, ServiceMetadata> adapters = new HashMap<String, ServiceMetadata>();
		Iterator<String> iter = this.supportedAdapters.keySet().iterator();
		while (iter.hasNext()) {
			String adapterName = iter.next();
			if (!ArrayUtils.contains(this.hiddenAdapters, adapterName)) {
                                logger.info("show adapter: "+adapterName);
				adapters.put(adapterName, makeMetadata(adapterName, accountName));
				this.supportedAdapters.put(adapterName, makeMetadata(adapterName, accountName));
			} else {
                                logger.info("hide adapter: "+adapterName);
				this.supportedAdapters.remove(adapterName);
			}
		}
		return this.supportedAdapters;
	}

	@Override
	@Deprecated
	public Map<String, ServiceMetadata> listSupportedAdapters() {
		return this.listSupportedAdapters("new");
	}

	// Generate URL that the browser should call to trigger OAuthAuthorization
	public String getHost(String tenantName) {
		return "https://" + this.policyManager.getPolicyString("AUTHSERVLET_HOST", null) + ":"
				+ this.policyManager.getPolicyString("AUTHSERVLET_PORT_SECURE", null)
				+ this.policyManager.getPolicyString("AUTHSERVLET_PATH", null) + "/services/"
				+ tenantName;
	}

	@Override
	public ServiceMetadata getServiceMetadata(String adapterName, String accountName) {
		return makeMetadata(adapterName, accountName);
	}

	@Override
	public void unsetServiceAdapter(String identifier) throws InvalidLoginException,
			ServiceNotAvailableException {
		ServiceAdapter adapter = this.adapters.get(identifier);
		if (adapter == null) {
			throw new ServiceNotAvailableException("Adapter with identifier " + identifier + " does not exist.");
		}
		this.adapters.remove(identifier);
	}

	/**
	 * Creates metadata for a new serviceAdapter
	 * 
	 * @param adapterName
	 * @return
	 */
	private ServiceMetadata makeMetadata(String adapterName, String accountName) {
		String settings = this.policyManager.getPolicyString("SETTINGS", adapterName);
		String description = this.policyManager.getPolicyString("DESCRIPTION", adapterName);
		
		if (this.policyManager.getPolicyString("SETTINGS", accountName) != null && this.policyManager.getPolicyString("SETTINGS", accountName).length() > 0)
			settings = this.policyManager.getPolicyString("SETTINGS", accountName);
		
		// Note: We set the GUID to adapterName so that client-side account creation can return this back to the server.
		return new ServiceMetadata(adapterName, adapterName, description, this.getAuthServlet(
				adapterName, accountName), ServiceMetadata.STATUS_UNUSED,
				this.policyManager.getPolicyString("ICON", adapterName), settings);
	}

	private String getAuthServlet(String adapterName, String tenantName) {
		if (this.policyManager.getPolicyString("AUTHSERVLET", adapterName) == null || this.policyManager.getPolicyString("AUTHSERVLET", adapterName).length() == 0)
			return null;
		else
			return this.getHost(tenantName)
				+ this.policyManager.getPolicyString("AUTHSERVLET", adapterName);
	}

	private ServiceAdapter buildAdapter(ServiceAccount account)
			throws ServiceNotAvailableException, ServiceAdapterNotSupportedException {
		ServiceAdapter adapter = null;
		if (account == null) {
			throw new ServiceAdapterNotSupportedException(new Exception(
					"You need to specify an account."));
		}

		String adapterName = account.getServiceProvider().getServiceName();

		// instanciate the proper service adapter
		if (LinkedInServiceAdapter.NAME.equals(adapterName)) {
			adapter = new LinkedInServiceAdapter();
		} else if (TwitterServiceAdapter.NAME.equals(adapterName)) {
			adapter = new TwitterServiceAdapter();
		} else if (FacebookServiceAdapter.NAME.equals(adapterName)) {
			adapter = new FacebookServiceAdapter();
		} else if (FitbitServiceAdapter.NAME.equals(adapterName)) {
			adapter = new FitbitServiceAdapter();
		} else if (GooglePlusServiceAdapter.NAME.equals(adapterName)) {
			adapter = new GooglePlusServiceAdapter();
		} else if (SocialRecommenderAdapter.adapterName.equals(adapterName)) {
			adapter = new SocialRecommenderAdapter();
		} else if (LocationServiceAdapter.adapterName.equals(adapterName)) {
			adapter = new LocationServiceAdapter();
		} else if (ProximityServiceAdapter.adapterName.equals(adapterName)) {
			adapter = new ProximityServiceAdapter();
                //FIXME - deactivated for github 
//		} else if (YMServiceAdapter.adapterName.equals(adapterName)) {
//			adapter = new YMServiceAdapter();
		} else if (DimeServiceAdapter.NAME.equals(adapterName)) {
			adapter = new DimeServiceAdapter(account.getName());
		}
		
		// check if an adapter has been created for the account
		if (adapter == null) {
			throw new ServiceAdapterNotSupportedException(new Exception(adapterName
					+ " is not a supported service."));
		}

		if (adapter instanceof OAuthServiceAdapter) {
			// sets consumer token
			String consumerKey = credentialStore.getConsumerKey(adapterName);
			String consumerSecret = credentialStore.getConsumerSecret(adapterName);
			((OAuthServiceAdapter) adapter)
					.setConsumerToken(new Token(consumerKey, consumerSecret));

			// sets user access token
			((OAuthServiceAdapter) adapter).setAccessToken(new Token(account.getAccessToken(),
					account.getAccessSecret()));
		}

		// set common attributes for any type of service adapter
		adapter.setIdentifer(account.getAccountURI());

		return adapter;
	}

}
