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

package eu.dime.ps.semantic.privacy.impl;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.pimo.Agent;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.semantic.service.logging.HistoryLogService;
import eu.dime.ps.semantic.util.StringUtils;

/**
 * Provides an implementation of {@link PrivacyPreferenceService}, storing
 * and reading the privacy preferences from an RDF store.
 * 
 * @author Ismael Rivera
 */
public class PrivacyPreferenceServiceImpl implements PrivacyPreferenceService {

	private static final Logger logger = LoggerFactory.getLogger(PrivacyPreferenceServiceImpl.class);

	private ModelFactory modelFactory;

	private TripleStore tripleStore;
	private ResourceStore resourceStore;
	private PimoService pimoService;
	
	private HistoryLogService historyLogService;
	
	private final BroadcastManager broadcastManager = BroadcastManager.getInstance();
	
	public void setPimoService(PimoService pimoService) {
		this.pimoService = pimoService;
		this.tripleStore = pimoService.getTripleStore();
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
	}
	
	public void setHistoryLogService(HistoryLogService historyLogService) {
		this.historyLogService = historyLogService;
	}

	public PrivacyPreferenceServiceImpl() {
		this.modelFactory = new ModelFactory();
	}

	public PrivacyPreferenceServiceImpl(PimoService pimoService) {
		this();
		setPimoService(pimoService);
	}
	
	@Override
	public PrivacyPreference get(URI privacyPreferenceUri) {
		return fetch(privacyPreferenceUri);
	}
	
	@Override
	public PrivacyPreference get(URI privacyPreferenceUri, URI... properties) {
		return fetch(privacyPreferenceUri, properties);
	}
	
	@Override
	public PrivacyPreference getOrCreate(String label, PrivacyPreferenceType type) {
		PrivacyPreference privacyPref = null;
		
		final Collection<org.ontoware.rdf2go.model.node.Resource> ids =
				resourceStore.find(PrivacyPreference.class)
				.distinct()
				.where(NAO.prefLabel).is(label)
				.where(RDFS.label).is(type.toString())
				.ids();
		
		if (ids.size() > 1) {
			logger.warn("Only one privacy preference is allowed with same label and type. "+
					ids.size()+" found of type '"+type+"' and label '"+label+"'.");
		} else if (ids.size() == 1) {
			privacyPref = fetch(ids.iterator().next());
		}
		
		if (privacyPref == null) {
			privacyPref = modelFactory.getPPOFactory().createPrivacyPreference();
			privacyPref.getModel().addStatement(privacyPref, PIMO.isDefinedBy, pimoService.getPimoUri());
			privacyPref.setPrefLabel(privacyPref.getModel().createPlainLiteral(label));
			privacyPref.setLabel(type.toString());
			resourceStore.createOrUpdate(pimoService.getPimoUri(), privacyPref);
		}
		
		return privacyPref;
	}

	public PrivacyPreferenceType getType(URI privacyPreferenceUri) {
		PrivacyPreference preference = null;
		
		try {
			preference = pimoService.get(privacyPreferenceUri, PrivacyPreference.class, RDFS.label);
			return getType(privacyPreferenceUri, preference.getModel());
		} catch (NotFoundException e) {
			logger.error("Cannot find type for privacy preference "+privacyPreferenceUri+": "+e.getMessage(), e);
		}
		
		return null;
	}
	
	public PrivacyPreferenceType getType(PrivacyPreference privacyPreference) {
		PrivacyPreferenceType type = null;
		
		// get type from object metadata
		type = getType(privacyPreference.asURI(), privacyPreference.getModel());
		
		// not found in object metadata, we'll look it up in the RDF store
		if (type == null) {
			type = getType(privacyPreference.asURI());
		}
		
		return type;
	}
	
