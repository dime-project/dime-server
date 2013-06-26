package eu.dime.ps.datamining.service.api.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.datamining.service.ServiceCrawler;
import eu.dime.ps.datamining.service.api.exception.ExternalServiceException;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ServiceAdapter;

/**
 * Thread safety and concurrency considerations have been taken. No locking of
 * methods or objects is required by this implementation as it will not enter
 * an inconsistent state with any modifications. 
 * <br/>
 * I have however used thread safe collections (@see CopyOnWriteArraySet} to 
 * prevent any {@see ConcurrentModifcationException} exceptions being thrown 
 * due to modifying the registered handlers or pathDescriptors while they are being
 * iterated over - an alternative to this is to not provide external access to
 * the handler/path sets (i.e. disable #set/get Paths/Handlers } and copy the
 * sets when iterating over them.
 * 
 * @author Will Fleury
 */
public class ServiceCrawlerImpl implements ServiceCrawler {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceCrawlerImpl.class);
	
	/**
	 * The tenant owner of the service crawler
	 */
	protected final Long tenantId;

	/**
	 * The cron schedule that the crawler is running on
	 */
	protected String cronSchedule;
	
	/**
	 * The service adapter being used to access the data during crawling
	 */
	protected final ServiceAdapter adapter;
	
	/**
	 * The service path descriptor to crawl
	 */
	protected final PathDescriptor pathDescriptor;
	
	/**
	 * Thread safe set of handlers to call after crawling
	 */
	protected CopyOnWriteArraySet<CrawlerHandler> handlers;
	
	protected final HandlersChangedListener listener;
	
	/**
	 * 
	 * 
	 * @param adapter the service adapter used to access the service
	 * @param cronScheudle the cron schedule the crawling is being run on
	 * @param pathDescriptor the service path descriptors to use when crawling
	 * @param handlers a list of handlers to notify with crawl results
	 */
	public ServiceCrawlerImpl(Long tenantId, ServiceAdapter adapter, String cronScheudle, 
			PathDescriptor path, Set<CrawlerHandler> handlers) {
		this(tenantId, adapter, cronScheudle, path, handlers, null);
	}
	
	/**
	 * 
	 * 
	 * @param adapter the service adapter used to access the service
	 * @param cronScheudle the cron schedule the crawling is being run on
	 * @param handlers a list of handlers to notify with crawl results
	 * @param pathDescriptors the service pathDescriptors descriptors to use when crawling
	 * @param persistence the persistence manager to use
	 */
	public ServiceCrawlerImpl(Long tenantId, ServiceAdapter adapter, String cronScheudle, 
			PathDescriptor path, Set<CrawlerHandler> handlers, HandlersChangedListener listener) {
		this.tenantId = tenantId;
		this.adapter = adapter;
		this.cronSchedule = cronScheudle;
		this.pathDescriptor = path;
		this.listener = listener;
		
		// copy to thread safe collection to use.
		this.handlers = new CopyOnWriteArraySet<CrawlerHandler>(handlers);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#getTenant()
	 */
	@Override
	public Long getTenant() {
		return tenantId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#getCronSchedule()
	 */
	@Override
	public String getCronSchedule() {
		return cronSchedule;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#setCronSchedule(String)
	 */
	@Override
	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#getServiceAccount()
	 */
	@Override
	public String getAccountIdentifier() {
		return adapter.getIdentifier();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#getPath()
	 */
	@Override
	public PathDescriptor getPath() {
		return pathDescriptor;
	}
  
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#addHandler(CronHandler)
	 */
	@Override
	public void addHandler(CrawlerHandler handler) {
		handlers.add(handler);
		
		//persist change
		if (listener != null)
			listener.handlersChanged(getHandlers());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#removeHandler(CronHandler)
	 */
	@Override
	public void removeHandler(CrawlerHandler handler) {
		handlers.remove(handler);
		
		//persist change
		if (listener != null)
			listener.handlersChanged(getHandlers());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see ServiceCrawler#getHandlers()
	 */
	@Override
	public Set<CrawlerHandler> getHandlers() {
		return Collections.unmodifiableSet(handlers);
	}
	
	@Override
	public void run() {
		logger.debug("Service Crawler run for service {}", adapter.getAdapterName());
		
		//add check to make sure descriptor and handlers aren't empty
		if (pathDescriptor == null || handlers.isEmpty()) {
			logger.warn("ServiceCrawler for " + getAccountIdentifier() + " either had "
					+ "no PathDescriptor or CrawlerHandler(s). \n"
					+ "Crawl will be skipped...");
			return;
		}
		
		//Will hold results in a map 
		Map<PathDescriptor, Collection<? extends Resource>> results = 
				new HashMap<PathDescriptor, Collection<? extends Resource>>();
		
		try {
			Collection<? extends Resource> pathResults = 
					adapter.get(pathDescriptor.getPath(), pathDescriptor.getReturnType());  
			results.put(pathDescriptor, pathResults);
			
			// allow read only access to the results..
			fireResult(Collections.unmodifiableMap(results));
		} catch (ExternalServiceException e) {
			logger.debug("Problem crawling service {}", adapter.getAdapterName(), e);
			fireError(e);
		} catch (AttributeNotSupportedException e) {
			logger.debug("Problem crawling service {}", adapter.getAdapterName(), e);
			fireError(e);
		} catch (ServiceNotAvailableException e) {
			logger.debug("Problem crawling service {}", adapter.getAdapterName(), e);
			fireError(e);
		} catch (InvalidLoginException e) {
			logger.debug("Problem crawling service {}", adapter.getAdapterName(), e);
			fireError(e);
		}
	}
	
	protected void fireResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
		//using concurrent maps so all good here for thread safety
		for (CrawlerHandler handler : handlers) {
			handler.onResult(resources);
		}
	}
	
	protected void fireError(Throwable e) {
		//using concurrent maps so all good here for thread safety
		for (CrawlerHandler handler : handlers) {
			handler.onError(e);
		}
	}

	/**
	 * Instances are equal if the {@see ServiceCrawler#getServiceName() } and
	 * {@see ServiceCrawler#getPaths() } 
	 * matches.
	 * 
	 * @param obj instance to check is equal to 
	 * @return true if equal ,false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceCrawlerImpl other = (ServiceCrawlerImpl) obj;
		if ((this.adapter == null) ? (other.adapter != null) : !this.getAccountIdentifier().equals(other.getAccountIdentifier())) {
			return false;
		}
		if ((this.pathDescriptor == null) ? (other.pathDescriptor != null) :
				!this.pathDescriptor.equals(other.pathDescriptor)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {	
		int hash = 3;
		hash = 79 * hash + (adapter != null ? getAccountIdentifier().hashCode() : 0);
		hash = 79 * hash + (pathDescriptor != null ? pathDescriptor.hashCode() : 0);
		return hash;
	}
	
	@Override
	public String toString() {
		return "ServiceCrawlerImpl{" + "identifier=" + getAccountIdentifier() + ", "
				+ "path=" + pathDescriptor + '}';
	}

}
