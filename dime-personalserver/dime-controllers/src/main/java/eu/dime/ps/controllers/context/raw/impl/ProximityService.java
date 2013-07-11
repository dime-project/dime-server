package eu.dime.ps.controllers.context.raw.impl;

import java.io.IOException;

import java.util.HashMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.dime.ps.contextprocessor.impl.RawContextNotification;
import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.Util;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.context.raw.data.ContextGroup;
import eu.dime.ps.controllers.context.raw.ifc.IContextGroupService;
import eu.dime.ps.controllers.context.raw.ifc.IProximityService;
import eu.dime.ps.controllers.context.raw.utils.Defaults;
import eu.dime.ps.controllers.context.raw.utils.Utility;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.policy.PolicyManager;
import eu.dime.ps.gateway.service.ServiceResponse;
import eu.dime.ps.gateway.service.noauth.LocationServiceAdapter;
import eu.dime.ps.gateway.service.noauth.ProximityServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.storage.entities.Tenant;

public class ProximityService implements IProximityService, IContextListener {
	
	Logger logger = Logger.getLogger(ProximityService.class);
	
	private ServiceGateway serviceGateway;
	private AccountManager accountManager;
	private TenantManager tenantManager;
	private PolicyManager policyManager;
	private ProximityServiceAdapter proximityService;
			
	private IContextGroupService groupService = null;
	private IContextProcessor contextProcessor;
	
	//public static HashMap<String, Person> proximityMap = new HashMap<String, Person>(); 
	
	private IScope userProximity = Factory.createScope(Constants.SCOPE_PROXIMITY);
	
	private int duration = Defaults.PROXIMITY_PERIOD + Defaults.PROXIMITY_TOLERANCE; // new proximity is fired if users are close by since duration secs ago
	
	public ProximityService() {}