	private PrivacyPreferenceType getType(URI preference, Model metadata) {
		PrivacyPreferenceType type = null;
		List<String> labels = new ArrayList<String>();
		
		ClosableIterator<Statement> labelIt = metadata.findStatements(preference, RDFS.label, Variable.ANY);
		while (labelIt.hasNext()) {
			String label = labelIt.next().getObject().asLiteral().getValue();
			labels.add(label);
			try {
				type = PrivacyPreferenceType.valueOf(label);
				break;
			} catch (IllegalArgumentException e) {
				logger.debug("'"+label+"' is a rdfs:label of "+preference+", but it's not a valid type.");
			}			
		}
		labelIt.close();

		if (type == null) {
			logger.warn("Values of rdfs:label for " + preference + " " + labels + " don't include a valid type.");
		}
		
		return type;
	}
	
	/**
	 * Creates a backup of a privacy preference in the History Log.
	 * This should be called before modifying any privacy preference in order to
	 * keep track of changes to them.
	 */
	private void auditChange(PrivacyPreference privacyPreference) {
		if (historyLogService != null) {
			historyLogService.createLogForPrivacyPreference(privacyPreference);
		}
	}

	@Override
	public PrivacyPreference grantAccess(PrivacyPreference privacyPref, URI sharedThrough, Agent... agents) {
		auditChange(privacyPref);
		addAgentToPrivacyPreference(privacyPref, sharedThrough, agents);
		return privacyPref;
	}
	
	@Override
	public PrivacyPreference grantAccess(DataObject dataObject, URI sharedThrough, Agent... agents) {
		PrivacyPreference privacyPref = getOrCreateForDataObject(dataObject);
		auditChange(privacyPref);
		addAgentToPrivacyPreference(privacyPref, sharedThrough, agents);
		return privacyPref;
	}

	@Override
	public PrivacyPreference grantAccess(LivePost livePost, URI sharedThrough, Agent... agents) {
		PrivacyPreference privacyPref = getOrCreateForLivePost(livePost);
		auditChange(privacyPref);
		addAgentToPrivacyPreference(privacyPref, sharedThrough, agents);
		return privacyPref;
	}

	@Override
	public PrivacyPreference revokeAccess(PrivacyPreference privacyPref, Agent... agents) {
		auditChange(privacyPref);
		removeAgentFromPrivacyPreference(privacyPref, agents);
		return privacyPref;
	}

	@Override
	public PrivacyPreference revokeAccess(LivePost livePost, Agent... agents) {
		PrivacyPreference privacyPref = getOrCreateForLivePost(livePost);
		auditChange(privacyPref);
		removeAgentFromPrivacyPreference(privacyPref, agents);
		return privacyPref;
	}

	@Override
	public PrivacyPreference revokeAccess(DataObject dataObject, Agent... agents) {
		PrivacyPreference privacyPref = getOrCreateForDataObject(dataObject);
		auditChange(privacyPref);
		removeAgentFromPrivacyPreference(privacyPref, agents);
		return privacyPref;
	}

	@Override
	public boolean hasAccessTo(PrivacyPreference privacyPreference, Agent agent) throws PrivacyPreferenceException {
		PrivacyPreferenceType type = getType(privacyPreference);
		
		if (PrivacyPreferenceType.PROFILECARD.equals(type)) {
			
			// profile cards can explicitly be shared with agents
			boolean hasAccess = hasAccessViaPrivacyPreference(privacyPreference, agent);
			if (hasAccess) {
				return true;
			}

			// but also, profile cards can be accessed by the agent there's any other (not profile card) privacy
			// preference granting access to the agent through the same di.me account (e.g. sharing a databox
			// with John through the business account implies John can access the profile card shared throught
			// the same business account.

			Collection<org.ontoware.rdf2go.model.node.Resource> accounts =
				resourceStore.find(Account.class)
					.distinct()
					.where(privacyPreference, PPO.hasAccessSpace).is(Query.X)
					.where(Query.X, NSO.sharedThrough).is(Query.THIS)
					.ids();
			for (org.ontoware.rdf2go.model.node.Resource sharedThrough : accounts) {
				Collection<PrivacyPreference> sharedThroughPreferences = getBySharedThrough(sharedThrough.asURI());
				for (PrivacyPreference sharedThroughPreference : sharedThroughPreferences) {
					PrivacyPreferenceType sharedThroughType = getType(sharedThroughPreference);
					
					// filter out other profile cards
					if (!PrivacyPreferenceType.PROFILECARD.equals(sharedThroughType)) {
						hasAccess = hasAccessViaPrivacyPreference(sharedThroughPreference, agent);
						
						// if any privacy preference for the same sharedThrough account grants access to the 
						// agent, then the agent can access the profile card.
						if (hasAccess) {
							return true;
						}
					}
				}
			}
		
			return false;
		} else {
			return hasAccessViaPrivacyPreference(privacyPreference, agent);
		}
	}
	
