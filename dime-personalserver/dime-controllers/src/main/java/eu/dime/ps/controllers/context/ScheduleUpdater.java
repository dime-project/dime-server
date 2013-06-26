package eu.dime.ps.controllers.context;

import ie.deri.smile.rdf.ResourceModel;
import ie.deri.smile.rdf.impl.ResourceModelImpl;
import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.runtime.converter.CalendarConverter;
import org.openrdf.repository.RepositoryException;
import org.semanticdesktop.aperture.vocabulary.NCAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.Schedule;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.util.DateUtils;
import eu.dime.ps.semantic.util.StringUtils;
import eu.dime.ps.storage.entities.Tenant;

/**
 * <p>Updates the Schedule aspect of the live context with current/upcoming events and tasks.</p>
 * <p>It queries the PIM for events and tasks and updates the Schedule aspect of the live
 * context with the current events and tasks, but also the upcoming events and tasks within
 * a 48h span.</p>
 * 
 * @author Ismael Rivera
 */
public class ScheduleUpdater implements LiveContextUpdater {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdater.class);

	private final Map<Long, List<ResourceModel>> eventsCache = new TreeMap<Long, List<ResourceModel>>(); 
	private final Map<Long, List<ResourceModel>> tasksCache = new TreeMap<Long, List<ResourceModel>>(); 

	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	@Override
	public void update() {
		Connection connection = null;
		PimoService pimoService = null;
		LiveContextService liveContextService = null;
		LiveContextSession liveContextSession = null;
		
		Calendar now = Calendar.getInstance();
		
		// finds current/upcoming events & tasks for each tenants
		for (Tenant tenant : tenantManager.getAll()) {
			try {
				connection = connectionProvider.getConnection(tenant.getId().toString());
				pimoService = connection.getPimoService();
				liveContextService = connection.getLiveContextService();

				URI[] currentEvents = findCurrent(now, eventsCache);
				URI[] upcomingEvents = findUpcoming(now, eventsCache);
				URI[] currentTasks = findCurrent(now, tasksCache);
				URI[] upcomingTasks = findUpcoming(now, tasksCache);
				logger.debug("Updating schedule aspect in live context: " +
						currentEvents.length + " current events; " +
						upcomingEvents.length + " upcoming events" +
						currentTasks.length + " current tasks; " +
						upcomingTasks.length + " current tasks; ");
				
				try {
					// the data source of the events/tasks is the PIM graph, the live context may contain
					// other schedule information coming from other sources, but here just the info coming
					// from the PIM is updated
					liveContextSession = liveContextService.getSession(pimoService.getPimoUri());
					liveContextSession.setAutoCommit(false);
					
					// remove all previous data
					liveContextSession.remove(Schedule.class, DCON.currentEvent);
					liveContextSession.remove(Schedule.class, DCON.upcomingEvent);
					liveContextSession.remove(Schedule.class, DCON.currentTask);
					liveContextSession.remove(Schedule.class, DCON.upcomingTask);
					
					// add the new events/tasks to the live context
					liveContextSession.add(Schedule.class, DCON.currentEvent, currentEvents);
					liveContextSession.add(Schedule.class, DCON.upcomingEvent, upcomingEvents);
					liveContextSession.add(Schedule.class, DCON.currentTask, currentTasks);
					liveContextSession.add(Schedule.class, DCON.upcomingTask, upcomingTasks);
					
					liveContextSession.commit();
				} catch (LiveContextException e) {
					logger.error("Live context couldn't be updated: "+e.getMessage(), e);
				}
			} catch (RepositoryException e) {
				logger.error("Cannot access RDF repository for tenant '"+tenant.getId()+"': "+e.getMessage(), e);
			}
		}
	}
	
	@Scheduled(fixedRate=1000*60*5) // every 5 min the cache is refreshed
	public void refresh() {
		Connection connection = null;
		PimoService pimoService = null;
		ClosableIterator<QueryRow> results = null;

		String query = StringUtils.strjoinNL(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>",
				"PREFIX ncal: "+NCAL.NS_NCAL.toSPARQL(),
				"SELECT DISTINCT ?resource ?dtstartValue ?dtendValue WHERE",
				"{",
				"  ?resource a %TYPE% ; ncal:dtstart ?dtstart; ncal:dtend ?dtend .",
				"  ?dtstart ncal:dateTime ?dtstartValue .",
				"  ?dtend ncal:dateTime ?dtendValue .",
				//"  FILTER(?dateTimeEnd > \""+DateUtils.currentDateTimeAsString()+"\"^^xsd:dateTime) .",
				"}");
		
		for (Tenant tenant : tenantManager.getAll()) {
			try {
				connection = connectionProvider.getConnection(tenant.getId().toString());
				pimoService = connection.getPimoService();

				// fetch and cache future ncal:Event
				try {
					results = pimoService.sparqlSelect(query.replace("%TYPE%", "ncal:Event")).iterator();
					QueryRow result = null;
					while (results.hasNext()) {
						result = results.next();
						Calendar dtstart = CalendarConverter.node2Calendar(result.getValue("dtstartValue"));
						Calendar dtend = CalendarConverter.node2Calendar(result.getValue("dtendValue"));
						addToCache(eventsCache, dtstart, dtend, result.getValue("resource").asURI());
					}
				} finally {
					if (results != null) {
						results.close();
					}
				}

				// fetch and cache future ncal:Todo
				try {
					results = pimoService.sparqlSelect(query.replace("%TYPE%", "ncal:Todo")).iterator();
					QueryRow result = null;
					while (results.hasNext()) {
						result = results.next();
						Calendar dtstart = CalendarConverter.node2Calendar(result.getValue("dtstartValue"));
						Calendar dtend = CalendarConverter.node2Calendar(result.getValue("dtendValue"));
						addToCache(tasksCache, dtstart, dtend, result.getValue("resource").asURI());
					}
				} finally {
					if (results != null) {
						results.close();
					}
				}
			} catch (RepositoryException e) {
				logger.error("Cannot access RDF repository for tenant '"+tenant.getId()+"': "+e.getMessage(), e);
			}
		}
	}
	
	private void addToCache(Map<Long, List<ResourceModel>> cache, Calendar dtstart, Calendar dtend, URI resource) {
		// create a resource with start and end dates
		ResourceModel rModel = new ResourceModelImpl(resource);
		rModel.set(NCAL.dtstart, dtstart);
		rModel.set(NCAL.dtend, dtend);

		// add resource to cache
		long millis = dtstart.getTimeInMillis();
		if (!cache.containsKey(millis)) {
			cache.put(millis, new ArrayList<ResourceModel>());
		}
		cache.get(millis).add(rModel);
	}
	
	protected URI[] findCurrent(Calendar when, Map<Long, List<ResourceModel>> choices) {
		List<URI> results = new ArrayList<URI>();
		long millis = when.getTimeInMillis();
		for (long start : choices.keySet()) {
			if (millis > start) {
				for (ResourceModel resource : choices.get(start)) {
					Calendar dtend = resource.getCalendar(NCAL.dtend);
					if (dtend != null && dtend.getTimeInMillis() > millis) {
						results.add(resource.getIdentifier().asURI());
					}
				}
			}
		}
		return results.toArray(new URI[results.size()]);
	}
	
	protected URI[] findUpcoming(Calendar when, Map<Long, List<ResourceModel>> choices) {
		List<URI> results = new ArrayList<URI>();
		long millis = when.getTimeInMillis();
		for (long start : choices.keySet()) {
			if (start > millis) {
				for (ResourceModel resource : choices.get(start)) {
					results.add(resource.getIdentifier().asURI());
				}
			}
		}
		return results.toArray(new URI[results.size()]);
	}
	
}
