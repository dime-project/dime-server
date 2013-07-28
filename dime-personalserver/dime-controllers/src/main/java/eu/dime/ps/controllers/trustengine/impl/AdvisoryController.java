/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.RepositoryException;

import eu.dime.commons.dto.GroupDistanceWarning;
import eu.dime.commons.dto.ProfileWarning;
import eu.dime.commons.dto.ReceiverWarning;
import eu.dime.commons.dto.ResourcesWarning;
import eu.dime.commons.dto.TrustWarning;
import eu.dime.commons.dto.Warning;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileCardManager;
import eu.dime.ps.controllers.trustengine.TrustEngine;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.RDFReactorThing;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * 
 * @author marcel
 *
 */
public class AdvisoryController extends AdvisoryBase {

	Logger logger = Logger.getLogger(AdvisoryController.class);
	
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;	
	private TrustEngine trustEngine;	
	private ProfileCardManager profileCardManager;
	
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	public void setTrustEngine(TrustEngine trustEngine) {
		this.trustEngine = trustEngine;
	}
	
	public void setProfileCardManager(ProfileCardManager profileCardManager) {
		this.profileCardManager = profileCardManager;
	}
	
	public AdvisoryController(){

	}
	
	public Collection<Warning> getAdvisory(List<String> agentIDs, List<String> sharedThingIDs, String profile) 
			throws NotFoundException, InfosphereException{
		
		Collection <Warning> warnings = new ArrayList<Warning>();
		
		List<String> persons;
		//HashedMap resources = this.getResourceList();
		try {
			persons = this.getPersonList(agentIDs);
			warnings.addAll(getTrustWarnings(persons, sharedThingIDs));
			warnings.addAll(getGroupWarnings(agentIDs, sharedThingIDs));
			warnings.addAll(getProfileWarnings(persons, profile));
			warnings.addAll(getResourceWarnings(sharedThingIDs));
			warnings.addAll(getReceiverWarnings(persons, sharedThingIDs));
		} catch (RepositoryException e) {
			logger.error("Oups, something went wrong. ",e);
            throw new InfosphereException(e.getMessage(), e);
		} catch (ClassCastException e) {
			logger.error("Oups, something went wrong. ",e);
            throw new InfosphereException(e.getMessage(), e);
		}
		return warnings;

	}
	
	private Collection<ReceiverWarning> getReceiverWarnings(List<String> persons, List<String> sharedThingIDs) {
		List <ReceiverWarning> warnings = new ArrayList<ReceiverWarning>();
		int size = persons.size();
		double warning_level = 0;

		for (String thingID : sharedThingIDs){
			RDFReactorThing sharedThing;
			try {

                sharedThing = getResource(new URIImpl(thingID));

            } catch (RepositoryException ex) {
                logger.warn("Could not retrieve resource for URI: "+thingID, ex);
				continue;
			} catch (NotFoundException e) {
				logger.warn("Could not retrieve resource for URI: "+thingID, e);
				continue;
			}
			if (!sharedThing.hasPrivacyLevel()){
				continue;
			}
			double privacyLevel;
			try {
				privacyLevel = sharedThing.getAllPrivacyLevel().next();
			} catch(RDFDataException e){
				logger.warn(sharedThing + " privacy level cannot be retrieved. RDF dump: " + sharedThing.getModel().serialize(Syntax.Turtle), e);
				continue;
			}
			if (TrustProcessor.getRecipientThreahold(size, privacyLevel)){
				//warning_level = warning_level + 1.0/size;
				//TODO: optimize warning level
				if (warning_level < privacyLevel){
					warning_level = privacyLevel;
				}
			}
			
		}

		if (warning_level > 0){
			ReceiverWarning receiverWarning = new ReceiverWarning();
			receiverWarning.setWarningLevel(warning_level);
			receiverWarning.setNumberOfReceivers(persons.size());
			warnings.add(receiverWarning);
		}
		return warnings;
	}
	