	@Override
	public boolean hasAccessTo(LivePost livePost, Agent agent) throws PrivacyPreferenceException {
		PrivacyPreference privacyPref = resourceStore.find(PrivacyPreference.class)
				.where(RDFS.label).is(PrivacyPreferenceType.LIVEPOST.toString())
				.where(PPO.appliesToResource).is(livePost)
				.first();
		return privacyPref == null ? false : hasAccessViaPrivacyPreference(privacyPref, agent);
	}
	
	@Override
	public boolean hasAccessTo(DataObject dataObject, Agent agent) throws PrivacyPreferenceException {
		PrivacyPreference privacyPref = resourceStore.find(PrivacyPreference.class)
				.where(RDFS.label).is(PrivacyPreferenceType.FILE.toString())
				.where(PPO.appliesToResource).is(dataObject)
				.first();
		boolean access = privacyPref == null ? false : hasAccessViaPrivacyPreference(privacyPref, agent);
		if (!access) {
			Collection<PrivacyPreference> databoxes = resourceStore.find(PrivacyPreference.class)
					.where(RDFS.label).is(PrivacyPreferenceType.DATABOX.toString())
					.where(PPO.appliesToResource).is(dataObject).results();
			for (PrivacyPreference databox : databoxes) {
				if (hasAccessViaPrivacyPreference(databox, agent)) {
					return true;
				}
			}
		}
		return access;
	}
	
