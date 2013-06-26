/**
 * 
 */
package eu.dime.ps.gateway.service;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Sophie.Wrobel
 *
 */
public class ServiceResponse {
	
	// Response Types
	public static final String XML = "XML";
	public static final String JSON = "JSON";
	public static final String CSV = "CSV";
	
	protected final String responseType;
	protected final String attribute;
	protected final String response;
	protected String path;
	protected String queryParams;

	public ServiceResponse(String responseType, String attribute, String url, String response) {
		this.responseType = responseType;
		this.attribute = attribute;
		this.response = response;
		try {
			URI uri = new URI(url);
			this.queryParams = uri.getQuery();
			this.path = uri.getPath();
		} catch (URISyntaxException e) {
			this.queryParams = null;
			this.path = url;
		}
	}
	
	public ServiceResponse(String responseType, String attribute, String path, String queryParams, String response) {
		this.responseType = responseType;
		this.attribute = attribute;
		this.response = response;
		this.queryParams = queryParams;
		this.path = path;
	}
	
	/**
	 * Format of the service response. Currently supported values: "JSON", "XML"
	 * 
	 * @return response type
	 */
	public String getResponseType() {
		return this.responseType;
	}
	
	/**
	 * Body of service response.
	 * 
	 * @return response from Service
	 */
	public String getResponse() {
		return this.response;
	}
	
	/**
	 * Returns the attribute that was called (e.g. "/person/@me/@all")
	 * 
	 * @return attribute that was called
	 */
	public String getAttribute() {
		return this.attribute;
	}
	
	/**
	 * Relative URL called on the external service
	 * @return path to external service
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Query parameters called on the external service
	 * @return query parameters sent
	 */
	public String getQueryParams() {
		return this.queryParams;
	}
}
