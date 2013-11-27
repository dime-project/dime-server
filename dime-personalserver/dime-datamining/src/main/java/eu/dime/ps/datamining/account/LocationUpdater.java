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

package eu.dime.ps.datamining.account;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DLPO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.pimo.Location;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;

public class LocationUpdater implements AccountUpdater<LivePost> {

	private static final Logger logger = LoggerFactory.getLogger(LocationUpdater.class);

	public static final String ACTION_CHECKIN = "eu.dime.ps.datamining.action.CHECKIN";
	
	private String tenant;
	private LiveContextService liveContextService;
	
	public LocationUpdater(String tenant) {
		this.tenant = tenant;
	}

	public LocationUpdater(String tenant, LiveContextService liveContextService) {
		this(tenant);
		this.liveContextService = liveContextService;
	}
	
	public void setLiveContextService(LiveContextService liveContextService) {
		this.liveContextService = liveContextService;
	}

	@Override
	public void update(URI accountUri, String path, LivePost livePost)
			throws AccountIntegrationException {
		Collection<LivePost> livePosts = new ArrayList<LivePost>(1);
		livePosts.add(livePost);
		update(accountUri, path, livePosts);
	}

	@Override
	public void update(URI accountUri, String path, Collection<LivePost> livePosts)
			throws AccountIntegrationException {
		
		// caching current places, to avoid sending user notifications repeatedly
		SpaTem spatem = liveContextService.get(SpaTem.class);
		Collection<Node> locations = ModelUtils.findObjects(spatem.getModel(), spatem, DCON.currentPlace);
		List<String> locationNames = new ArrayList<String>(locations.size());
		for (Node entry : locations) {
			try {
				Location location = liveContextService.get(entry.asURI(), Location.class);
				locationNames.add(location.getPrefLabel());
			} catch (NotFoundException e) {}
		}
		
		LiveContextSession session = liveContextService.getSession(accountUri);
		try {
			session.setAutoCommit(false);
			
			// remove previous current place
			session.remove(SpaTem.class, DCON.currentPlace);
			
			// set current place to the location of the most recent checkin 
			for (LivePost livePost : livePosts) {
				if (livePost.isInstanceof(DLPO.Checkin)) {					
					Resource locationId = ModelUtils.findObject(livePost.getModel(), livePost, DLPO.definingResource).asResource();
					if (locationId != null) {
						// load data related to the location
						Model sinkModel = RDF2Go.getModelFactory().createModel().open();
						ModelUtils.fetch(livePost.getModel(), sinkModel, locationId);
						
						// transform locationId into a URI
						URI locationUri = new URIImpl("urn:uuid:" + UUID.randomUUID());
						ModelUtils.replaceIdentifier(sinkModel, locationId, locationUri);
						
						// replace all blank nodes with URIs and create Location object
						Model locationModel = RDF2Go.getModelFactory().createModel().open();
						ModelUtils.skolemize(sinkModel, locationModel);
						Location location = new Location(locationModel, locationUri, false);
						
						logger.info("Checkin @ '" + location.getPrefLabel() + "' found in livepost from account " + accountUri);
						
						// send user notification if new place detected
						logger.info("Current locations are: " + Arrays.toString(locationNames.toArray(new String[0])));
						if (!locationNames.contains(location.getPrefLabel())) {
							logger.info("Broadcasting 'Checkin' event at location " + location.getPrefLabel());
							BroadcastManager.getInstance().sendBroadcast(new Event(tenant, ACTION_CHECKIN, location));
						}
						
						// adding location as current place in live context
						session.add(SpaTem.class, DCON.currentPlace, location);
					}
				}
			}

			// commit changes
			session.commit();
			
		} catch (LiveContextException e) {
			throw new AccountIntegrationException("Error while updating location/places in live context for "+
					"account "+accountUri+": "+e.getMessage(), e);
		}
	}

}
