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

package eu.dime.ps.controllers.notification;

import ie.deri.smile.vocabulary.DCON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.user.UNSituationRecommendation;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.situation.SituationDetector;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.model.dcon.Situation;

/**
 * Sends user notifications recommending the activation of situations.
 * 
 * @author Ismael Rivera
 */
public class SituationNotifier implements BroadcastReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(SituationNotifier.class);

	public static final String DIME_ACCOUNT_TYPE = "di.me";
	
	private NotifierManager notifierManager = null;
	
	public SituationNotifier() {
		BroadcastManager.getInstance().registerReceiver(this);
	}

	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	@Override
	public void onReceive(Event event) {
		
		// do nothing if notification manager was not set
		if (notifierManager != null) {
			logger.warn("Notifier manager bean not set for SharingNotifier: " +
					"notifications for sharing actions won't be sent");
			return;
		}
		
		org.ontoware.rdfreactor.schema.rdfs.Resource resource = event.getData();
			
		if (resource != null && event.is(DCON.Situation)
				&& SituationDetector.ACTION_SITUATION_MATCH.equals(event.getAction())) {
			
			logger.debug("Sending notification: situation "+resource+" matches the live context.");
			
			Situation situation = (Situation) resource.castTo(Situation.class);
			if (!situation.hasScore()) {
				logger.error("Couldn't send notification (situation_recommendation) for " + situation + " because no score was provided.");
			}
			
			UNSituationRecommendation unSituationRecomendation = new UNSituationRecommendation(resource.toString(), situation.getScore());
			UserNotification notification = new UserNotification(Long.parseLong(event.getTenant()), unSituationRecomendation);
				
			try {
				notifierManager.pushInternalNotification(notification);
			} catch (NotifierException e) {
				logger.error("Cannot push notification: "+e.getMessage(), e);
			}
		}
	}
	
}
