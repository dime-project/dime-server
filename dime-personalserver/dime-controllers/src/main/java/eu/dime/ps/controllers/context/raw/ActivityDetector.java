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

import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.Util;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.IContextProvider;
import eu.dime.ps.contextprocessor.IProviderManager;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.context.raw.utils.Defaults;
import eu.dime.ps.controllers.context.raw.utils.Utility;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.storage.entities.Role;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

public class ActivityDetector implements IContextProvider {
	
	Logger logger = Logger.getLogger(ActivityDetector.class);
	
	private int duration = Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE; // new activity is fired if it is not changed since duration secs ago

	private IContextProcessor contextProcessor;
	private IProviderManager providerManager;
	//private NotifierManager notifierManager;
	
	private IScope providedScope = Factory.createScope(Constants.SCOPE_ACTIVITY);
	private IScope[] inputScopes = null;
	//private String lastNotifiedSituation = "";
	private HashMap<String,String> lastNotifiedActivities = new HashMap<String, String>();
	
	private ADThread thread = null;
	private LocationProvider locationProvider;
	private ProximityProvider proximityProvider;
	private ServiceGateway serviceGateway;
	private PolicyManager policyManager;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;
	private TenantManager tenantManager;
	private AccountManager accountManager;
	private UserManager userManager;
	
