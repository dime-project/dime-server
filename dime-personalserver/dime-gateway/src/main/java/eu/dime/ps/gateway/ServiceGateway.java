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

package eu.dime.ps.gateway;

import java.util.Map;

import javax.naming.NamingException;

import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.external.DimeUserResolverServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.entities.Tenant;

public interface ServiceGateway {

	@Deprecated
	public Map<String, ServiceMetadata> listSupportedAdapters();
	
	/**
	 * Lists classnames and authorization servlet URL of all the currently available adapters
	 * 
	 * @return Map<String adapterName, String authURL>
	 */
	public Map<String, ServiceMetadata> listSupportedAdapters(String accountName);

	/**
	 * Retrieves a defined service adapter. If connection is not yet open
	 * establishes connection to remote service.
	 * 
	 * @param identifier
	 *            unique string identifying the adapter along with authenticated
	 *            user
	 * @return ServiceAdapter
	 */
	public ServiceAdapter getServiceAdapter(String identifier, Tenant localTenant)
			throws ServiceNotAvailableException, ServiceAdapterNotSupportedException;
	
	/**
	 * Retrieves a defined service adapter by name for Services with a unique adapter. If connection is not yet open
	 * establishes connection to remote service.
	 * 
	 * @return
	 * @throws ServiceNotAvailableException
	 */
	public DimeUserResolverServiceAdapter getDimeUserResolverServiceAdapter() throws ServiceNotAvailableException ;
	
	
	/**
	 * Creates a dime service adapter.
	 * 
     * @param identifier
     * @param localTenant
     * @return
	 * @throws ServiceNotAvailableException 
	 */
	public DimeServiceAdapter getDimeServiceAdapter(String identifier) throws ServiceNotAvailableException;

//	/**
//	 * Stores a defined service adapter.
//	 * 
//	 * @param ServiceAdapter
//	 * @throws ServiceAdapterNotSupportedException 
//	 */
//	public void setServiceAdapter(ServiceAdapter adapter) throws ServiceAdapterNotSupportedException;

	/**
	 * Stores a defined service adapter.
	 * 
	 * @param identifier
	 *            unique string identifying the adapter along with authenticated
	 *            user
	 * @throws ServiceNotAvailableException 
	 * @throws InvalidLoginException 
	 */
	public void unsetServiceAdapter(String identifier) throws InvalidLoginException, ServiceNotAvailableException;

	/**
	 * Returns the service metadata for a defined service adapter
	 * @param adapterName
	 * @throws ServiceNotAvailableException 
	 * @return ServiceMetadata
	 */
	public ServiceMetadata getServiceMetadata(String adapterName, String accountName);
	
	/**
	 * Creates a new service adapter corresponding to adapterName
	 * @param adapterName
	 * @return ServiceAdapter
	 * @throws Exception 
	 */
	public ServiceAdapter makeServiceAdapter(String adapterName) throws Exception;

        public boolean isHiddenServiceAdapter(String adapterName);
}
