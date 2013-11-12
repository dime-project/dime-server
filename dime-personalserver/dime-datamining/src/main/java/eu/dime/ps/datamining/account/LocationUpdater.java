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
import java.util.Collection;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.dpo.Place;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;

public class LocationUpdater implements AccountUpdater<LivePost> {

	private static final Logger logger = LoggerFactory.getLogger(LocationUpdater.class);

	private final LiveContextService liveContextService;
	
	public LocationUpdater(LiveContextService liveContextService) {
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
		
		LiveContextSession session = liveContextService.getSession(accountUri);
		try {
			session.setAutoCommit(false);
			
			// remove previous current place
			session.remove(SpaTem.class, DCON.currentPlace);
			
			// set current place to the location of the most recent checkin 
			for (LivePost livePost : livePosts) {
				if (livePost.isInstanceof(DLPO.Checkin)) {					
					Resource placeUri = ModelUtils.findObject(livePost.getModel(), livePost, DLPO.definingResource).asResource();
					if (placeUri != null) {
						Model placeModel = RDF2Go.getModelFactory().createModel().open();
						ModelUtils.fetch(livePost.getModel(), placeModel, placeUri);
						Place place = new Place(placeModel, placeUri, false);
						session.add(SpaTem.class, DCON.currentPlace, place);
						
						logger.info("Checkin @ '" + place.getPrefLabel() + "' found in livepost from account " + accountUri);

						// committing changes
						session.commit();
					}
				}
			}
		} catch (LiveContextException e) {
			throw new AccountIntegrationException("Error while updating location/places in live context for "+
					"account "+accountUri+": "+e.getMessage(), e);
		}
	}

}
