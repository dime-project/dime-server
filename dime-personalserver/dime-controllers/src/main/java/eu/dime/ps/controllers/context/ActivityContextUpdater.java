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

import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DPO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.DimeInternalNotification;
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
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dcon.Peers;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.model.dpo.Activity;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.storage.entities.Tenant;

public class ActivityContextUpdater implements LiveContextUpdater, IContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivityContextUpdater.class);
	
	private URI dataSource = new URIImpl("urn:ActivityContextUpdater");
	private Connection connection = null;
	private LiveContextService liveContextService = null;
	//private ModelFactory mf = new ModelFactory();
	
	private IContextProcessor contextProcessor;
	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;
	
	private List<IContextElement> rawContextQueue = new ArrayList<IContextElement>();
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void init() {
		// TI Temporary commented out
		/*logger.debug("INIT Activity Context Updater");
		try {
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_ACTIVITY),this);
		} catch (ContextException e) {
			logger.error(e.toString(),e);
		} */
	}

	@Override
	public void contextChanged(RawContextNotification notification) throws Exception {
		
		String name = notification.getName(); 
		logger.debug("Raw Activity Context notification received: " + name);
		
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
				// [TI] ce.getEntity().getEntityIDAsString() contains said
				Tenant t = tenantManager.getByAccountName(ce.getEntity().getEntityIDAsString());
				if (t != null) {
						if (ce.getScope().getScopeAsString().equalsIgnoreCase(Constants.SCOPE_ACTIVITY)) {
							TenantContextHolder.setTenant(t.getId());
							connection = connectionProvider.getConnection(t.getId().toString());
							liveContextService = connection.getLiveContextService();
							LiveContextSession session = liveContextService.getSession(dataSource);
							session.remove(State.class, DCON.currentActivity);
							//String act = (String)ce.getContextData().getValue(Factory.createScope(Constants.SCOPE_ACTIVITY_CURRENT)).getValue();
							//Activity activity = mf.getDPOFactory().createActivity("urn:activity:" + act);
							session.set(State.class,DCON.currentActivity,DPO.AttendingEvent);
							TenantContextHolder.unset();
						}
				} else logger.debug("No tenant found for account " + ce.getEntity().getEntityIDAsString() + ": peers not updated");
				
			} catch (RepositoryException e) {
				logger.error(e.toString(),e);
			} catch (ClassCastException e) {
				logger.error(e.toString(),e);
			} catch (LiveContextException e) {
				logger.error(e.toString(),e);
			} 
			it.remove();
		}
		
	}

}
