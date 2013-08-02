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

package eu.dime.ps.datamining.crawler.handler;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.account.AccountIntegrationException;
import eu.dime.ps.datamining.account.ActivityUpdater;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.semantic.model.dpo.Activity;
import eu.dime.ps.semantic.service.LiveContextService;

/**
 * Crawler handler which updates the live context for those crawlers
 * retrieving context data from the services (e.g. current activities from Fitbit).
 * 
 * @author Ismael Rivera
 */
public class ContextUpdaterHandler implements CrawlerHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountUpdaterHandler.class);
	
	private URI accountIdentifier;
	private ActivityUpdater activityUpdater;
	
	public void setAccountIdentifier(URI accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}
	
	public void setLiveContextService(LiveContextService liveContextService) {
		this.activityUpdater = new ActivityUpdater(liveContextService);
	}

	public ContextUpdaterHandler() {}
	
	public ContextUpdaterHandler(URI accountIdentifier, LiveContextService liveContextService) {
		this.accountIdentifier = accountIdentifier;
		setLiveContextService(liveContextService);
	}

	@Override
	public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
		logger.debug("received an ContextUpdaterHandler.onResult() call [account="+accountIdentifier+"]");
		for (PathDescriptor path : resources.keySet()) {
			if (Activity.class.equals(path.getReturnType())) {
				try {
					activityUpdater.update(accountIdentifier, path.getPath(), (Collection<Activity>) resources.get(path));
				} catch (AccountIntegrationException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void onError(Throwable error) {
		logger.error(error != null ? error.getMessage() : "Throwable was null", error);
	}

}
