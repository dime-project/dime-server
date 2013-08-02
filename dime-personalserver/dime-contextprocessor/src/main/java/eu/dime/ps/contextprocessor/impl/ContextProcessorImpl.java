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

package eu.dime.ps.contextprocessor.impl;

import java.util.Arrays;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.dime.commons.notifications.DimeInternalNotification;
//import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.ContextProvider;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.IContextProvider;
import eu.dime.ps.contextprocessor.IProviderManager;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.IStorage;
import eu.dime.ps.storage.StorageException;
import eu.dime.ps.storage.entities.Tenant;


/**
 * ContextProcessor implementation.
 */
public class ContextProcessorImpl implements IContextProcessor, IProviderManager
{
	
	private Logger logger = Logger.getLogger(ContextProcessorImpl.class);
	
	private IStorage storage = null;
	
	//private Account publicAccount = null;
	private HashMap<String, Account> proximityAccounts = new HashMap<String, Account>();
	
    public ContextProcessorImpl() {
        super();
    }
    
    public ContextProcessorImpl(IStorage storageImpl) {
        super();
        storage = storageImpl;
    }
    
    public void setStorage(IStorage storageImpl) {
    	storage = storageImpl;
    }
    
    private Map<String,Vector<IContextListener>> contextSubscriptions = Collections.synchronizedMap(new HashMap<String,Vector<IContextListener>>());
    private Map<String,Vector<IContextListener>> generalSubscriptions = Collections.synchronizedMap(new HashMap<String,Vector<IContextListener>>());

    /*@Override
	public void setPublicAccount(Account account) {
		this.publicAccount = account;
	}
    
    @Override
	public Account getPublicAccount() {
    	return this.publicAccount;
	}*/
    
    @Override
	public void addProximityAccount(String said, Account account) {
		this.proximityAccounts.put(said,account);
	}

	@Override
	public Account getProximityAccount(String said) {
		return proximityAccounts.get(said);
	}
    
	public void contextUpdate(Tenant tenant, IContextDataset context) throws ContextException {
		
		if (context != IContextDataset.EMPTY_CONTEXT_DATASET) logger.debug("Context Update received");
		else logger.debug("Context Update received (empty dataset - NOP)");
		
		long now = System.currentTimeMillis();
		
		try {
			long timeRef = -1;
			String timeRefStr = context.getTimeRef();
			if (timeRefStr!=null)
				timeRef = Factory.timestampFromXMLString(timeRefStr);
			IContextElement[] ctxElArr = context.getContextElements();
			for (int i=0; i<ctxElArr.length; i++){
				// Ctx elements are resynchronized on current time
				IContextElement currEl = Factory.cloneResynchdContextElement(ctxElArr[i], timeRef, now);
				
				if (currEl.getScope().getScopeAsString().equalsIgnoreCase(Constants.SCOPE_LOCATION_POSITION) &&
						mobilePositionAvailable(tenant, currEl)) continue;

				logger.debug("Storing " + currEl.getScope().getScopeAsString() + " in cache");
				storage.storeContextElement(tenant,currEl);
				
				// Start a thread to notify subscribed applications
				NotificationThread notifThr = new NotificationThread(tenant,currEl);
				notifThr.start();
			}
			
		} catch (Exception e) {
			logger.error(e.toString(),e);
			throw new ContextException("contextUpdate exception: " + e.getMessage(),e);
		}
		
	}
	
	private boolean mobilePositionAvailable(Tenant tenant, IContextElement newPosition) {
		
		// if new position comes from mobile crawler it's normally updated
		if (newPosition.getSource().equalsIgnoreCase("mobile-crawler")) return false;
		
		try {
			IContextDataset dataset = getContext(tenant,newPosition.getEntity(),newPosition.getScope());
			if (dataset == IContextDataset.EMPTY_CONTEXT_DATASET) {
				logger.debug("No position available in context cache");
				return false;
			}
			IContextElement position = dataset.getCurrentContextElement(newPosition.getEntity(), newPosition.getScope());
			if (position == null) {
				logger.debug("No valid position available in context cache");
				return false;
			} else {
				if (position.getSource().equalsIgnoreCase("mobile-crawler")) {
					logger.debug("Valid mobile position available in context cache, desktop position ignored!");
					return true;
				} else return false;
			}
		} catch (ContextException e) {
			return false;
		}
	}

