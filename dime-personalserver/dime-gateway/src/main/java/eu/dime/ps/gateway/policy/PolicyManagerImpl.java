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

package eu.dime.ps.gateway.policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * @author Sophie.Wrobel
 * 
 */
public class PolicyManagerImpl implements PolicyManager {

	private static final Logger logger = LoggerFactory.getLogger(PolicyManagerImpl.class);

	/**
	 * Distinguishes global policies (default) from adapter policies (overrides global policies) 
	 */
	private static final String GLOBAL_PREFIX = "GLOBAL";
	
	/**
	 * Contains global-level policy setting values
	 */
	private Map<String, String> globalPolicy;
	
	/**
	 * Contains adapter-level policy setting values
	 */
	private Map<String, String> adapterPolicy;
	
	/**
	 * Accessor to the services.properties file 
	 */
	private Properties properties;
	
	/**
	 * List of registered policy plugins
	 */
	private List<ServicePolicy> policyPlugins;
	
	private static final PolicyManagerImpl INSTANCE = new PolicyManagerImpl();
	

	@Autowired
	private PolicyStore policyStore;

	/**
	 * Returns singleton instance.
	 *  
	 * @return the singleton instance
	 */
	public static PolicyManagerImpl getInstance() {
		return INSTANCE;
	}
	
	public void setPolicyStore(PolicyStore policyStore) {
		this.policyStore = policyStore;
	}

