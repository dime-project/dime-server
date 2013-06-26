package eu.dime.ps.gateway.exception;

import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * @author Sophie.Wrobel
 */
public class AttributeNotSupportedException extends ServiceException {

	private static final long serialVersionUID = 1L;

	private String attribute;
	private ServiceAdapter adapter;
	
	public AttributeNotSupportedException(String attribute, ServiceAdapter adapter) {
		super("Attribute '"+attribute+"' is not supported.", "SERV-002");
		this.attribute = attribute;
		this.adapter = adapter;
	}

	public AttributeNotSupportedException(String attribute, String reason, ServiceAdapter adapter) {
		super("Attribute '"+attribute+"' is not supported: "+reason, "SERV-002");
		this.attribute = attribute;
		this.adapter = adapter;
	}

	public String getAttribute() {
		return attribute;
	}

	public ServiceAdapter getAdapter() {
		return adapter;
	}
	
}