	public IContextDataset getContext(Tenant tenant, IEntity entity, IScope scope)
			throws ContextException {
		
		logger.debug("getContext " + entity.getEntityAsString() + "," + scope.getScopeAsString());
		
		IContextDataset resp = IContextDataset.EMPTY_CONTEXT_DATASET;
		if ((entity==null)||(scope==null))
			throw new ContextException("GetContext: bad request, entity or scope are null");
		try {
			IContextElement[] ctxElArr = storage.getCurrentContextElements(tenant,entity, scope);
			if (ctxElArr.length>0){
				resp = Factory.createContextDataset(ctxElArr);
			}else{
				// No context in cache, try provider invocation
				Vector<IContextElement> ceVect = new Vector<IContextElement>();
				invokeProviders(tenant,entity, scope, ceVect);
				if (ceVect.size()>0)
					resp = Factory.createContextDataset(ceVect.toArray(new IContextElement[0]));
			}
			if (resp==null)
				throw new ContextException("Unknown error");
		} catch (Exception e) {
			throw new ContextException("getContext: cannot retrieve context, cause: " + e.getMessage(),e);
		}
		
		return resp;
	}

	// Note: Subscription duration is assumed unlimited
	public void subscribeContext(IEntity entity, IScope scope,
			IContextListener listener) throws ContextException {
		
		logger.debug("subscribeContext for " + entity.getEntityAsString() + "," + scope.getScopeAsString());
		
		if (listener==null)
			throw new ContextException("subscribeContext: null listener");
		
		String wildcardKey = Constants.ENTITY_ALL_USERS.getEntityAsString() + "-" + scope.getScopeAsString();
		
		// key --> <complete entity>-<scope>
		String key = entity.getEntityAsString() + "-" + scope.getScopeAsString();
		
		if (key.equalsIgnoreCase(wildcardKey)) {
			// key for general subscription --> <scope> 
			if (!generalSubscriptions.containsKey(scope.getScopeAsString())) {
				generalSubscriptions.put(scope.getScopeAsString(), new Vector<IContextListener>());
			}
			Vector<IContextListener> listenerVect = generalSubscriptions.get(scope.getScopeAsString());
			listenerVect.add(listener);
		} else {
			if (!contextSubscriptions.containsKey(key)) {
				contextSubscriptions.put(key, new Vector<IContextListener>());
			}
			Vector<IContextListener> listenerVect = contextSubscriptions.get(key);
			listenerVect.add(listener);
		}
	}
	
	public void deleteContext(IEntity entity, IScope scope)
			throws ContextException {
		try {
			storage.deleteContextElements(entity, scope);
		} catch (StorageException e) {
			throw new ContextException(e.getMessage());
		}
		return;
	}
	
	/*
	 * The method search for context data in history, if until is not present it involves also the current values,
	 * if necessary invocating providers
	 * (non-Javadoc)
	 * @see eu.dime.ps.contextprocessor.IContextProcessor#getContext(eu.dime.context.model.api.IEntity, eu.dime.context.model.api.IScope, java.lang.String, java.lang.String)
	 */
    public IContextDataset getContext(
    		Tenant tenant,
    	    final IEntity entity,
    	    final IScope scope,
    	    String since,
    	    String until) throws ContextException{
    	
    	logger.debug("getContext " + scope.getScopeAsString() + " since " + since);
    	
		IContextDataset resp = IContextDataset.EMPTY_CONTEXT_DATASET;
		Vector<IContextElement> ceVect = new Vector<IContextElement>();
		if ((entity==null)||(scope==null))
			throw new ContextException("GetContext: bad request, entity or scope are null");
		try {
			long sinceLong = Factory.timestampFromXMLString(since);
			long untilLong = -1;
			if ((until!=null)&&(!until.equals("")))
				untilLong = Factory.timestampFromXMLString(until);
			IContextElement[] ctxElArr = storage.getContextElements(tenant,entity, scope, sinceLong, untilLong);
			
			if (ctxElArr.length>0){
				String expTime = ctxElArr[0].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue().toString();
				long exp = Factory.timestampFromXMLString(expTime);
				long now = System.currentTimeMillis();
				if (exp<now){
					// Since no element is valid, try to invoke provider
					invokeProviders(tenant,entity,scope,ceVect);
				}
			}else{ //ctxElArr==0
				invokeProviders(tenant,entity,scope,ceVect);
			}
			
			// Now elements returned from cache are added to the Vector
			ceVect.addAll(Arrays.asList(ctxElArr));
			
			if (ceVect.size()>0){
				resp = Factory.createContextDataset(ceVect.toArray(new IContextElement[0]));
			}
			if (resp==null)
				throw new ContextException("Unknown error");
		} catch (Exception e) {
			logger.error(e.toString(),e);
			throw new ContextException("getContext: cannot retrieve context, cause: " + e.getMessage(),e);
		}
		return resp;
    }
    
