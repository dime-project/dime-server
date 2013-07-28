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

package eu.dime.ps.datamining.account;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Updates an account with streams of data. The previous data received will not be deleted or
 * overridden, instead new data is continuously added for the account.
 * 
 * @author Ismael Rivera
 */
public class StreamAccountUpdater extends AccountUpdaterBase implements AccountUpdater<LivePost> {

	private static final Logger logger = LoggerFactory.getLogger(StreamAccountUpdater.class);

	public StreamAccountUpdater(ResourceStore resourceStore) {
		super(resourceStore);
	}
	
	@Override
	public void update(URI accountUri, String path, LivePost livepost)
			throws AccountIntegrationException {
		Collection<LivePost> liveposts = new ArrayList<LivePost>(1);
		liveposts.add(livepost);
		update(accountUri, path, liveposts);
	}

	@Override
	public void update(URI accountUri, String path, Collection<LivePost> liveposts)
			throws AccountIntegrationException {
		logger.info("Adding "+liveposts.size()+" items in "+path+" for account "+accountUri);

		URI accountPathGraph = getGraph(accountUri, path);

		Collection<? extends Resource> matchedResources = match(liveposts);
		for (Resource resource : matchedResources) {
			resourceStore.createOrUpdate(accountPathGraph, resource);
		}
		
		if (liveposts.size() > 0) {
			tripleStore.touchGraph(accountPathGraph);
		}
	}

}
