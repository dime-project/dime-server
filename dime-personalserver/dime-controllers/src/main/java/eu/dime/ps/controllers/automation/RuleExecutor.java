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

package eu.dime.ps.controllers.automation;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rules.actions.ActionRegistry;
import ie.deri.smile.rules.actions.IllegalActionException;
import ie.deri.smile.rules.eventprocessor.Action;
import ie.deri.smile.rules.eventprocessor.EventProcessor;
import ie.deri.smile.rules.eventprocessor.Rule;
import ie.deri.smile.rules.eventprocessor.network.RulesNetwork;
import ie.deri.smile.rules.log.EventLogger;
import ie.deri.smile.rules.transformer.exception.InvalidOperatorException;
import ie.deri.smile.rules.transformer.exception.MapperException;
import ie.deri.smile.rules.transformer.exception.RuleMalformedException;
import ie.deri.smile.rules.util.Tuple;
import ie.deri.smile.vocabulary.DRMO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.automation.action.IncreaseTrustLevel;
import eu.dime.ps.controllers.automation.action.Notify;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.service.impl.PimoService;

public class RuleExecutor implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(RuleExecutor.class);
	
	private static final ActionRegistry actionRegistry = ActionRegistry.getInstance();
	
	private ConnectionProvider connectionProvider;
	private NotifierManager notifierManager;
	
	private ConcurrentMap<String, EventLogger> loggerCache = new ConcurrentHashMap<String, EventLogger>();
	private ConcurrentMap<String, EventProcessor> processorCache = new ConcurrentHashMap<String, EventProcessor>();
	
	public RuleExecutor() {
		BroadcastManager.getInstance().registerReceiver(this);
		actionRegistry.register(Notify.IDENTIFIER, Notify.class);
		actionRegistry.register(IncreaseTrustLevel.IDENTIFIER, IncreaseTrustLevel.class);
	}
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	private static final String[] ACTIONS = new String[] {
		Event.ACTION_RESOURCE_ADD,
		Event.ACTION_RESOURCE_MODIFY,
		Event.ACTION_RESOURCE_DELETE
	};

	@Override
	public void onReceive(Event event) {
		String eventAction = event.getAction();
		
		// only interested in create/update/delete actions
		if (!ArrayUtils.contains(ACTIONS, eventAction)) {
			return;
		}
		
		EventLogger eventLogger = loggerCache.get(event.getTenant());
		EventProcessor eventProcessor = processorCache.get(event.getTenant());

		TripleStore tripleStore = null;
		PimoService pimoService = null;
		try {
			tripleStore = connectionProvider.getConnection(event.getTenant()).getTripleStore();
			pimoService = connectionProvider.getConnection(event.getTenant()).getPimoService();
		} catch (RepositoryException e) {
			logger.error("Cannot process event, error when retrieving the PIMO service and its underlying data: "+e.getMessage(), e);
			return;
		}

		// lazy initialization of event loggers
		if (eventLogger == null) {
			eventLogger = new EventLogger(tripleStore.getModel(new URIImpl("urn:eventLog")));
			loggerCache.putIfAbsent(event.getTenant(), eventLogger);
		}

		// lazy initialization of event processors
		if (eventProcessor == null) {
			ModelSet dataset = pimoService.getTripleStore().getUnderlyingModelSet();
			
			// create new EventProcessor and register active rules
			eventProcessor = new RulesNetwork(dataset, eventLogger.getURI(), pimoService.getPimoUri(), pimoService.getPimoUri());
			
			try {
				eventProcessor.registerRulesInStore();
			} catch (MapperException e) {
				logger.error("Couldn't register rules.", e);
			} catch (RuleMalformedException e) {
				logger.error("Couldn't register rules", e);
			} catch (InvalidOperatorException e) {
				logger.error("Couldn't register rules", e);
			}
			
			processorCache.putIfAbsent(event.getTenant(), eventProcessor);
		}

		Model metadata = event.getData() == null ? RDF2Go.getModelFactory().createModel().open() : event.getData().getModel();
		Resource eventResource = event.getIdentifier().asURI();

		// registering event in EventLogger
		if (Event.ACTION_RESOURCE_ADD.equals(eventAction)) {
			eventLogger.resourceAdded(event.getIdentifier(), metadata);
		} else if (Event.ACTION_RESOURCE_MODIFY.equals(eventAction)) {
			eventLogger.resourceModified(event.getIdentifier(), metadata);
		} else if (Event.ACTION_RESOURCE_DELETE.equals(eventAction)) {
			eventLogger.resourceDeleted(event.getIdentifier());
		}

		// register new rules
		if (event.is(DRMO.Rule) && Event.ACTION_RESOURCE_ADD.equals(eventAction)) {
			try {
				eventProcessor.registerRule(eventResource.asURI());
			} catch (Exception e) {
				logger.error("Couldn't register new rule " + eventResource, e);
			}
			return;
		}

		// pass event to processor, and execute the actions of the satisfied rules
		logger.info("Check rules for " + eventAction + " " + event.getIdentifier());
		for (Tuple<Rule, QueryRow> tuple : eventProcessor.executeEventProcessor(eventResource.asURI())) {
			Rule rule = tuple.firstElement;
			QueryRow queryRow = tuple.secondElement;
			
			List<Node> results = new ArrayList<Node>(); 
			for (String variable : rule.getVariables()) {
				results.add(queryRow.getValue(variable));
			}
			
			for (Action ruleAction : rule.getActions()) {
				ie.deri.smile.rules.actions.Action action = null;
				try {
					action = actionRegistry.build(ruleAction.getAction(), ruleAction.getActionSubject(), ruleAction.getActionObject());
					
					if (action instanceof Notify) {
						((Notify) action).setNotifierManager(this.notifierManager);
					}
					
					action.execute(results, pimoService.getTripleStore().getUnderlyingModelSet(), event.getTenant());
				} catch (InstantiationException e) {
					logger.error("Action 'NotifyUI' could not be registered. Rules triggering this action won't result in the expected behaviour." , e);
				} catch (IllegalAccessException e) {
					logger.error("Action 'NotifyUI' could not be registered. Rules triggering this action won't result in the expected behaviour." , e);
				} catch (IllegalActionException e) {
					logger.error("Action 'NotifyUI' cannot be executed. Rules triggering this action won't result in the expected behaviour." , e);
				}
			}
		}
	}
}
