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

		if (Event.ACTION_RESOURCE_ADD.equals(event.getAction())) {
			pimoService.getOrCreateThingForOccurrence(event.getIdentifier());
		} else if (Event.ACTION_RESOURCE_MODIFY.equals(event.getAction())) {
			// TODO add modify action implementation
		} else if (Event.ACTION_RESOURCE_DELETE.equals(event.getAction())) {
			// TODO add the 'ON CASCADE' deletions logic
		}
	}

}
