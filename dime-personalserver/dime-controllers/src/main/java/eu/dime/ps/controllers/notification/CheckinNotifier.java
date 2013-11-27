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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.user.UNMessage;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.datamining.account.LocationUpdater;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;

/**
 * Notifies the UI/clients of checkins.
 * 
 * @author Ismael Rivera
 */
public class CheckinNotifier implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(CheckinNotifier.class);

	private NotifierManager notifierManager;	

	public CheckinNotifier() {
		BroadcastManager.getInstance().registerReceiver(this);
	}

	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	@Override
	public void onReceive(Event event) {
		final String action = event.getAction();

		if (!LocationUpdater.ACTION_CHECKIN.equals(action)
				|| !event.is(PIMO.Location)) {
			return;
		}

		final Resource location = event.getData();
		final Node prefLabel = ModelUtils.findObject(location.getModel(), location, NAO.prefLabel);
		
		if (prefLabel != null) {
			final UNMessage message = new UNMessage();
			// TODO make this generic for any service account, instead of hardcoding `Twitter`
			String text = "You've checked in @ '" + prefLabel + "' on Twitter";
			message.setMessage(text);
			
			final UserNotification notification = new UserNotification(event.getTenantId(), message);
			try {
				logger.info("CheckinNotifier sends user notification `" + text + "`");
				notifierManager.pushInternalNotification(notification);
			} catch (NotifierException e) {
				logger.error("Error while pushing notification ["+notification+"].", e);
			}
		}
	}

}
