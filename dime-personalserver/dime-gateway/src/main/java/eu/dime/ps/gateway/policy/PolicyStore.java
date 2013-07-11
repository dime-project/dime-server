package eu.dime.ps.gateway.policy;

/**
 * Responsible to store and retrieve server or user/tenant specific settings 
 * @author marcel
 *
 */
public interface PolicyStore {
	
	public void storeOrUpdate(String key, String value, Long tenantId);
	
	public void storeOrUpdate(String key, String value, Long tenantId, String appliesTo,
			String targetElement, Boolean allowOveride);
	
	public String getValue(String key, Long tenantId);
}
