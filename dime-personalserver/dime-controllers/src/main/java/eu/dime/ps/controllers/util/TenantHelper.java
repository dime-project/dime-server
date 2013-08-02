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


package eu.dime.ps.controllers.util;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.storage.entities.Tenant;

/**
 *
 * @author simon
 */
public class TenantHelper {


    /**
     * returns a tenant by id (database lookup)
     * @param tenantId
     * @return
     */
    public static  Tenant getTenant(Long tenantId){
        Tenant result = Tenant.find(tenantId);
        if (result==null){
            throw new TenantNotFoundException("Unable to find tenant with id: "+tenantId+" in database!");
        }
        return result;
    }

    public static Tenant getCurrentTenant(){
        return getTenant(getCurrentTenantId());
    }

    public static Long getCurrentTenantId() {
        Long result = TenantContextHolder.getTenant();
        if (result==null){
            throw new TenantNotFoundException("Tenant id is not set in TenantContextHolder!");
        }
        return result;
    }

}
