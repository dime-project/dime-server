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
import java.util.Calendar;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.semantic.util.StringUtils;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Updates the SpaTem aspect of the live context with the
 * matching TimePeriod instances for the current datetime.
 * 
 * @author Ismael Rivera
 */
public class TimePeriodUpdater implements LiveContextUpdater {

	private static final Logger logger = LoggerFactory.getLogger(TimePeriodUpdater.class);

	private static final URI DATASOURCE = new URIImpl("urn:dime:time-period-updater");
	
	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	/**
	 * Queries for matching TimePeriod instances for the current datetime,
	 * and updates the SpaTem aspect of the live context, adding them to
	 * the SpaTem instance using the property dcon:currentTime.
	 */
	@Override
	public void update() {
		Connection connection = null;
		ResourceStore resourceStore = null;
		LiveContextService liveContextService = null;
		LiveContextSession liveContextSession = null;
		
		// for each tenant, it finds the time periods defined by the user, and
		// updates its live context
		for (Tenant tenant : tenantManager.getAll()) {
			try {
				connection = connectionProvider.getConnection(tenant.getId().toString());
				resourceStore = connection.getResourceStore();
				liveContextService = connection.getLiveContextService();
				liveContextSession = liveContextService.getSession(DATASOURCE);
				
				List<URI> timePeriods = findTimePeriods(resourceStore, Calendar.getInstance());
				logger.info("Updating live context (SpaTem) with current time periods: "+timePeriods);
				try {
					liveContextSession.setAutoCommit(false);
					liveContextSession.remove(SpaTem.class, DCON.currentTime);
					for (URI timePeriod : timePeriods) {
						liveContextSession.add(SpaTem.class, DCON.currentTime, timePeriod);
					}
					liveContextSession.commit();
				} catch (LiveContextException e) {
					logger.error("Live context couldn't be updated: "+e.getMessage(), e);
				}
			} catch (RepositoryException e) {
				logger.error("Cannot access RDF repository for tenant '"+tenant.getId()+"': "+e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Returns the TimePeriod instances in which a given date is in
	 * their range.
	 * @param resourceStore the data repository where to find the time periods
	 * @param when the datime used to query for time periods
	 * @return the list of the TimePeriod identifiers
	 */
	protected List<URI> findTimePeriods(ResourceStore resourceStore, Calendar when) {
		int hour = when.get(Calendar.HOUR_OF_DAY);
		int dayOfWeek = getDayOfWeekFromMonday(when);
		int dayOfMonth = when.get(Calendar.DAY_OF_MONTH);
		int weekOfYear = when.get(Calendar.WEEK_OF_YEAR);
		int month = when.get(Calendar.MONTH) + 1; // 1=jan ... 12=dec
		int year = when.get(Calendar.YEAR);
		
		String query = StringUtils.strjoinNL(
				"PREFIX dpo: <http://www.semanticdesktop.org/ontologies/2011/10/05/dpo#>",
				"SELECT DISTINCT ?instance WHERE {",
				"  {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minHour ?minHour ;",
				"      dpo:maxHour ?maxHour .",
				"    FILTER(?minHour <= "+hour+") .",
				"    FILTER(?maxHour >= "+hour+") .",
				"  } UNION {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minDayOfWeek ?minDayOfWeek ;",
				"      dpo:maxDayOfWeek ?maxDayOfWeek .",
				"    FILTER(?minDayOfWeek <= "+dayOfWeek+") .",
				"    FILTER(?maxDayOfWeek >= "+dayOfWeek+") .",
				"  } UNION {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minDayOfMonth ?minDayOfMonth ;",
				"      dpo:maxDayOfMonth ?maxDayOfMonth .",
				"    FILTER(?minDayOfMonth <= "+dayOfMonth+") .",
				"    FILTER(?maxDayOfMonth >= "+dayOfMonth+") .",
				"  } UNION {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minWeek ?minWeek ;",
				"      dpo:maxWeek ?maxWeek .",
				"    FILTER(?minWeek <= "+weekOfYear+") .",
				"    FILTER(?maxWeek >= "+weekOfYear+") .",
				"  } UNION {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minMonth ?minMonth ;",
				"      dpo:maxMonth ?maxMonth .",
				"    FILTER(?minMonth <= "+month+") .",
				"    FILTER(?maxMonth >= "+month+") .",
				"  } UNION {",
				"    ?instance a dpo:TimePeriod ;",
				"      dpo:minYear ?minYear ;",
				"      dpo:maxYear ?maxYear .",
				"    FILTER(?minYear <= "+year+") .",
				"    FILTER(?maxYear >= "+year+") .",
				"  }",
				"}");
		
		ClosableIterator<QueryRow> rows = resourceStore.sparqlSelect(query).iterator();
		List<URI> results = new ArrayList<URI>();
		while (rows.hasNext()) {
			results.add(rows.next().getValue("instance").asURI());
		}
		
		return results;
	}
	
	// monday = 1 .. sunday = 7
	private int getDayOfWeekFromMonday(Calendar when) {
		int dayOfWeek = when.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek == 0 ? 7 : dayOfWeek;
	}
	
}
