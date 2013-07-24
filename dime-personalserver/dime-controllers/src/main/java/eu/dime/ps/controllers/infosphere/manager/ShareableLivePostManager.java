package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.ForbiddenException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.dlpo.LivePost;

/**
 * Implementation of ShareableManager for live posts, compliant with the
 * privacy preferences of the user.
 * 
 * @author Ismael Rivera
 */
public class ShareableLivePostManager extends ShareableManagerBase<LivePost> implements ShareableManager<LivePost> {

	public static final List<URI> SHAREABLE_LIVEPOST_PROPERTIES;
	static {
		SHAREABLE_LIVEPOST_PROPERTIES = new ArrayList<URI>(6);
		SHAREABLE_LIVEPOST_PROPERTIES.add(NAO.created);
		SHAREABLE_LIVEPOST_PROPERTIES.add(NAO.lastModified);
		SHAREABLE_LIVEPOST_PROPERTIES.add(NAO.prefLabel);		
		SHAREABLE_LIVEPOST_PROPERTIES.add(DLPO.textualContent);
		SHAREABLE_LIVEPOST_PROPERTIES.add(DLPO.timestamp);
	};

	private LivePostManager livepostManager;

	public void setLivePostManager(LivePostManager livepostManager) {
		this.livepostManager = livepostManager;
	}
	
	@Override
	public boolean exist(String resourceId) throws InfosphereException {
		return livepostManager.exist(resourceId);
	}

	@Override
	public LivePost get(String livePostId, String requesterId) throws NotFoundException, ForbiddenException, InfosphereException {
		LivePost livepost = livepostManager.get(livePostId, SHAREABLE_LIVEPOST_PROPERTIES);
		checkAuthorized(livepost, requesterId);

		// sets the live post as sharedWith the requester user
		setSharedWith(livepost, requesterId);
		
		return livepost;
	}

	@Override
	public Collection<LivePost> getAll(String accountId, String requesterId) throws InfosphereException {
		Collection<LivePost> all = livepostManager.getAll(SHAREABLE_LIVEPOST_PROPERTIES);
		Collection<LivePost> authorized = filterAuthorized(all, requesterId);
		
		// sets the live posts as sharedWith the requester user
		setSharedWith(authorized, requesterId);
		
		return authorized;
	}

}