	int rounds = -1;
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}
	
	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}
	public void setPolicyManager(PolicyManager policyManager) {
		this.policyManager = policyManager;
	}
	
	public void setProviderManager(IProviderManager providerManager) {
		this.providerManager = providerManager;
	}
	
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}
	
	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	/*public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}*/
	
	public void init() {
		logger.info("INIT Activity Detector");
		this.locationProvider = new LocationProvider(this.tenantManager,this.contextProcessor,
				this.providerManager, this.serviceGateway, this.accountManager);
		this.proximityProvider = new ProximityProvider(this.tenantManager,this.serviceGateway,
				this.policyManager, this.contextProcessor, this.providerManager, 
				this.personManager, this.personGroupManager, this.accountManager);
		this.providerManager.registerProvider(providedScope, inputScopes, this);
		this.thread = new ADThread(this.tenantManager,this.contextProcessor);
	}
	
	@Override
	public IContextDataset getContext(Tenant t, IEntity entity, IScope scope,
			IContextDataset inputContextDataset) {
		
		logger.debug("getContext " + Constants.SCOPE_ACTIVITY);
		
		//Tenant t = tenantManager.getByAccountName(entity.getEntityIDAsString());
		
		IContextDataset civilAddrs = null;
		try {
			civilAddrs = this.contextProcessor.getContext(
				    t,entity,
				    Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS),
				    Util.getDateTime(System.currentTimeMillis() - (duration * 1000)),
				    null);
		} catch (ContextException e) {
			logger.error(e.toString());
		}
		IContextDataset activity = processLastLocations(t,civilAddrs);
		String activityValue = getActivityValue(activity);
		//lastFiredSituation = situationValue;
		logger.debug("returned activity " + activityValue);
		return activity;
	}

	private IContextDataset processLastLocations(Tenant t, IContextDataset civilAddrs) {
		
		IContextElement[] addrs = civilAddrs.getContextElements();
		
		String activity = "";
		IEntity entity = null;
		
		if ((addrs != null) && (addrs.length > 0)) {
			entity = addrs[0].getEntity();
			String now = Factory.timestampAsXMLString(new Date());
			logger.debug(now + " - " + entity.getEntityIDAsString() + " - processLastLocations (" + addrs.length + " civilAddress)");
			for (int i=0; i<addrs.length; i++) {
				logger.debug(i + " - " 
						+ (String)addrs[i].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME)).getValue().getValue() + " " 
						+ (String)addrs[i].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
			}
			
			long recentItem = Factory.timestampFromXMLString((String)addrs[0].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
			if (recentItem < (System.currentTimeMillis() - (Defaults.ACTIVITY_PERIOD * 1000))) {
				logger.debug("Received civil addresses are out of permanence period");
				return IContextDataset.EMPTY_CONTEXT_DATASET;
			}
			
			activity = getActivity(entity,addrs);
		}
		
		if (activity.equalsIgnoreCase("")) return IContextDataset.EMPTY_CONTEXT_DATASET;
		
		IContextDataset act = Util.createSimpleContextDataset(entity, 
				Factory.createScope(Constants.SCOPE_ACTIVITY), 
				Constants.SCOPE_ACTIVITY_CURRENT, 
				activity, (int)(Defaults.SCAN_PERIOD * 1.25));
		return act;
	}
	
	protected String getActivity(IEntity entity, IContextElement[] addrs) {
		
		if (addrs.length != 0) {
			int index = Utility.getFirstSignificativeItem(addrs,Defaults.ACTIVITY_PERIOD,Defaults.ACTIVITY_TOLERANCE);
			if (index == -1) {
				logger.debug("Received civil addresses are too recent to infer activity");
				return "";
			} else {
				logger.debug("Significative civil address for activity:");
				for (int i=0; i<addrs.length; i++) {
					String prefix = "";
					if (i <= index) prefix = "*" + i;
					else prefix = "" + i;
					logger.debug(prefix + " - " 
							+ (String)addrs[i].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME)).getValue().getValue() + " " 
							+ (String)addrs[i].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
				}
			}
			String placeName = (String)addrs[0].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME)).getValue().getValue();
			for (int i=1; i<=index; i++) {
				String currentPlace = (String)addrs[i].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME)).getValue().getValue();
				if (!currentPlace.equalsIgnoreCase(placeName)) return "";
			}
			return "@" + placeName;
		}
		return "";
	}

	private void fireNewActivity(Tenant t, IEntity entity, IContextDataset activity) {
		try {
			this.contextProcessor.contextUpdate(t,activity);
			
			String currentActivity = getActivityValue(activity);
			
			if (!currentActivity.equalsIgnoreCase("")) {
				
				String lastActivity = lastNotifiedActivities.get(entity.getEntityAsString());
				if (lastActivity == null) lastActivity = "";
				
				logger.debug("Last notified activity: " + lastActivity);
				
				if (currentActivity.equalsIgnoreCase(lastActivity)) {
					logger.debug("Activity " + currentActivity + " has already been notified");
					return;
				}
				
				// [TI] notification of OLD situation is no more needed now
				// New SituationDetector should do it
				// notifyNewSituation(t,entity, activity);
				
			}
			
		} catch (ContextException e) {
			logger.error(e.toString());
		}
	}
	
	/*private void notifyNewSituation(Tenant t, IEntity entity, IContextDataset situation) {
			
		DimeNotification notification = new DimeNotification(t.getId());
		notification.setItemID(Long.toString(System.currentTimeMillis())); // TODO set id from ctxDataset
		notification.setName(currentSituation); // TODO set name from ctxDataset
		notification.setItemType(DimeNotification.TYPE_SITUATION);
		notification.setOperation(DimeNotification.OP_CREATE);
		notification.setSender("@me");
		notification.setTarget("@me");

		try {
			logger.info("PUSH NOTIFICATION: " + currentSituation);
			//lastNotifiedSituation = currentSituation;
			lastNotifiedActivities.put(entity.getEntityAsString(),currentSituation);
			this.notifierManager.pushInternalNotification(notification);
		} catch (NotifierException e) {
			logger.error(e.toString());
		}
		
	}*/

	protected String getActivityValue(IContextDataset activityDataset) {
		if (activityDataset != null) {
			IContextElement[] ctxEls = activityDataset.getContextElements(); // FIXME specify scope
			if ((ctxEls != null) && (ctxEls.length > 0))
				return (String)ctxEls[0].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_ACTIVITY_CURRENT)).getValue().getValue();
			else return "";
		} else return "";
	}
	
	private class ADThread implements Runnable {
		
		private boolean running = false;
		private Thread thread = null;
		private int scanPeriod = Defaults.SCAN_PERIOD; // evaluate activity every scanPeriod secs
		private IContextProcessor contextProcessor;
		private TenantManager tenantManager;
		
		public ADThread(TenantManager tenantManager, IContextProcessor contextProcessor) {
			logger.info("Starting AD thread...");
			this.contextProcessor = contextProcessor;
			this.tenantManager = tenantManager;
			running = true;
		    thread = new Thread(this);
		    thread.start();
		}

		@Override
		public void run() {
			while (running) {
				
				try {
					
					rounds++;
					
					if (rounds != 0) {
						
						List<String> owners = getOwners();
						Iterator<String> it = owners.iterator();
						while (it.hasNext()) {	
							String owner = it.next();
							Tenant t = tenantManager.getByAccountName(owner);
							logger.debug("Evaluating activity of " + owner + ", round " + rounds);
							IEntity entity = Factory.createEntity(owner);
							IContextDataset civilAddrs = this.contextProcessor.getContext(
								    t,entity,
								    Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS),
								    Util.getDateTime((System.currentTimeMillis() - (duration * 1000))),
								    null);
						
							IContextDataset activity = processLastLocations(t,civilAddrs);
							String activityValue = getActivityValue(activity);
							
							if (!activityValue.equalsIgnoreCase("")) {
								logger.info("UPDATE new activity: " + activityValue);
								fireNewActivity(t,entity,activity);
							}
						}
					}
				
				} catch (ContextException e) {
					logger.error(e.toString());
				}
				
				synchronized (this) {
					try {
						wait(scanPeriod * 1000);
					} catch (InterruptedException ex) {
						
					}
				}
			}
		}
		
		private List<String> getOwners() {
			List<String> owners = new ArrayList<String>();
			List<User> users = userManager.getAll();
			Iterator<User> it = users.iterator();
			while (it.hasNext()) {
				User u = it.next();
				if (u.getRole().equals(Role.OWNER)) 
					owners.add(u.getUsername());
			}
			return owners;
		}
	}
	
}
