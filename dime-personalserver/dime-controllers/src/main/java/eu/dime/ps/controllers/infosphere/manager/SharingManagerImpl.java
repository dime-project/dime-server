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

import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;
import java.util.HashSet;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.jfix.util.Arrays;

/**
 * 
 * @author Ismael Rivera
 */
public class SharingManagerImpl extends ConnectionBase implements SharingManager {

	private static final Logger logger = LoggerFactory.getLogger(SharingManagerImpl.class);

	private static final PrivacyPreferenceType[] CARDINALITY_ONE = new PrivacyPreferenceType[] {
		PrivacyPreferenceType.FILE,
		PrivacyPreferenceType.LIVEPOST
	}; 

	private void onShare(PrivacyPreference item, Agent... receivers) {
	}
	
	private void onUnshare(PrivacyPreference item, Agent... receivers) {
	}

	private Agent[] fetchAgents(String[] agentIds) throws NotFoundException, InfosphereException {
		Agent[] agents = new Agent[agentIds.length];
		for (int idx = 0; idx < agentIds.length; idx++) {
			agents[idx] = getResourceStore().get(new URIImpl(agentIds[idx]), Agent.class, new URI[0]);
		}
		return agents;
	}
	
	@Override
	public void shareDatabox(String databoxId, String sharedThrough, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		PrivacyPreference databox = null;
		Agent[] agents = null;
		try {
			agents = fetchAgents(agentIds);
			databox = getResourceStore().get(new URIImpl(databoxId), PrivacyPreference.class);
			privacyPreferenceService.grantAccess(databox, new URIImpl(sharedThrough), agents);
			
			onShare(databox, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot share databox "+databoxId+": "+e, e);
		}
	}

	@Override
	public void unshareDatabox(String databoxId, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		PrivacyPreference databox = null;
		Agent[] agents = null;
		try {
			agents = fetchAgents(agentIds);
			databox = getResourceStore().get(new URIImpl(databoxId), PrivacyPreference.class);
			privacyPreferenceService.revokeAccess(databox, agents);

			onUnshare(databox, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot unshare databox "+databoxId+": "+e, e);
		}
	}

	@Override
	public boolean hasAccessToDatabox(String databoxId, String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		Agent agent = null;
		PrivacyPreference databox = null;
		try {
			agent = resourceStore.get(new URIImpl(agentId), Agent.class, new URI[0]);
			databox = resourceStore.get(new URIImpl(databoxId), PrivacyPreference.class);
			return privacyPreferenceService.hasAccessTo(databox, agent);
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check access to databox "+databoxId+": "+e, e);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot check access to databox "+databoxId+": "+e, e);
		}
	}

	@Override
	public Collection<PrivacyPreference> getSharedDataboxes(String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		Agent agent = null;
		try {
			agent = getResourceStore().get(new URIImpl(agentId), Agent.class);
			return privacyPreferenceService.getByAgentAndType(agent, PrivacyPreferenceType.DATABOX);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot retrieve databoxes, agent "+agentId+" not found", e);
		}
	}

	@Override
	public void shareProfileCard(String profileCardId, String sharedThrough, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		PrivacyPreference profileCard = null;
		Agent[] agents = null;
		try {
			agents = fetchAgents(agentIds);
			profileCard = getResourceStore().get(new URIImpl(profileCardId), PrivacyPreference.class);
			privacyPreferenceService.grantAccess(profileCard, new URIImpl(sharedThrough), agents);
			
			onShare(profileCard, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot share profile card "+profileCard+": "+e, e);
		}
	}
	
	@Override
	public void unshareProfileCard(String profileCardId, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		PrivacyPreference profileCard = null;
		Agent[] agents = null;
		try {
			agents = fetchAgents(agentIds);
			profileCard = getResourceStore().get(new URIImpl(profileCardId), PrivacyPreference.class);
			privacyPreferenceService.revokeAccess(profileCard, agents);

			onUnshare(profileCard, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot unshare profile card "+profileCardId+": "+e, e);
		}
	}

	@Override
	public boolean hasAccessToProfileCard(String profileCardId, String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		Agent agent = null;
		PrivacyPreference profileCard = null;
		try {
			agent = resourceStore.get(new URIImpl(agentId), Agent.class, new URI[0]);
			profileCard = resourceStore.get(new URIImpl(profileCardId), PrivacyPreference.class);
			return privacyPreferenceService.hasAccessTo(profileCard, agent);
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check access to profile card "+profileCardId+": "+e, e);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot check access to profile card "+profileCardId+": "+e, e);
		}
	}
	
	@Override
	public Collection<PrivacyPreference> getSharedProfileCards(String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		Agent agent = null;
		try {
			agent = getResourceStore().get(new URIImpl(agentId), Agent.class);
			return privacyPreferenceService.getByAgentAndType(agent, PrivacyPreferenceType.PROFILECARD);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot retrieve profile cards, agent "+agentId+" not found", e);
		}
	}

	@Override
	public void shareLivePost(String livePostId, String sharedThrough, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		LivePost livePost = null;
		Agent[] agents = null;
		PrivacyPreference pp = null;
		try {
			agents = fetchAgents(agentIds);
			livePost = getResourceStore().get(new URIImpl(livePostId), LivePost.class);
			pp = privacyPreferenceService.grantAccess(livePost, new URIImpl(sharedThrough), agents);

			onShare(pp, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot share live post "+livePostId+": "+e, e);
		}
	}

	@Override
	public void unshareLivePost(String livePostId, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		LivePost livePost = null;
		Agent[] agents = null;
		PrivacyPreference pp = null;
		try {
			agents = fetchAgents(agentIds);
			livePost = getResourceStore().get(new URIImpl(livePostId), LivePost.class);
			pp = privacyPreferenceService.revokeAccess(livePost, agents);

			onUnshare(pp, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot unshare live post "+livePostId+": "+e, e);
		}
	}

	@Override
	public boolean hasAccessToLivePost(String livePostId, String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		LivePost livePost = null;
		Agent agent = null;
		try {
			livePost = resourceStore.get(new URIImpl(livePostId), LivePost.class, new URI[0]);
			agent = resourceStore.get(new URIImpl(agentId), Agent.class, new URI[0]);
			return privacyPreferenceService.hasAccessTo(livePost, agent);
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check access to live post "+livePostId+": "+e, e);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot check access to live post "+livePostId+": "+e, e);
		}
	}
	
	public Collection<LivePost> getSharedLivePost(String agentId)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		Agent agent = null;
		try {
			agent = resourceStore.get(new URIImpl(agentId), Agent.class);
			// TODO needs to check the exclusion list as well
			// TODO need to check if agent is in a person group included in a PP
			return resourceStore.find(LivePost.class)
					.distinct()
					.where(BasicQuery.X, RDF.type, PPO.PrivacyPreference)
					.where(BasicQuery.X, RDFS.label, PrivacyPreferenceType.LIVEPOST)
					.where(BasicQuery.X, PPO.hasAccessSpace, BasicQuery.Y)
					.where(BasicQuery.Y, NSO.includes).is(agent)
					.where(BasicQuery.X, PPO.appliesToResource, BasicQuery.THIS)

					// checks for members in person groups
					.orWhere(BasicQuery.X, RDF.type, PPO.PrivacyPreference)
					.where(BasicQuery.X, RDFS.label, PrivacyPreferenceType.LIVEPOST)
					.where(BasicQuery.X, PPO.hasAccessSpace, BasicQuery.Y)
					.where(BasicQuery.Y, NSO.includes).is(BasicQuery.Z)
					.where(BasicQuery.Z, RDF.type, PIMO.PersonGroup)
					.where(BasicQuery.Z, PIMO.hasMember).is(agent)

					.results();
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot retrieve shared live posts, agent "+agentId+" not found", e);
		}
	}
	
	public void shareFile(String fileId, String sharedThrough, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		FileDataObject file = null;
		Agent[] agents = null;
		PrivacyPreference pp = null;
		try {
			agents = fetchAgents(agentIds);
			file = getResourceStore().get(new URIImpl(fileId), FileDataObject.class, new URI[0]);
			pp = privacyPreferenceService.grantAccess(file, new URIImpl(sharedThrough), agents);

			onShare(pp, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot share file "+fileId+": "+e, e);
		}
	}
	
	public void unshareFile(String fileId, String[] agentIds)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		FileDataObject file = null;
		Agent[] agents = null;
		PrivacyPreference pp = null;
		try {
			agents = fetchAgents(agentIds);
			file = getResourceStore().get(new URIImpl(fileId), FileDataObject.class, new URI[0]);
			pp = privacyPreferenceService.revokeAccess(file, agents);

			onUnshare(pp, agents);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot unshare file "+fileId+": "+e, e);
		}
	}
	
	public boolean hasAccessToFile(String fileId, String agentId)
			throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		ResourceStore resourceStore = getResourceStore();
		FileDataObject file = null;
		Agent agent = null;
		try {
			file = resourceStore.get(new URIImpl(fileId), FileDataObject.class, new URI[0]);
			agent = resourceStore.get(new URIImpl(agentId), Agent.class, new URI[0]);
			return privacyPreferenceService.hasAccessTo(file, agent);
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check access to file "+fileId+": "+e, e);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot check access to file "+fileId+": "+e, e);
		}
	}
	
