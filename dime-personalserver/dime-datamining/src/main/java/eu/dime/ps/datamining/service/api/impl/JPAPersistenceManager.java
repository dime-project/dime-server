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

package eu.dime.ps.datamining.service.api.impl;

import ie.deri.smile.rdf.TripleStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import eu.dime.ps.datamining.account.AccountUpdaterService;
import eu.dime.ps.datamining.account.AccountUpdaterServiceImpl;
import eu.dime.ps.datamining.account.ProfileAccountUpdater;
import eu.dime.ps.datamining.crawler.handler.AccountUpdaterHandler;
import eu.dime.ps.datamining.crawler.handler.ContextUpdaterHandler;
import eu.dime.ps.datamining.crawler.handler.ProfileUpdaterHandler;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.PersistenceManager;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.datamining.service.UniqueCrawlerConstraint;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.service.impl.ResourceMatchingServiceImpl;
import eu.dime.ps.storage.entities.CrawlerJob;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;
import eu.dime.ps.storage.util.QueryUtil;

/**
 * Note that this class logs and swallows all exceptions as they are not 
 * essential to the proper operation of the registry, only its ability to recover
 * from a crash.. This leaves the registry implementation cleaner instead of 
 * swallowing them there. 
 * 
 * NOTE: An instance of this class must ONLY be used by a SINGLE 
 * ServiceCrawlerRegistry implementation (in the current design there would never
 * be more than one anyway). If used by more than one registry then inconsistencies
 * can arise.
 * 
 * @author Will Fleury
 */
public class JPAPersistenceManager implements PersistenceManager {
	
	private static Logger logger = LoggerFactory.getLogger(JPAPersistenceManager.class);
	
