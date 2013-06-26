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