	private RDFReactorThing getResource(URI resUri) throws NotFoundException, RepositoryException {
		RDFReactorThing resource = null;
        ResourceStore resourceStore=getResourceStore();
		if (resourceStore.isTypedAs(resUri, NIE.DataObject)){
			resource = resourceStore.get(resUri, DataObject.class);
		} else if (resourceStore.isTypedAs(resUri, DLPO.LivePost)){
			resource = resourceStore.get(resUri, LivePost.class);
		} else if (resourceStore.isTypedAs(resUri, PPO.PrivacyPreference)){
			resource = resourceStore.get(resUri, PrivacyPreference.class);
		} else {
			resource = resourceStore.get(resUri, RDFReactorThing.class);
		}		
		return resource;
	}

	private Collection<ResourcesWarning> getResourceWarnings(List<String> sharedThingIDs) {
		List <ResourcesWarning> warnings = new ArrayList<ResourcesWarning>();
		ResourcesWarning resourceWarning = new ResourcesWarning();
		//TODO: how to decide that it are too many resources ??
		//simple solution res>30 warning level = res/100, res>100 always 1
		int size = sharedThingIDs.size();
		resourceWarning.setNumberOfResources(size);

		if (size < AdvisoryConstants.RESOURCE_WARNING_TRIGGER){
			return warnings;
		} else if (size > 100){
			resourceWarning.setWarningLevel(1.0);
			warnings.add(resourceWarning);
		} else {
			resourceWarning.setWarningLevel(size/100.0);
			warnings.add(resourceWarning);
		}
		return warnings;
	}
	
	private Collection<ProfileWarning> getProfileWarnings(List<String> persons, String profile) {
		List <ProfileWarning> warnings = new ArrayList<ProfileWarning>();
		List <String> newPersons = new ArrayList<String>();
		//TODO: get Profile object, get mySaid,
		PrivacyPreference pc;
		try {
            ResourceStore resourceStore=getResourceStore();
			pc = resourceStore.get(new URIImpl(profile), PrivacyPreference.class);
			Set<Person> set = new HashSet<Person>();
			if (pc.hasAccessSpace()){
				List<AccessSpace> accessSpaces = pc.getAllAccessSpace_as().asList();
				for (AccessSpace as : accessSpaces){
					eu.dime.ps.semantic.model.nso.AccessSpace asNSO = 
							resourceStore.get(as.asURI(), eu.dime.ps.semantic.model.nso.AccessSpace.class);
					set.addAll(getPersonsFromAccesSpace(asNSO, resourceStore));
				}				
			}
			for (String pString : persons) {
				Person person = resourceStore.get(new URIImpl(pString), Person.class);
				if(!set.contains(person)){
					newPersons.add(person.asURI().toString());
				}
			}	
		} catch (NotFoundException e) {
			logger.warn("could not find resource.",e);
		} catch (ClassCastException e) {
			logger.warn("Uri is not a PersonContact",e);
		} catch (RepositoryException e) {
			logger.warn("Could not get resource.", e);
		} catch (InfosphereException e) {
			logger.warn("Could not resolve accesspace.",e);
		}
		
		if (!newPersons.isEmpty()){
			ProfileWarning profileWarning = new ProfileWarning();
			profileWarning.addProfile(profile);
			profileWarning.setWarningLevel(1.0);
			profileWarning.setPersonGuids(newPersons);
			warnings.add(profileWarning);
		}
		return warnings;
	}
	
	private Set<Person> getPersonsFromAccesSpace(eu.dime.ps.semantic.model.nso.AccessSpace as, 
			ResourceStore resourceStore) throws NotFoundException, RepositoryException, InfosphereException {
		List<Agent> agents = as.getAllIncludes_as().asList();
		List <String> aStrings = new ArrayList();
		HashSet <Person> resultSet = new HashSet<Person>();
		for (Agent agent : agents){				
				if (getResourceStore().isTypedAs(agent, PIMO.Person)){
					resultSet.add(resourceStore.get(agent, Person.class));
				} else {
					PersonGroup group = personGroupManager.get(agent.toString());
					Collection<Person> members = personManager.getAllByGroup(group);
					for (Person member : members) {
						resultSet.add(member);
					}
				}
			}
		return resultSet;
		}
		
	
	public List<TrustWarning> getTrustWarnings(List<String> agentIDs, List<String> sharedThingIDs){
		List <TrustWarning> warnings = trustEngine.getRecommendation(agentIDs, sharedThingIDs);
		return warnings;
	}
	
