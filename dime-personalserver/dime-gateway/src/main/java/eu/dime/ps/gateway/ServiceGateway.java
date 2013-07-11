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
	public ServiceAdapter getServiceAdapter(String identifier)
			throws ServiceNotAvailableException, ServiceAdapterNotSupportedException;
	
	/**
	 * Retrieves a defined service adapter by name for Services with a unique adapter. If connection is not yet open
	 * establishes connection to remote service.
	 * 
	 * @return
	 * @throws ServiceNotAvailableException
	 * @throws ServiceAdapterNotSupportedException
	 */
	public DimeUserResolverServiceAdapter getDimeUserResolverServiceAdapter() throws ServiceNotAvailableException ;
	
	
	/**
	 * Creates a dime service adapter.
	 * 
	 * @param senderURI
	 * @param receiverURI
	 * @param isAuthenticated
	 * @return
	 * @throws RepositoryStorageException 
	 * @throws NotFoundException 
	 * @throws ServiceNotAvailableException 
	 * @throws NamingException 
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
