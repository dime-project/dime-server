package eu.dime.ps.gateway.service.external;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.proxy.HttpRestProxy;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.ServiceResponse;

/**
 * @author Sophie.Wrobel
 * 
 */
public class AMETICDummyAdapter extends BasicAuthServiceAdapter implements ExternalServiceAdapter {

	private static final Logger logger = LoggerFactory.getLogger(AMETICDummyAdapter.class);

	// Mapping to all known resources
	HttpRestProxy proxy;
	public final static String adapterName = "AMETICDummyAdapter";

	// Connection parameters
	private String serviceURL = null;
	private String username = null;
	private String password = null;
	private String realm;
	private int port;

	public AMETICDummyAdapter()
			throws ServiceNotAvailableException {
		super("FIXME");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.dime.ps.communications.services.ServiceAdapter#getAdapterName()
	 */
	public String getAdapterName() {
		return AMETICDummyAdapter.adapterName;
	}

	/* (non-Javadoc)
	 * @see eu.dime.ps.communications.services.ExternalServiceAdapter#_set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void _set(String attribute, Object value)
			throws AttributeNotSupportedException, ServiceNotAvailableException {
		throw new AttributeNotSupportedException(attribute, this);
	}

	@Override
	public void _delete(String attribute)
			throws AttributeNotSupportedException, ServiceNotAvailableException {
		throw new AttributeNotSupportedException(attribute, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.dime.ps.communications.services.AbstractServiceAdapter#getRaw(java
	 * .lang.String)
	 */
	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException, ServiceNotAvailableException {

		if (!this.isConnected()) {
			throw new ServiceNotAvailableException();
		}
		
		// Retrieve attribute from service
		AttributeMap attributeMap = new AttributeMap();
		String genericAttribute = attributeMap.getAttribute(attribute);
		Map<String, String> ids = attributeMap.extractIds(genericAttribute,
				attribute);

		// Retrieve all users attending an event
		// Attribute: /events/@me/{eid}/@all
		if (genericAttribute.equals(AttributeMap.EVENT_ATTENDEES)) {
			String url = "/ameticevents/" + ids.get(AttributeMap.EVENT_ID) + "/@all";
			ServiceResponse[] r = new ServiceResponse[1];
			r[0] = new ServiceResponse(ServiceResponse.XML, attribute, url, this.proxy.get(url));
			return r;
		}

		// Retrieve event details
		// Attribute: /events/@me/{eid}
		else if (genericAttribute.equals(AttributeMap.EVENT_DETAILS)) {
			String url = "/ameticevents/" + ids.get(AttributeMap.EVENT_ID) + "/info";
			ServiceResponse[] r = new ServiceResponse[1];
			r[0] = new ServiceResponse(ServiceResponse.XML, attribute, url, this.proxy.get(url));
			return r;
		}

		// Retrieve event details
		// Attribute: /events/@all
		else if (genericAttribute.equals(AttributeMap.EVENT_ALL)) {
			String url = "/ameticevents/@all";
			ServiceResponse[] r = new ServiceResponse[1];
			r[0] = new ServiceResponse(ServiceResponse.XML, attribute, url, this.proxy.get(url));
			return r;
		}

		// No action found - throw an exception.
		throw new AttributeNotSupportedException(attribute, this);
	}


	/********************
	 * Unused functions *
	 ********************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.dime.ps.communications.services.ServiceAdapter#isConnected()
	 */
	@Override
	public Boolean isConnected() {
		/*
		try {
			// TODO USIEGEN added by Mohamed: Provide here the access to the
			// access control repository!
			this.serviceURL = this.policyManager.getPolicyString("SERVICEURL", "AMETICDUMMY");
			this.username = this.policyManager.getPolicyString("USER", "AMETICDUMMY");
			this.password = this.policyManager.getPolicyString("PASS", "AMETICDUMMY");
			this.realm = this.policyManager.getPolicyString("REALM", "AMETICDUMMY");
			this.port = this.policyManager.getPolicyInteger("PORT", "AMETICDUMMY");
			this.proxy = new HttpRestProxy(new URL(this.serviceURL), this.port,
					this.realm, this.username, this.password);
		} catch (MalformedURLException e) {
			throw new ServiceNotAvailableException(e);
		} catch (IOException e) {
			throw new ServiceNotAvailableException(e);
		} */
		return (this.proxy != null);
	}

}