	public List<GroupDistanceWarning> getGroupWarnings(List<String> agentIDs, List<String> sharedThingIDs) 
			throws NotFoundException, InfosphereException, RepositoryException, ClassCastException{
		
		List <GroupDistanceWarning> warnings = new ArrayList<GroupDistanceWarning>();
		
		List <String> sharedSingleElements = new ArrayList<String>();
		for (String res : sharedThingIDs){
			URI resUri = new URIImpl(res);
			if (getResourceStore().isTypedAs(resUri, PPO.PrivacyPreference)){
				sharedSingleElements.addAll(getAllItemsInDataboxAsString(resUri));
			} else {
				sharedSingleElements.add(res);
			}
		}
		
		for (String res_uri : sharedSingleElements) {
			RDFReactorThing thing = getResource(new URIImpl(res_uri));
			List<Resource> related_groups = thing.getAllIsRelated_as().asList();
			if ((related_groups == null) || (related_groups.isEmpty())){
				// nothing is related
				//TODO: return new Warning "resource unshared"
				continue;
			}
			HashedMap targetGroups = getGroupList(agentIDs);
			MapIterator it = targetGroups.mapIterator();

			while (it.hasNext()){
				String element_key = (String) it.next();
				PersonGroup groupA = (PersonGroup) getResourceStore().get(new URIImpl(element_key), PersonGroup.class);
				List<Resource> a = groupA.getAllIsRelated_as().asList();
				if (a.contains(thing)){
					//shared resource already related to target group
					targetGroups.remove(element_key);
				}
			}
			MapIterator it2 = targetGroups.mapIterator();
			while (it2.hasNext()) {
				String target_element_key = (String) it2.next();
				URI targetURI = new URIImpl(target_element_key);
				if (getResourceStore().isTypedAs(targetURI, PIMO.PersonGroup)){
						// resource is shared to an unrelated group
					for (Resource groupRes: related_groups) {
						if (getResourceStore().isTypedAs(groupRes, PIMO.PersonGroup)){
							PersonGroup groupA = (PersonGroup) getResourceStore().get(groupRes.asURI(), PersonGroup.class);
							PersonGroup groupB = (PersonGroup) getResourceStore().get(targetURI.asURI(), PersonGroup.class);
							double distance = GroupDistanceProcessor.getGroupDistance(groupA, groupB);
							if (distance > AdvisoryConstants.MIN_GROUP_DISTANCE){ //TODO: which min distance?
								GroupDistanceWarning warning = new GroupDistanceWarning();
								//List<Agent> members = groupB.getAllMembers_as().asList();
								warning.addGroup(groupRes.toString());
								warning.addResource(res_uri);
								warning.setWarningLevel(distance);
								warnings.add(warning);
							}
						}
					}
	
				}
			}
			
		}
		return warnings;
	}

//	/**
//	 * Different implementation
//	 * @param agentIDs
//	 * @param sharedThingIDs
//	 * @return
//	 * @throws NotFoundException
//	 * @throws InfosphereException
//	 * @throws RepositoryException
//	 * @throws ClassCastException
//	 */
//	public List<GroupDistanceWarning> getGroupWarnings2(List<String> agentIDs, List<String> sharedThingIDs) 
//			throws NotFoundException, InfosphereException, RepositoryException, ClassCastException{
//		HashedMap targetGroups = getGroupList(agentIDs);
//		Iterator it = targetGroups.mapIterator();
//		while (it.hasNext()) {
//			URI groupUri = (URI) it.next();
//				PersonGroup groupA = (PersonGroup) getResourceStore().get(groupUri, PIMO.PersonGroup);
//				List<Resource> related = groupA.getAllIsRelated_as().asList();
//				for (Resource resource : related){
//					if (sharedThingIDs.contains(resource)){
//						
//					}
//				}
//		}
//		List <GroupDistanceWarning> warnings = new ArrayList<GroupDistanceWarning>();
//		
//		for (String res_uri : sharedThingIDs) {
//			Thing thing = getResourceStore().get(new URIImpl(res_uri), Thing.class);
//			ClosableIterator<Resource> it = thing.getAllIsRelated();
//			if ((it != null && !it.hasNext()) || it == null){
//				// nothing is related
//				//TODO: return new Warning "resource unshared"
//			}
//			while (it.hasNext()) {
//				Resource related_element = it.next();
//				if (getResourceStore().isTypedAs(related_element.asURI(), PIMO.PersonGroup)){
//					if (!targetGroups.containsKey(related_element.asURI().toString())){
//						// resource is shared to an unrelated group
//						MapIterator cn = targetGroups.mapIterator();
//						while (cn.hasNext()) {
//							URI groupUri = (URI) cn.next();
//							PersonGroup groupA = (PersonGroup) getResourceStore().get(groupUri, PIMO.PersonGroup);
//							PersonGroup groupB = (PersonGroup) getResourceStore().get(related_element, PIMO.PersonGroup);
//							double distance = GroupDistanceProcessor.getGroupDistance(groupA, groupB);
//							//TODO: return new GroupWarning
//							if (distance > 0){ //TODO: which min distance?
//								GroupDistanceWarning warning = new GroupDistanceWarning();
//								ClosableIterator<Resource> members = groupA.getAllMember();
//								while (members.hasNext()) {
//									Resource resource = (Resource) members.next();
//									warning.addPerson(resource.asURI().toString());
//								}
//								warning.addGroup(related_element.asURI().toString());
//								warning.addResource(res_uri);
//								warning.setWarningLevel(distance);
//								warnings.add(warning);
//							}
//							
//						}
//					}
//				}
//			}
//			
//		}
//		return warnings;
//	}
	
	
	
	
	
// ------------------ Helper Methods -------------------
	