	@Override
	public Collection<FileDataObject> getSharedFiles(String agentId)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		Collection<FileDataObject> sharedFiles = new HashSet<FileDataObject>();
		Agent agent = null;
		try {
			agent = resourceStore.get(new URIImpl(agentId), Agent.class);
			
			// adds all files shared through a databox
			for (PrivacyPreference databox : getSharedDataboxes(agentId)) {
				for (Resource resource : databox.getAllAppliesToResource_as().asList()) {
					sharedFiles.add(resourceStore.get(resource.asResource(), FileDataObject.class));
				}
			}
			
			// adds all files shared directly
			sharedFiles.addAll(resourceStore.find(FileDataObject.class)
					.distinct()
					.where(BasicQuery.X, RDF.type, PPO.PrivacyPreference)
					.where(BasicQuery.X, RDFS.label, PrivacyPreferenceType.FILE)
					.where(BasicQuery.X, PPO.hasAccessSpace, BasicQuery.Y)
					.where(BasicQuery.Y, NSO.includes).is(agent)
			// TODO needs to check the exclusion list as well
			// don't remove comment until then...
//					.where(BasicQuery.Y, NSO.excludes).isNot(agent)
					.where(BasicQuery.X, PPO.appliesToResource, BasicQuery.THIS)
					
					// checks for members in person groups
					.orWhere(BasicQuery.X, RDF.type, PPO.PrivacyPreference)
					.where(BasicQuery.X, RDFS.label, PrivacyPreferenceType.FILE)
					.where(BasicQuery.X, PPO.hasAccessSpace, BasicQuery.Y)
					.where(BasicQuery.Y, NSO.includes).is(BasicQuery.Z)
					.where(BasicQuery.Z, RDF.type, PIMO.PersonGroup)
					.where(BasicQuery.Z, PIMO.hasMember).is(agent)

					.results());
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot retrieve shared files to agent "+agentId+": "+e.getMessage(), e);
		}
		return sharedFiles;
	}
	
	@Override
	public PrivacyPreference findPrivacyPreference(String resourceId, PrivacyPreferenceType type) throws InfosphereException {
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();
		
		Collection<org.ontoware.rdf2go.model.node.Resource> ids = null;
		ids = getResourceStore().find(PrivacyPreference.class)
				.distinct()
				.where(RDFS.label).is(type.toString())
				.where(PPO.appliesToResource).is(new URIImpl(resourceId))
				.ids();
		
		if (ids.size() > 1 && Arrays.contains(CARDINALITY_ONE, type)) {
			throw new InfosphereException("Only one privacy preference is allowed for the type "+type+
					". but "+ids.size()+" were found for the resource "+resourceId);
		} else if (ids.size() == 1) {
			return privacyPreferenceService.get(ids.iterator().next().asURI());
		}

		return null;
	}
	
	@Override
	public void save(PrivacyPreference privacyPreference) throws InfosphereException {
		PimoService pimoService = getPimoService();
		pimoService.createOrUpdate(privacyPreference, false);
	}
	
	@Override
	public void remove(String privacyPreferenceId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			pimoService.remove(new URIImpl(privacyPreferenceId));
		} catch (NotFoundException e) {
			throw new InfosphereException("Privacy Preference "+privacyPreferenceId+" couldn't be removed.", e);
		}
	}

}
