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

package eu.dime.ps.controllers.trustengine.impl;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.FileManager;
import eu.dime.ps.controllers.infosphere.manager.SharingManager;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.RDFReactorThing;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Listens for share events and updates resources accordingly (trust, privacy, relationships etc..)
 * @author marcel
 *
 */
public class TrustEngineUpdateService extends AdvisoryBase implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(TrustEngineUpdateService.class);
	
	@Autowired
	private SharingManager sharingManager;
	
	@Autowired
	FileManager filemanager;
	
	public TrustEngineUpdateService(){
		BroadcastManager.getInstance().registerReceiver(this);
	}

	@Override
	public void onReceive(Event event) {
		PimoService pimoService = null;
		if (event.getAction().equals(Event.ACTION_RESOURCE_READ)){
			return;
		}
		PrivacyPreferenceService ppoService = null;
		if (event.getData() == null){
			return;
		}
		String tenant = event.getTenant();
		if(tenant != null && (!tenant.equals(""))){
			TenantContextHolder.setTenant(Long.valueOf(tenant));
		} else{
			return;
		}
		
		if (Event.ACTION_RESOURCE_MODIFY.equals(event.getAction())){
			try {
				ppoService = connectionProvider.getConnection(event.getTenant()).getPrivacyPreferenceService();
				pimoService  = connectionProvider.getConnection(event.getTenant()).getPimoService();
			} catch (RepositoryException e) {
				logger.error("Could not load PrivacyPreferenceService.",e);
				return;
			}		
			if (event.is(DLPO.LivePost)){
				//livepost share
				Resource resource = event.getData();
				processLivePost(resource, ppoService);
			} else if (event.is(NFO.FileDataObject) || event.is(NIE.DataObject)){
				// data share
				Resource resource = event.getData();			
				processDataObject(resource, ppoService, pimoService);
				
			} else if (event.is(PIMO.PersonGroup)){
				//TODO: update things for group modifications
			}
			else if (event.is(PPO.PrivacyPreference)){
				//databox share -... under construction
				Resource resource = event.getData();
				processDatabox(resource, ppoService);
			}
		} else if (Event.ACTION_RESOURCE_ADD.equals(event.getAction()) && event.is(PPO.PrivacyPreference)){
			// add new pp -> (happens when file shared for the first time)
			Resource resource = event.getData();			
			if (resource == null){
				return;
			}
			try {
				ppoService = getPrivPrefService();
			} catch (RepositoryException e) {
				logger.error("could not load rdf services. not TenantContext set?", e);
			}
			PrivacyPreference pp = ppoService.get(resource.asURI());
			List<org.ontoware.rdfreactor.schema.rdfs.Resource> resources = pp.getAllAppliesToResource_as().asList();
			for (Resource res : resources) {
				try {
					if (getResourceStore().isTypedAs(res, NIE.DataObject) || 
							getResourceStore().isTypedAs(res, NFO.FileDataObject)){
							processDataObject(res, ppoService, pimoService);
						} else if (getResourceStore().isTypedAs(res, DLPO.LivePost)){
							processLivePost(res, ppoService);
						} else if (getResourceStore().isTypedAs(res, PIMO.Person)){
					} else if (getResourceStore().isTypedAs(res, PIMO.PersonGroup)){
					}
				} catch (NotFoundException e) {
					logger.error("Could not process trust for: <"+res.toString()+"> . ",e);
					return;
				} catch (RepositoryException e) {
					logger.error("Could not process trust for: <"+res.toString()+"> . ",e);
					return;
				}
			}			
		}
	}
	
	private void processDatabox(Resource resource, PrivacyPreferenceService ppoService) {
		//TODO: enable
//		try {
//		PrivacyPreference databox = resourceStore.get(resource.asURI(), PrivacyPreference.class);
//		List<org.ontoware.rdfreactor.schema.rdfs.Resource> related_resources = databox.getAllIsRelated_as().asList();
//		ClosableIterator<AccessSpace> as_it = databox.getAllAccessSpace();
//		while (as_it.hasNext()) {
//			eu.dime.ps.semantic.model.nso.AccessSpace as = 
//					resourceStore.get(as_it.next(), eu.dime.ps.semantic.model.nso.AccessSpace.class);
//			List<PersonGroup> groups = getGroupsFromAccesSpace(as, resourceStore);
//			for (PersonGroup personGroup : groups) {
//				if (!related_resources.contains(personGroup)){
//					personGroup.addIsRelated(databox);
//					databox.addIsRelated(personGroup);
//					resourceStore.update(personGroup, true);
//					resourceStore.update(databox, true);
//				}							
//			}
//		}
//	} catch (ClassCastException e) {
//		logger.error("Could not load DataBox for: <"+resource.toString()+"> . ",e);
//		return;
//	} catch (NotFoundException e) {
//		logger.error("Could not load DataBox for: <"+resource.toString()+"> . ",e);
//		return;
//	}
	}

	private void processLivePost(Node resource, PrivacyPreferenceService ppoService){
		try {
			LivePost livePost = getResourceStore().get(resource.asURI(), LivePost.class);
			List<org.ontoware.rdfreactor.schema.rdfs.Resource> related_resources = livePost.getAllIsRelated_as().asList();
			PrivacyPreference pp = ppoService.getForLivePost(livePost);
			if (pp == null){
				logger.debug("No PrivacyPreference found for LivePost: "+livePost);
				return;
			}
			if (pp.hasAccessSpace()){
				updateRelatedTo(pp, livePost);
				processEvalData(DLPO.LivePost, pp, livePost);
			}
		} catch (ClassCastException e) {
			logger.error("Could not load LivePost for: <"+resource.toString()+"> . ",e);
			return;
		} catch (NotFoundException e) {
			logger.error("Could not load LivePost for: <"+resource.toString()+"> . ",e);
			return;
		} catch (RepositoryException e) {
			logger.error("Could not update relatedTo for: <"+resource.toString()+"> . ",e);
			return;
		} catch (InfosphereException e) {
			logger.error("Could not process trust updates for: <"+resource.toString()+"> . ",e);
			return;
		}
	}
	
	private void processDataObject(Node resource, PrivacyPreferenceService ppoService, PimoService pimoService){
		DataObject dataObject = null;
		try {
			dataObject = getPimoService().get(resource.asURI(), DataObject.class);			
			PrivacyPreference pp = ppoService.getForDataObject(dataObject);
			if (pp == null){
				logger.debug("No PrivacyPreference found for Dataobject: "+dataObject);
				return;
			}
			
			if (pp.hasAccessSpace()){
				updateRelatedTo(pp, dataObject);	
				processEvalData(NFO.FileDataObject, pp, dataObject);
			}

		} catch (ClassCastException e) {
			logger.error("Could not load DataObject for: <"+resource.toString()+"> . ",e);
			return;
		} catch (NotFoundException e) {
			logger.error("Could not load DataObject for: <"+resource.toString()+"> . ",e);
			return;
		} catch (InfosphereException e) {
			logger.error("Could not process trust for: <"+resource.toString()+"> . ",e);
			return;
		} catch (RepositoryException e) {
			logger.error("Could not process trust for: <"+resource.toString()+"> . ",e);
			return;
		}
	}
	
	
	private void updateRelatedTo(PrivacyPreference pp, RDFReactorThing resource) throws NotFoundException{
		PimoService pimoService = null;
		PrivacyPreferenceService ppService = null;
		ResourceStore resourceStore = null;
		try {
			pimoService = getPimoService();
			ppService = getPrivPrefService();
			resourceStore = getResourceStore();
		} catch (RepositoryException e) {
			logger.warn("Could not update relatedTo. Error in loading rdf services. TenantContext set?",e);
		}
		ClosableIterator<AccessSpace> as_it = pp.getAllAccessSpace();
		List<org.ontoware.rdfreactor.schema.rdfs.Resource> related_resources = 
				resource.getAllIsRelated_as().asList();

		while (as_it.hasNext()) {
			eu.dime.ps.semantic.model.nso.AccessSpace as = 
					resourceStore.get(as_it.next(), eu.dime.ps.semantic.model.nso.AccessSpace.class);
			List<PersonGroup> groups = getGroupsFromAccesSpace(as, resourceStore);
			for (PersonGroup personGroup : groups) {
				if (!related_resources.contains(personGroup)){
					resourceStore.addValue(pimoService.getPimoUri(), personGroup, NAO.isRelated, resource);
					resourceStore.addValue(pimoService.getPimoUri(), resource, NAO.isRelated, personGroup);
				}							
			}
		}
	}
	


	private void processEvalData(URI type, PrivacyPreference pp, RDFReactorThing rdfThing) 
			throws NotFoundException, InfosphereException, RepositoryException {
		ResourceStore resourceStore = getResourceStore();
		List <Agent> agentsWithAccess = getPersonsFromPP(pp, resourceStore);
		double privacy_level = rdfThing.getAllPrivacyLevel().next();
		if (agentsWithAccess.isEmpty()){
			return;
		}
		if (TrustProcessor.getThreshold(agentsWithAccess.size(), privacy_level)){
			if (resourceStore.isTypedAs(rdfThing, DLPO.LivePost)){
				notifyUI(UNRefToItem.OPERATION_DEC_PRIVACY, UNRefToItem.TYPE_LIVEPOST, rdfThing.asURI().toString(), rdfThing.getPrefLabel());
			} else if (resourceStore.isTypedAs(rdfThing, NFO.FileDataObject)){
				notifyUI(UNRefToItem.OPERATION_DEC_PRIVACY, UNRefToItem.TYPE_RESOURCE, rdfThing.asURI().toString(), rdfThing.getPrefLabel());
			}
		} else {
			// iterate over persons...
			Set <URI> personsToAdapt = new HashSet<URI>();
			for (Agent agent : agentsWithAccess) {
				Collection<LivePost> liveposts = sharingManager.getSharedLivePost(agent.toString());
				Collection<FileDataObject> files = sharingManager.getSharedFiles(agent.toString());
				//Collection<PrivacyPreference> dbs = sharingManager.getSharedDataboxes(agent.toString()); does files include the ones in a databox??
//					List <Resource> resourceList = new ArrayList<Resource>();
//					resourceList.addAll(liveposts);
//					resourceList.addAll(files);
				int itemCount = liveposts.size() + files.size();
				double avg = 0;
				if (itemCount >= AdvisoryConstants.TRUST_ADAPTION_TRIGGER){
					double sum = 0.0;
					for (LivePost lp : liveposts) {
						double pl = lp.getAllPrivacyLevel().next();
						if(pl == AdvisoryConstants.PV_HIGH){ //stronger influence of private stuff
							pl = 3* pl;
							itemCount = itemCount +2;
						}
						sum += pl;
					}
					for (FileDataObject file : files) {
						double pl = file.getAllPrivacyLevel().next();
						if(pl == AdvisoryConstants.PV_HIGH){ //stronger influence of private stuff
							pl = 3* pl;
							itemCount = itemCount +2;
						}
						sum += pl;
					}
					avg = sum/itemCount;	
				} else return;
				double targetTrustValue = TrustProcessor.calculateAdopted3AbasedDirectTrust(avg, 1);
				Person person = resourceStore.get(agent.asURI(), Person.class);
				if (!person.hasTrustLevel()){
					logger.debug("notify: no trust level");
				}
				else if (targetTrustValue > person.getAllTrustLevel().next()){
					//send notification
					logger.debug("notify: trust level should increase");
					personsToAdapt.add(person.asURI());
				}
				
			}
			sendTrustNotifications(personsToAdapt);
		}
	}
	
	private void sendTrustNotifications(Set<URI> personsToAdapt) throws RepositoryException {
		ResourceStore resourceStore = getResourceStore();
		if (personsToAdapt.isEmpty()){
			return;
		} else {
			for (URI personUri : personsToAdapt){
				Person person;
				try {
					person = resourceStore.get(personUri, Person.class);
				} catch (NotFoundException e) {
					logger.error("Could not send notification because Person not found.", e);
					return;
				}
				notifyUI(UNRefToItem.OPERATION_INC_TRUST, UNRefToItem.TYPE_PERSON, person.asURI().toString(), person.getPrefLabel());
			}
		}
	}

	private List<Agent> getPersonsFromPP(PrivacyPreference pp, 
			ResourceStore resourceStore) throws NotFoundException{
		List <AccessSpace> asList =  pp.getAllAccessSpace_as().asList();
		HashSet<Agent> resultSet = new HashSet<Agent>();
		for (AccessSpace as_ppo : asList){
			eu.dime.ps.semantic.model.nso.AccessSpace as_nfo = 
					resourceStore.get(as_ppo, eu.dime.ps.semantic.model.nso.AccessSpace.class);
			List <Agent> agents = as_nfo.getAllIncludes_as().asList();
			for (Agent agent : agents){
				if (resourceStore.isTypedAs(agent, PIMO.Person)){
					Person person = resourceStore.get(agent, Person.class);
					resultSet.add(person);
				} else if (resourceStore.isTypedAs(agent, PIMO.PersonGroup)){
					PersonGroup group = resourceStore.get(agent, PersonGroup.class);
					resultSet.addAll(group.getAllMembers_as().asList());
				}
			}
		}
		return new ArrayList<Agent>(resultSet);
	}

	private List<PersonGroup> getGroupsFromAccesSpace(eu.dime.ps.semantic.model.nso.AccessSpace as, 
			ResourceStore resourceStore) throws NotFoundException {
		List<Agent> agents = as.getAllIncludes_as().asList();
		Set<PersonGroup> groups = new HashSet<PersonGroup>();
		for (Agent agent : agents) {
			if (resourceStore.isTypedAs(agent, PIMO.PersonGroup)){
				PersonGroup pg = resourceStore.get(agent, PersonGroup.class);
				groups.add(pg);
			} else if (resourceStore.isTypedAs(agent, PIMO.Person)){
				Person p = resourceStore.get(agent, Person.class);
				List <PersonGroup> memberPinG = p.getAllMemberOf_as().asList();
				for (PersonGroup g: memberPinG){
					groups.add(g);
				}
			}
		}
		return new ArrayList<PersonGroup>(groups);
	}
		
}
