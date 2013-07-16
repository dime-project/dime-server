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