	/**
	 * Resolves List of URIs, that may be persons and/or groups to only personURIs (unique) 
	 * @param agentUri
	 * @return
	 * @throws NotFoundException 
	 * @throws InfosphereException 
	 * @throws RepositoryException 
	 */
	private List<String> getPersonList(List<String> agentIDs) 
			throws NotFoundException, InfosphereException, RepositoryException{
		HashedMap map = this.getPersonMap(agentIDs);
		List <String> result = new ArrayList<String>();
		Iterator it = map.mapIterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			result.add(id);
		}
		return result;
	}
	
	private HashedMap getPersonMap (List<String> agentIDs) 
			throws RepositoryException, NotFoundException, InfosphereException {
		boolean	isPerson = false;
		HashedMap map = new HashedMap();
		Iterator<String> it = agentIDs.iterator();
		while (it.hasNext()) {
			URI agentUri = new URIImpl(it.next());
			Resource res = getResourceStore().get(agentUri);
			isPerson = getResourceStore().isTypedAs(res, PIMO.Person);
			if (isPerson && !map.containsKey(agentUri.toString())){
				map.put(agentUri.toString(), getResourceStore().get(agentUri, Person.class));
				isPerson = false;
			} else {
				PersonGroup group = personGroupManager.get(agentUri.toString());
				Collection<Person> members = personManager.getAllByGroup(group);
				for (Person member : members) {
					if (!map.containsKey(member.asURI().toString())){
						map.put(member.asURI().toString(), member);
					}
				}
			}
		}
		return map;
	}
	

	
	private HashedMap getGroupList(List<String> agentIDs) 
			throws NotFoundException, InfosphereException, RepositoryException{
		boolean isGroup = false;
		HashedMap map = new HashedMap();
		Iterator<String> it = agentIDs.iterator();
		while (it.hasNext()){
			URI agentUri = new URIImpl(it.next());
			isGroup = getResourceStore().isTypedAs(agentUri, PIMO.PersonGroup);
			if (isGroup && !map.containsKey(agentUri.toString())) {
				map.put(agentUri.toString(), getResourceStore().get(agentUri, PersonGroup.class));
				isGroup = false;
			} else {
				Person person = (Person) getResourceStore().get(agentUri, Person.class);
				Collection <PersonGroup>  groups = personGroupManager.getAll(person);
				for (Iterator<PersonGroup> iterator = groups.iterator(); iterator.hasNext();) {
					PersonGroup personGroup = iterator.next();
					if (!map.containsKey(personGroup.asURI().toString())){
						map.put(personGroup.asURI().toString(), personGroup);
					}
				}
			}
		}
		return map;
	}
	
    @Override
	public ResourceStore getResourceStore() throws RepositoryException {
		return connectionProvider.getConnection(TenantContextHolder.getTenant().toString()).getResourceStore();
		
	}
	
}