    private IContextDataset getContext(
    		Tenant tenant,
    	    final IEntity entity,
    	    final IScope[] scopeArr){
    	
    	Vector<IContextElement> ceVect = new Vector<IContextElement>();
		for (int i=0; i<scopeArr.length; i++){
			IContextDataset cd = null;
			try {
				cd = getContext(tenant,entity,scopeArr[i]);
			} catch (Exception e) {}
			if ((cd!=null)&&(!cd.equals(IContextDataset.EMPTY_CONTEXT_DATASET))){
				IContextElement[] ceArr = cd.getContextElements();
				for (int j=0; j<ceArr.length; j++)
					ceVect.add(ceArr[j]);
			}
		}
		return Factory.createContextDataset(ceVect.toArray(new IContextElement[0]));
    }
    
    public IContextDataset getContext(
    		Tenant tenant,
    	    final IEntity entity,
    	    final IScope scope,
    	    int limit)
    throws ContextException{
    	
    	logger.debug("getContext " + scope.getScopeAsString());
    	
    	/*
    	 * A valid context element is searched, if not present try to invoke a provider.
    	 */
		IContextDataset resp = IContextDataset.EMPTY_CONTEXT_DATASET;
		Vector<IContextElement> ceVect = new Vector<IContextElement>();
		if ((entity==null)||(scope==null))
			throw new ContextException("GetContext: bad request, entity or scope are null");
		try {
			IContextElement[] ctxElArr = storage.getContextElements(tenant,entity, scope,limit);
			
			String expTime = ctxElArr[0].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue().toString();
			long exp = Factory.timestampFromXMLString(expTime);
			long now = System.currentTimeMillis();
			if (exp<now){
				// Since no element is valid, try to invoke provider
				invokeProviders(tenant,entity,scope,ceVect);
			}
			// Now elements returned from cache are added to the Vector
			ceVect.addAll(Arrays.asList(ctxElArr));
			
			int lim = ceVect.size();
			if (lim > limit)
				lim = limit;
			if (lim!=0)
				resp = Factory.createContextDataset(ceVect.subList(0, lim).toArray(new IContextElement[0]));
			
			if (resp==null)
				throw new ContextException("Unknown error");
		} catch (Exception e) {
			throw new ContextException("getContext: cannot retrieve context, cause: " + e.getMessage(),e);
		}
		return resp;
		
    }

    private HashMap<IScope,ContextProvider> providerMap = new HashMap<IScope,ContextProvider>();

    /* Add a provider to providerMap
     * (non-Javadoc)
     * @see eu.dime.ps.contextprocessor.IProviderManager#registerProvider(eu.dime.context.model.api.IScope, eu.dime.context.model.api.IScope[], eu.dime.ps.contextprocessor.IContextProvider)
     */
	public void registerProvider(IScope outScope, IScope[] inScopeArr, IContextProvider providerRef){
		
		logger.debug("registerProvider for " + outScope.getScopeAsString());

		ContextProvider cp = new ContextProvider();
		cp.setCp(providerRef);
		cp.setOutputScope(outScope);
		cp.setInputScopeArr(inScopeArr);
		
		providerMap.put(outScope, cp);
	}
	
	/*
	 * Removes every entry in providerMap related to the unregistering provider 
	 * (non-Javadoc)
	 * @see eu.dime.ps.contextprocessor.IProviderManager#unregisterProvider(eu.dime.ps.contextprocessor.IContextProvider)
	 */
	public void unregisterProvider(IContextProvider provider){
		
		Set<Entry<IScope,ContextProvider>> entries = providerMap.entrySet();
		Iterator<Entry<IScope,ContextProvider>> it = entries.iterator();
		while (it.hasNext()){
			Entry<IScope,ContextProvider> entry = it.next();
			if (entry.getValue().getCp()==provider)
				it.remove();
		}
	}
	
