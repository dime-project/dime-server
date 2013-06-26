/**
 * 
 */
package eu.dime.ps.gateway.policy;

import java.util.Collection;

import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * @author Sophie.Wrobel
 *
 */
public interface ServicePolicy {
	
	/**
	 * Determines whether the policy applies to a particular attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public boolean appliesTo (String attribute);

	/**
	 * Executes registered policies before a get operation.
	 * 
	 * @param adapter
	 * @param attribute
	 * @param returnType
	 */
	public <T> void before_get(ServiceAdapter adapter, String attribute, Class<T> returnType);

	/**
	 * Executes registered policies after a get operation.
	 * 
	 * @param adapter
	 * @param attribute
	 * @param returnType
	 * @param data
	 * @return
	 */
	public <T> Collection<T> after_get(ServiceAdapter adapter, String attribute, Class<T> returnType, Collection<T> data);
	
	/**
	 * Executes registered policies before a set operation.
	 * 
	 * @param adapter
	 * @param attribute
	 * @param value
	 * @return
	 */
	public Object before_set(ServiceAdapter adapter, String attribute, Object value);

	/**
	 * Executes registered policies after a set operation.
	 * 
	 * @param adapter
	 * @param attribute
	 * @param value
	 */
	public void after_set(ServiceAdapter adapter, String attribute, Object value);
}
