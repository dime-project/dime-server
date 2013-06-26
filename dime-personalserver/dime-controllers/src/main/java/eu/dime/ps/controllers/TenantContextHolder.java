package eu.dime.ps.controllers;

import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as a container to our thread local variables to support multi-tenancy.
 * 
 * @author Ismael Rivera
 */
public class TenantContextHolder {
	
	private static final ThreadLocal<Map<String, Object>> tenantThreadLocal = new ThreadLocal<Map<String, Object>>() {
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		};
	};

	public static void setTenant(Long tenant) {
		tenantThreadLocal.get().put("tenant", tenant);
	}

	public static void unset() {
		tenantThreadLocal.get().clear();
	}
	
	public static void clear() {
		unset();
	}

	public static Long getTenant() {
		return (Long) tenantThreadLocal.get().get("tenant");
	}
	
}
