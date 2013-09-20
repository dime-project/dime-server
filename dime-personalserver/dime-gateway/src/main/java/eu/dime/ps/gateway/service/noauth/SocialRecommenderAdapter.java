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

package eu.dime.ps.gateway.service.noauth;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.proxy.HttpRestProxy;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;

public class SocialRecommenderAdapter extends ServiceAdapterBase implements ExternalServiceAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(SocialRecommenderAdapter.class);

	private HttpRestProxy proxy;
	public static String adapterName = "SocialRecs";
	
	private String serviceURL = null;
	private String username;
	private String password;
	private String realm;
	private int port;
	

	/**
	 * @param identifier
	 *            The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public SocialRecommenderAdapter()
			throws ServiceNotAvailableException {
		
		super();
		
		try {
			this.serviceURL = this.policyManager.getPolicyString("SERVICEURL", "SOCIALRECOMMENDER");
			this.username = this.policyManager.getPolicyString("USER", "SOCIALRECOMMENDER");
			this.password = this.policyManager.getPolicyString("PASS", "SOCIALRECOMMENDER");
			this.realm = this.policyManager.getPolicyString("REALM", "SOCIALRECOMMENDER");
			this.port = this.policyManager.getPolicyInteger("PORT","SOCIALRECOMMENDER");
			this.proxy = new HttpRestProxy(new URL(this.serviceURL), this.username, this.password);
			this.display = false;
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
			throw new ServiceNotAvailableException(e);
		} 
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override //OK
	public String getAdapterName() {
		return this.adapterName;
	}

	@Override // OK
	public Boolean isConnected() {
		return (this.proxy != null);
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException, ServiceException {
		
		String xml = this.proxy.get(attribute);
		
		ServiceResponse[] sa = new ServiceResponse[1];
		sa[0] = new ServiceResponse(ServiceResponse.XML, attribute, null, null, xml);
		return sa;
	}

}
