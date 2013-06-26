/**
 * 
 */
package eu.dime.commons.object;

/**
 * @author Sophie.Wrobel
 *
 */
public class ServiceMetadata implements IServiceMetadata {

	private String adapterName;
	private String authURL;
	private String status;
	private String icon;
	private String guid;
	private String settings;
	private String description;
	
	public static final String STATUS_ACTIVE="active";
	public static final String STATUS_INACTIVE="inactive";
	public static final String STATUS_UNUSED="unused";
	
	/**
	 * Creates a new ServiceMetadata object
	 */
	public ServiceMetadata(String guid, String adapterName, String description, String authURL, String status, String icon, String settings) {
		this.guid = guid;
		this.adapterName = adapterName;
		this.authURL = authURL;
		this.status = status;
		this.icon = icon;
		this.settings = settings;
		this.description = description;
	}

	/**
	 * Retrieve the adapter name (eg. LinkedIn).
	 * 
	 * @return 
	 */
	public String getAdapterName() {
		return this.adapterName;
	}
	
	/**
	 * Retrieve the adapter name (eg. LinkedIn).
	 * 
	 * @return 
	 */
	public String getGuid() {
		return this.guid;
	}

	/**
	 * Retrieve the status.
	 * 
	 * @return 
	 */
	public String getStatus() {
		return this.status;
	}
	
	/**
	 * Retrieve the URL to the authorization servlet.
	 * 
	 * @return 
	 */
	public String getAuthURL() {
		return this.authURL;
	}

	/**
	 * Retrieve the URL to the service's icon.
	 * 
	 * @return 
	 */
	public String getIcon() {
		return this.icon;
	}
	

	/**
	 * Retrieve the service settings
	 * 
	 * @return JSON Array
	 */
	public String getSettings() {
		return this.settings;
	}
	

	/**
	 * Retrieve the description
	 * 
	 * @return 
	 */
	public String getDescription() {
		return this.description;
	}

}
