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

package eu.dime.ps.gateway.policy;

/**
 * Responsible to store and retrieve global server or user/tenant specific settings 
 * @author marcel
 *
 */
public interface PolicyStore {
	
	/**
	 * Store or update tenant specific settings
	 * @param key
	 * @param value
	 * @param tenantId
	 */
	public void storeOrUpdate(String key, String value, Long tenantId);
	
	/**
	 * Store or update tenant specific settings
	 * @param key
	 * @param value
	 * @param tenantId
	 * @param appliesTo
	 * @param targetElement
	 * @param allowOveride
	 */
	public void storeOrUpdate(String key, String value, Long tenantId, String appliesTo,
			String targetElement, Boolean allowOveride);
	
	/**
	 * Retrieve tenant specific settings
	 * @param key
	 * @param tenantId
	 * @return
	 */
	public String getValue(String key, Long tenantId);
	
	/**
	 * Store or update global settings
	 * @param key
	 * @param value
	 */
	public void storeOrUpdate(String key, String value);
	
	/**
	 * Retrieve global settings
	 * @param key
	 * @return
	 */
	public String getValue(String key);

	/**
	 * Store or update global settings
	 * @param key
	 * @param value
	 * @param appliesTo
	 * @param allowOveride
	 * @param targetElement
	 */
	void storeOrUpdate(String key, String value, String appliesTo,
			Boolean allowOveride, String targetElement);

	String getValueThatAppliesTo(String key, String appliesTo);
}
