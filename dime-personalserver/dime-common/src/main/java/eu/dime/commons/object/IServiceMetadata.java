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

/**
 * 
 */
package eu.dime.commons.object;

/**
 * @author Sophie.Wrobel
 *
 */
public interface IServiceMetadata extends AbstractDimeObject {
	
	public static final String STATUS_ACTIVE="active";
	public static final String STATUS_INACTIVE="inactive";
	public static final String STATUS_UNUSED="unused";
	
	/**
	 * Retrieve the adapter name (eg. LinkedIn).
	 * 
	 * @return 
	 */
	public String getAdapterName();

	/**
	 * Retrieve the status.
	 * 
	 * @return 
	 */
	public String getStatus();

	/**
	 * Retrieve the status.
	 * 
	 * @return 
	 */
	public String getGuid();
	
	/**
	 * Retrieve the URL to the authorization servlet.
	 * 
	 * @return 
	 */
	public String getAuthURL();

	/**
	 * Retrieve the URL to the service's icon.
	 * 
	 * @return 
	 */
	public String getIcon();

	/**
	 * Retrieve the service settings
	 * 
	 * @return empty string if there are no settings, otherwise a JSON array of the service settings
	 */
	public String getSettings();
	

	/**
	 * Retrieve the service description
	 * 
	 * @return empty string if there are no settings, otherwise a short description of the service
	 */
	public String getDescription();

}
