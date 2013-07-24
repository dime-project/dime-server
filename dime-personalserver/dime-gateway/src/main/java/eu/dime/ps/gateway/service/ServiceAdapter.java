package eu.dime.ps.gateway.service;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;

/**
 * Allows accessing an external service and returns di.me objects corresponding
 * to the requested information.
 *
 * @author Sophie.Wrobel
 *
 */
public interface ServiceAdapter {

    /**
     * Retrieves the service identifier - this is the full URI identifier in
     * form urn:account:<TYPE-ADAPTER>-<INTERNALID>
     *
     * @return Unique internal Service Identifier for this serviceAdapter
     * instance
     */
    public String getIdentifier();

    public void setIdentifer(String identifier);

    /**
     * Sets the value of a service-specific di.me setting. This is saved in the
     * policy manager in services.properties as both a adapter-specific setting,
     * and in the adapter-specific SETTINGS json output string.
     *
     * @param name
     * @param value
     * @throws ServiceNotAvailableException
     */
    public void setSetting(String name, String value) throws ServiceNotAvailableException;

    /**
     * Sets the policy manager.
     *
     * @param policyManager
     */
    public void setPolicyManager(PolicyManager policyManager);

    /**
     * Retrieves an attribute. This method invokes the Datamining Transformer
     * component in order to convert raw data from the service to a di.me
     * resource.
     *
     * Attributes should follow the di.me Personal Server API naming
     * conventions:
     * https://docs.google.com/spreadsheet/ccc?key=0Av-4EU0c8D8TdDlaZ0lmMEU5cS1pbGdGRlRmU255V1E
     *
     * Example: Contact c = (Contact) service.get("/profiles/{@me
     * pid}/profileId/");
     *
     * @param attribute to retrieve
     * @return Resource as provided by transformer
     * @throws AttributeNotSupportedException
     * @throws ServiceNotAvailableException
     * @throws InvalidLoginException
     */
    public <T extends Resource> Collection<T> get(String attribute, Class<T> returnType)
            throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, ServiceException;

    /**
     * Updates an attribute. Does not propagate update to remote service. Not
     * implemented at this time.
     *
     * Attributes should follow the di.me Personal Server API naming
     * conventions:
     * https://docs.google.com/spreadsheet/ccc?key=0Av-4EU0c8D8TdDlaZ0lmMEU5cS1pbGdGRlRmU255V1E
     *
     * Example: service.set("/profiles/@me/", profile);
     *
     * @param attribute to set
     * @param value Resources containing values to be transmitted to the service
     * @throws AttributeNotSupportedException
     * @throws ServiceNotAvailableException
     * @throws InvalidDataException
     */
    public void set(String attribute, Object value)
            throws AttributeNotSupportedException, ServiceNotAvailableException,
            InvalidDataException;

    /**
     * Searches a particular attribute for a particular set of values. Not
     * implemented at this time.
     *
     * Attributes should follow the di.me Personal Server API naming
     * conventions:
     * https://docs.google.com/spreadsheet/ccc?key=0Av-4EU0c8D8TdDlaZ0lmMEU5cS1pbGdGRlRmU255V1E
     *
     * Example: Collection results = service.search("/persons/@me/@all",
     * personWithOnlyNameAttribute);
     *
     * @param attribute to search against
     * @param values Resource containing only the values to be searched and no
     * other values
     * @return collection of Resources as provided by transformer
     * @throws ServiceNotAvailableException
     */
    public <T extends Resource> Collection<T> search(String attribute, Resource values, Class<T> returnType) throws ServiceNotAvailableException, ServiceException;

    /**
     * Searches all attributes for a particular set of values. This method will
     * also put the fetched attribute to the RDF store. Not implemented at this
     * time.
     *
     * Example: Collection results = service.search(textphrase);
     *
     * @param values Resource containing only the values to be searched and no
     * other values
     * @return collection of Resources as provided by transformer
     * @throws ServiceNotAvailableException
     * @throws InvalidLoginException
     * @throws AttributeNotSupportedException
     */
    public <T extends Resource> Collection<T> search(Resource values, Class<T> returnType) throws ServiceNotAvailableException, AttributeNotSupportedException, InvalidLoginException, ServiceException;

    /**
     * Processes an asynchronous response from a remote server. Not implemented
     * at this time.
     *
     * @param attribute to expect as answer from remote server
     * @param value to expect as answer from remote server
     * @throws ServiceNotAvailableException
     */
    public void response(String attribute, Resource value) throws ServiceNotAvailableException;

    /**
     * Removes an attribute. Does not propagate to remove on remote service. Not
     * implemented at this time.
     *
     * Example: service.delete("/people/" + userId + "/gender");
     *
     * @param attribute to delete
     * @throws ServiceNotAvailableException
     * @throws AttributeNotSupportedException
     */
    public void delete(String attribute) throws ServiceNotAvailableException, AttributeNotSupportedException;

    /**
     * Returns the name of the adapter (e.g. "LinkedIn")
     *
     * @return name of class
     */
    public String getAdapterName();

    /**
     * Returns whether a connection exists.
     *
     * @return TRUE if connection exists, FALSE otherwise.
     */
    public Boolean isConnected();

    /**
     *
     * @return
     */
    public List<SAdapterSetting> getSettings();

    /**
     * will normally be called directly after instantiation
     * gives the adapter a chance to update settings and other
     * values from metaData
     * @param metaData
     */
    public void initFromMetaData(ServiceMetadata metaData);
}
