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