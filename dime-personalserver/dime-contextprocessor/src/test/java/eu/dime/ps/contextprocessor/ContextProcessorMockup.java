package eu.dime.ps.contextprocessor;

import java.util.HashMap;


import java.util.Vector;

import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.Tenant;

/**
 * ConetxtProcessor mockup implementation.
 */
public class ContextProcessorMockup implements IContextProcessor
{

	public ContextProcessorMockup()
    {
        super(  );
    }
    
    private HashMap<String,IContextElement> contextCache = new HashMap<String,IContextElement>();
    private HashMap<String,Vector<IContextListener>> contextSubscriptions = new HashMap<String,Vector<IContextListener>>();

	public void contextUpdate(Tenant tenant, IContextDataset context) throws ContextException {
		
		try {
			IContextElement[] ctxElArr = context.getContextElements();
			for (int i=0; i<ctxElArr.length; i++){
				IEntity entity = ctxElArr[i].getEntity();
				IScope scope = ctxElArr[i].getScope();
				String key = entity.getEntityAsString() + scope.getScopeAsString();
				
				contextCache.put(key, ctxElArr[i]);
				
				// Notification to active subscritions
				if (contextSubscriptions.containsKey(key)){
					Vector<IContextListener> subscrVect = contextSubscriptions.get(key);
					IContextDataset ctxDataset = Factory.createContextDataset(ctxElArr[i]);
					for (int j=0; j<subscrVect.size(); j++){
						// Note: Subscription duration is assumed unlimited
						RawContextNotification notification = new RawContextNotification();
						notification.setTenant(Long.parseLong("1"));
						notification.setItemID(""); 
						notification.setName(""); 
						notification.setItemType("context");
						notification.setOperation("create");
						notification.setSender("ContextProcessor");
						notification.setTarget("@me");
						subscrVect.get(j).contextChanged(notification);
					}
				}
			}
		} catch (Exception e) {
			throw new ContextException("contextUpdate exception",e);
		}
		
	}

	public IContextDataset getContext(Tenant tenant, IEntity entity, IScope scope)
			throws ContextException {

		IContextElement ctxEl = null;
		IContextDataset resp = IContextDataset.EMPTY_CONTEXT_DATASET;
		if ((entity==null)||(scope==null))
			throw new ContextException("GetContext: bad request, entity or scope are null");
		try {
			String key = entity.getEntityAsString() + scope.getScopeAsString();
			if (contextCache.containsKey(key)){
				ctxEl = contextCache.get(key);

				// Check on expiration is disabled, in order to not have problem with tests
				//if (!Util.isContextDataExpired(ctxEl))
					resp = Factory.createContextDataset(ctxEl);
				//else{
					//contextCache.remove(key);
				//}
			}
			if (resp==null)
				throw new ContextException("Context not found or expired");
		} catch (Exception e) {
			throw new ContextException("getContext: cannot retrieve context, cause: " + e.getMessage(),e);
		}
		return resp;
	}

	// Note: Subscription duration is assumed unlimited
	public void subscribeContext(IEntity entity, IScope scope,
			IContextListener listener) throws ContextException {
		if (listener==null)
			throw new ContextException("subscribeContext: null listener");
		String key = entity.getEntityAsString() + scope.getScopeAsString();
		if (!contextSubscriptions.containsKey(key)){
			contextSubscriptions.put(key, new Vector<IContextListener>());
		}
		Vector<IContextListener> listenerVect = contextSubscriptions.get(key);
		listenerVect.add(listener);
	}
	
    public void deleteContext(IEntity entity, IScope scope)
	throws ContextException {
	
	}
	
	public IContextDataset getContext(Tenant tenant, IEntity entity, IScope scope,
		String since, String until) throws ContextException {
	
	return null;
	}
	
    public IContextDataset getContext(
    		Tenant tenant, 
    	    final IEntity entity,
    	    final IScope scope,
    	    int limit)
    throws ContextException {
    	return null;
    }

	/*@Override
	public void setPublicAccount(Account account) {
		
		
	}

	@Override
	public Account getPublicAccount() {
		
		return null;
	}*/

	@Override
	public void addProximityAccount(String said, Account account) {}

	@Override
	public Account getProximityAccount(String said) {
		return null;
	}

}
