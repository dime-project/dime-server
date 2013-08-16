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

package eu.dime.ps.controllers.context;

import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
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
import eu.dime.ps.controllers.context.raw.utils.Utility;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dcon.Peers;
import eu.dime.ps.semantic.model.nao.Party;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

public class PeersContextUpdater implements LiveContextUpdater, IContextListener {
	
	private static final Logger logger = LoggerFactory.getLogger(PeersContextUpdater.class);
	
	private URI dataSource = new URIImpl("urn:PeersContextUpdater");
	
	private IContextProcessor contextProcessor;
	private ConnectionProvider connectionProvider;
	private PersonManager personManager;
	private AccountManager accountManager;
	private TenantManager tenantManager;
	
	private Connection connection = null;
	private LiveContextService liveContextService = null;
	
	private List<IContextElement> rawContextQueue = new ArrayList<IContextElement>();
	
	public void setContextProcessor(IContextProcessor contextProcessor) {
		this.contextProcessor = contextProcessor;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}
	
	public void init() {
		logger.debug("INIT Peers Context Updater");
		try {
			this.contextProcessor.subscribeContext(Constants.ENTITY_ALL_USERS, Factory.createScope(Constants.SCOPE_PROXIMITY),this);
			//dataSource = new URIImpl("urn:PeersContextUpdater");
		} catch (ContextException e) {
			logger.error(e.toString(),e);
		} 
	}
	
	@Override
	public void contextChanged(RawContextNotification notification) throws Exception {
		
		String name = notification.getName(); 
		logger.debug("Raw Proximity Context notification received: " + name);
		
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
			IContextElement ce = it.next();
			// [TI] ce.getEntity().getEntityIDAsString() contains account used to share proximity (e.g. urn:juan:account)
			Tenant tenant = tenantManager.getByAccountName(ce.getEntity().getEntityIDAsString());
			if (tenant == null) {
				logger.error("No tenant found for account " + ce.getEntity().getEntityIDAsString() + ": peers not updated");
			} else {
				if (ce.getScope().getScopeAsString().equalsIgnoreCase(Constants.SCOPE_PROXIMITY)) {
					
					try {
						connection = connectionProvider.getConnection(tenant.getId().toString());
						liveContextService = connection.getLiveContextService();
					} catch (RepositoryException e) {
						logger.error("Peers couldn't be updated: " + e.getMessage(), e);
					}
					
					LiveContextSession session = liveContextService.getSession(dataSource);
					try {
						session.setAutoCommit(false);
						session.remove(Peers.class, DCON.nearbyPerson);
						
						String namesNearby = (String)ce.getContextData().getValue(Factory.createScope(Constants.SCOPE_PROXIMITY_USERS)).getValue();
						StringTokenizer tok = new StringTokenizer(namesNearby,",");
						while (tok.hasMoreTokens()) {
							String said = tok.nextToken();
							User u = Utility.findByUsername(said);
							if (u != null) {
								String userUri = u.getAccountUri();
								Account account;
								try {
									account = accountManager.get(userUri);
									if (account != null) {
										Party pt = account.getCreator();
										if (pt != null) {
											Person p = personManager.get(pt.toString());
											if (p != null) {
												session.add(Peers.class,DCON.nearbyPerson,p.asURI());
												logger.debug("Peer " + p.getPrefLabel() + " added to Live Context");
											}
										}
									}
								} catch (InfosphereException e) {
									logger.warn(e.getMessage(),e);
								}
							}
						}
						session.commit();
					} catch (LiveContextException e) {
						logger.error("Peers couldn't be updated: " + e.getMessage(), e);
					}
					
				}
			}
			// TODO review if the following line should be there...
			it.remove();
		}
	}

}