	public ProximityService(TenantManager tenantManager, ServiceGateway serviceGateway, PolicyManager policyManger,
			IContextProcessor contextProcessor, AccountManager accountManager, 
			PersonManager personManager, PersonGroupManager personGroupManager) {
		
		this.contextProcessor = contextProcessor;
		
		try {
			this.serviceGateway = serviceGateway;
			this.accountManager = accountManager;
			this.tenantManager = tenantManager;
			this.policyManager = policyManger;
			// Approach with subs to wildcards
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_BT),this);
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_WF),this);
		} catch (ContextException e) {
			logger.error(e.toString());
		}
		this.groupService = new ContextGroupService(tenantManager, accountManager, personManager, personGroupManager);
	}
	
	private Account retrieveProximityAccount(Tenant t, IEntity entity) {
		// [TI] commented code was used when service config was not yet available
		/*if (this.accountManager != null && this.serviceGateway != null) {
			Account proximityAccount = contextProcessor.getProximityAccount(entity.getEntityIDAsString());
				if (proximityAccount == null) {
					logger.debug("ServiceAccount to share proximity not found");
					return null;
				} else return proximityAccount;
		}
		return null;*/
		if (this.accountManager != null && this.serviceGateway != null) {
			Account adapterAccount = retrieveAdapterAccount(t);
			if (adapterAccount == null) {
				logger.debug("Proximity Adapter account not found");
				return null;
			}
			String accountId = this.policyManager.getPolicyString("accountId",adapterAccount.asURI().toString().replaceAll(":","-"));
			//String accountId = "urn:uuid:j000071";
			//String accountId = "urn:uuid:a000018";
			if (accountId == null || accountId.equalsIgnoreCase("")) {
				logger.debug("Configured account to share proximity not found");
				return null;
			}
			Account proximityAccount = null;
			try {
				proximityAccount = this.accountManager.get(accountId);
			} catch (InfosphereException e) {
				logger.debug(e.toString(),e);
			}
			if (proximityAccount == null) {
				logger.debug("Configured account to share proximity not found");
				return null;
			} else return proximityAccount;
		}
		return null;
	}
	
	private Account retrieveAdapterAccount(Tenant t) {
		if (this.accountManager != null && this.serviceGateway != null) {
				Collection<Account> proxAccts = null;
				try {
					proxAccts = this.accountManager.getAllByType(ProximityServiceAdapter.adapterName);
				} catch (InfosphereException e) {
					logger.error(e.getMessage(),e);
				}
				if (proxAccts != null && proxAccts.size() > 0) {
					Iterator<Account> it = proxAccts.iterator();
					Object[] accts = proxAccts.toArray();
					return (Account)accts[accts.length-1];
					//return it.next();
				}
		} 
		return null;
	}
	
	private void setServiceReference(Account account) {
		try {
			this.proximityService = (ProximityServiceAdapter)this.serviceGateway.getServiceAdapter(account.asURI().toString());
		} catch (ServiceNotAvailableException e) {
			logger.error(e.getMessage(),e);
			this.proximityService = null;
		} catch (ServiceAdapterNotSupportedException e) {
			logger.error(e.getMessage(),e);
			this.proximityService = null;
		} catch (ClassCastException e) {
			logger.error(e.getMessage(),e);
			this.proximityService = null;
		} 
	}
	
	@Override
	public void contextChanged(RawContextNotification notification) throws Exception {
		
		String name = notification.getName(); 
		logger.debug("Context notification received: " + name);
		
		StringTokenizer tok = new StringTokenizer(name,",");
		String strEntity = tok.nextToken();
		String strScope = tok.nextToken();
		
		IEntity entity = Factory.createEntity(strEntity);
		IScope scope = Factory.createScope(strScope);
		
		IContextDataset dataset;

		try {
			
			Tenant t = new Tenant();
			t.setId(notification.getTenant());
			t.setName(entity.getEntityIDAsString());
			dataset = this.contextProcessor.getContext(t, entity, scope);
			
			processNotifiedData(t,entity,dataset);
			
		} catch (ContextException e) {
			logger.error(e.toString());
		}
	}

	private void processNotifiedData(Tenant t, IEntity entity, IContextDataset dataset) {
		
		// sending notified raw data to ProximityService
		postRawData(t,entity,dataset);
	
		// retrieve proximity from ProximityService
		IContextDataset proximities = IContextDataset.EMPTY_CONTEXT_DATASET;
		try {
			proximities = this.contextProcessor.getContext(t, entity, Factory.createScope(Constants.SCOPE_PROXIMITY), 
					Util.getDateTime(System.currentTimeMillis() - (duration * 1000)),
					null);
		} catch (ContextException e) {
			logger.error(e.toString());
		}
		
		// check proximity for ad hoc group creation
		String stableProximity = processDataToStableProximity(proximities);
		checkGroup(t,entity,stableProximity);
		
	}
	
	private void checkGroup(Tenant t, IEntity said, String stableProximity) {
		if (stableProximity.equalsIgnoreCase("")) return;
		
		try {
			IContextDataset currentPlace = this.contextProcessor.getContext(t,said, 
					Factory.createScope(Constants.SCOPE_CURRENT_PLACE));
			String placeValue = getPlaceValue(currentPlace);
			// Group is created also if there's no current place known
			//if (!placeValue.equalsIgnoreCase("")) {	
				String[] members = stableProximity.split(",");
				Set<String> membersSet = new HashSet<String>();
				for (int i=0; i<members.length; i++) {
					membersSet.add(members[i]);
				}
				if (membersSet.size() > 0) {
					this.groupService.addContextGroup(said,new ContextGroup(membersSet,placeValue));
				}
			//}
		} catch (ContextException e) {
			logger.error(e.toString());
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
	}
	
	private String getPlaceValue(IContextDataset dataset) {
		if (dataset != null) {
			IContextElement[] ctxEls = dataset.getContextElements(); 
			if (ctxEls != null && ctxEls.length > 0)
				return (String)ctxEls[0].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_CURRENT_PLACE_NAME)).getValue().getValue();
			else return "";
		} else return "";
	}

	public void postRawData(Tenant t, IEntity entity, IContextDataset dataset) {
		
		/*Account proximityAccount = contextProcessor.getProximityAccount(entity.getEntityIDAsString());
		if (proximityAccount == null) {
			logger.debug("ServiceAccount to share proximity not found, raw data not sent to Cloud Proximity Service");
			return;
		}*/
		
		if (t != null) TenantContextHolder.setTenant(t.getId());
		
		Account proximityAccount = retrieveProximityAccount(t,entity);
		if (proximityAccount == null) return;
		
		Account adapterAccount = retrieveAdapterAccount(t);
		if (adapterAccount != null) {
			setServiceReference(adapterAccount);
		} else {
			logger.debug("No adapter account found for " + ProximityServiceAdapter.adapterName + " service");
			TenantContextHolder.unset();
			return;
		}
		TenantContextHolder.unset();
		
		IScope scope = null;
		String body = null;
		
		IContextElement[] wfs = dataset.getContextElements(Factory.createScope(Constants.SCOPE_WF)); 
		IContextElement[] bts = dataset.getContextElements(Factory.createScope(Constants.SCOPE_BT)); 
		
		IContextElement wfe = null;
		if ((wfs != null) && (wfs.length > 0)) {
			wfe = wfs[0];
		} 
		
		IContextElement bte = null;
		if ((bts != null) && (bts.length > 0)) {
			bte = bts[0];
		} 
		
		String[] wfNames = null;
		int[] wfSignals = null;
		String wfTs = null;
		String wfExp = null;
		
		if (wfe != null) {
			
			entity = wfe.getEntity();
			scope = wfe.getScope();
			
			IValue wfAddressValues = wfe.getContextData().getValue(Factory.createScope(Constants.SCOPE_WF_LIST));
			IValue wfSignalsValues = wfe.getContextData().getValue(Factory.createScope(Constants.SCOPE_WF_SIGNALS));
			wfTs = wfe.getTimestampAsString();
			wfExp = wfe.getExpiresAsString();
			if ((wfAddressValues != null) && (wfSignalsValues != null)) {
				wfNames = (String[])wfAddressValues.getValue();
				wfSignals = (int[])wfSignalsValues.getValue();
			}
			
			body = createWfPostBody(proximityAccount.asURI().toString(),wfNames, wfSignals, wfTs, wfExp);
			
		}
		
		String btList = null;
		String localBt = null;
		String btTs = null;
		String btExp = null;
		
		if (bte != null) {
			
			entity = bte.getEntity();
			scope = bte.getScope();
			
			IValue localBtValues = bte.getContextData().getValue(Factory.createScope(Constants.SCOPE_BT_LOCAL));
			IValue btListValues = bte.getContextData().getValue(Factory.createScope(Constants.SCOPE_BT_LIST));
			btTs = bte.getTimestampAsString();
			btExp = bte.getExpiresAsString();
			if ((btListValues != null) && (btListValues != null)) {
				localBt = (String)localBtValues.getValue();
				btList = (String)btListValues.getValue();
			}
			
			body = createBtPostBody(proximityAccount.asURI().toString(),localBt, btList, btTs, btExp);
			
			
		}
		
		if (entity == null || scope == null) {
			logger.error("No raw data to post to Proximity Service");
			return;
		}
		
		//String response = helper.post(this.serviceUrl + "/" + proximityAccount.asURI().toString() + "/" + scope.getScopeAsString(),this.universalToken,body);
		//String query = "/" + proximityAccount.asURI().toString() + "/" + scope.getScopeAsString();
		//String response = this.proximityService.postRaw(query,body);
		String response;
		try {
			// TI note: commented line was used before service configuration was available
			// response = this.proximityService.postRaw(proximityAccount.asURI().toString() + "/" + scope.getScopeAsString(),body);
			response = this.proximityService.postRaw(proximityAccount.asURI().toString() + "/" + scope.getScopeAsString(),body);
		} catch (AttributeNotSupportedException e) {
			logger.error(e.toString(),e);
			return;
		} catch (ServiceNotAvailableException e) {
			logger.error(e.toString(),e);
			return;
		} catch (InvalidLoginException e) {
			logger.error(e.toString(),e);
			return;
		} catch (ClassCastException e) {
			logger.error(e.toString(),e);
			return;
		} 
		
		if (response != null) logger.debug("Raw data posted successfully to Proximity Service");
		
		// after post raw data retrieve from Proximity Service current user proximity..
		IContextDataset proximity = getProximity(entity,t);
		
		try {
			// .. and store it in cache
			if (proximity != null) this.contextProcessor.contextUpdate(t,proximity);
		} catch (ContextException e) {
			logger.error(e.toString(),e);
		}
	
	}

	private String createBtPostBody(String ent, String localBt, String btList, String btTs, String btExp) {
		
		try {
			
			JSONObject provider = new JSONObject();
			provider.put("id","Personal Server");
			provider.put("v","1.0");
			JSONObject entity = new JSONObject();
			entity.put("id",ent);
			entity.put("type","username");
			
			JSONObject btEl = null;
			
			if (localBt != null && btList != null) {
				btEl = new JSONObject();
				btEl.put("contextProvider",provider);
				btEl.put("entity",entity);
				btEl.put("scope",Constants.SCOPE_BT);
				btEl.put("timestamp",btTs);
				btEl.put("expires",btExp);
				JSONObject datapart = new JSONObject();
				datapart.put(Constants.SCOPE_BT_LOCAL,localBt);
				datapart.put(Constants.SCOPE_BT_LIST,btList);
				btEl.put("dataPart",datapart);
			}
			
			JSONArray items = new JSONArray();
			if (btEl != null) items.put(btEl);
			
			JSONObject ctxEl = new JSONObject();
			ctxEl.put("ctxEl",items);
			
			JSONArray items2 = new JSONArray();
			items2.put(ctxEl);
			JSONObject ctxEls = new JSONObject();
			ctxEls.put("ctxEls",items2);
			
			JSONObject data = new JSONObject();
			data.put("data",ctxEls);
			
			return data.toString();
			
			
		} catch (JSONException e) {
			logger.error(e.toString(),e);
		}
		
		return null;
	}
	
	private String createWfPostBody(String ent, String[] wfNames, int[] wfSignals, String wfTs, String wfExp) {
		
		try {
			
			JSONObject provider = new JSONObject();
			provider.put("id","Personal Server");
			provider.put("v","1.0");
			JSONObject entity = new JSONObject();
			entity.put("id",ent);
			entity.put("type","username");
			
			JSONObject wfEl = null;
			
			if (wfNames != null && wfSignals != null) {
				wfEl = new JSONObject();
				wfEl.put("contextProvider",provider);
				wfEl.put("entity",entity);
				wfEl.put("scope",Constants.SCOPE_WF);
				wfEl.put("timestamp",wfTs);
				wfEl.put("expires",wfExp);
				JSONObject datapart = new JSONObject();
				datapart.put(Constants.SCOPE_WF_LIST,createWfList(wfNames));
				JSONArray wfDevice = new JSONArray();
				for (int i=0; i<wfNames.length; i++) {
					JSONObject wfap = new JSONObject();
					wfap.put("wfBssid",wfNames[i]);
					wfap.put("wfSignal",wfSignals[i]);
					wfDevice.put(i,wfap);
				}
				datapart.put("wfDevice",wfDevice);
				wfEl.put("dataPart",datapart);
			}
			
			JSONArray items = new JSONArray();
			if (wfEl != null) items.put(wfEl);
			
			JSONObject ctxEl = new JSONObject();
			ctxEl.put("ctxEl",items);
			
			JSONArray items2 = new JSONArray();
			items2.put(ctxEl);
			JSONObject ctxEls = new JSONObject();
			ctxEls.put("ctxEls",items2);
			
			JSONObject data = new JSONObject();
			data.put("data",ctxEls);
			
			return data.toString();
			
			
		} catch (JSONException e) {
			logger.error(e.toString(),e);
		}
		
		return null;
	}

	private String createWfList(String[] wfNames) {
		String wfList = "";
		for (int i=0; i<wfNames.length; i++) {
			wfList += wfNames[i];
			if (i != wfNames.length - 1) wfList += ";";
		}
		return wfList;
	}

	@Override
	public IContextDataset getProximity(IEntity entity, Tenant t) {
		
		/*Account proximityAccount = contextProcessor.getProximityAccount(entity.getEntityIDAsString());
		if (proximityAccount == null) {
			logger.debug("ServiceAccount related to said " + entity.getEntityIDAsString() + " not found. No proximity returned");
			return IContextDataset.EMPTY_CONTEXT_DATASET;
		}*/
		
		Account proximityAccount = retrieveProximityAccount(t,entity);
		if (proximityAccount == null) return IContextDataset.EMPTY_CONTEXT_DATASET;
		
		
		Account adapterAccount = retrieveAdapterAccount(t);
		if (adapterAccount != null) {
			setServiceReference(adapterAccount);
		} else {
			logger.debug("No adapter account found for " + ProximityServiceAdapter.adapterName + " service");
			return IContextDataset.EMPTY_CONTEXT_DATASET;
		}
		
		// Step 1: invoke ContextAPI to retrieve userProximity ContextML
		//String response = helper.get(this.serviceUrl + "/" + proximityAccount.asURI().toString() + "/" + this.userProximity.getScopeAsString() + "?output=json",this.universalToken);
		ServiceResponse[] responses = null;
		try {
			responses = this.proximityService.getRaw("/" + proximityAccount.asURI().toString() + "/" + this.userProximity.getScopeAsString() + "?output=json");
		} catch (AttributeNotSupportedException e) {
			logger.error(e.toString(),e);
		} catch (ServiceNotAvailableException e) {
			logger.error(e.toString(),e);
		} catch (InvalidLoginException e) {
			logger.error(e.toString(),e);
		} catch (ClassCastException e) {
			logger.error(e.toString(),e);
		}
		// Step 2: create context dataset with users in proximity (with ad hoc group purpose meaning ==> BT + High WF)
		if (responses != null && responses[0] != null) {
			logger.debug("Received response from Cloud Proximity Service: " + responses[0].getResponse());
			IContextDataset dataset = parseCloudResponse(entity,responses[0].getResponse());
			return dataset;
		}
		return IContextDataset.EMPTY_CONTEXT_DATASET;
	}

	private IContextDataset parseCloudResponse(IEntity entity, String strResponse) {
		IContextDataset dataset;
		String proximity = "";
		try {
			JSONObject jsonResponse = new JSONObject(strResponse);
			JSONObject response = jsonResponse.getJSONObject("response");
			JSONObject result = response.getJSONObject("result");
			JSONArray ctxElsArr = result.getJSONArray("ctxEls");
			JSONObject ctxEls = ctxElsArr.getJSONObject(0);
			JSONArray ctxElArr = ctxEls.getJSONArray("ctxEl");
			if (ctxElArr.length() > 0) {
				JSONObject ctxEl = ctxElArr.getJSONObject(0);
				JSONObject datapart = ctxEl.getJSONObject("dataPart");
				JSONArray users = datapart.getJSONArray("user");
				for (int i=0; i<users.length(); i++) {
					JSONObject user = users.getJSONObject(i);
					String said = user.getString("userName");
					String tech = user.getString("tech");
					String prox = user.getString("prox");
					if (tech.equalsIgnoreCase("BT") || (tech.equalsIgnoreCase("WF") && prox.equalsIgnoreCase("high"))) {
						if (!proximity.equalsIgnoreCase("")) proximity += ",";
						proximity += said;
					}
				}
				dataset = Util.createSimpleContextDataset(entity, 
						Factory.createScope(Constants.SCOPE_PROXIMITY), Constants.SCOPE_PROXIMITY_USERS, proximity, duration);
				return dataset;
			}
		} catch (JSONException e) {
			logger.debug(e.toString());
			return IContextDataset.EMPTY_CONTEXT_DATASET;
		}
		return IContextDataset.EMPTY_CONTEXT_DATASET;
	}
	
	private String processDataToStableProximity(IContextDataset dataset) {
		
		// dataset contains last user proximities
		IContextElement[] proxs = dataset.getContextElements();
		if ((proxs != null) && (proxs.length > 0)) {
			
			logger.info("processDataToProximity (" + proxs.length + " proximities)");
			
			for (int i=0; i<proxs.length; i++) {
				logger.debug(i + " - " 
						+ (String)proxs[i].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_PROXIMITY_USERS)).getValue().getValue() + " " 
						+ (String)proxs[i].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
			}
			
			long recentItem = Factory.timestampFromXMLString((String)proxs[0].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
			if (recentItem < (System.currentTimeMillis() - (Defaults.PROXIMITY_PERIOD * 1000))) {
				logger.debug("Received proximities are out of permanence period");
				return "";
			}
			
			int index = Utility.getFirstSignificativeItem(proxs,Defaults.PROXIMITY_PERIOD,Defaults.PROXIMITY_TOLERANCE);
			if (index == -1) {
				logger.debug("Received proximities are too recent to infer proximity");
				return "";
			} else {
				logger.debug("Significative users list for stable proximity:");
				for (int i=0; i<proxs.length; i++) {
					String prefix = "";
					if (i <= index) prefix = "*" + i;
					logger.debug(prefix + " - " 
							+ (String)proxs[i].getContextData().getContextValue(Factory.createScope(Constants.SCOPE_PROXIMITY_USERS)).getValue().getValue() + " " 
							+ (String)proxs[i].getMetadata().getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue());
				}
			}
			
			int all = index + 1;
			HashMap<String, Integer> users = new HashMap<String,Integer>();
			for (int i=0; i<=index; i++) {
				String usersList = (String)proxs[i].getContextData().getValue(Factory.createScope(Constants.SCOPE_PROXIMITY_USERS)).getValue();
				StringTokenizer tok = new StringTokenizer(usersList,",");
				while (tok.hasMoreTokens()) {
					String user = tok.nextToken();
					Integer n = users.get(user);
					if (n == null) users.put(user,new Integer(1));
					else users.put(user, new Integer(n.intValue()+1));
				}
			}
			
			String stableProximity = "";
			Set<String> keys = users.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String user = it.next();
				Integer n = users.get(user);
				//if (n.intValue() == all) {
				// user is in stable proximity if he is in proximity at least in all scans -1
				if (n.intValue() >= (all - 1)) {
					stableProximity += user;
					stableProximity += ",";					
				}
			}
			if (stableProximity.endsWith(",")) stableProximity = stableProximity.substring(0, stableProximity.length()-1);
			return stableProximity;
		} else return "";
		
	}

}
