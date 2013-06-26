package eu.dime.ps.datamining.account;

import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.model.dpo.Activity;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;

public class ActivityUpdater implements AccountUpdater<Activity> {

	private static final Logger logger = LoggerFactory.getLogger(ActivityUpdater.class);

	private final LiveContextService liveContextService;
	
	public ActivityUpdater(LiveContextService liveContextService) {
		this.liveContextService = liveContextService;
	}
	
	@Override
	public void update(URI accountUri, String path, Activity activity)
			throws AccountIntegrationException {
		Collection<Activity> activities = new ArrayList<Activity>(1);
		activities.add(activity);
		update(accountUri, path, activities);
	}

	@Override
	public void update(URI accountUri, String path, Collection<Activity> activities)
			throws AccountIntegrationException {
		logger.info("Updating "+path+" for "+accountUri+" with "+activities.size()+" activities.");

		LiveContextSession session = liveContextService.getSession(accountUri);
		try {
			session.setAutoCommit(false);
			
			// remove all previous activities
			session.remove(State.class, DCON.currentActivity);
			
			// add new activities
			// TODO Fitbit provides several recent activities, for the review we only need the latest = current activity
//			for (Activity activity : activities)
			if (activities.size() > 0)
				session.add(State.class, DCON.currentActivity, activities.iterator().next());
					
			// committing changes
			session.commit();
				
		} catch (LiveContextException e) {
			throw new AccountIntegrationException("Error while updating activities in live context for "+
					"account "+accountUri+": "+e.getMessage(), e);
		}
	}

}
