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

package eu.dime.ps.datamining.crawler.handler;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.datamining.account.StreamAccountUpdater;
import eu.dime.ps.datamining.service.CrawlerHandler;
import eu.dime.ps.datamining.service.PathDescriptor;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.semantic.model.dlpo.LivePost;

/**
 * 
 * @author Ismael Rivera
 */
public class StreamUpdaterHandler implements CrawlerHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(StreamUpdaterHandler.class);
	
	private URI accountIdentifier;
	private StreamAccountUpdater streamAccountUpdater;
	
	public void setAccountIdentifier(URI accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}
	
	public void setStreamAccountUpdater(StreamAccountUpdater streamAccountUpdater) {
		this.streamAccountUpdater = streamAccountUpdater;
	}

	public StreamUpdaterHandler() {}
	
	public StreamUpdaterHandler(URI accountIdentifier, StreamAccountUpdater streamAccountUpdater) {
		this.accountIdentifier = accountIdentifier;
		this.streamAccountUpdater = streamAccountUpdater;
	}
	
	@Override
	public void onResult(Map<PathDescriptor, Collection<? extends Resource>> resources) {
		logger.debug("received an StreamUpdaterHandler.onResult() call [account="+accountIdentifier+"]");
		Collection<LivePost> pathResources = null;
		for (PathDescriptor pathDescriptor : resources.keySet()) {
			String path = pathDescriptor.getPath();
			try {
				pathResources = (Collection<LivePost>) resources.get(pathDescriptor);
				if (pathResources.size() < 1) {
					logger.info("No resources to be updated at "+path+" for account "+accountIdentifier);
				} else {
					// FIXME restricting to livepost for now here, until we can do this in the crawler settings
					if (AttributeMap.LIVEPOST_ALL.equals(path)
							|| AttributeMap.LIVEPOST_ALLMINE.equals(path)
							|| AttributeMap.LIVEPOST_ALLUSER.equals(path)) {
						
						logger.info("Updating "+path+" for account "+accountIdentifier);
						streamAccountUpdater.update(accountIdentifier, path, pathResources);
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
