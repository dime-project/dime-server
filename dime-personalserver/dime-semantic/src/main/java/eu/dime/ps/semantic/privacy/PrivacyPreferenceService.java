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

package eu.dime.ps.semantic.privacy;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

/**
 * Provides convenience methods to work with privacy preferences, add
 * resources to them, manage the access spaces and the agents to be
 * included/excluded in them.
 * 
 * @author Ismael Rivera
 */
public interface PrivacyPreferenceService {

	PrivacyPreference get(URI privacyPreferenceUri);
	PrivacyPreference get(URI privacyPreferenceUri, URI... properties);

	PrivacyPreferenceType getType(URI privacyPreferenceUri);
	PrivacyPreferenceType getType(PrivacyPreference privacyPreference);

	PrivacyPreference getForDataObject(DataObject dataObject);
	PrivacyPreference getOrCreateForDataObject(DataObject dataObject);

	PrivacyPreference getForLivePost(LivePost livePost);
	PrivacyPreference getOrCreateForLivePost(LivePost livePost);

	/**
	 * Retrieves, or in the case it does not exist creates, a privacy preference
	 * with a specific label and type.
	 * @param label label which identifies the privacy preference
	 * @param type type of the privacy preference
	 * @return an existing or new privacy preference with the given label and type
	 */
	PrivacyPreference getOrCreate(String label, PrivacyPreferenceType type);

	/**
	 * Same as {@link #grantAccess(String, PrivacyPreferenceType, Agent...)}, except the
	 * label and type are not needed, but the PrivacyPreference object, returned by previous
	 * calls or retrieve from the store.
	 * 
	 * @param privacyPref the privacy preference object in which the agents are added
	 * @param agents the agents to be added to the privacy preference
	 * @return the privacy preference passed as a parameter
	 */
	PrivacyPreference grantAccess(PrivacyPreference privacyPref, URI sharedThrough, Agent... agents);
	
	/**
	 * Grants access to the individual data objects.
	 * 
	 * @param dataObject the label set by the user to the privacy preference
	 * @param items the DataObject items to be added to the databox
	 * @return the privacy preference which shares the data object with the agents
	 */
	PrivacyPreference grantAccess(DataObject dataObject, URI sharedThrough, Agent... agents);

	/**
	 * Grants access to a specific live post.
	 * @param livePost
	 * @param agents
	 * @return
	 */
	PrivacyPreference grantAccess(LivePost livePost, URI sharedThrough, Agent... agents);

	/**
	 * Same as {@link #revokeAccess(String, PrivacyPreferenceType, Agent...),, except the
	 * label and type are not needed, but the PrivacyPreference object, returned by previous
	 * calls or retrieve from the store. 
	 *  
	 * @param privacyPref the privacy preference object in which the agents are removed
	 * @param agents the agents to be removed from the privacy preference
	 * @return the privacy preference matching the given label
	 */
	PrivacyPreference revokeAccess(PrivacyPreference privacyPref, Agent... agents);

	PrivacyPreference revokeAccess(LivePost livePost, Agent... agents);
	
	PrivacyPreference revokeAccess(DataObject dataObject, Agent... agents);

	/**
	 * Checks whether a given agent has access to a specific databox,
	 * and returns true if so.
	 * 
	 * @param databox the databox which may be accessible by the agent
	 * @param agent the agent which may have access to the databox
	 * @return true if the agent has access to the databox
	 */
	boolean hasAccessTo(PrivacyPreference databox, Agent agent) throws PrivacyPreferenceException;
	
	/**
	 * Checks whether a given agent has access to a specific livepost,
	 * and returns true if so.
	 * 
	 * @param databox the livepost which may be accessible by the agent
	 * @param agent the agent which may have access to the livepost
	 * @return true if the agent has access to the livepost
	 */
	boolean hasAccessTo(LivePost livePost, Agent agent) throws PrivacyPreferenceException;
	
	/**
	 * Checks whether a given agent has access to a specific DataObject instance,
	 * and returns true if so.
	 * The access can be direct, or through a Databox.
	 * 
	 * @param databox the dataObject which may be accessible by the agent
	 * @param agent the agent which may have access to the dataObject
	 * @return true if the agent has access to the dataObject
	 */
	boolean hasAccessTo(DataObject dataObject, Agent agent) throws PrivacyPreferenceException;
	
	/**
	 * Checks whether a given agent has access to a specific resource (databox, livepost,
	 * profile attribute, etc.) and returns true if so.
	 * 
	 * @param resource can be anything such as a databox, a profile card, a livepost, or even a profile attribute
	 * @param agent the agent which may have access to the attribute
	 * @return true if the agent has access to the attribute
	 */
	boolean hasAccessTo(Resource resource, Agent agent) throws PrivacyPreferenceException;

	Collection<Account> getAllRecipients(AccessSpace accessSpace);

	Collection<PrivacyPreference> getByType(PrivacyPreferenceType type);
	Collection<PrivacyPreference> getByType(PrivacyPreferenceType type, URI... properties);
	
	Collection<PrivacyPreference> getByAgentAndType(Agent agent, PrivacyPreferenceType type);
		
	/**
	 * Returns all the privacy preferences which can be access by a given agent. 
	 */
	Collection<PrivacyPreference> getByAgentAndType(Agent agent, PrivacyPreferenceType type, URI... properties);

	/**
	 * Retrieves all the agents (pimo:Agent) who have access to
	 * a given resource (any rdfs:Resource).
	 * 
	 * @param resourceUri the resource to look for agents with access to
	 * @return the collection of agents to access to the resource
	 */
	Collection<Agent> getAgentsWithAccessTo(org.ontoware.rdf2go.model.node.Resource resourceIdentifier);

}
