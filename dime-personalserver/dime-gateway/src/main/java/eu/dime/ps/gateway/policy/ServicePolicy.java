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
