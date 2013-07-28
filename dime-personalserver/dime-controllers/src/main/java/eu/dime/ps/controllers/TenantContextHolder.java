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
