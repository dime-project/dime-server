package eu.dime.ps.datamining.crawler.handler;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.account.AccountUpdaterService;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.gateway.service.AttributeMap;

/**
 * Implementation of CrawlerHandler which stores the results returned by
 * the crawl process in the RDF store.
 * 
 * @author Ismael Rivera
 */
public class AccountUpdaterHandler implements CrawlerHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountUpdaterHandler.class);
	
	private URI accountIdentifier;
	private AccountUpdaterService accountUpdater;
	
	public void setAccountIdentifier(URI accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}
	
	public void setAccountUpdaterService(AccountUpdaterService accountUpdater) {
		this.accountUpdater = accountUpdater;
	}
	
	public AccountUpdaterHandler() {}
	
	public AccountUpdaterHandler(URI accountIdentifier, AccountUpdaterService accountUpdater) {
		this.accountIdentifier = accountIdentifier;
		this.accountUpdater = accountUpdater;
	}
	
	@Override
	public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
		logger.debug("received an AccountUpdaterHandler.onResult() call [account="+accountIdentifier+"]");
		Collection<? extends Resource> pathResources = null;
		for (PathDescriptor pathDescriptor : resources.keySet()) {
			String path = pathDescriptor.getPath();
			try {
				pathResources = resources.get(pathDescriptor);
				if (pathResources.size() < 1) {
					logger.info("No resources to be updated at "+path+" for account "+accountIdentifier);
				} else {
					if (AttributeMap.PROFILE_MYDETAILS.equals(path)) {

						// TODO this check is needed until we can configure different handlers for each path
						// now this is set up manually to be updated by the ProfileUpdaterHandler

					} else if (AttributeMap.FRIEND_ALL.equals(path)
							|| AttributeMap.FRIEND_DETAILS.equals(path)) {
						
						// TODO this check is needed until we can configure different handlers for each path
						// now this is set up manually to be updated by the ProfileUpdaterHandler
						
					} else if (AttributeMap.LIVEPOST_ALL.equals(path)
							|| AttributeMap.LIVEPOST_ALLMINE.equals(path)
							|| AttributeMap.LIVEPOST_ALLUSER.equals(path)) {
						
						// TODO this check is needed until we can configure different handlers for each path
						// now this is set up manually to be updated by the StreamUpdaterHandler
					
					} else if (path.contains("activity")){

						// TODO this check is needed until we can configure different handlers for each path
						// now this is set up manually to be updated by the ContextUpdaterHandler
						
					} else {
						// fallback to generic resource updater
						accountUpdater.updateResources(accountIdentifier, path, pathResources);
					}
				}
			} catch (Exception e) {
				logger.error("Cannot update crawled resources from " + path + " at " + accountIdentifier + ": " + e, e);
			}
		}
	}

	@Override
	public void onError(Throwable error) {
		logger.error(error != null ? error.getMessage() : "Throwable was null", error);
	}

}
