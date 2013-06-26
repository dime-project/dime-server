package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;

/**
 * Manager in charge of the sharing functionality. It allows to
 * share databoxes, live posts, files, etc. with agents.
 * 
 * @author Ismael Rivera
 */
public interface SharingManager {

	public void shareDatabox(String databoxId, String sharedThrough, String[] agentIds)
			throws InfosphereException;
	
	public void unshareDatabox(String databoxId, String[] agentIds)
			throws InfosphereException;

	public boolean hasAccessToDatabox(String databoxId, String agentId)
			throws InfosphereException;
	
	public Collection<PrivacyPreference> getSharedDataboxes(String agentId)
			throws InfosphereException;
	
	public void shareProfileCard(String profileCardId, String sharedThrough, String[] agentIds)
			throws InfosphereException;
	
	public void unshareProfileCard(String profileCardId, String[] agentIds)
			throws InfosphereException;

	public boolean hasAccessToProfileCard(String profileCardId, String agentId)
			throws InfosphereException;
	
	public Collection<PrivacyPreference> getSharedProfileCards(String agentId)
			throws InfosphereException;
	
	public void shareLivePost(String livePostId, String sharedThrough, String[] agentIds)
			throws InfosphereException;
	
	public void unshareLivePost(String livePostId, String[] agentIds)
			throws InfosphereException;
	
	public boolean hasAccessToLivePost(String databoxId, String agentId)
			throws InfosphereException;

	public Collection<LivePost> getSharedLivePost(String agentId)
			throws InfosphereException;
	
	public void shareFile(String fileId, String sharedThrough, String[] agentIds)
			throws InfosphereException;
	
	public void unshareFile(String fileId, String[] agentIds)
			throws InfosphereException;
	
	public boolean hasAccessToFile(String fileId, String agentId)
			throws InfosphereException;

	public Collection<FileDataObject> getSharedFiles(String agentId)
			throws InfosphereException;
	
	public PrivacyPreference findPrivacyPreference(String resourceId, PrivacyPreferenceType type) throws InfosphereException;
	
	@Deprecated
	public void save(PrivacyPreference privacyPreference) throws InfosphereException;
	
	@Deprecated
	public void remove(String privacyPreferenceId) throws InfosphereException;

}