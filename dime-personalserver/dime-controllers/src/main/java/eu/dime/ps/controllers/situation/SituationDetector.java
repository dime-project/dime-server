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

package eu.dime.ps.controllers.situation;

import ie.deri.smile.context.ContextMatcher;
import ie.deri.smile.context.MatchingException;
import ie.deri.smile.context.semmf.SemMFContextMatcher;
import ie.deri.smile.rdf.TripleStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.SituationManager;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Executes the situation matching process against the user's live context, and for every
 * situation detected to be a match an event (ACTION_SITUATION_MATCH) is sent.
 * 
 * The similarity score of the situation (with respect to the live context) is also updated.
 * 
 * @author Ismael Rivera
 */
public class SituationDetector {

	private static final Logger logger = LoggerFactory.getLogger(SituationDetector.class);
	
	public static final String ACTION_SITUATION_MATCH = "eu.dime.ps.controllers.situation.action.ACTION_SITUATION_MATCH";

	private BroadcastManager broadcastManager;
	private TenantManager tenantManager;
	private SituationManager situationManager;
	private ConnectionProvider connectionProvider;
	
	private final ConcurrentMap<Long, ContextMatcher> matchers;

	public SituationDetector() {
		this.matchers = new ConcurrentHashMap<Long, ContextMatcher>();
		this.broadcastManager = BroadcastManager.getInstance();
	}

	public void setBroadcastManager(BroadcastManager broadcastManager) {
		this.broadcastManager = broadcastManager;
	}
	
	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}
	
	public void setSituationManager(SituationManager situationManager) {
		this.situationManager = situationManager;
	}
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void detect() {
		logger.debug("detect()");
	
		if (connectionProvider == null) {
			logger.error("No connection provider was provided, cannot access RDF data to detect situations.");
			return;
		}
		
		if (tenantManager == null) {
			logger.error("No tenant manager was provided, cannot access information about tenants.");
			return;
		}
		
		// find situation matches for all tenants 
		for (Tenant tenant : tenantManager.getAll()) {
			String tenantId = tenant.getId().toString();
			
			Connection connection = null;
			TripleStore tripleStore = null;
			LiveContextService liveContextService = null;
			ContextMatcher contextMatcher = null;

			try {
				connection = connectionProvider.getConnection(tenantId);
				tripleStore = connection.getTripleStore();
				liveContextService = connection.getLiveContextService();
			} catch (RepositoryException e) {
				logger.error("Couldn't access RDF services of tenant with id "+tenantId);
				continue;
			}
			
			// setting tenant to be used by the infosphere managers, etc. during this iteration
			TenantContextHolder.setTenant(tenant.getId());
			
			// retrieve the context matcher from a cache of matchers, or
			// create a new context matcher for each tenant
			if (matchers.containsKey(tenant.getId())) {
				contextMatcher = matchers.get(tenant.getId());
			} else {
				URI queryContext = liveContextService.getLiveContext().getContextURI();
				contextMatcher = new SemMFContextMatcher(queryContext, tripleStore.getUnderlyingModelSet());
	
				try {
					// adding all situations as candidates
					for (Situation situation : situationManager.getAll()) {
						contextMatcher.addCandidate(situation.asURI());
					}
				} catch (InfosphereException e) {
					logger.error("Couldn't add situation candidates to context matcher: " + e.getMessage(), e);
				}
			}
			
			// perform the matching, and broadcasting the situation match events (if similarity > threshold)
			logger.debug("Running context matching [tenant="+tenantId+"]");
			try {
				Map<URI, Double> matchResults = contextMatcher.match();
				for (URI candidate : matchResults.keySet()) {
					Double score = matchResults.get(candidate);
					if (score == null || score.equals(Double.NaN)) {
						score = 0d;
					}
					try {
						Situation situation = situationManager.get(candidate.toString());
						if (!situation.hasScore()
								|| situation.getScore() != score.floatValue()) {
							situation.setScore(score.floatValue());
							situationManager.update(situation);

							logger.info("Situation match found [tenant="+tenantId+", situation="+candidate+", score="+score+"]");
							broadcastManager.sendBroadcast(new Event(tenantId, ACTION_SITUATION_MATCH, situation));
						}
					} catch (InfosphereException e) {
						logger.error("Situation score couldn't be updated: " + e.getMessage(), e);
					}
				}
			} catch (MatchingException e) {
				logger.error("Error while performing context matching for tenant "+tenantId, e);
			}
			
			// clear up tenant from the thread local
			TenantContextHolder.clear();
		}
	}
	
}
