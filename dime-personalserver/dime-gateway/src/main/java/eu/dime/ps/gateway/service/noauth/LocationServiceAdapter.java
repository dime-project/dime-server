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

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;

public class LocationServiceAdapter extends ServiceAdapterBase implements ExternalServiceAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(LocationServiceAdapter.class);

	//private HttpRestProxy proxy;
	public static String adapterName = "LocationService";
	//private String identifier;
	
	private PolicyManager policyManager;
	private CloudServiceHelper helper;
	
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
	public LocationServiceAdapter()
			throws ServiceNotAvailableException {
		
		super();
		this.identifier = "urn:account:"+UUID.randomUUID();
		this.policyManager = PolicyManagerImpl.getInstance();
		
		//try {
			this.serviceURL = this.policyManager.getPolicyString("SERVICEURL", "LOCATION");
			this.realm = this.policyManager.getPolicyString("REALM", "LOCATION");
			this.port = this.policyManager.getPolicyInteger("PORT","LOCATION");
			this.helper = new CloudServiceHelper();
			//this.proxy = new HttpRestProxy(new URL(this.serviceURL));
		/*} catch (MalformedURLException e) {
			logger.error(e.getMessage(),e);
			throw new ServiceNotAvailableException(e);
		} */
		
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
		return adapterName;
	}

	@Override // OK
	public Boolean isConnected() {
		//return (this.proxy != null);
		return (this.helper != null);
	}

	@Override
	public ServiceResponse[] getRaw(String attribute)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {
		
		//String xml = this.proxy.get(attribute);
		String xml = this.helper.get(this.serviceURL + "/" + attribute, "");
		
		ServiceResponse[] sa = new ServiceResponse[1];
		sa[0] = new ServiceResponse(ServiceResponse.XML, attribute, null, null, xml);
		return sa;
	}
	
	public String postRaw(String body)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException, IOException {
		
		//String xml = this.proxy.postAndGetResponse("",body,"application/json","");
		String xml = this.helper.post(this.serviceURL,"",body);
		return xml;
	}

}
