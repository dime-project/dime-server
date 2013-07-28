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
