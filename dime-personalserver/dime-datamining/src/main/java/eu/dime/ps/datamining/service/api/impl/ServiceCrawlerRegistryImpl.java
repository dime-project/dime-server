/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import eu.dime.ps.datamining.service.ConfigurationNotFoundException;
import eu.dime.ps.datamining.service.ConfigurationParsingException;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.JobConfiguration;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.PersistenceManager;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.ServiceCrawlerConfigurator;
import eu.dime.ps.datamining.service.ServiceCrawlerRegistry;
import eu.dime.ps.datamining.service.UniqueCrawlerConstraint;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.storage.entities.Tenant;

/**
 * This is the implementation of {@see ServiceCrawlerRegistry}. It communicates
 * with the {@see ServiceGateway} and uses the {@see ServiceAdapter} for each 
 * service to retrieve the data. Springs {@see TaskScheduler} is used as the
 * scheduling mechanism so as to keep the implementations open.. 
 * <br/>
 * Thread safety and concurrency considerations have been taken. All methods 
 * which can affect the consistency of the registry are synchronized. As all the
 * synchronized methods use the same class variables no gains can be achieved 
 * through using synchronized(object) other locking mechanisms and this is
 * the cleanest way. 
 * <br/>
 * Access to the list of services/crawlers is done using unmodifiable collections
 * and the Maps used are ConcurrentHashMap's so that any methods which do not
 * edit the maps but do iterate over them are safe and never throw a 
 * {@see ConcurrentModificationException}
 * 
 * @author Will Fleury
 * @author Ismael Rivera
 */