	@Override
	public boolean hasAccessTo(Resource resource, Agent agent) throws PrivacyPreferenceException {
		if (resource instanceof PrivacyPreference) {
			return hasAccessTo((PrivacyPreference) resource, agent);
		} else if (resource instanceof DataContainer
				&& resource.getModel().contains(resource, RDF.type, PPO.PrivacyPreference)) {
			return hasAccessTo((PrivacyPreference) resource.castTo(PrivacyPreference.class), agent);
		} else if (resource instanceof LivePost) {
			return hasAccessTo((LivePost) resource, agent);
		} else if (resource instanceof DataObject) {
			return hasAccessTo((DataObject) resource, agent);
		} else { // it must be a profile attribute
			Collection<PrivacyPreference> cards = resourceStore.find(PrivacyPreference.class)
					.where(RDFS.label).is(PrivacyPreferenceType.PROFILECARD.toString())
					.where(PPO.appliesToResource).is(resource).results();
			for (PrivacyPreference card : cards) {
				if (hasAccessViaPrivacyPreference(card, agent)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void addAgentToPrivacyPreference(PrivacyPreference privacyPref, URI sharedThrough, Agent... agents) {
		if (privacyPref == null) {
			throw new IllegalArgumentException("Must specify a valid privacy preference (privacyPref), not null.");
		}
		if (sharedThrough == null) {
			throw new IllegalArgumentException("Must specify a valid account as 'sharedThrough', not null.");
		}
		if (agents == null || agents.length == 0) {
			throw new IllegalArgumentException("Must specify at least one agent.");
		}
		
		Model metadata = privacyPref.getModel();
		boolean found = false;
		boolean modified = false;

		ClosableIterator<Node> asIt = privacyPref.getAllAccessSpace_asNode();
		while (asIt.hasNext() && !found) {
			URI accessSpace = asIt.next().asURI();

			if (metadata.contains(accessSpace, NSO.sharedThrough, sharedThrough)) {
				found = true;

				// adds the agents to the access space (if they don't exist yet)
				for (Agent agent : agents) {
					if (!metadata.contains(accessSpace, NSO.includes, agent)) {
						metadata.addStatement(accessSpace, NSO.includes, agent.asResource());
						modified = true;
					}
				}
			}
		}
		asIt.close();

		// saves privacy preference if nso:includes was modified
		if (modified) {
			resourceStore.createOrUpdate(pimoService.getPimoUri(), privacyPref);
		}

		// no access space was found for the specified 'sharedThrough', a new one will be created
		if (!found) {
			AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
			accessSpace.setSharedThrough(sharedThrough);
			for (Agent agent : agents) {
				accessSpace.addIncludes(agent.asResource());
			}
			privacyPref.getModel().addAll(accessSpace.getModel().iterator());
			privacyPref.setAccessSpace(accessSpace);
			resourceStore.createOrUpdate(pimoService.getPimoUri(), privacyPref);
		}
	}
	
	private void removeAgentFromPrivacyPreference(PrivacyPreference privacyPref, Agent... agents) {
		boolean modified = false;
		ClosableIterator<Node> accessSpaceIt = privacyPref.getAllAccessSpace_asNode();		
		
		while (accessSpaceIt.hasNext()) {
			URI accessSpaceUri = accessSpaceIt.next().asURI();
			AccessSpace accessSpace;
			try {
				accessSpace = pimoService.get(accessSpaceUri, AccessSpace.class);
				for (Agent agent : agents) {
					accessSpace.removeIncludes(agent);
				}
				resourceStore.createOrUpdate(pimoService.getPimoUri(), accessSpace);
				modified = true;
			} catch (NotFoundException e) {
				logger.error("PrivacyPreference "+privacyPref+" refers to an inexistent AccessSpace: "+accessSpaceUri, e);
			}
		}
		
		if (modified) {
			broadcastManager.sendBroadcast(new Event(pimoService.getName(), Event.ACTION_RESOURCE_MODIFY, privacyPref));
		}
	}

	private PrivacyPreference getForType(Resource resource, PrivacyPreferenceType type) {
		final Collection<org.ontoware.rdf2go.model.node.Resource> ids =
			resourceStore.find(PrivacyPreference.class)
				.distinct()
				.where(RDFS.label).is(type.toString())
				.where(PPO.appliesToResource).is(resource)
				.ids();
		
		if (ids.size() > 1) {
			logger.warn("Only one privacy preference is allowed for each "+type.toString().toLowerCase()+
					". "+ids.size()+" found for "+resource);
		} else if (ids.size() == 1) {
			// fetched here instead of on the query because this also loads
			// the access space metadata, etc.
			return fetch(ids.iterator().next());
		}
		
		return null;
	}
	
	public PrivacyPreference getForLivePost(LivePost livePost) {
		return getForType(livePost, PrivacyPreferenceType.LIVEPOST);
	}
	
	public PrivacyPreference getOrCreateForLivePost(LivePost livePost) {
		PrivacyPreference privacyPref = getForLivePost(livePost);
		if (privacyPref == null) {
			privacyPref = modelFactory.getPPOFactory().createPrivacyPreference();
			privacyPref.getModel().addStatement(privacyPref, PIMO.isDefinedBy, pimoService.getPimoUri());
			privacyPref.setLabel(PrivacyPreferenceType.LIVEPOST.toString());
			privacyPref.setAppliesToResource(livePost);
			resourceStore.createOrUpdate(pimoService.getPimoUri(), privacyPref);
		}
		return privacyPref;
	}

	@Override
	public PrivacyPreference getForDataObject(DataObject dataObject) {
		return getForType(dataObject, PrivacyPreferenceType.FILE);
	}

	@Override
	public PrivacyPreference getOrCreateForDataObject(DataObject dataObject) {
		PrivacyPreference privacyPref = getForDataObject(dataObject);
		if (privacyPref == null) {
			privacyPref = modelFactory.getPPOFactory().createPrivacyPreference();
			privacyPref.getModel().addStatement(privacyPref, PIMO.isDefinedBy, pimoService.getPimoUri());
			privacyPref.setLabel(PrivacyPreferenceType.FILE.toString());
			privacyPref.setAppliesToResource(dataObject);
			resourceStore.createOrUpdate(pimoService.getPimoUri(), privacyPref);
		}
		return privacyPref;
	}

	/**
	 * Checks whether an agent (pimo:PersonGroup, pimo:Person or dao:Account) is given access
	 * to a resource via a privacy preference. It checks if it's included in the access space,
	 * but also not excluded (e.g. includes group 'friends' but not 'Angela').
	 * 
	 * @param privacyPref
	 * @param agent
	 * @return true in case the given privacy preference grants access to the agent
	 * @throws PrivacyPreferenceException
	 */
	private boolean hasAccessViaPrivacyPreference(PrivacyPreference privacyPref, Agent agent) throws PrivacyPreferenceException {
		boolean included = false;
		boolean excluded = false;

		try {
			privacyPref = pimoService.get(privacyPref.asURI(), PrivacyPreference.class);
		} catch (NotFoundException e) {
			throw new PrivacyPreferenceException("Cannot check access: privacy preference "+privacyPref+" does not exist.", e);
		}

		if (privacyPref.hasAccessSpace()) {
			ClosableIterator<Node> asIt = privacyPref.getAllAccessSpace_asNode();
			while (asIt.hasNext()) {
				AccessSpace accessSpace = null;
				try {
					accessSpace = pimoService.get(asIt.next().asResource(), AccessSpace.class);
					included = included || isIncluded(accessSpace, agent);
					excluded = excluded || isExcluded(accessSpace, agent);
				} catch (NotFoundException e) {
					throw new PrivacyPreferenceException("Cannot check access: access space "+accessSpace+" does not exist.", e);
				}
			}
		} else {
			throw new PrivacyPreferenceException("Cannot check access: privacy preference "+privacyPref+" does not define an access space.");
		}
		
		return included && !excluded;
	}
	
	private boolean isIncluded(AccessSpace accessSpace, Agent agent) throws PrivacyPreferenceException {
		boolean included = false;
		
		try {
			if (pimoService.isTypedAs(agent, DAO.Account)) {
				// check if the account is explicitly set in the nso:includes
				included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, agent.asResource());
				
				// check if the creator of the di.me account is set in the nso:includes
				if (!included) {
					included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, agent.getCreator().asResource());
				}
	
				// check if the person is member of a group specified in nso:includes
				if (!included) {
					Collection<org.ontoware.rdf2go.model.node.Resource> groups = null;
					groups = pimoService.find(PersonGroup.class).distinct().where(PIMO.hasMember).is(agent.getCreator()).ids();
					for (org.ontoware.rdf2go.model.node.Resource group : groups) {
						included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, group);		
					}
				}
			} else if (pimoService.isTypedAs(agent, PIMO.Person)) {
				// check if the person is explicitly set in the nso:includes
				included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, agent.asResource());
				
				// check if any di.me account of the person is set in the nso:includes
				if (!included) {
					Collection<org.ontoware.rdf2go.model.node.Resource> accounts = null;
					accounts = pimoService.find(Account.class).distinct().where(DAO.accountType).is("di.me").where(NAO.creator).is(agent).ids();
					for (org.ontoware.rdf2go.model.node.Resource account : accounts) {
						included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, account);		
					}
				}
	
				// check if the person is member of a group specified in nso:includes
				if (!included) {
					Collection<org.ontoware.rdf2go.model.node.Resource> groups = null;
					groups = pimoService.find(PersonGroup.class).distinct().where(PIMO.hasMember).is(agent).ids();
					for (org.ontoware.rdf2go.model.node.Resource group : groups) {
						included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, group);		
					}
				}
			} else if (pimoService.isTypedAs(agent, PIMO.PersonGroup)) {
				// check if the group is explicitly set in the nso:includes
				included = included | accessSpace.getModel().contains(accessSpace.asResource(), NSO.includes, agent.asResource());
			}
		} catch (NotFoundException e) {
			throw new PrivacyPreferenceException("Cannot check access: agent "+agent+" seems not to exist.", e);
		}

		return included;
	}
	
	private boolean isExcluded(AccessSpace accessSpace, Agent agent) throws PrivacyPreferenceException {
		boolean excluded = false;
		
		try {
			if (pimoService.isTypedAs(agent, DAO.Account)) {
				// check if the account is explicitly set in the nso:excludes
				excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, agent.asResource());
				
				// check if the creator of the di.me account is set in the nso:excludes
				if (!excluded) {
					excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, agent.getCreator().asResource());
				}
	
				// check if the person is member of a group specified in nso:excludes
				if (!excluded) {
					Collection<org.ontoware.rdf2go.model.node.Resource> groups = null;
					groups = pimoService.find(PersonGroup.class).distinct().where(PIMO.hasMember).is(agent.getCreator()).ids();
					for (org.ontoware.rdf2go.model.node.Resource group : groups) {
						excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, group);		
					}
				}
			} else if (pimoService.isTypedAs(agent, PIMO.Person)) {
				// check if the person is explicitly set in the nso:excludes
				excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, agent.asResource());
				
				// check if any di.me account of the person is set in the nso:excludes
				if (!excluded) {
					Collection<org.ontoware.rdf2go.model.node.Resource> accounts = null;
					accounts = pimoService.find(Account.class).distinct().where(DAO.accountType).is("di.me").where(NAO.creator).is(agent).ids();
					for (org.ontoware.rdf2go.model.node.Resource account : accounts) {
						excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, account.asResource());
					}
				}
	
				// check if the person is member of a group specified in nso:excludes
				if (!excluded) {
					Collection<org.ontoware.rdf2go.model.node.Resource> groups = null;
					groups = pimoService.find(PersonGroup.class).distinct().where(PIMO.hasMember).is(agent).ids();
					for (org.ontoware.rdf2go.model.node.Resource group : groups) {
						excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, group);		
					}
				}
			} else if (pimoService.isTypedAs(agent, PIMO.PersonGroup)) {
				// check if the group is explicitly set in the nso:excludes
				excluded = excluded | accessSpace.getModel().contains(accessSpace.asResource(), NSO.excludes, agent.asResource());
			}
		} catch (NotFoundException e) {
			throw new PrivacyPreferenceException("Cannot check access: agent "+agent+" seems not to exist.", e);
		}

		return excluded;
	}

	@Override
	public Collection<Account> getAllRecipients(AccessSpace accessSpace) {
		Set<Account> include = new HashSet<Account>();
		Set<URI> exclude = new HashSet<URI>();
		
		// get all dao:Account instances to include
		for (Agent agent : accessSpace.getAllIncludes_as().asList()) {
			try {
				if (pimoService.isTypedAs(agent, DAO.Account)) {
					include.add(pimoService.get(agent.asURI(), Account.class));
				} else {
					// extract all pimo:Person instances from the AccessSpace
					Set<Resource> people = new HashSet<Resource>();
					if (pimoService.isTypedAs(agent, PIMO.Person)) {
						people.add(agent);
					} else if (pimoService.isTypedAs(agent, PIMO.PersonGroup)) {
						PersonGroup group = pimoService.get(agent, PersonGroup.class);
						people.addAll(group.getAllMembers_as().asList());
					}
					
					// finds the 'default' di.me account for every person
					for (Resource personId : people) {
						// retrieve the 'default' account for the person, in order words, the data source
						// of the person's default profile (grounding occurrence PersonContact instance).
						Account recipient = pimoService.find(Account.class)
								.distinct()
								.where(DAO.accountType).is("di.me")
								.where(personId, PIMO.groundingOccurrence).is(Query.X)
								.where(Query.X, NIE.dataSource).is(Query.THIS)
								.first();
						if (recipient == null) {
							logger.warn("Person "+personId+" doesn't have a di.me account, " +
									"he/she won't be included in the list of recipients.");
						} else {
							include.add(recipient);
						}
					}
				} 
			} catch (NotFoundException e) {
				logger.error("Could not retrieve agent "+agent+" used in AccessSpace "+accessSpace, e);
			}
		}

		// get all dao:Account to exclude
		for (Agent agent : accessSpace.getAllExcludes_as().asList()) {
			try {
				if (pimoService.isTypedAs(agent, DAO.Account)) {
					exclude.add(agent.asURI());
				} else {
					// extract all pimo:Person instances from the AccessSpace
					Set<Resource> people = new HashSet<Resource>();
					if (pimoService.isTypedAs(agent, PIMO.Person)) {
						people.add(agent);
					} else if (pimoService.isTypedAs(agent, PIMO.PersonGroup)) {
						PersonGroup group = pimoService.get(agent, PersonGroup.class);
						people.addAll(group.getAllMembers_as().asList());
					}

					// all accounts for every person should be excluded
					for (Resource personId : people) {
						Collection<org.ontoware.rdf2go.model.node.Resource> accounts = pimoService.find(Account.class).distinct().where(NAO.creator).is(personId).ids();
						for (org.ontoware.rdf2go.model.node.Resource account : accounts) {
							exclude.add(account.asURI());
						}
					}
				}
			} catch (NotFoundException e) {
				logger.error("Could not retrieve agent "+agent+" used in AccessSpace "+accessSpace, e);
			}
		}

		// do not return recipients that are to be excluded
		Collection<Account> results = new ArrayList<Account>(include.size());
		for (Account account : include) {
			if (!exclude.contains(account.asURI())) {
				results.add(account);
			}
		}
		
		return results;
	}

	private Collection<PrivacyPreference> getBySharedThrough(URI sharedThrough) {
		Collection<org.ontoware.rdf2go.model.node.Resource> ids =
			resourceStore.find(PrivacyPreference.class)
				.distinct()
				.where(PPO.hasAccessSpace).is(Query.X)
				.where(Query.X, NSO.sharedThrough).is(sharedThrough)
				.ids();
		return fetch(ids);
	}
	
	@Override
	public Collection<PrivacyPreference> getByType(PrivacyPreferenceType type) {
		Node label = new PlainLiteralImpl(type.toString());
		return fetch(tripleStore.findStatements(Variable.ANY, Variable.ANY, RDFS.label, label));
	}
	
	@Override
	public Collection<PrivacyPreference> getByType(PrivacyPreferenceType type, URI... properties) {
		Node label = new PlainLiteralImpl(type.toString());
		return fetch(tripleStore.findStatements(Variable.ANY, Variable.ANY, RDFS.label, label), properties);
	}
	
	@Override
	public Collection<PrivacyPreference> getByAgentAndType(Agent agent, PrivacyPreferenceType type) {
		return getByAgentAndType(agent, type, new URI[0]);
	}
	
	@Override
	public Collection<PrivacyPreference> getByAgentAndType(Agent agent, PrivacyPreferenceType type, URI... properties) {
		final Collection<org.ontoware.rdf2go.model.node.Resource> ids =
			resourceStore.find(PrivacyPreference.class)
				.distinct()
				.select(properties)
				// checks the agent is in includes but not in excludes
				.where(RDFS.label).is(type.toString())
				.where(PPO.hasAccessSpace).is(Query.X)
				.where(Query.X, NSO.includes).is(agent)
				.where(Query.X, NSO.excludes).isNot(agent)
				// and checks if the agent is a member of a group which is
				// included in the privacy preference
				.orWhere(RDFS.label).is(type.toString())
				.where(Query.THIS, PPO.hasAccessSpace, Query.X)
				.where(Query.X, NSO.includes, Query.Y)
				.where(Query.Y, RDF.type, PIMO.PersonGroup)
				.where(Query.Y, PIMO.hasMember, agent)
				.ids();
		return fetch(ids);
	}
	
	@Override
	public Collection<Agent> getAgentsWithAccessTo(org.ontoware.rdf2go.model.node.Resource resourceIdentifier) {
		Collection<Agent> agents = new ArrayList<Agent>();
		
		// FIXME this should also check if the agent is not excluded...
		String query = StringUtils.strjoinNL(
				PimoService.SPARQL_PREAMBLE,
				"SELECT DISTINCT ?agent WHERE {",
				"  ?pp a ppo:PrivacyPreference .",
				"  ?pp ppo:hasAccessSpace ?space .",
				"  ?space nso:includes ?agent .",
//				"  {",
//				"    {",
//				"      ?pp ppo:appliesToNamedGraph ?graph .",
//				"      GRAPH ?graph { ?s ?p "+resourceUri.toSPARQL()+" .} .",
//				"    } UNION {",
				"      ?pp ppo:appliesToResource "+resourceIdentifier.toSPARQL()+" .",
//				"    }",
//				"  }",
				"}");
		ClosableIterator<QueryRow> rows = tripleStore.sparqlSelect(query).iterator();
		while (rows.hasNext()) {
			QueryRow row = rows.next();
			try {
				agents.add(resourceStore.get(row.getValue("agent").asURI(), Agent.class));
			} catch (NotFoundException e) {
				logger.warn("Agent "+row.getValue("agent").asURI()+" used in a privacy preference but it does not exist.");
			}
		}
		rows.close();
		
		return agents;
	}

	private Collection<PrivacyPreference> fetch(ClosableIterator<Statement> statements, URI... properties) {
		// retrieve distinct ids of all subjects 
		Set<org.ontoware.rdf2go.model.node.Resource> ids = new HashSet<org.ontoware.rdf2go.model.node.Resource>(); 
		while (statements.hasNext()) {
			ids.add(statements.next().getSubject());
		}
		statements.close();

		// fetch the privacy preferences
		Collection<PrivacyPreference> collection = new ArrayList<PrivacyPreference>();
		for (org.ontoware.rdf2go.model.node.Resource identifier : ids) {
			collection.add(fetch(identifier, properties));
		}
		
		return collection;
	}
	
	private PrivacyPreference fetch(org.ontoware.rdf2go.model.node.Resource identifier, URI... properties) {
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.fetch(tripleStore, sinkModel, identifier, true, true, null, new URI[]{ PPO.hasAccessSpace }, false, properties);
		return new PrivacyPreference(sinkModel, identifier, false);
	}

	private Collection<PrivacyPreference> fetch(Collection<org.ontoware.rdf2go.model.node.Resource> identifiers, URI... properties) {
		Collection<PrivacyPreference> results = new ArrayList<PrivacyPreference>(identifiers.size());
		for (org.ontoware.rdf2go.model.node.Resource identifier : identifiers) {
			Model sinkModel = RDF2Go.getModelFactory().createModel().open();
			ModelUtils.fetch(tripleStore, sinkModel, identifier, true, true, null, new URI[]{ PPO.hasAccessSpace }, false, properties);
			results.add(new PrivacyPreference(sinkModel, identifier, false));
		}
		return results;
	}

}