	private EntityFactory entityFactory;
	private ConnectionProvider connectionProvider;
	
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	} 
	
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	} 
	
	/**
	 * This variable indicates if a restore is in progress. If it is, then no
	 * other methods will affect the state of the data. Will come up with a nicer
	 * pattern for doing this soon. 
	 */
	protected volatile boolean isRestoreInProgress = false;
	
	public JPAPersistenceManager() {  }
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#restoreRegistryState()
	 * 
	 * Note: This method is synchronized (locked) on the class object.
	 */
	@Override
	public synchronized void restoreRegistryState(ServiceCrawlerRegistry registry) {
		isRestoreInProgress = true;
		logger.info("Attempting to restore Registry State..");
		
		try {
			List<CrawlerJob> jobs = CrawlerJob.findAll();
			
			for (CrawlerJob job : jobs) {
				
				//lets get the path descriptor
				PathDescriptor pathDescriptor = constructPathDescriptor(job);
				
				//now lets see if there was any path reloaded. If not then 
				//try add default or abort if no default.
				if (pathDescriptor == null) {
					logger.warn("No path descriptor reloaded... "+"Aborting restart of: "+job.getAccountIdentifier());
						
					//remove this one to keep registry consistent...
					job.remove();
					
					//try next one
					continue;
				}
				
				//now look after handlers
				Set<eu.dime.ps.storage.entities.CrawlerHandler> handlerDAOs = job.getCrawlerHandlers();
				Set<CrawlerHandler> handlers = constructHandlers(job, handlerDAOs);
				
				//now lets
				if (handlers.isEmpty()) {
					if (registry.getDefaultHandler() == null) {
						logger.warn("No CrawlerHandler reloaded and no defaultHandler value.. "
								+ "Aborting restart of: "+job.getAccountIdentifier());
						
						//remove this one to keep registry consistent..
						job.remove();
						
						//try next one
						continue;
					}
					
					handlers.add(registry.getDefaultHandler());
				}
				
				//ok.. now lets try restart this service
				try {
					registry.add(job.getTenant(), job.getAccountIdentifier(), job.getCron(),
							pathDescriptor, handlers.toArray(new CrawlerHandler[0]));
				} catch (IllegalArgumentException e) {
					logger.warn("Unable to restart crawler for service : "+job.getAccountIdentifier());
				}
				
			}
			
			logger.info("Registry successfully restored!");
		} catch (Exception e) {
			logger.error("Error restoring registry state.", e);
		} finally {
			isRestoreInProgress = false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#crawlerAdded(ServiceCrawler)
	 */
	@Override
	public void crawlerAdded(ServiceCrawler crawler) {
		if (isRestoreInProgress)
			return;
		
		try {
			Tenant tenant = Tenant.find(crawler.getTenant());
			
			CrawlerJob job = getCrawlerJob(tenant, new UniqueCrawlerConstraint(crawler));
			if (job != null) {
				logger.warn("Registry / Persistence were not matching.. Possible inconsitency");
				job.remove();
			}

			//now put all together
			job = entityFactory.buildCrawlerJob();
			job.setTenant(tenant);
			job.setAccountIdentifier(crawler.getAccountIdentifier());
			job.setPath(crawler.getPath().getPath());
			job.setReturnType(crawler.getPath().getReturnType().getName());
			job.setCron(crawler.getCronSchedule());
			job.setSuspended(false);
			job.persist();
			
			//persist handlers for the crawler
			persistCrawlerHandlers(tenant, crawler, job);

		} catch (Exception e) {
			logger.error("Error persisting handler addition for service: "+crawler.getAccountIdentifier(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#crawlerUpdated(ServiceCrawler, boolean)
	 */
	@Override
	public void crawlerUpdated(ServiceCrawler crawler, boolean suspended) {
		if (isRestoreInProgress)
			return;
		
		try {
			Tenant tenant = Tenant.find(crawler.getTenant());
			CrawlerJob job = getCrawlerJob(tenant, new UniqueCrawlerConstraint(crawler));
			
			if (job == null) {
				logger.warn("Registry in inconsistent state.. fix..!!");
				
				//add it since its not there.. 
				crawlerAdded(crawler);
				return;
			}
			
			job.setCron(crawler.getCronSchedule());
			job.setSuspended(suspended);
			
			//now update it..
			job.merge();
			
		} catch (Exception e) {
			logger.error("Error updating state for service: "+crawler.getAccountIdentifier(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#crawlersCleared(ServiceCrawlerRegistry)
	 */
	@Override
	public void crawlersCleared() {
		if (isRestoreInProgress)
			return;
		
		//unfortunately need to loop like this for JPA with relations to ensure
		//no problems.
		try {
			List<CrawlerJob> jobs = CrawlerJob.findAll();
			
			//remove its kids first..
			for (CrawlerJob job : jobs) {
				job.remove();
			}
		} catch (Exception e) {
			logger.error("Error clearing crawlers",e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#crawlerRemoved(ServiceCrawler)
	 */
	@Override
	public void crawlerRemoved(ServiceCrawler crawler) {
		if (isRestoreInProgress)
			return;
		
		try {
			Tenant tenant = Tenant.find(crawler.getTenant());
			CrawlerJob job = QueryUtil.getSingleResultOrNull(CrawlerJob.findByAccountIdentifierEquals(crawler.getAccountIdentifier()));
//			CrawlerJob job = getCrawlerJob(tenant, new UniqueCrawlerConstraint(crawler));
			if (job == null) {
				logger.warn("Registry / Persistence were not matching.. Possible inconsitency");
			}
			
			else {
				job.remove();
			}
		} catch (Exception e) {
			logger.error("Error persisting handler removal for service: "+crawler.getAccountIdentifier(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see PersistenceManager#handlersUpdated(ServiceCrawler)
	 */
	@Override
	public void handlersUpdated(ServiceCrawler crawler) {
		if (isRestoreInProgress)
			return;

		try {
			Tenant tenant = Tenant.find(crawler.getTenant());
			persistCrawlerHandlers(tenant, crawler);
		} catch (Exception e) {
			logger.error("Error persisting handlers update for account: "+crawler.getAccountIdentifier(), e);
		}
	}
	
	/**
	 * This method takes a list of CrawlerPath entities and reconstructs them into
	 * PathDescriptors for use in the ServiceCrawlerRegistry.
	 * 
	 * @param crawlerJob
	 * @return 
	 */
	public static PathDescriptor constructPathDescriptor(CrawlerJob crawlerJob) {
		try {
			PathDescriptor path = new PathDescriptor();
			path.setPath(crawlerJob.getPath());

			//lets try find the class --						
			Class<?> returnType = ClassUtils.getDefaultClassLoader()
					.loadClass(crawlerJob.getReturnType());

			//set it by casting to appropriate sub type
			path.setReturnType(returnType.asSubclass(Resource.class));
			
			return path;
		} catch (Exception e) {
			logger.warn("Could not reload path descriptor return type "
					+ crawlerJob.getReturnType()
					+ " for account with id " + crawlerJob.getAccountIdentifier(), e);
			return null;
		}
	}
	
	/**
	 * This is used to reconstruct crawler handlers from the eu.dime.ps.storage.entities.CrawlerHandler entities
	 * loaded from the datastore. 
	 * <br>
	 * 
	 * Note: We may need to do some extra stuff here such as having spring inject
	 * dependencies etc, or manually inject a few constructors once a standard
	 * for the CrawlerHandler constructors is agreed upon..
	 * 
	 * @param crawlerJob
	 * @param handlerDAOs
	 * @return 
	 */
	protected Set<CrawlerHandler> constructHandlers(CrawlerJob job, Set<eu.dime.ps.storage.entities.CrawlerHandler> handlerDAOs) {
		Set<CrawlerHandler> handlers = new HashSet<CrawlerHandler>();

		for (eu.dime.ps.storage.entities.CrawlerHandler handlerDAO : handlerDAOs) {
			try {

				// lets try load the class and then call its default constructor
				Class tmp = ClassUtils.getDefaultClassLoader().loadClass(handlerDAO.getClassName());
				
				CrawlerHandler handler = CrawlerHandler.class.cast(tmp.newInstance());

				// TODO this is not flexible enough, every time we create a new type of handler we have to put it
				// here, and if the handler needs any spring beans, we have to pass them through JPAPersistantManager...
				
				URI accountIdentifier = new URIImpl(job.getAccountIdentifier());
				
				Connection connection = connectionProvider.getConnection(job.getTenant().getId().toString());
				TripleStore tripleStore = connection.getTripleStore();
				ResourceStore resourceStore = connection.getResourceStore();
				PimoService pimoService = connection.getPimoService();
				LiveContextService liveContextService = connection.getLiveContextService();

				if (handler instanceof AccountUpdaterHandler) {
					AccountUpdaterService accountUpdater = new AccountUpdaterServiceImpl(pimoService, new ResourceMatchingServiceImpl(tripleStore));
					AccountUpdaterHandler accountHandler = (AccountUpdaterHandler) handler;
					accountHandler.setAccountIdentifier(accountIdentifier);
					accountHandler.setAccountUpdaterService(accountUpdater);
				} else if (handler instanceof ProfileUpdaterHandler) {
					ProfileAccountUpdater profileUpdater = new ProfileAccountUpdater(resourceStore, pimoService);
					ProfileUpdaterHandler profileHandler = (ProfileUpdaterHandler) handler;
					profileHandler.setAccountIdentifier(accountIdentifier);
					profileHandler.setProfileAccountUpdater(profileUpdater);
				} else if (handler instanceof ContextUpdaterHandler) {
					ContextUpdaterHandler contextHandler = (ContextUpdaterHandler) handler;
					contextHandler.setResourceStore(resourceStore);
					contextHandler.setAccountIdentifier(accountIdentifier);
					contextHandler.setLiveContextService(liveContextService);
				}
				// Pertaining to decision in daily call on 17.07.2013: Disable livepost crawling functionality
//				else if (handler instanceof StreamUpdaterHandler) {
//					StreamAccountUpdater streamUpdater = new StreamAccountUpdater(resourceStore);
//					StreamUpdaterHandler streamHandler = (StreamUpdaterHandler) handler;
//					streamHandler.setAccountIdentifier(accountIdentifier);
//					streamHandler.setStreamAccountUpdater(streamUpdater);
//				}

				handlers.add(handler);
			} catch (Exception e) {
				logger.warn("Could not restart handler " + handlerDAO.getClassName()
						+ " for account with id " + job.getAccountIdentifier(), e);
			}
		}

		return handlers;
	}
	
	public static CrawlerJob getCrawlerJob(Tenant tenant, UniqueCrawlerConstraint key) {
		List<CrawlerJob> jobs = CrawlerJob.findByAccountIdentifierEquals(key.getAccountIdentifier()).getResultList();
		for (CrawlerJob job : jobs) {
			if (key.getPath().equals(constructPathDescriptor(job))) {
				return job;
			}
		}
		
		return null;
	}

	
	/*
	 * Private Methods..
	 */
	
	private Set<eu.dime.ps.storage.entities.CrawlerHandler> persistCrawlerHandlers(Tenant tenant, ServiceCrawler crawler) {
		return persistCrawlerHandlers(tenant, crawler, null);
	}
	
	/**
	 * This is used to persist crawler handlers to the datastore.. 
	 * 
	 * @param crawler
	 * @param job
	 * @return 
	 */
	private Set<eu.dime.ps.storage.entities.CrawlerHandler> persistCrawlerHandlers(Tenant tenant, ServiceCrawler crawler, CrawlerJob job) {
		//TODO: would be nicer to only change the modified ones..   
		
		//remove all old ones
		List<eu.dime.ps.storage.entities.CrawlerHandler> toRemove =
				eu.dime.ps.storage.entities.CrawlerHandler.findCrawlerHandlersByJobId(job).getResultList();
		for (eu.dime.ps.storage.entities.CrawlerHandler handler : toRemove) 
			handler.remove();
		
		//get handlers for the crawler
		Set<eu.dime.ps.storage.entities.CrawlerHandler> handlerDAOs = new HashSet<eu.dime.ps.storage.entities.CrawlerHandler>();
		for (CrawlerHandler handler : crawler.getHandlers()) {
			eu.dime.ps.storage.entities.CrawlerHandler handlerDAO = entityFactory.buildCrawlerHandler();
			handlerDAO.setTenant(tenant);
			handlerDAO.setClassName(handler.getClass().getName());

			if (job != null) {
				handlerDAO.setJobId(job);
			}
			
			//persist...
			handlerDAO.persist();

			handlerDAOs.add(handlerDAO);
		}

		return handlerDAOs;
	}   
	
}
