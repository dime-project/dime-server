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
import eu.dime.ps.controllers.context.raw.ifc.ILocationService;
import eu.dime.ps.controllers.context.raw.impl.LocationService;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.storage.entities.Tenant;

public class LocationProvider implements IContextProvider {
	
	Logger logger = Logger.getLogger(LocationProvider.class);
	
	private IProviderManager providerManager;
	private IContextProcessor contextProcessor;
	private ServiceGateway serviceGateway;
	private AccountManager accountManager;
	private TenantManager tenantManager;
	
	private IScope providedScope = Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS);
	private IScope[] inputScopes = {
			// Currently civilAddress from GPS position is not supported on Location Service
			// It will extend it if needed
			// Factory.createScope(Constants.SCOPE_LOCATION_POSITION),
			Factory.createScope(Constants.SCOPE_WF),
			Factory.createScope(Constants.SCOPE_CELL),
			Factory.createScope(Constants.SCOPE_CURRENT_PLACE)
	};
	
	private ILocationService locationService = null;

	public LocationProvider(TenantManager tenantManager, IContextProcessor contextProcessor, 
			IProviderManager providerManager, ServiceGateway serviceGateway, AccountManager accountManager) {
		this.providerManager = providerManager;
		this.contextProcessor = contextProcessor;
		this.tenantManager = tenantManager;
		this.serviceGateway = serviceGateway;
		this.accountManager = accountManager;
		init();
	}
	
	public void init() {
		logger.info("Init Location Service");
		providerManager.registerProvider(providedScope, inputScopes, this);
		//this.locationService = new LocationService(this.contextProcessor);
		this.locationService = new LocationService(this.contextProcessor, this.serviceGateway, this.accountManager);
	}

	@Override
	public IContextDataset getContext(Tenant t, IEntity entity, IScope scope,
			IContextDataset inputContextDataset) {
		
		//Tenant t = tenantManager.getByAccountName(entity.getEntityIDAsString());
		IContextDataset civilAddress = this.locationService.getCivilAddress(t,inputContextDataset); 
		return civilAddress;
	}

}
