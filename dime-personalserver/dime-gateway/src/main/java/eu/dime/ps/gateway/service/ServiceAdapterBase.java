package eu.dime.ps.gateway.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.commons.dto.SAdapter;
import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.commons.object.ServiceMetadata;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.RateLimitException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.service.external.oauth.FacebookServiceAdapter;
import eu.dime.ps.gateway.transformer.FormatUtils;
import eu.dime.ps.gateway.transformer.Transformer;
import eu.dime.ps.gateway.transformer.TransformerException;
import eu.dime.ps.gateway.transformer.impl.XSparqlTransformer;

/**
 * @author Sophie.Wrobel
 *
 */
public abstract class ServiceAdapterBase implements ServiceAdapter {

    protected String identifier;
    protected Map<String, Object> attributes;
    protected Map<String, String[]> rawAttributes;
    protected Transformer transformer;
    protected PolicyManager policyManager;
    // Rate limiting: -1 = no rate limiting
    protected int rateLimit = -1;
    protected int MAX_RATE_LIMIT = -1;
    protected List<Long> callLog = new ArrayList<Long>();
    // Display adapter in web UI
    protected boolean display = true;
    // Whether adapter has special configuration settings in di.me
    protected SAdapter sadapter;

    /**
     * Constructor Generates a random ID for the service adapter.
     *
     * @throws ServiceNotAvailableException
     */
    public ServiceAdapterBase() throws ServiceNotAvailableException {
        this.identifier = "urn:uuid:" + UUID.randomUUID().toString();
        this.policyManager = PolicyManagerImpl.getInstance();
        this.attributes = new HashMap<String, Object>();
        this.rawAttributes = new HashMap<String, String[]>();
        this.callLog = new ArrayList<Long>();
        this.sadapter = new SAdapter();

        this.sadapter.setGuid(identifier);
        this.sadapter.setName(this.getAdapterName());
        this.sadapter.setType(this.getAdapterName());
    }

    /**
     * Constructor
     *
     * @param identifier for the service adapter.
     * @throws ServiceNotAvailableException
     */
    public ServiceAdapterBase(String identifier) throws ServiceNotAvailableException {
        this();
        this.setIdentifer(identifier);
    }

