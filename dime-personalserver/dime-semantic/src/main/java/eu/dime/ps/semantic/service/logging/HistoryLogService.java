package eu.dime.ps.semantic.service.logging;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.DateUtil;
import ie.deri.smile.vocabulary.DUHO;
import ie.deri.smile.vocabulary.NAO;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.util.StringUtils;

public class HistoryLogService {

	private static final Logger logger = LoggerFactory.getLogger(HistoryLogService.class);

	private ResourceStore resourceStore;
	private BroadcastManager broadcastManager;
	
	public HistoryLogService(ResourceStore resourceStore) {
		this.resourceStore = resourceStore;
		this.broadcastManager = BroadcastManager.getInstance();
	}
	
	/**
	 * Removes all Log instances of a specific type which timestamp is lower
	 * than a given datetime.
	 * @param type the type of log (subclass of duho:Log) 
	 * @param from the date which sets the threshold for the oldest log to keep
	 */
	public void cleanup(URI type, Calendar from) {
		logger.info("History logs cleanup started...");
		String query = StringUtils.strjoinNL(
				"PREFIX duho: "+DUHO.NS_DUHO.toSPARQL()+"\n",
				"SELECT ?log WHERE {",
				"?log a "+type.toSPARQL()+";",
				"  duho:timestamp ?timestamp .",
				"FILTER (timestamp < "+DateUtil.dateTimeToString(from)+") .",
				"}");
		Iterator<QueryRow> results = resourceStore.sparqlSelect(query).iterator();
		int count = 0;
		while (results.hasNext()) {
			resourceStore.removeGraph(results.next().getValue("log").asURI());
			count++;
		}
		logger.info("History logs cleanup finished: "+count+" logs have been removed");
	}

	public URI createLogForContext(URI contextGraph) {
		TripleStore tripleStore = resourceStore.getTripleStore();
		URI logGraph = resourceStore.createGraph(DUHO.ContextLog);
		URI metadataGraph = tripleStore.getOrCreateMetadataGraph(logGraph);
		
		Model metadata = RDF2Go.getModelFactory().createModel().open();
		metadata.addAll(tripleStore.getModel(contextGraph).iterator());
		
		// remove hasDataGraph/isDataGraphFor metadata
		metadata.removeStatements(Variable.ANY, NAO.hasDataGraph, Variable.ANY);
		metadata.removeStatements(Variable.ANY, NAO.isDataGraphFor, Variable.ANY);
		
		// add live context metadata to log graph
		tripleStore.addAll(logGraph, metadata.iterator());
		tripleStore.addStatement(metadataGraph, logGraph, DUHO.timestamp, DateUtil.currentDateTimeAsLiteral());
		
		// broadcast new graph has been added
		broadcastManager.sendBroadcast(new Event(resourceStore.getName(), Event.ACTION_GRAPH_ADD, logGraph));
		
		return logGraph;
	}

	public URI createLogForPrivacyPreference(PrivacyPreference privacyPref) {
		TripleStore tripleStore = resourceStore.getTripleStore();
		URI logGraph = resourceStore.createGraph(DUHO.PrivacyPreferenceLog);
		URI metadataGraph = tripleStore.getOrCreateMetadataGraph(logGraph);
		tripleStore.addAll(logGraph, privacyPref.getModel().iterator());
		try {
			tripleStore.addAll(logGraph, resourceStore.get(privacyPref.getAllAccessSpace().next().asResource()).getModel().iterator());
		} catch (NoSuchElementException e) {
			logger.error("The privacy preference "+privacyPref.asResource()+" does not contain any AccessSpace: "+e.getMessage(), e);
		} catch (NotFoundException e) {
			logger.error("The privacy preference "+privacyPref.asResource()+" references an AccessSpace which does not exist: "+e.getMessage(), e);
		}
		tripleStore.addStatement(metadataGraph, logGraph, DUHO.timestamp, DateUtil.currentDateTimeAsLiteral());
		broadcastManager.sendBroadcast(new Event(resourceStore.getName(), Event.ACTION_GRAPH_ADD, logGraph));
		return logGraph;
	}

}