	public PolicyManagerImpl() {

		this.globalPolicy = new HashMap<String, String>();
		this.adapterPolicy = new HashMap<String, String>();
		this.policyPlugins = new ArrayList<ServicePolicy>();
		
		try {
			if (this.properties == null || this.properties.size() == 0) {
				this.properties = PropertiesLoaderUtils.loadAllProperties("services.properties");
				Enumeration<?> iterator = this.properties.propertyNames();
				while (iterator.hasMoreElements()) {
					String propertyName = (String) iterator.nextElement();
					if (propertyName.matches("^"+GLOBAL_PREFIX+"_.*")) {
						this.globalPolicy.put(propertyName.substring(
								GLOBAL_PREFIX.length()+1), 
								this.properties.getProperty(propertyName));	
					} else {
						String adapterId = propertyName.replaceFirst("_(.*)$", "");
						this.adapterPolicy.put(clean(adapterId) + "_" + 
								propertyName.substring(adapterId.length()+1), 
								this.properties.getProperty(propertyName));
					}
				}
			}
		} catch (IOException e) {
			logger.warn("Could not load properties: "+e.getMessage(), e);
		}
		
		// bootstrap ServiceProvider entity
		if (this.globalPolicy.containsKey("ENABLED")) {
			
			String[] enabled = this.globalPolicy.get("ENABLED").split(",");
			
			logger.debug("Bootstraping service providers: "+this.globalPolicy.get("ENABLED"));

			// load or update key & secret for the different service providers
			for (String providerName : enabled) {
				providerName = providerName.trim();
				ServiceProvider dbProvider = ServiceProvider.findByName(providerName);

				String key = this.getPolicyString("CONSUMER_KEY", providerName);
				if (key == null || key.trim().equals("")){
					key = "COULD NOT LOAD FROM PREFERENCES";
				}
				
				String secret = this.getPolicyString("CONSUMER_SECRET", providerName);
				if (secret == null || secret.trim().equals("")){
					secret = "COULD NOT LOAD FROM PREFERENCES";
				}					

				// create provider if it does not exist yet
				if (dbProvider == null) {
					dbProvider = EntityFactory.getInstance().buildServiceProvider();
					dbProvider.setEnabled(Boolean.TRUE);
					dbProvider.setServiceName(providerName);
					dbProvider.setConsumerKey(key);
					dbProvider.setConsumerSecret(secret);
					
					dbProvider.persist();
				} else {
					// try to update key and secret
					dbProvider.setConsumerKey(key);
					dbProvider.setConsumerSecret(secret);
					
					dbProvider.merge();
					dbProvider.flush();
				}
			}
			
			// enable/disable providers if they are found in GLOBAL_ENABLED or not
			for (ServiceProvider dbProvider : ServiceProvider.findAll()) {
				dbProvider.setEnabled(ArrayUtils.contains(enabled, dbProvider.getServiceName()));
				dbProvider.merge();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.PolicyManager#getPolicyInteger(java.lang.
	 * String, java.lang.String)
	 */
	public Integer getPolicyInteger(String policyName, String adapterId) {
		Integer value = null;
		policyName = clean(policyName);
		adapterId = clean(adapterId);
		if (globalPolicy.get(policyName) != null) {
			value = Integer.parseInt(globalPolicy.get(policyName));
		}
		if (adapterId != null 
				&& adapterPolicy.get(adapterId + "_" + policyName) != null
				&& adapterPolicy.get(adapterId + "_" + policyName).length() > 0) {
			value = Integer.parseInt(adapterPolicy.get(adapterId + "_" + policyName));
		}
		if (adapterId != null && policyStore != null 
				&& policyStore.getValue(adapterId + "_" + policyName) != null 
				&& policyStore.getValue(adapterId + "_" + policyName).length() > 0) {
			value = Integer.parseInt(policyStore.getValue(adapterId + "_" + policyName));
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.PolicyManager#getPolicyInteger(java.lang.
	 * String, java.lang.String)
	 */
	public String getPolicyString(String policyName, String adapterId) {
		policyName = clean(policyName);
		adapterId = clean(adapterId);
		String value = globalPolicy.get(policyName);
		if (adapterId != null 
				&& adapterPolicy.get(adapterId + "_" + policyName) != null 
				&& adapterPolicy.get(adapterId + "_" + policyName).length() > 0) {
			value = adapterPolicy.get(adapterId + "_" + policyName);
		}
		try {
			if (adapterId != null && policyStore != null 
					&& policyStore.getValue(adapterId + "_" + policyName) != null 
					&& policyStore.getValue(adapterId + "_" + policyName).length() > 0) {
				value = policyStore.getValue(adapterId + "_" + policyName);
			}
		} catch (NullPointerException e) {
			// Ignore - this happens if the key does not exist
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.PolicyManager#setGlobalPolicy(java
	 * .lang.String, java.lang.Integer)
	 */
	public void setGlobalPolicy(String policyName, String value) {
		policyName = clean(policyName);
		
		globalPolicy.put(policyName, value);

		// Write changes to disk
		if (this.properties != null) {
			this.properties.setProperty(
					GLOBAL_PREFIX + "_" + policyName, value.toString());
			if (value.toString().length() > 0)
				policyStore.storeOrUpdate(GLOBAL_PREFIX + "_" + policyName, value.toString());
		} else {
			logger.warn("Could not save policy: services.properties was not loaded.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.PolicyManager#setAdapterPolicy(java
	 * .lang.String, java.lang.String, java.lang.Integer)
	 */
	public void setAdapterPolicy(String policyName, String adapterId,
			String value) {
		policyName = clean(policyName);
		adapterId = clean(adapterId);

		// Note: PolicyStore does not allow deleting, so we set an empty string
		if (value == null)
			value = "";
		adapterPolicy.put(adapterId + "_" + policyName, value);
		policyStore.storeOrUpdate(adapterId + "_" + policyName, value.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.PolicyManager#before_get(eu.dime.ps.communications.services.ServiceAdapter, java.lang.String, java.lang.Class)
	 */
	public <T> void before_get(ServiceAdapter adapter, String attribute, Class<T> returnType) {
		Iterator<ServicePolicy> iter = this.policyPlugins.iterator();
		while (iter.hasNext()) {
			ServicePolicy plugin = iter.next();
			if (plugin.appliesTo(attribute)) {
				plugin.after_set(adapter, attribute, returnType);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.PolicyManager#after_get(eu.dime.ps.communications.services.ServiceAdapter, java.lang.String, java.lang.Class, java.util.Collection)
	 */
	
	public <T> Collection<T> after_get(ServiceAdapter adapter, String attribute, Class<T> returnType, Collection<T> data) {
		Iterator<ServicePolicy> iter = this.policyPlugins.iterator();
		Collection<T> ret = data;
		while (iter.hasNext()) {
			ServicePolicy plugin = iter.next();
			if (plugin.appliesTo(attribute)) {
				ret = plugin.after_get(adapter, attribute, returnType, ret);
			}
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.PolicyManager#before_set(eu.dime.ps.communications.services.ServiceAdapter, java.lang.String, java.lang.Object)
	 */
	public Object before_set(ServiceAdapter adapter, String attribute, Object value) {
		Iterator<ServicePolicy> iter = this.policyPlugins.iterator();
		Object ret = value;
		while (iter.hasNext()) {
			ServicePolicy plugin = iter.next();
			if (plugin.appliesTo(attribute)) {
				ret = plugin.before_set(adapter, attribute, ret);
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.PolicyManager#after_set(eu.dime.ps.communications.services.ServiceAdapter, java.lang.String, java.lang.Object)
	 */
	public void after_set(ServiceAdapter serviceAdapterWrapper,
			String attribute, Object value) {
		Iterator<ServicePolicy> iter = this.policyPlugins.iterator();
		while (iter.hasNext()) {
			ServicePolicy plugin = iter.next();
			if (plugin.appliesTo(attribute)) {
				plugin.after_set(serviceAdapterWrapper, attribute, value);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.policy.PolicyManager#registerPolicyPlugin(eu.dime.ps.communications.services.policy.ServicePolicy)
	 */
	@Override
	public void registerPolicyPlugin(ServicePolicy plugin) {
		this.policyPlugins.add(plugin);
	}
	
	private String clean(String dirty) {
		if (dirty == null)
			return null;
		else
			return dirty.replaceAll(":", "-");
	}

}
