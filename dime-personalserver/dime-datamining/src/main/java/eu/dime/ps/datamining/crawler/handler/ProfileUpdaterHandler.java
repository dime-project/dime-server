package eu.dime.ps.datamining.crawler.handler;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.account.ProfileAccountUpdater;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.semantic.model.nco.PersonContact;

/**
 * Implementation of CrawlerHandler which specialises in updating profile information 
 * of the users/people.
 * 
 * @author Ismael Rivera
 */
public class ProfileUpdaterHandler implements CrawlerHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ProfileUpdaterHandler.class);
	
	private URI accountIdentifier;
	private ProfileAccountUpdater profileAccountUpdater;
	
	public void setAccountIdentifier(URI accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}
	
	public void setProfileAccountUpdater(ProfileAccountUpdater profileAccountUpdater) {
		this.profileAccountUpdater = profileAccountUpdater;
	}

	public ProfileUpdaterHandler() {}

	public ProfileUpdaterHandler(URI accountIdentifier, ProfileAccountUpdater profileUpdater) {
		this.accountIdentifier = accountIdentifier;
		this.profileAccountUpdater = profileUpdater;
	}
	
	@Override
	public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
		logger.debug("received an ProfileUpdaterHandler.onResult() call [account="+accountIdentifier+"]");
		Collection<? extends Resource> pathResources = null;
		for (PathDescriptor pathDescriptor : resources.keySet()) {
			String path = pathDescriptor.getPath();
			try {
				pathResources = resources.get(pathDescriptor);
				if (pathResources.size() < 1) {
					logger.info("No resources to be updated at "+path+" for account "+accountIdentifier);
				} else {
					if (AttributeMap.PROFILE_MYDETAILS.equals(path)) {
						if (pathResources.size() > 1) {
							logger.warn("Only one profile expected at "+path+" for account "+accountIdentifier+
									", but "+pathResources.size()+" received.");
						} else {
							logger.info("Updating "+path+" for account "+accountIdentifier);
							PersonContact profile = (PersonContact) pathResources.iterator().next();
							profileAccountUpdater.update(accountIdentifier, path, profile);
						}
					} else if (AttributeMap.FRIEND_ALL.equals(path)
							|| AttributeMap.FRIEND_DETAILS.equals(path)) {
						profileAccountUpdater.update(accountIdentifier, path, (Collection<PersonContact>) pathResources);
					} else {
						// no profile information, nothing to do...
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
