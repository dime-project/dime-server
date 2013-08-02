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

package eu.dime.ps.controllers.context.raw;

import org.apache.log4j.Logger;


import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.IContextProvider;
import eu.dime.ps.contextprocessor.IProviderManager;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.context.raw.ifc.IProximityService;
import eu.dime.ps.controllers.context.raw.impl.ProximityService;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.storage.entities.Tenant;

public class ProximityProvider implements IContextProvider {

	Logger logger = Logger.getLogger(ProximityProvider.class);
	
	private IContextProcessor contextProcessor;
	private IProviderManager providerManager;
	private IScope providedScope = Factory.createScope(Constants.SCOPE_PROXIMITY);
	private IScope[] inputScopes = null;
	
	private IProximityService proximityService = null;
	
	public ProximityProvider(TenantManager tenantManager, ServiceGateway serviceGateway, 
			PolicyManager policyManager, IContextProcessor contextProcessor, 
			IProviderManager providerManager, PersonManager personManager, 
			PersonGroupManager personGroupManager, AccountManager accountManager) {
		this.contextProcessor = contextProcessor;
		this.providerManager = providerManager;
		init(tenantManager, serviceGateway, policyManager, personManager, personGroupManager, accountManager);
	}
	
	private void init(TenantManager tenantManager, ServiceGateway serviceGateway, PolicyManager policyManager, 
			PersonManager personManager, PersonGroupManager personGroupManager, AccountManager accountManager) {
		logger.info("Init Proximity Service");
		providerManager.registerProvider(providedScope, inputScopes, this);
		this.proximityService = new ProximityService(tenantManager, serviceGateway, policyManager, this.contextProcessor, accountManager, personManager, personGroupManager);
	}

	@Override
	public IContextDataset getContext(Tenant t, IEntity entity, IScope scope,
			IContextDataset inputContextDataset) {
		return proximityService.getProximity(entity,t);
	}

}
