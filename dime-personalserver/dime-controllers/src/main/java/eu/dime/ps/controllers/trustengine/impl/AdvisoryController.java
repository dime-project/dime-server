package eu.dime.ps.controllers.trustengine.impl;

import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.runtime.RDFDataException;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

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
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.controllers.trustengine.TrustEngine;
import eu.dime.ps.dto.Profile;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.pimo.Thing;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * 
 * @author marcel
 *
 */
public class AdvisoryController {

	Logger logger = Logger.getLogger(TrustEngineImpl.class);
	
	private ResourceStore resourceStore;
	private PersonManager personManager;
	private PersonGroupManager personGroupManager;	
	private TrustEngine trustEngine;	
	private ConnectionProvider connectionProvider;
	private ShareableProfileManager shareableProfileManager;

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	public void setPersonManager(PersonManager personManager) {
		this.personManager = personManager;
	}

	public void setPersonGroupManager(PersonGroupManager personGroupManager) {
		this.personGroupManager = personGroupManager;
	}

	public void setTrustEngine(TrustEngine trustEngine) {
		this.trustEngine = trustEngine;
	}
	
	public void setShareableProfileManager (ShareableProfileManager shareableProfileManager){
		this.shareableProfileManager = shareableProfileManager;
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
			//warnings.addAll(getProfileWarnings(persons, profile));
			warnings.addAll(getResourceWarnings(sharedThingIDs));
			warnings.addAll(getReceiverWarnings(persons, sharedThingIDs));
		} catch (RepositoryException e) {
			logger.error("Oups, something went wrong. ",e);
		} catch (ClassCastException e) {
			logger.error("Oups, something went wrong. ",e);
		}
		return warnings;

	}
	
	private Collection<ReceiverWarning> getReceiverWarnings(List<String> persons, List<String> sharedThingIDs) {
		List <ReceiverWarning> warnings = new ArrayList<ReceiverWarning>();
		int size = persons.size();
		double warning_level = 0;

		for (String thingID : sharedThingIDs){
			Thing sharedThing;
			try {
				sharedThing = this.getResourceStore().get(new URIImpl(thingID), Thing.class);
			} catch (RepositoryException e) {
				logger.warn("Could not retrieve resource for URI: "+thingID, e);
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
				logger.warn("Privacy values are not retrievable. Wrong data format?", e);
				continue;
			}
			if (TrustProcessor.getThreshold(size, privacyLevel)){
				warning_level = warning_level + 1/size;
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
	
	private Collection<ResourcesWarning> getResourceWarnings(List<String> sharedThingIDs) {
		List <ResourcesWarning> warnings = new ArrayList<ResourcesWarning>();
		ResourcesWarning resourceWarning = new ResourcesWarning();
		//TODO: how to decide that it are too many resources ??
		//simple solution res>30 warning level = res/100, res>100 always 1
		int size = sharedThingIDs.size();
		resourceWarning.setNumberOfResources(size);

		if (size < 5){
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
		PersonContact pc;
		try {
			pc = resourceStore.get(new URIImpl(profile), PersonContact.class);
			Account account = pc.getSharedThrough();
			for (String person : persons){
				//Collection<PersonContact> profiles = shareableProfileManager.getAll(account.asURI().toString(), person); 
				//TODO: if profiles empty -> profile unshared
				newPersons.add(person);
			}
		} catch (NotFoundException e) {
			logger.warn("could not find resource.",e);
		} catch (ClassCastException e) {
			logger.warn("Uri is not a PersonContact)",e);

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
	
	public List<TrustWarning> getTrustWarnings(List<String> agentIDs, List<String> sharedThingIDs){
		List <TrustWarning> warnings = trustEngine.getRecommendation(agentIDs, sharedThingIDs);
		return warnings;
	}
	
	public List<GroupDistanceWarning> getGroupWarnings(List<String> agentIDs, List<String> sharedThingIDs) 
			throws NotFoundException, InfosphereException, RepositoryException, ClassCastException{
		
		List <GroupDistanceWarning> warnings = new ArrayList<GroupDistanceWarning>();
		for (String res_uri : sharedThingIDs) {
			Thing thing = getResourceStore().get(new URIImpl(res_uri), Thing.class);
			List<Resource> related_elements = thing.getAllIsRelated_as().asList();
			if ((related_elements == null) || (related_elements.isEmpty())){
				// nothing is related
				//TODO: return new Warning "resource unshared"
				continue;
			}
			HashedMap targetGroups = getGroupList(agentIDs);
			MapIterator it = targetGroups.mapIterator();

			while (it.hasNext()) {
				String target_element_key = (String) it.next();
				URI targetURI = new URIImpl(target_element_key);
				if (getResourceStore().isTypedAs(targetURI, PIMO.PersonGroup)){
					if (!related_elements.contains(target_element_key)){
						// resource is shared to an unrelated group
						for (Resource groupRes: related_elements) {
							if (getResourceStore().isTypedAs(groupRes, PIMO.PersonGroup)){
								PersonGroup groupA = (PersonGroup) getResourceStore().get(groupRes.asURI(), PersonGroup.class);
								PersonGroup groupB = (PersonGroup) getResourceStore().get(targetURI.asURI(), PersonGroup.class);
								double distance = GroupDistanceProcessor.getGroupDistance(groupA, groupB);
								if (distance > 0){ //TODO: which min distance?
									GroupDistanceWarning warning = new GroupDistanceWarning();
									List<Agent> members = groupB.getAllMembers_as().asList();
									for (Agent member : members) {
										if (!thing.hasSharedWith(member)){
											warning.addPerson(member.asURI().toString());
										}
										
									}
									warning.addGroup(targetURI.toString());
									warning.addResource(res_uri);
									warning.setWarningLevel(distance);
									warnings.add(warning);
								}
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
	
	public ResourceStore getResourceStore() throws RepositoryException {
		if (this.resourceStore== null){
			this.resourceStore = connectionProvider.getConnection(TenantContextHolder.getTenant().toString()).getResourceStore();
		}
		return this.resourceStore;
	}

}
