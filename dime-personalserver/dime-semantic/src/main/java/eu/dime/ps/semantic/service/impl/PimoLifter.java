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

package eu.dime.ps.semantic.service.impl;

import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;

/**
 * Creates PIMO instances for new/modified information elements, or other
 * resources (ie. it will create a pimo:Person instance if a new PersonContact
 * resource has been created).
 * 
 * @author Ismael Rivera
 */
public class PimoLifter implements BroadcastReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(PimoLifter.class);

	@Autowired
	ConnectionProvider connectionProvider;
	
	public PimoLifter() {
		BroadcastManager.getInstance().registerReceiver(this);
	}

	@Override
	public void onReceive(Event event) {
		Connection conn = null;
		PimoService pimoService = null;

		if (event.getTenant() == null) {
			logger.error("Cannot connect with the RDF repository: tenant not specified in event object.");
			return;
		} else {
			try {
				conn = connectionProvider.getConnection(event.getTenant());
				pimoService = conn.getPimoService();
			} catch (RepositoryException e) {
				logger.error("Cannot connect with the RDF repository '"+event.getTenant()+"': "+e, e);
				return;
			}
		}

		String action = event.getAction();
		if (Event.ACTION_RESOURCE_ADD.equals(action)
				|| Event.ACTION_RESOURCE_MODIFY.equals(action)) {
			pimoService.getOrCreateThingForOccurrence(event.getIdentifier());
		} else if (Event.ACTION_RESOURCE_DELETE.equals(action)) {
			// no-op
		}
	}

}
