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

package eu.dime.ps.controllers.context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.DeviceManager;
import eu.dime.ps.semantic.model.ddo.Device;
import eu.dime.ps.storage.entities.Tenant;

public class DeviceStatusContextUpdater implements LiveContextUpdater, IContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceStatusContextUpdater.class);
	//private URI dataSource = new URIImpl("urn:DeviceStatusContextUpdater");
	
	private IContextProcessor contextProcessor;
	//private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;
	private DeviceManager deviceManager;
	
	//private Connection connection = null;
	//private LiveContextService liveContextService = null;
	
	private List<IContextElement> rawContextQueue = new ArrayList<IContextElement>();
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	
	public void init() {
		logger.debug("INIT Device Info Context Updater");
		try {
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_STATUS),this);
		} catch (ContextException e) {
			logger.error(e.toString(),e);
		} 
	}
	
	@Override
	public void contextChanged(RawContextNotification notification) throws Exception {
		
		String name = notification.getName(); 
		logger.debug("Device Status Raw Context notification received: " + name);
		
		StringTokenizer tok = new StringTokenizer(name,",");
		IEntity entity = Factory.createEntity(tok.nextToken());
		IScope scope = Factory.createScope(tok.nextToken());
		
		Tenant t = new Tenant();
		t.setId(notification.getTenant());
		
		IContextDataset dataset;
		try {
			dataset = this.contextProcessor.getContext(t,entity, scope);
			if (!dataset.equals(IContextDataset.EMPTY_CONTEXT_DATASET)) {
				IContextElement[] ces = dataset.getContextElements(scope);
				if (ces != null && ces.length == 1) {
					this.rawContextQueue.add(ces[0]);
					update();
				}
			}
		} catch (ContextException e) {
			logger.error(e.toString());
		}
		
	}
	
	@Override
	public void update() {
		Iterator<IContextElement> it = this.rawContextQueue.iterator();
		while (it.hasNext()) {
			try {
				IContextElement ce = it.next();
				Tenant t = tenantManager.getByAccountName(ce.getEntity().getEntityIDAsString());
				TenantContextHolder.setTenant(t.getId());
				//connection = connectionProvider.getConnection(t.getId().toString());
				//liveContextService = connection.getLiveContextService();
				if (ce.getScope().getScopeAsString().equalsIgnoreCase(Constants.SCOPE_STATUS)) {
					String source = ce.getSource();
					Boolean isActive = (Boolean)ce.getContextData().getContextValue(Factory.createScope(Constants.SCOPE_STATUS_IS_ALIVE)).getValue().getValue();
					//LiveContextSession session = liveContextService.getSession(dataSource);
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					if (isActive && source != null && source.equalsIgnoreCase("mobile-crawler")) {
						Device device = deviceManager.get("ddo:Mobile");
						device.setLastActive(calendar);
					} else if (isActive && source != null && source.equalsIgnoreCase("desktop-crawler")) {
						Device device = deviceManager.get("ddo:Laptop");
						device.setLastActive(calendar);
					}
				}
				TenantContextHolder.unset();
			} /*catch (RepositoryException e) {
				logger.error(e.toString(),e);
			}*/ catch (ClassCastException e) {
				logger.error(e.toString(),e);
			} /*catch (LiveContextException e) {
				logger.error(e.toString(),e);
			}*/ catch (InfosphereException e) {
				logger.error(e.toString(),e);
			}
			it.remove();
		}
	}

}
