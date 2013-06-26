package eu.dime.ps.semantic.service.logging;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.DUHO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;

import java.util.Calendar;
import java.util.Iterator;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.rdf.ResourceModel;
import eu.dime.ps.semantic.rdf.impl.ResourceModelImpl;
import eu.dime.ps.semantic.util.DateUtils;
import eu.dime.ps.semantic.util.StringUtils;

/**
 * THIS HAS BEEN MOVED TO smile-rules-core
 * 
 * in the event log graph we store only the triples specifying the type
 * of the resource that is new/modified, and the createdDate and lastModified
 * datetimes. (for knowing if it's a new item or a modified one).
 * the graph that this resource was modified is also passed.
 * the constraint is (to simplify the rule interpreter):
 *  - for new resources, we only put the created (nao:created, nfo:fileCreated) date, NOT the lastModified 
 *  - for modified resources, we only put the last modified date, NOT the created date.
 * 
 * it will track what aspects and elements changed in the live context.
 * the live context will move changes to the previous context based on changes on elements,
 * meaning that if I modify element X from A to B, live context will have A, and previous
 * context will have B; then I modify element Y from 1 to 2, live context will have A and 1,
 * and previous context will have B and 2. 
 * 
 * @author Ismael Rivera
 */
public class EventLogger implements BroadcastReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(EventLogger.class);

	private ConnectionProvider connectionProvider;
	private HistoryLogService historyLogService;
	
	private URI EVENT_LOG_GRAPH = new URIImpl("urn:eventLog");
	
	public EventLogger(ConnectionProvider connectionProvider, HistoryLogService historyLogService) {
		this.connectionProvider = connectionProvider;
		this.historyLogService = historyLogService;
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	public Model getModel(String name) throws RepositoryException {
		return connectionProvider.getConnection(name).getTripleStore().getModel(EVENT_LOG_GRAPH);
	}
	
	@Override
	public void onReceive(Event event) {
		// remove all event logs which are older than 1 hour
		Calendar from = Calendar.getInstance();
		from.add(Calendar.MINUTE, -60);
//		historyLogService.cleanup(DUHO.EventLog, from);
		
		TripleStore tripleStore = null;
		try {
			tripleStore = connectionProvider.getConnection(event.getTenant()).getTripleStore();
			boolean initialised = tripleStore.containsStatements(EVENT_LOG_GRAPH, EVENT_LOG_GRAPH, RDF.type, DUHO.Log);
			if (!initialised) {
				tripleStore.addStatement(EVENT_LOG_GRAPH, EVENT_LOG_GRAPH, RDF.type, DUHO.Log);
				tripleStore.addStatement(EVENT_LOG_GRAPH, EVENT_LOG_GRAPH, NAO.created, DateUtils.currentDateTimeAsLiteral());
			}
		} catch (RepositoryException e) {
			logger.error("Couldn't store event in Log "+EVENT_LOG_GRAPH+": cannot access triple store for tenant "+event.getTenant(), e);
			return;
		}
		
		// remove all resources that have been in the event log for longer than 1 hour
		DatatypeLiteral timestamp = DateUtils.dateTimeAsLiteral(from);
		String query = StringUtils.strjoinNL(
				"PREFIX nao: "+NAO.NS_NAO.toSPARQL(),
				"PREFIX nfo: "+NFO.NS_NFO.toSPARQL(),
				"SELECT ?resource",
				"WHERE {",
				"  GRAPH "+EVENT_LOG_GRAPH.toSPARQL()+" {",
				"    { ?resource nao:created ?timestamp . } UNION",
				"    { ?resource nao:lastModified ?timestamp . } UNION",
				"    { ?resource nfo:fileCreated ?timestamp . } UNION",
				"    { ?resource nfo:fileLastModified ?timestamp . }",
				"    FILTER (?timestamp < \""+timestamp.getValue()+"\"^^"+timestamp.getDatatype().toSPARQL()+") .",
				"  }",
				"}");
		Iterator<QueryRow> toDelete = tripleStore.sparqlSelect(query).iterator();
		while (toDelete.hasNext()) {
			Resource resourceToDelete = toDelete.next().getValue("resource").asResource();
			logger.debug("Deleting "+resourceToDelete+" from Log "+EVENT_LOG_GRAPH);
			tripleStore.remove(EVENT_LOG_GRAPH, resourceToDelete);
		}

		// add the resource to the event log
		Model model = event.getData() == null ? RDF2Go.getModelFactory().createModel().open() : event.getData().getModel();
		ResourceModel resource = new ResourceModelImpl(model, event.getIdentifier());
		String action = event.getAction();
		
		if (Event.ACTION_RESOURCE_ADD.equals(action)) {
			for (URI type : event.getTypes()) {
				tripleStore.addStatement(EVENT_LOG_GRAPH, resource.getResourceIdentifier(), RDF.type, type);
			}
			tripleStore.addStatement(EVENT_LOG_GRAPH,
					resource.getResourceIdentifier(), NAO.created, extractDate(resource, NAO.created, DateUtils.currentDateTimeAsLiteral()));
		} else if (Event.ACTION_RESOURCE_MODIFY.equals(action)) {
			tripleStore.removeFromGraph(EVENT_LOG_GRAPH, resource.getResourceIdentifier());
			for (URI type : event.getTypes()) {
				tripleStore.addStatement(EVENT_LOG_GRAPH, resource.getResourceIdentifier(), RDF.type, type);
			}
			tripleStore.addStatement(EVENT_LOG_GRAPH,
					resource.getResourceIdentifier(), NAO.lastModified, extractDate(resource, NAO.lastModified, DateUtils.currentDateTimeAsLiteral()));
		} else if (Event.ACTION_RESOURCE_DELETE.equals(action)) {
			tripleStore.removeFromGraph(EVENT_LOG_GRAPH, resource.getResourceIdentifier());
		}
	}
	
	private DatatypeLiteral extractDate(ResourceModel resource, URI property, DatatypeLiteral defaultValue) {
		Calendar date = resource.getCalendar(property);
		return date == null ? defaultValue : DateUtils.dateTimeAsLiteral(date);
	}

}
