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

package eu.dime.ps.controllers.notification;

import ie.deri.smile.matching.MatchAttributes;
import ie.deri.smile.matching.MatchResult;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.notifications.user.UNMergeRecommendation;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.PersonMatch;
import eu.dime.ps.semantic.service.PersonMatchingService;
import eu.dime.ps.semantic.service.impl.PersonMatchingConfiguration;
import eu.dime.ps.storage.entities.AttributeMatch;
import eu.dime.ps.storage.entities.ProfileMatch;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Triggers the person matching process, and sends user notifications for all found matches.
 * The person matching process is triggered when a new person has been created in the user's
 * repository, independently of the origin of the person (API, crawlers, etc.).
 * 
 * @author Ismael Rivera
 */
public class PersonMatchingNotifier implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(PersonMatchingNotifier.class);

	private static final boolean PERSIST_ATTRIBUTE_MATCH = false;
	
	// every person matching job is inserted in the queue, and executed by the thread pool
	private ThreadPoolExecutor threadPool = null;
	private final int poolSize = 1;
	private final int maxPoolSize = 2;
	private final long keepAliveTime = 60;
	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	private NotifierManager notifierManager;
	private ConnectionProvider connectionProvider;
	private EntityFactory entityFactory;
	
	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public PersonMatchingNotifier() {
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, keepAliveTime,
				TimeUnit.SECONDS, queue);
		BroadcastManager.getInstance().registerReceiver(this);
	}
	
	@Override
	public void onReceive(Event event) {
		
		if (!PersonMatchingConfiguration.ENABLED) {
			return; // no-op if not enabled
		}
		
		String tenant = event.getTenant();
		String action = event.getAction();
		
		if (tenant == null) {
			logger.warn("Tenant not specified in event: "+event.toString());
		} else {
			if (Event.ACTION_RESOURCE_ADD.equals(action) && event.is(PIMO.Person)) {
				logger.debug("Starting person matching task for new person: " + event.getIdentifier());
				Runnable task = new PersonMatchingTask(tenant, event);
				threadPool.execute(task);
			}
		}
	}

	private void storeInDb(String tenantId, List<PersonMatch> personMatchList, Integer technique, boolean semanticExtension) {
		
		Tenant tenant = Tenant.find(Long.valueOf(tenantId));
		
		for (PersonMatch personMatch : personMatchList) {
			
			eu.dime.ps.storage.entities.PersonMatch dbPersonMatch = null;
			
			if (personMatch.getSource() == null || personMatch.getTarget() == null) {
				logger.warn("PersonMatch object content is incorrect or incomplete: source = "+personMatch.getSource()+", target = "+personMatch.getTarget()+", score = "+personMatch.getSimilarityScore()+" [tenant = "+tenantId+"]");
				continue;
			}
			
			// source and target are interchangeable, the match is a symmetric operation
			dbPersonMatch = eu.dime.ps.storage.entities.PersonMatch.findByTenantAndBySourceAndByTargetAndByTechnique(
					tenant, personMatch.getSource().toString(), personMatch.getTarget().toString(), technique);
			if (dbPersonMatch == null) {
				dbPersonMatch = eu.dime.ps.storage.entities.PersonMatch.findByTenantAndBySourceAndByTargetAndByTechnique(
						tenant, personMatch.getTarget().toString(), personMatch.getSource().toString(), technique);
			}
			
			if (dbPersonMatch == null) {
				dbPersonMatch = entityFactory.buildPersonMatch();
				dbPersonMatch.setTenant(tenant);
				dbPersonMatch.setSource(personMatch.getSource().toString());
				dbPersonMatch.setTarget(personMatch.getTarget().toString());
				dbPersonMatch.setTechnique(technique);
				dbPersonMatch.setStatus(eu.dime.ps.storage.entities.PersonMatch.PENDING);
				dbPersonMatch.setSemanticExtension(semanticExtension);
				dbPersonMatch.setLastPerformed(new Date());
				dbPersonMatch.setSimilarityScore(personMatch.getSimilarityScore());
				
				// persist the record
				dbPersonMatch.persist();
			} else {
				// only update the similarity score + profile/attribute values for 'pending' matches
				// 'accepted' should never happen (when merging, one of the person URIs disappear)
				// FIXME 'dismissed' is a special case, because maybe more data of the person is known
				// and the similarity score has changed
				if (!eu.dime.ps.storage.entities.PersonMatch.PENDING.equals(dbPersonMatch.getStatus())) {
					continue;
				}
				
				// updating record
				dbPersonMatch.setSource(personMatch.getSource().toString());
				dbPersonMatch.setTarget(personMatch.getTarget().toString());
				dbPersonMatch.setTechnique(technique);
				dbPersonMatch.setSemanticExtension(semanticExtension);
				dbPersonMatch.setLastPerformed(new Date());
				dbPersonMatch.setSimilarityScore(personMatch.getSimilarityScore());
				
				dbPersonMatch.merge();

				// remove all previous data, and new data will be stored (profile/attribute matching results)
				for (ProfileMatch dbProfileMatch : dbPersonMatch.getProfileMatches()) {
					dbProfileMatch.remove();
				}
				dbPersonMatch.getProfileMatches().clear();
			}

			for (MatchResult profileMatch : personMatch.getProfileMatches()) {
				ProfileMatch dbProfileMatch = entityFactory.buildProfileMatch();
				dbProfileMatch.setTenant(tenant);
				dbProfileMatch.setPersonMatch(dbPersonMatch);
				dbProfileMatch.setSource(profileMatch.getProfileSource().toString());
				dbProfileMatch.setTarget(profileMatch.getProfileTarget().toString());
				dbProfileMatch.setAttNoSource(profileMatch.getAttributesNoSource());
				dbProfileMatch.setAttNoTarget(profileMatch.getAttributesNoTarget());
				dbProfileMatch.setSimilarityScore(profileMatch.getSimilarityScore());
				
				dbProfileMatch.persist();
				
				if (PERSIST_ATTRIBUTE_MATCH) {
					for (MatchAttributes attributeMatch : profileMatch.getAttributesList()) {
						AttributeMatch dbAttributeMatch = entityFactory.buildAttributeMatch();
						dbAttributeMatch.setTenant(tenant);
						dbAttributeMatch.setProfileMatch(dbProfileMatch);
						dbAttributeMatch.setAttribute(attributeMatch.getAttributeName());
						dbAttributeMatch.setSourceType(attributeMatch.getAttributeSourceType());
						dbAttributeMatch.setTargetType(attributeMatch.getAttributeTargetType());
						dbAttributeMatch.setTechnique(attributeMatch.getTechnique());
						dbAttributeMatch.setSimilarityScore(attributeMatch.getAttributeScore());
						dbAttributeMatch.persist();
					}
				}
			}
		}
	}
	
	private class PersonMatchingTask implements Runnable {
		private String tenant;
		private Person person;
		
		public PersonMatchingTask(String tenant, Event event) {
			this.tenant = tenant;
			if (event.getData() != null) {
				this.person = (Person) event.getData().castTo(Person.class);
			}
		}
		
		@Override
		public void run() {
			Connection connection = null;
			ResourceStore resourceStore = null;
			PersonMatchingService matcher = null;
			try {
				connection = connectionProvider.getConnection(tenant);
				resourceStore = connection.getResourceStore();
				matcher = connection.getPersonMatchingService();
			} catch (RepositoryException e) {
				logger.error("Couldn't obtain PersonMatching service for tenant '"+tenant+"': "+e.getMessage(), e);
				return;
			}
		
			if (person != null) {
				// performs person matching, stores all results in the DB
				List<PersonMatch> matchList = matcher.match(person, 0);

				// TODO set attributes in PersonMatch for the technique, approach, semanticExtension, etc.
				storeInDb(tenant, matchList, PersonMatchingConfiguration.MATCHING_TECHNIQUE, PersonMatchingConfiguration.SEMANTIC_EXTENSION);
				
				// to notify the user/UI only sends notifications for all found matches over a certain threshold
				// also, no notifications will be sent if the source or target prefLabel (names) are missing

				String sourceName = null;
				String targetName = null;
				
				// read prefLabel of person source
				sourceName = person.getPrefLabel();
				if (sourceName == null) {
					logger.error("User notifications (merge_recommendation) can't be send for any found match: source name is " +
							"missing for " + person);
				} else {
					for (PersonMatch match : matchList) {
						if (match.getSimilarityScore() > PersonMatchingConfiguration.THRESHOLD) {
							
							// read prefLabel of person target
							try {
								Person target = resourceStore.get(match.getTarget(), Person.class, new URI[]{ NAO.prefLabel });
								targetName = target.getPrefLabel();
								if (targetName == null) {
									logger.error("User notification (merge_recommendation) can't be send: target name is missing " +
											"for " + match.getTarget());
									continue;
								} else if (sourceName.contains("Test User") || targetName.contains("Test User")) {
									// discarding notifications for test users
									logger.info("User notification (merge_recommendation) omitted for test user " +
											"[sourceName=" + sourceName + ", targetName=" + targetName + "]");
									continue;	
								}
							} catch (NotFoundException e) {
								logger.error("User notification (merge_recommendation) can't be send: target id " + match.getTarget() +
										" does not belong to any known person: " + e.getMessage(), e);
								continue;
							}
							
							// create a merge_recommendation notification
							UNMergeRecommendation unMergeRecommendation = new UNMergeRecommendation();
							unMergeRecommendation.setSourceId(match.getSource().toString());
							unMergeRecommendation.setSourceName(person.getPrefLabel());
							unMergeRecommendation.setTargetId(match.getTarget().toString());
							unMergeRecommendation.setTargetName(targetName);
							unMergeRecommendation.setStatus(UNMergeRecommendation.STATUS_PENDING);

							// push user notification
							try {
								notifierManager.pushInternalNotification(new UserNotification(Long.parseLong(tenant), unMergeRecommendation));
							} catch (NotifierException e) {
								logger.error("Error while sending notification for person matching results for '"+person+"': "+e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}

}
