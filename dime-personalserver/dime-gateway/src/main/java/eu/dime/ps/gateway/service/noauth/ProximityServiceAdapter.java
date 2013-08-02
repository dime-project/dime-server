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

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.dto.SAdapterSetting;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.policy.PolicyManagerImpl;
import eu.dime.ps.gateway.service.ServiceAdapterBase;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.external.ExternalServiceAdapter;

public class ProximityServiceAdapter extends ServiceAdapterBase implements ExternalServiceAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(ProximityServiceAdapter.class);
	
	public static String adapterName = "ProximityService";
	//private String identifier;
	private String accountId;
	
	private PolicyManager policyManager;
	private CloudServiceHelper helper;
	
	private String serviceURL = null;
	private String token = null;
	
	/**
	 * @param identifier
	 *            The identifier URI to identify the service adapter
	 * 
	 * @throws ServiceNotAvailableException
	 */
	public ProximityServiceAdapter()
			throws ServiceNotAvailableException {
		
		super();
		
		//this.identifier = "urn:account:"+UUID.randomUUID();
		this.policyManager = PolicyManagerImpl.getInstance();
		this.serviceURL = this.policyManager.getPolicyString("SERVICEURL", "PROXIMITY");
		this.token = this.policyManager.getPolicyString("PASS", "PROXIMITY");
		this.accountId = this.policyManager.getPolicyString("accountId",this.identifier);
		if (this.accountId != null) this.sadapter.addSetting(new SAdapterSetting("accountId", true, SAdapterSetting.ACCOUNT, this.accountId));
			
		this.helper = new CloudServiceHelper();
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
		
		String xml = this.helper.get(this.serviceURL + "/" + attribute,this.token);
		
		ServiceResponse[] sa = new ServiceResponse[1];
		sa[0] = new ServiceResponse(ServiceResponse.XML, attribute, null, null, xml);
		return sa;
	}
	
	public String postRaw(String path, String body)
			throws AttributeNotSupportedException,
			ServiceNotAvailableException, InvalidLoginException {
		
		String xml = this.helper.post(this.serviceURL + "/" + path, this.token, body);
		return xml;
	}

}