    @Override
    public void initFromMetaData(ServiceMetadata metaData) {
        this.sadapter.importSettings(metaData.getSettings());
    }



    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public boolean getDisplayValue() {
        return this.display;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifer(String identifier) {
        this.identifier = identifier;
        this.sadapter.setGuid(identifier);
    }

    // TODO what is this doing? it's not used by anything!!
//	@Override
//    public Token getUserToken() throws ServiceNotAvailableException {
//    	return new Token(this.policyManager.getPolicyString("USER_TOKEN", this.identifier), this.policyManager.getPolicyString("USER_SECRET", this.identifier));
//    }
    public List<SAdapterSetting> getSettings() {
        return this.sadapter.getSettings();
    }

    /**
     *
     * @param name
     * @param value
     */
    public void setSetting(String name, String value) {
        this.policyManager.setAdapterPolicy(name, this.identifier, value);
        this.sadapter.updateSetting(name, value);
        this.policyManager.setAdapterPolicy(name, this.identifier, value);
    }

    /**
     * Retrieves an attribute. This method returns the raw data from the
     * service. In case multiple objects are returned, the objects will be
     * sequentially listed in the returned string array.
     *
     * Attributes should follow the di.me Personal Server API naming
     * conventions: https://docs.google.com/spreadsheet/ccc?key=0Av-
     * 4EU0c8D8TdDlaZ0lmMEU5cS1pbGdGRlRmU255V1E
     *
     * Example: String profileData = service.get("/profiles/{@me pid}
     * /profileId/");
     *
     * @param attribute to retrieve
     * @return array with one API call per element
     * @throws AttributeNotSupportedException
     * @throws ServiceNotAvailableException
     * @throws InvalidLoginException
     */
    public abstract ServiceResponse[] getRaw(String attribute)
            throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, ServiceException;

    public <T extends Resource> Collection<T> get(String attribute, Class<T> returnType)
            throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, ServiceException {
        this.checkRateLimit();
        this.policyManager.before_get(this, attribute, returnType);
        Collection<T> ret = this._get(attribute, returnType);
        ret = (Collection<T>) this.policyManager.after_get(this, attribute, returnType, ret);
        return ret;
    }

    protected <T extends Resource> Collection<T> _get(String attribute, Class<T> returnType)
            throws AttributeNotSupportedException, ServiceNotAvailableException, InvalidLoginException, ServiceException {
        try {
            String xml = null;
            ServiceResponse response = this.getRaw(attribute)[0];
            if (ServiceResponse.XML.equals(response.getResponseType())) {
                xml = response.getResponse();
            } else if (ServiceResponse.JSON.equals(response.getResponseType())) {
                xml = response.getResponse();

                // TODO this XML->JSON transformation fails for Facebook, we need to handle this in a better way, this
                // is not the place but works until we do something better
                if (FacebookServiceAdapter.NAME.equals(getAdapterName())
                        && attribute.contains("livepost")) {
                    xml = FormatUtils.removeStoryTags(xml);
                }

                xml = FormatUtils.convertJSONToXML(xml, null);
            } else {
                throw new ServiceNotAvailableException("Error parsing XML response: Format " + response.getResponseType() + " is not supported by the transformer.");
            }

            // process XML response with the transformer
            return getTransformer().deserialize(xml, this.getAdapterName(), attribute, returnType);
        } catch (TransformerException e) {
            throw new ServiceNotAvailableException("Error parsing XML response: " + e.getMessage(), e);
        }
    }

    public void set(String attribute, Object value)
            throws AttributeNotSupportedException, ServiceNotAvailableException,
            InvalidDataException {
        this.checkRateLimit();
        policyManager.before_set(this, attribute, value);
        this.attributes.put(attribute, value);
        this._set(attribute, value);
        policyManager.after_set(this, attribute, value);
    }

    /**
     * Posts an update to a remote di.me note, e.g. send a notification.
     *
     * @param attribute The attribute to set (e.g. "/notification")
     * @param value The object to be posted to the remote server
     * @throws AttributeNotSupportedException
     * @throws ServiceNotAvailableException
     */
    protected void _set(String attribute, Object value)
            throws AttributeNotSupportedException, ServiceNotAvailableException,
            InvalidDataException {
        // METOD OVERWRITED ON DimeServiceAdaptor
    }

    public void delete(String attribute)
            throws AttributeNotSupportedException, ServiceNotAvailableException {
        this.attributes.remove(attribute);
        this._delete(attribute);
        this.rawAttributes.remove(attribute);
    }

    /**
     * How many API calls can be made per hour. -1 for no limit.
     *
     * @return
     */
    public int getRateLimit() {
        return this.rateLimit;
    }

    /**
     * Set the hourly API call rate limit for this service adapter.
     *
     * @param ratelimit Use -1 for no rate limit
     */
    public void setRateLimit(int ratelimit) {
        if (ratelimit < this.rateLimit && MAX_RATE_LIMIT >= 0) {
            this.rateLimit = ratelimit;
        } else {
            this.rateLimit = MAX_RATE_LIMIT;
        }
    }

    /**
     * Checks to make sure that the rate limit is not exceeded. This function
     * should be called before sending any request to the twitter adapter.
     *
     * @throws ServiceNotAvailableException if the rate limit is exceeded.
     */
    protected void checkRateLimit() throws RateLimitException {
        // Skip indefinite rate limit
        if (this.rateLimit < 0) {
            return;
        }

        // Check rate limit
        long now = System.currentTimeMillis();
        if (this.callLog == null) {
            this.callLog = new ArrayList<Long>();
        }
        if (this.callLog.size() > 0) {
            while (this.callLog.get(0) < now - 3600000) {
                this.callLog.remove(0);
            }
        }
        if (this.callLog.size() > this.rateLimit) {
            throw new RateLimitException("Rate limit exceeded.");
        }
        this.callLog.add(now);
    }

    /**
     * Posts an delete request to a remote service, e.g. delete a profile
     *
     * @param attribute The attribute to delete (e.g. "/notification")
     * @throws AttributeNotSupportedException
     * @throws ServiceNotAvailableException
     */
    protected void _delete(String attribute)
            throws AttributeNotSupportedException, ServiceNotAvailableException {
        throw new AttributeNotSupportedException(attribute, this);
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public <T extends Resource> Collection<T> search(String attribute,
            Resource values, Class<T> returnType) throws ServiceNotAvailableException, ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends Resource> Collection<T> search(Resource values,
            Class<T> returnType) throws ServiceNotAvailableException, ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public void register(String username, String password)
            throws InvalidLoginException, ServiceNotAvailableException {
        // TODO Auto-generated method stub
    }

    public void unregister()
            throws InvalidLoginException, ServiceNotAvailableException {
        // Note: OAuth does not support programatic revoking.
    }

    public void response(String attribute, Resource value) throws ServiceNotAvailableException {
        // TODO Auto-generated method stub
    }

    protected Transformer getTransformer() {
        if (this.transformer == null) {
            this.transformer = new XSparqlTransformer();
        }

        return this.transformer;
    }

    public class AdapterSetting {

        private String name;
        private String value;
        private boolean required;
        private String type;

        public AdapterSetting(String name, String value, boolean required, String type) {
            this.name = name;
            this.value = value;
            this.required = required;
            this.type = type;
        }

        public String getName() {
            return this.name;
        }

        public AdapterSetting setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return this.value;
        }

        public AdapterSetting setValue(String value) {
            this.value = value;
            return this;
        }

        public boolean getRequired() {
            return this.required;
        }

        public AdapterSetting setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public String getType() {
            return this.type;
        }

        public AdapterSetting setType(String type) {
            this.type = type;
            return this;
        }
    }
}
