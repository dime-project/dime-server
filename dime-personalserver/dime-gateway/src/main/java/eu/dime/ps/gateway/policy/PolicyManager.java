/**
 * 
 */
package eu.dime.ps.gateway.policy;

import java.util.Collection;

import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * Allows registration of policy plugins and accessing policy setting values.
 * 
 * @author Sophie.Wrobel
 *
 */
public interface PolicyManager {

	/**
	 * Retrieves a policy setting value as an Integer value. Returns null if the policy does not exist.
	 * 
	 * @param policyName
	 * @param adapterId The ID of the requesting Adapter, or null for global policy
	 * @return Integer
	 */
	public Integer getPolicyInteger(String policyName, String adapterId);

	/**
	 * Retrieves a policy setting value as a String value. Returns null if the policy does not exist.
	 * 
	 * @param policyName
	 * @param adapterId The ID of the requesting Adapter, or null for global policy
	 * @return String
	 */
	public String getPolicyString(String policyName, String adapterId);
	
	/**
	 * Sets a policy setting value with system-wide scope.
	 * 
	 * @param policyName
	 * @param value
	 */
	public void setGlobalPolicy(String policyName, String value);
	
	/**
	 * Sets a policy setting value with adapter-level scope.
	 * 
	 * @param policyName
	 * @param adapterId
	 * @param value
	 */
	public void setAdapterPolicy(String policyName, String adapterId, String value);
	
	/**
	 * Registers a policy plugin to modify operations.
	 * 
	 * @param plugin
	 */
	public void registerPolicyPlugin(ServicePolicy plugin);
	
	/**
	 * Executes registered policies before a get operation.
	 * 
	 * @param serviceAdapterImpl
	 * @param attribute
	 * @param returnType
	 */
	public <T> void before_get(ServiceAdapter serviceAdapter, String attribute, Class<T> returnType);

	/**
	 * Executes registered policies after a get operation.
	 * 
	 * @param serviceAdapterWrapper
	 * @param attribute
	 * @param returnType
	 * @param ret
	 * @return
	 */
	public <T> Collection<T> after_get(ServiceAdapter serviceAdapter, String attribute, Class<T> returnType, Collection<T> ret);
	
	/**
	 * Executes registered policies before a set operation.
	 * 
	 * @param serviceAdapterWrapper
	 * @param attribute
	 * @param value
	 * @return
	 */
	public Object before_set(ServiceAdapter serviceAdapter, String attribute, Object value);

	/**
	 * Executes registered policies after a set operation.
	 * 
	 * @param serviceAdapterWrapper
	 * @param attribute
	 * @param value
	 */
	public void after_set(ServiceAdapter serviceAdapter, String attribute, Object value);
	
}
