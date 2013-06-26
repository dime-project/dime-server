package eu.dime.ps.controllers.context.browing;

import ie.deri.smile.vocabulary.DCON;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.context.exceptions.ContextException;
import eu.dime.ps.controllers.context.LiveContextManager;
import eu.dime.ps.semantic.model.dcon.Attention;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.LiveContextSession;
import eu.dime.ps.semantic.service.exception.LiveContextException;

public class BrowsingManager extends LiveContextManager {
	
	private static final Logger logger = LoggerFactory.getLogger(BrowsingManager.class);

	private LiveContextService liveContextService;
	
	public void setLiveContextService(LiveContextService liveContextService) {
		this.liveContextService = liveContextService;
	}
	
	public void register(BrowsingEvent event) throws ContextException {
		logger.debug("Received event: "+event);
		
		// TODO include data source (user's web browser) for this context data
		LiveContextSession session = liveContextService.getSession(null);
		
		URI webpage = new URIImpl(event.getFullUrl(), false);
		
		try {
			if (BrowsingEvent.ACTION_PAGE_LOADED.equals(event.getAction())) {
				session.add(Attention.class, DCON.activeFile, webpage);
			} else if (BrowsingEvent.ACTION_PAGE_UNLOADED.equals(event.getAction())) {
				session.remove(Attention.class, DCON.activeFile, webpage);
			} else if (BrowsingEvent.ACTION_PAGE_FOCUSED.equals(event.getAction())) {
				session.set(webpage, DCON.inForeground, true);
			} else if (BrowsingEvent.ACTION_PAGE_BLURRED.equals(event.getAction())) {
				session.set(webpage, DCON.inForeground, false);
			}
		} catch (LiveContextException e) {
			throw new ContextException("Couldn't register event "+event, e);
		}
	}
	
}