	/*
	 * Search a provider returning the required scope and invokes it, adding obtained context elements
	 * in ceVect array
	 */
	private void invokeProviders(Tenant tenant, IEntity entity, IScope scope, Vector<IContextElement> ceVect)
	throws ContextException{
		long now = System.currentTimeMillis();
		try {
			IContextDataset provDs = null;
			ContextProvider cp = providerMap.get(scope);
			if (cp!=null){
				// search for invocation scopes
				IContextDataset paramCd = null;
				if ((cp.getInputScopeArr()!=null)&&(cp.getInputScopeArr().length>0)){
					paramCd = getContext(tenant,entity,cp.getInputScopeArr());
					if ((paramCd==null)||(paramCd.getContextElements().length==0))
						return; // if no input scope is available, cannot invoke provider, and return
				}
				// here paramCd is null only if no input scope is needed
				provDs = cp.getCp().getContext(tenant,entity, scope, paramCd);
				// The ctx elements returned are stored in cache and added to the vector ceVect 
				contextUpdate(tenant,provDs);
				
				long timeRef = -1;
				String timeRefStr = provDs.getTimeRef();
				if (timeRefStr!=null)
					timeRef = Factory.timestampFromXMLString(timeRefStr);
				IContextElement[] ceArr = provDs.getContextElements();
				for (int i=0; i<ceArr.length; i++)
					ceArr[i] = Factory.cloneResynchdContextElement(ceArr[i],timeRef, now);
				ceVect.addAll(Arrays.asList(ceArr));
			}
		} catch (Exception e) {
			logger.debug(e.toString(),e);
			throw new ContextException(e.getMessage());
		}
	}
	
	/*
	 * Notify a context element to subscribed applications (listeners)
	 */
	private void notifySubscribers(Tenant tenant, IContextElement ctxEl){
		IEntity entity = ctxEl.getEntity();
		IScope scope = ctxEl.getScope();
		try {
			// Single-User approach
			/*String specKey = entity.getEntityAsString() + scope.getScopeAsString(); // for specific entity (i.e. device)
			String genKey = scope.getScopeAsString(); // for generic entity (i.e. user)
			String[] keyArr = new String[]{specKey, genKey};*/
			
			// Notification to wildcard subscriber
			if (generalSubscriptions.containsKey(ctxEl.getScope().getScopeAsString())) {
				Vector<IContextListener> subscrVect = generalSubscriptions.get(ctxEl.getScope().getScopeAsString());
				IContextDataset ctxDataset = Factory.createContextDataset(ctxEl);
				for (int j=0; j<subscrVect.size(); j++){
					// Note: Subscription duration is assumed unlimited
					// DimeInternalNotification notification = new DimeInternalNotification(tenant.getId());
					
					RawContextNotification notification = new RawContextNotification();
					notification.setTenant(tenant.getId());
					notification.setItemID(""); 
					notification.setName(ctxEl.getEntity().getEntityAsString() + "," + ctxEl.getScope().getScopeAsString());//""); // TODO set name from ctxDataset
					notification.setItemType("context");
					notification.setOperation("create");
					notification.setSender("ContextProcessor");
					notification.setTarget("@me");
					
					subscrVect.get(j).contextChanged(notification);
				}
			}
			
			// Notification to active subscriptions
			String key = entity.getEntityAsString() + "-" + scope.getScopeAsString();
				if (contextSubscriptions.containsKey(key)) {
					Vector<IContextListener> subscrVect = contextSubscriptions.get(key);
					IContextDataset ctxDataset = Factory.createContextDataset(ctxEl);
					for (int j=0; j<subscrVect.size(); j++){
						// Note: Subscription duration is assumed unlimited

					    //DimeInternalNotification notification = new DimeInternalNotification(tenant.getId());
						RawContextNotification notification = new RawContextNotification();
						notification.setTenant(tenant.getId());
						notification.setItemID(""); 
						notification.setName(ctxEl.getEntity().getEntityAsString() + "," + ctxEl.getScope().getScopeAsString());//""); // TODO set name from ctxDataset
						notification.setItemType("context");
						notification.setOperation("create");
						notification.setSender("ContextProcessor");
						notification.setTarget("@me");
						
						subscrVect.get(j).contextChanged(notification);
					}
				}
		} catch (Exception e) {
			logger.error("context notification failed for " + entity.getEntityAsString() + "," + scope.getScopeAsString(),e);
		}
	}
	
	class NotificationThread extends Thread {
		private IContextElement ctxEl;
		private Tenant tenant;

		public NotificationThread(Tenant tenant, IContextElement ctxEl){
			this.ctxEl = ctxEl;
			this.tenant = tenant;
		}
		
		public void run() {
			notifySubscribers(tenant,ctxEl);
		}

	}
	
}
