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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.automation.action.NotifyUI;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.service.impl.PimoService;

public class EventProcessorTree implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(EventProcessorTree.class);
	
	private ConnectionProvider connectionProvider;
	private NotifierManager notifierManager;
	
	private ConcurrentMap<String, EventLogger> loggerCache = new ConcurrentHashMap<String, EventLogger>();
	private ConcurrentMap<String, EventProcessor> processorCache = new ConcurrentHashMap<String, EventProcessor>();
	
	public EventProcessorTree() {
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	@Override
	public void onReceive(Event event) {
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

		// registering event in EventLogger
		Model metadata = event.getData() == null ? RDF2Go.getModelFactory().createModel().open() : event.getData().getModel();
		String eventAction = event.getAction();
		if (Event.ACTION_RESOURCE_ADD.equals(eventAction)) {
			eventLogger.resourceAdded(event.getIdentifier(), metadata);
		} else if (Event.ACTION_RESOURCE_MODIFY.equals(eventAction)) {
			eventLogger.resourceModified(event.getIdentifier(), metadata);
		} else if (Event.ACTION_RESOURCE_DELETE.equals(eventAction)) {
			eventLogger.resourceDeleted(event.getIdentifier());
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
				logger.error("Malformed Rule" , e);
			} catch (InvalidOperatorException e) {
				logger.error("Invalid Operator" , e);
			}
			
			// FIXME do we need to register the rules in the event processor?? -- [jer] rule transformer is separate from the EventProcessor, thus not directly linked to the controller. we might need to add a method in this class to register new rules after the EP is intialised?
			//for (Rule rule : getRulesFromPIMO(pimoService))
			//	eventProcessor.registerRule(rule);
			
			processorCache.putIfAbsent(event.getTenant(), eventProcessor);
		}
		
		for (Tuple<Rule, QueryRow> tuple : eventProcessor.executeEventProcessor(event.getIdentifier().asURI())) {
			Rule rule = tuple.firstElement;
			QueryRow queryRow = tuple.secondElement;
			
			List<Node> results = new ArrayList<Node>(); 
			for (String variable : rule.getVariables())
				results.add(queryRow.getValue(variable));
			
			for (Action actionDef : rule.getActions()) {
				ie.deri.smile.rules.actions.Action action = null;
				try {
					action = ActionRegistry.getInstance().build(actionDef.getAction());
					
					// FIXME [Ismael] injecting the notifier manager in a hacky way; temporary solution for 2nd review
					if (action instanceof NotifyUI)
						((NotifyUI) action).setNotifierManager(this.notifierManager);
					
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