public class ServiceCrawlerRegistryImpl implements ServiceCrawlerRegistry {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceCrawlerRegistryImpl.class);
	
	/**
	 * Keeps a map of the active service crawlers by service identifier
	 */
	protected ConcurrentMap<UniqueCrawlerConstraint, ServiceCrawler> activeCrawlers;
	
	/**
	 * Keeps a map of the suspended service crawlers by service identifier
	 */
	protected ConcurrentMap<UniqueCrawlerConstraint, ServiceCrawler> suspendedCrawlers;
	
	/**
	 * Keeps a map of the {@see ScheduledFuture} for each active ServiceCrawler
	 * by service identifier.
	 */
	protected ConcurrentMap<UniqueCrawlerConstraint, ScheduledFuture> schedulerFutures;
	
	/**
	 * This is the scheduler instance which will be used to schedule the given
	 * crawling tasks. 
	 */
	protected TaskScheduler scheduler;
	
	/**
	 * This gives us access to the SerivceAdapters which are used to access the
	 * service data..
	 */
	protected ServiceGateway serviceGateway;
	
	/**
	 * If set, the registry will use the persistence manager to keep track of 
	 * the state of the registry so it can recover from crashes / restarts etc.
	 */
	protected volatile PersistenceManager persistence;
	
	/**
	 * This stores the default crawler handler to use if none is specified.
	 */
	protected CrawlerHandler defaultHandler;

	/**
	 * Default cron scheduled in case the service configuration doesn't specify it.
	 */
	public static final String DEFAULT_CRON = "0 0/30 * * * ?";

	/**
	 * Reads the default settings for each crawler/service 
	 */
	private final ServiceCrawlerConfigurator configurator = ServiceCrawlerConfigurator.getInstance();

	/**
	 * Creates an instance of a this class which will manage all service 
	 * crawling tasks. Note this sets the defaultHandler to null.
	 * 
	 * @param scheduler the spring task scheduler to use 
	 * {@see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
	 * @param gateway the {@see ServiceGateway} which provides access to the 
	 * service adapters which in turn provide access to the service data.
	 */
	public ServiceCrawlerRegistryImpl(TaskScheduler scheduler, ServiceGateway gateway) {
		this(scheduler, gateway, null, null);
	}
	
	/**
	 * Creates an instance of a this class which will manage all service 
	 * crawling tasks. Note this sets the defaultHandler to null.
	 * 
	 * @param scheduler the spring task scheduler to use 
	 * {@see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
	 * @param gateway the {@see ServiceGateway} which provides access to the 
	 * service adapters which in turn provide access to the service data.
	 * @param persistence  the persistence implementation to use for recording state
	 * 
	 */
	public ServiceCrawlerRegistryImpl(TaskScheduler scheduler, ServiceGateway gateway, 
			PersistenceManager persistence) {
		this(scheduler, gateway, null, persistence);
	}
	
	/**
	 * Creates an instance of a this class which will manage all service 
	 * crawling tasks. 
	 * 
	 * @param scheduler the spring task scheduler to use 
	 * {@see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
	 * @param gateway the {@see ServiceGateway} which provides access to the 
	 * service adapters which in turn provide access to the service data.
	 * @param defaultHandler the default handler to use
	 */
	public ServiceCrawlerRegistryImpl(TaskScheduler scheduler, ServiceGateway gateway,
			CrawlerHandler defaultHandler) {
		this(scheduler, gateway, defaultHandler, null);
	}
	
	/**
	 * Creates an instance of a this class which will manage all service 
	 * crawling tasks. 
	 * 
	 * @param scheduler the spring task scheduler to use 
	 * {@see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler}
	 * @param gateway the {@see ServiceGateway} which provides access to the 
	 * service adapters which in turn provide access to the service data.
	 * @param defaultHandler the default handler to use
	 * @param persistence  the persistence implementation to use for recording state
	 */
	public ServiceCrawlerRegistryImpl(TaskScheduler scheduler, ServiceGateway gateway,
			CrawlerHandler defaultHandler, PersistenceManager persistence) {
		this.scheduler = scheduler;
		this.serviceGateway = gateway;
		this.defaultHandler = defaultHandler;
		this.persistence = persistence;
		
		//initialize maps with thread safe hash maps.. 
		activeCrawlers = new ConcurrentHashMap<UniqueCrawlerConstraint, ServiceCrawler>();
		suspendedCrawlers = new ConcurrentHashMap<UniqueCrawlerConstraint, ServiceCrawler>();
		schedulerFutures = new ConcurrentHashMap<UniqueCrawlerConstraint, ScheduledFuture>();
		
		//now if we have a persistence provider, try restore
		if (isPersistenceEnabled())
			persistence.restoreRegistryState(this);//shouldnt allow this reference to escape..
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getAvailableServices()
	 */
	@Override
	public Collection<String> getAvailableServices() {
		return Collections.unmodifiableCollection(serviceGateway.listSupportedAdapters().keySet());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getActiveCrawlers()
	 */
	@Override
	public Collection<ServiceCrawler> getActiveCrawlers() {
		return Collections.unmodifiableCollection(activeCrawlers.values());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getActiveCrawlerIdentifiers()
	 */
	@Override
	public Collection<UniqueCrawlerConstraint> getActiveCrawlerKeys() {
		return Collections.unmodifiableSet(activeCrawlers.keySet());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getSuspendedCrawler(String)
	 */
	@Override
	public Collection<ServiceCrawler> getSuspendedCrawlers() {
		return Collections.unmodifiableCollection(suspendedCrawlers.values());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getSuspendedCrawlerIdentifiers()
	 */
	@Override
	public Collection<UniqueCrawlerConstraint> getSuspendedCrawlerKeys() {
		return Collections.unmodifiableSet(suspendedCrawlers.keySet());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getActiveCrawler(String)
	 */
	@Override
	public ServiceCrawler getActiveCralwer(UniqueCrawlerConstraint key) {
		return activeCrawlers.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#isServiceCrawling(String)
	 */
	@Override
	public ServiceCrawler getSuspendedCralwer(UniqueCrawlerConstraint key) {
		return suspendedCrawlers.get(key);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#isServiceCrawling(String)
	 */
	@Override
	public boolean isServiceCrawling(UniqueCrawlerConstraint key) {
		return activeCrawlers.containsKey(key);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#isServiceCrawling(String)
	 */
	@Override
	public boolean isServiceSuspended(UniqueCrawlerConstraint key) {
		return suspendedCrawlers.containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#add(Long, ServiceAdapter, CrawlerHandler[])
	 */
	@Override
	public synchronized void add(final Long tenantId, final ServiceAdapter adapter, final CrawlerHandler[] handlers) {
		// construct crawler jobs from settings
		try {
			for (JobConfiguration jobConfig : configurator.getJobConfigurations(adapter.getAdapterName())) {
				PathDescriptor path = new PathDescriptor(jobConfig.getPath(), jobConfig.getType());
				String cron = jobConfig.getCronSchedule() == null ? DEFAULT_CRON : jobConfig.getCronSchedule();

				// activate crawler
				UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(tenantId, adapter.getIdentifier(), path);
				if (isServiceCrawling(key) || isServiceSuspended(key)) {
					logger.warn("Crawler with Constraint exists. Removing and re-adding: "+key.toString());
					remove(key);
				}
				
				Tenant tenant = Tenant.find(tenantId);
				add(tenant, adapter.getIdentifier(), cron, path, handlers, true);
				logger.debug("Crawler for account "+adapter.getIdentifier()+" added to registry [cron="+cron+"]");
			}
		} catch (ConfigurationNotFoundException e) {
			logger.warn("No crawlers would be activated for adapter '"+adapter.getAdapterName()
					+"', some adapters do not require crawling; if your adapter does, please check create a config file for it.");
			return;
		} catch (ConfigurationParsingException e) {
			logger.error("No crawlers would be activated for adapter '"+adapter.getAdapterName()+"'", e);
			return;
		}
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized ServiceCrawler add(final Tenant tenant, final String accountIdentifier, final String cronSchedule, 
			final PathDescriptor path) {
		return add(tenant, accountIdentifier, cronSchedule, path, new CrawlerHandler[]{ defaultHandler });
	}

	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized ServiceCrawler add(final Tenant tenant, final String accountIdentifier, final String cronSchedule, 
			final PathDescriptor path, final CrawlerHandler[] handlers) {
		return add(tenant, accountIdentifier, cronSchedule, path, handlers, false);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized ServiceCrawler add(final Tenant tenant, final String accountIdentifier, final String cronSchedule, 
			final PathDescriptor path, final CrawlerHandler[] handlers, boolean fireImmediately) {
		
    	if (tenant == null)
    		throw new IllegalArgumentException("Crawler could not be added: tenant not specified.");

		// check to see if we're already crawling it or if its suspended
		if (activeCrawlers.containsKey(accountIdentifier))
			throw new IllegalArgumentException("Crawler is already running.. "
					+ "Use reschedule() if you want to change cron Schedule");
		
		if (suspendedCrawlers.containsKey(accountIdentifier))
			throw new IllegalArgumentException("Crawler is suspended.. "
					+ "Use resume() if you want to resume");
		
		Set<CrawlerHandler> crawlHandlers = new HashSet<CrawlerHandler>(Arrays.asList(handlers));
		
		// get the adapter from the gateway 
		ServiceAdapter adapter = null;
		try {
			adapter = serviceGateway.getServiceAdapter(accountIdentifier);
		} catch (ServiceNotAvailableException e) {
			throw new IllegalArgumentException("Adapter is not available for account "+accountIdentifier, e);
		} catch (ServiceAdapterNotSupportedException e) {
			throw new IllegalArgumentException("Adapter for account "+accountIdentifier+" is not supported.", e);
		}
		
		UniqueCrawlerConstraint key = new UniqueCrawlerConstraint(tenant.getId(), accountIdentifier, path);
		
		ServiceCrawler crawler = new ServiceCrawlerImpl(tenant.getId(), adapter, cronSchedule,
				path, crawlHandlers, new HandlersChangedListenerImpl(key));

		// now start the crawler.. 
		startCrawler(crawler, fireImmediately);
		
		// persist state
		if (isPersistenceEnabled())
			persistence.crawlerAdded(crawler);
		
		return crawler;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#remove(String)
	 */
	@Override
	public synchronized void remove(String accountIdentifier) {
		for (UniqueCrawlerConstraint key : getActiveCrawlerKeys()) {
			if (key.getAccountIdentifier().equals(accountIdentifier)) {
				remove(key);
			}
		}
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void remove(UniqueCrawlerConstraint key) {
		ServiceCrawler crawler = getServiceCrawler(key);
		
		//if its active stop it first..
		if (activeCrawlers.containsKey(key)) {
			stopCrawler(crawler);
		}
		//else just remove it from suspended crawlers.
		else {
			suspendedCrawlers.remove(key);
		}
		
		//persist state
		if (isPersistenceEnabled())
			persistence.crawlerRemoved(crawler);
	 }
	
	 @Override
	 public synchronized void removeAll() {
		 //need to stop all active crawlers (this also removes from active list)
		 for (UniqueCrawlerConstraint key : activeCrawlers.keySet())
			 stopCrawler(getServiceCrawler(key));
		 
		 //clear list of suspended 
		 suspendedCrawlers.clear();
		 schedulerFutures.clear();
		 
		 //persist state
		 if (isPersistenceEnabled())
			 persistence.crawlersCleared();
	 }
	 
	 /**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void reschedule(UniqueCrawlerConstraint key, String cronSchedule) {
		ServiceCrawler crawler = getServiceCrawler(key);
		
		//update cron schedule
		crawler.setCronSchedule(cronSchedule);
		
		//need to suspend first if its active..
		if (activeCrawlers.containsKey(key)) {
			suspend(key);
		}
		
		resume(key);
		
		//persist state
		if (isPersistenceEnabled())
			persistence.crawlerUpdated(crawler, false);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void suspend(UniqueCrawlerConstraint key) {
		ServiceCrawler crawler = activeCrawlers.get(key);
		
		if (crawler == null)
			throw new IllegalArgumentException("Service:" +key+ "was not active..");
		
		stopCrawler(crawler);
		
		suspendedCrawlers.put(key, crawler);
		
		//persist state
		if (isPersistenceEnabled())
			persistence.crawlerUpdated(crawler, true);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void resume(UniqueCrawlerConstraint key) {
		ServiceCrawler crawler = suspendedCrawlers.get(key);
		
		if (crawler == null)
			throw new IllegalArgumentException("Service: "+key+" was not suspended."
					+ "Cannot Restart..");
		
		startCrawler(crawler);
		
		suspendedCrawlers.remove(key);
		
		//persist state
		if (isPersistenceEnabled())
			persistence.crawlerUpdated(crawler, false);
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void suspendActive() {
		//stop all the active crawlers..
		for (UniqueCrawlerConstraint key : activeCrawlers.keySet()) {
			suspend(key);
		}
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException {@inheritDoc}
	 */
	@Override
	public synchronized void resumeSuspended() {
		//start all the suspsended crawlers
		for (UniqueCrawlerConstraint key : suspendedCrawlers.keySet()) {
			resume(key);
		}
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#setDefaultHandler(CrawlerHandler)
	 */
	@Override
	public void setDefaultHandler(CrawlerHandler handler) {
		defaultHandler = handler;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#getDefaultHandler()
	 */
	@Override
	public CrawlerHandler getDefaultHandler() {
		return defaultHandler;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#setPersitenceManager(PersistenceManager)
	 */
	@Override
	public void setPersitenceManager(PersistenceManager persistence) {
		this.persistence = persistence;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawlerRegistry#isPersistenceEnabled()
	 */
	@Override
	public final boolean isPersistenceEnabled() {
		return persistence != null;
	}
	
	/**
	 * @throws IllegalArgumentException if no service exists for the given serviceIdentifier
	 */
	@Override
	public void addServiceHandler(UniqueCrawlerConstraint key, CrawlerHandler handler) {
		ServiceCrawler crawler = getServiceCrawler(key);
		
		crawler.addHandler(handler);
	}
	
	/**
	 * @throws IllegalArgumentException if no service exists for the given serviceIdentifier
	 */
	@Override
	public void removeServiceHandler(UniqueCrawlerConstraint key, CrawlerHandler handler) {
		ServiceCrawler crawler = getServiceCrawler(key);

		crawler.removeHandler(handler);
	}
	
	protected synchronized void startCrawler(ServiceCrawler crawler) {
		startCrawler(crawler, false);
	}

	protected synchronized void startCrawler(ServiceCrawler crawler, boolean fireImmediately) {		
		//schedule task & set future reference
		ScheduledFuture future = scheduler.schedule(
				crawler, new CronTrigger(crawler.getCronSchedule()));
		
		// fires immediately
		if (fireImmediately)
			(new Thread(crawler)).start();
		
		schedulerFutures.put(new UniqueCrawlerConstraint(crawler), future);
		
		//update list of active crawlers.
		activeCrawlers.put(new UniqueCrawlerConstraint(crawler), crawler);				
	}
	
	@Override
	public synchronized void fireCrawler(String accountIdentifier) {
		for (UniqueCrawlerConstraint key : getActiveCrawlerKeys()) {
			if (key.getAccountIdentifier().equals(accountIdentifier)) {
				fireCrawler(key);
			}
		}
	}

	@Override
	public synchronized void fireCrawler(UniqueCrawlerConstraint key) {
		ServiceCrawler crawler = getServiceCrawler(key);
		(new Thread(crawler)).start();
	}

	/**
	 * 
	 * @throws IllegalArgumentException if the crawler object wasn't running
	 */
	protected synchronized void stopCrawler(ServiceCrawler crawler) {
		ScheduledFuture future = schedulerFutures.get(new UniqueCrawlerConstraint(crawler));
		
		if (future == null)
			throw new IllegalArgumentException("Crawler had no associated future (wasnt running)");
					
		if (!future.isCancelled())
			future.cancel(true);

		activeCrawlers.remove(new UniqueCrawlerConstraint(crawler));
	}
	
	/**
	 * 
	 * @throws IllegalArgumentException if no service exists for the given serviceIdentifier
	 */
	protected ServiceCrawler getServiceCrawler(UniqueCrawlerConstraint key) {
		ServiceCrawler crawler = null;
		
		//this will either return it from the active list if its there or search
		//the suspended list
		crawler = activeCrawlers.containsKey(key) ? 
				activeCrawlers.get(key) : suspendedCrawlers.get(key);
		
		if (crawler == null)
			throw new IllegalArgumentException("No service exists for "+key);
		
		return crawler;
	}

	/**
	 * This class is simply used to handle changes in the ServiceHandler list
	 * in the service crawler impl so that it can be persisted..
	 */
	class HandlersChangedListenerImpl implements ServiceCrawler.HandlersChangedListener {
		
		private UniqueCrawlerConstraint key;
		
		public HandlersChangedListenerImpl(UniqueCrawlerConstraint key) {
			this.key = key;
		}

		@Override
		public void handlersChanged(Set<CrawlerHandler> handlers) {
			if (isPersistenceEnabled()) {
				persistence.handlersUpdated(getServiceCrawler(key));
			}
		}
	}

}
