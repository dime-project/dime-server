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

package eu.dime.ps.controllers.context.raw.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;
import eu.dime.commons.dto.Data;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.exceptions.JsonConversionException;

import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.context.raw.ifc.RawContextManager;
import eu.dime.ps.controllers.context.raw.utils.JSONContextTransformer;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.storage.entities.Tenant;

public class RawContextManagerImpl implements RawContextManager {
	
	private Logger logger = Logger.getLogger(RawContextManagerImpl.class);
			
	private IContextProcessor contextProcessor = null;
	private AccountManager accountManager = null;
	private TenantManager tenantManager = null;
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}
	
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	@Override
	public Data<Context> getContext(String said, String scopeId)
			throws ContextException {
		
		if (contextProcessor.getProximityAccount(said) == null) {
	    	try {
	     		//Collection<Account> accounts = this.accountManager.getAllByCreatorAndByType(this.accountManager.getMe().asURI().toString(),DimeServiceAdapter.adapterName);
	     		Collection<Account> accounts = this.accountManager.getAllByCreator(this.accountManager.getMe());
	     		if (accounts != null && accounts.size() > 0) {
                    Account proximityAccount = accounts.iterator().next();
                    if (proximityAccount != null) this.contextProcessor.addProximityAccount(said,proximityAccount);
                }
	    	 } catch (InfosphereException e) {
	    		 logger.error(e.toString(),e);
	    	 }
    	}
		
		IEntity entity = Factory.createEntity(said);
		IScope scope = Factory.createScope(scopeId);
		Tenant t = tenantManager.getByAccountName(said);
		IContextDataset ctxDataset = contextProcessor.getContext(t,entity, scope); 
		
		try { 
			 List<Context> entry = JSONContextTransformer.contextDataset2jsonContextEntry(ctxDataset);
			 Data<Context> data = new Data<Context>(); 
			 data.setEntry(entry); 
			 return data;
		 } catch (JsonConversionException e) { 
			 logger.error(e.getMessage(),e);
			 throw new ContextException();
		 }
		
	}

	@Override
	public void contextUpdate(String said, ContextData contextData) throws ContextException {
		
		IContextDataset ctxds;
		try {
			ctxds = JSONContextTransformer.jsonContextData2contextDataset(contextData);
		} catch (JsonConversionException e) {
			logger.error(e.getMessage(),e);
			return;
		}
		
		if (contextProcessor.getProximityAccount(said) == null) {
	    	try {
	     		//Collection<Account> accounts = this.accountManager.getAllByCreatorAndByType(this.accountManager.getMe().asURI().toString(),DimeServiceAdapter.adapterName);
	     		Collection<Account> accounts = this.accountManager.getAllByCreator(this.accountManager.getMe());
	     		if (accounts != null && accounts.size() > 0) {
                    Account proximityAccount = accounts.iterator().next();
                    if (proximityAccount != null) this.contextProcessor.addProximityAccount(said,proximityAccount);
                }
	    	 } catch (InfosphereException e) {
	    		 logger.error(e.toString(),e);
	    	 }
    	}
	
		Tenant t = tenantManager.getByAccountName(said);
		contextProcessor.contextUpdate(t,ctxds);
		
	}

	@Override
	public void deleteContext(String said, String scopeId)
			throws ContextException {
		
		IEntity entity = Factory.createEntity(said);
		IScope scope = Factory.createScope(scopeId);
		contextProcessor.deleteContext(entity, scope);
	}

}
