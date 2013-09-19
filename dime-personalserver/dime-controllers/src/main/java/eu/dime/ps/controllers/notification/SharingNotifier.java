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

package eu.dime.ps.controllers.notification;

import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.jfix.util.Arrays;



import eu.dime.ps.controllers.eventlogger.exception.EventLoggerException;
import eu.dime.ps.controllers.eventlogger.manager.LogEventManager;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.dto.ShareableType;
import eu.dime.ps.dto.Type;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidDataException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.AttributeMap;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.BroadcastManager;
import eu.dime.ps.semantic.BroadcastReceiver;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Deals with the generation of notifications for sharing resources between di.me
 * accounts/users, but also with external accounts/services.
 * 
 * It's actively monitoring changes in the user's privacy preferences and resources shared
 * through them, in order to notify users of shared resources, and keep them up to date. 
 * 
 * @author Ismael Rivera
 */
public class SharingNotifier implements BroadcastReceiver {

	private static final Logger logger = LoggerFactory.getLogger(SharingNotifier.class);

	private NotifierManager notifierManager = null;
	private ConnectionProvider connectionProvider = null;
	private ServiceGateway serviceGateway = null;

	private final ModelFactory modelFactory = new ModelFactory();

	private LogEventManager logEventManager;

	@Autowired
	public void setLogEventManager(LogEventManager logEventManager) {
		this.logEventManager = logEventManager;
	}

	public SharingNotifier() {
		BroadcastManager.getInstance().registerReceiver(this);
	}

	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	@Override
	public void onReceive(Event event) {

		// do nothing if notification manager was not set
		if (notifierManager == null) {
			logger.warn("NotifierManager bean not set for SharingNotifier: " +
					"notifications for sharing actions won't be sent!");
			return;
		}

		Tenant tenant = TenantHelper.getTenant(event.getTenantId());

		Connection connection = null;
		ResourceStore resourceStore = null;
		PimoService pimoService = null;
		PrivacyPreferenceService ppoService = null;

		try {
			// reject events with no data (resource's metadata)
			org.ontoware.rdfreactor.schema.rdfs.Resource resource = event.getData();
			if (resource == null) {
				return;
			}

			// reject events of non-allowed actions
			String[] allowedActions = new String[] { Event.ACTION_RESOURCE_ADD, Event.ACTION_RESOURCE_MODIFY, Event.ACTION_RESOURCE_DELETE };
			if (!Arrays.contains(allowedActions, event.getAction())) {
				return;
			}

			// reject events of non-allowed types
			URI[] allowedTypes = new URI[] { PPO.PrivacyPreference, DLPO.LivePost, NIE.DataObject, PIMO.PersonGroup };
			boolean typeAllowed = false;
			for (URI allowedType : allowedTypes) {
				if (event.is(allowedType)) {
					typeAllowed = true;
					break;
				}
			}
			if (!typeAllowed) {
				return;
			}

			// prepare connection to RDF repository
			connection = connectionProvider.getConnection(event.getTenant());
			resourceStore = connection.getResourceStore();
			pimoService = connection.getPimoService();
			ppoService = connection.getPrivacyPreferenceService();

			if (event.is(PPO.PrivacyPreference)
					&& (Event.ACTION_RESOURCE_ADD.equals(event.getAction())
							|| Event.ACTION_RESOURCE_MODIFY.equals(event.getAction()))) {

				// loading updated metadata for privacy preference directly from the store
				PrivacyPreference preference = ppoService.get(resource.asURI());

				org.ontoware.rdfreactor.schema.rdfs.Resource sharedItem = null;
				try {
					sharedItem = getSharedItem(preference, ppoService, pimoService);
				} catch (NotFoundException e) {
					logger.error("Privacy preference " + preference + " could not be shared.", e);
					return;
				}
				
				sendNotifications(connection, preference, sharedItem, true, tenant);
			} else if (event.is(PPO.PrivacyPreference)
					&& Event.ACTION_RESOURCE_DELETE.equals(event.getAction())) {

				// TODO a privacy preference is deleted, should we notify the others PSs so they
				// can remove them as well?

			} else if (event.is(DLPO.LivePost)
					&& Event.ACTION_RESOURCE_MODIFY.equals(event.getAction())) {

				// loading privacy preference for livepost and notify recipients
				try {
					LivePost livePost = resourceStore.get(resource.asURI(), LivePost.class);
					PrivacyPreference preference = ppoService.getForLivePost(livePost);

					// if no privacy preference is found = livepost has not been shared yet 
					if (preference != null) {
					
						sendNotifications(connection, preference, livePost, true, tenant);
					}
				} catch (NotFoundException e) {
					logger.error("A 'resource modified' event was received for " + resource.asURI() + 
							" (livepost), but it could not be found in the RDF store", e);
				} 

			} else if (event.is(NIE.DataObject)
					&& Event.ACTION_RESOURCE_MODIFY.equals(event.getAction())) {

				// loading privacy preference for data object and notify recipients
				try {
					DataObject dataObject = resourceStore.get(resource.asURI(), DataObject.class);
					PrivacyPreference preference = ppoService.getForDataObject(dataObject);

					// if no privacy preference is found = data object has not been shared yet 
					if (preference != null) {						
						sendNotifications(connection, preference, dataObject, true, tenant);
					}
				} catch (NotFoundException e) {
					logger.error("A 'resource modified' event was received for " + resource.asURI() + 
							" (livepost), but it could not be found in the RDF store", e);
				}

			} else if (event.is(PIMO.PersonGroup)
					&& Event.ACTION_RESOURCE_MODIFY.equals(event.getAction())) {

				// when adding a person to a group, the person also receives a notification for all
				// items that are shared with the group

				Collection<Resource> ppUris = resourceStore.find(PrivacyPreference.class)
						.distinct()
						.where(PPO.hasAccessSpace).is(Query.X)
						.where(Query.X, NSO.includes).is(resource.asURI())
						.ids();

				for (Resource ppUri : ppUris) {
					PrivacyPreference preference = ppoService.get(ppUri.asURI());

					org.ontoware.rdfreactor.schema.rdfs.Resource sharedItem = null;
					try {
						sharedItem = getSharedItem(preference, ppoService, pimoService);
					} catch (NotFoundException e) {
						logger.error("Privacy preference " + preference + " could not be shared.", e);
						return;
					}
					
					sendNotifications(connection, preference, sharedItem, false, tenant);
				}
			}

			// TODO what should we do when a shared resource (livepost, dataobject, etc.) gets deleted??

		} catch (RepositoryException e) {
			logger.error("Cannot connect with the RDF repository '" + Long.parseLong(event.getTenant()) + "': " + e, e);
			return;
		}
	}

	// returns the item shared through a specific privacy preference
	private org.ontoware.rdfreactor.schema.rdfs.Resource getSharedItem(PrivacyPreference preference,
			PrivacyPreferenceService ppoService, PimoService pimoService) throws NotFoundException {

		// 'databox' and 'profilecard' privacy preferences are converted to nfo:DataContainer and nco:PersonContact and shared
		// 'livepost', 'file' privacy preferences are not shared, but the referenced resources are (ppo:appliesToResource)

		PrivacyPreferenceType ppType = ppoService.getType(preference);
		org.ontoware.rdfreactor.schema.rdfs.Resource resource = null;

		if (PrivacyPreferenceType.DATABOX.equals(ppType)) {
			resource = modelFactory.getNFOFactory().createDataContainer(preference.asURI());

			// add to data container all resources from the databox
			for (Resource part : preference.getAllAppliesToResource_as().asList()) {
				resource.getModel().addStatement(resource, NIE.hasPart, part);
			}
		} else if (PrivacyPreferenceType.PROFILECARD.equals(ppType)) {
			resource = modelFactory.getNCOFactory().createPersonContact(preference.asURI());

			// add to profile all profile attributes from the profile card
			for (Resource attribute : preference.getAllAppliesToResource_as().asList()) {
				ClosableIterator<Statement> relationIt = pimoService.getTripleStore().findStatements(pimoService.getPimoUri(), preference, Variable.ANY, attribute);
				if (relationIt.hasNext()) {
					resource.getModel().addStatement(resource, relationIt.next().getPredicate(), attribute);
				} else {
					logger.error("Couldn't share profile attribute " + attribute + " from " + preference + ": it is not used in any PersonContact instance.");
				}
				relationIt.close();
			}
		} else if (PrivacyPreferenceType.FILE.equals(ppType)) {
			if (preference.getAllAppliesToResource().hasNext()) {
				resource = pimoService.get(preference.getAllAppliesToResource().next(), DataObject.class);

				// FIXME filter out some metadata not meant to be shared?

			} else {
				logger.warn("Privacy preference " + preference + " is not refering to any item: nothing to be shared.");
				return null;
			}
		} else if (PrivacyPreferenceType.LIVEPOST.equals(ppType)) {
			if (preference.getAllAppliesToResource().hasNext()) {
				resource = pimoService.get(preference.getAllAppliesToResource().next(), LivePost.class);

				// FIXME filter out some metadata not meant to be shared?

			} else {
				logger.warn("Privacy preference " + preference + " is not refering to any item: nothing to be shared.");
				return null;
			}
		} else {
			logger.error("Privacy preference " + preference + " of type " + ppType + " is not yet supported by the sharing notifier.");
			return null;
		}

		// specify the creator
		resource.getModel().addStatement(resource, NAO.creator, pimoService.getUserUri());

		return resource;
	}

	private void sendNotifications(Connection connection, PrivacyPreference preference, 
			org.ontoware.rdfreactor.schema.rdfs.Resource sharedItem, boolean notifyAll,
			Tenant localTenant) throws RepositoryException {
		PimoService pimoService = connection.getPimoService();
		PrivacyPreferenceService ppoService = connection.getPrivacyPreferenceService();

		ShareableType itemType = ShareableType.get(preference);
		if (itemType == null) {
			logger.error("Cannot determine item type for privacy preference " + preference);
			return;
		}

		if (preference.hasAccessSpace()) {
			ClosableIterator<Node> accessSpaceIt = preference.getAllAccessSpace_asNode();
			while (accessSpaceIt.hasNext()) {
				Resource accessSpaceUri = accessSpaceIt.next().asResource();
				AccessSpace accessSpace = null;
				try {
					accessSpace = pimoService.get(accessSpaceUri, AccessSpace.class);

					Account sender = accessSpace.getSharedThrough();
					if (sender == null) {
						logger.error("AccessSpace " + accessSpaceUri + " must specify a valid 'sharedThrough' dao:Account " +
								"(di.me account of the sender). No notifications will be sent for this AccessSpace.");
						continue;
					}

					// notify all people included in the privacy preference
					// account URIs of the recipients (specific people accounts to receive the shared item)
					Collection<Account> recipients = ppoService.getAllRecipients(accessSpace);

					// sends notifications to all recipients
					for (Account recipient : recipients) {
						Node creator = recipient.getCreator_asNode();

						// item only shared with persons which haven't previously received the item.
						if (creator == null) {
							logger.error("Account recipient " + recipient + " does not specify a creator. A creator is required " +
									"to check if the resource was already shared with that person. No notification will be sent.");
							continue;
						} else if (!notifyAll && sharedItem.getModel().contains(sharedItem, NSO.sharedWith, creator)) {
							logger.debug("Not sending 'sharing' notification to " + recipient + ", the resource has been already " +
									"shared with that account");
							continue;
						}

						DimeExternalNotification notification = new DimeExternalNotification(Long.parseLong(connection.getName()));
						notification.setOperation(DimeExternalNotification.OP_SHARE);
						notification.setItemID(sharedItem.toString());
						notification.setItemType(itemType.toString());
						notification.setSender(sender.toString());
						notification.setTarget(recipient.toString());

						logger.debug("Pushing notification " + notification.toString());
						try {
							notifierManager.pushExternalNotification(notification);
						} catch (NotifierException e) {
							logger.error("Cannot push notification: " + notification, e);
						}
					}
					
					//store the operation on the server logs data
					try {
						logEventManager.setLog("share", itemType.toString(),localTenant);
					} catch (EventLoggerException e) {
						logger.error("Share operation log could not be stored",e);
					}

					// send the shared item to external accounts (posting to Facebook, tweeting on Twitter, etc.)
					Collection<Account> accounts = pimoService.find(Account.class)
							.distinct()
							.select(DAO.accountType)
							.where(preference, PPO.hasAccessSpace, Query.X)
							.where(Query.X, NSO.includes, Query.THIS)
							.results();
					for (Account account : accounts) {

						// discard all di.me accounts
						if (account.hasAccountType() && account.getAccountType().equals(DimeServiceAdapter.NAME)) {
							continue;
						}

						try {
							ServiceAdapter serviceAdapter = serviceGateway.getServiceAdapter(account.asURI().toString(), localTenant);
							if (sharedItem instanceof PersonContact) {
								serviceAdapter.set(AttributeMap.PROFILE_MYDETAILS, sharedItem);
							} else if (sharedItem instanceof LivePost) {
								serviceAdapter.set(AttributeMap.LIVEPOST_ALLMINE, sharedItem);
							} else {
								logger.warn(sharedItem+" could not be shared with account " + account + ". This resource is not yet supported for sharing.");
							}
						} catch (ServiceNotAvailableException e) {
							logger.error("Could not share item with account " + account + " of type " + account.getAccountType(), e);
						} catch (ServiceAdapterNotSupportedException e) {
							logger.error("Could not share item with account " + account + " of type " + account.getAccountType(), e);
						} catch (InvalidDataException e) {
							logger.error("Could not share item with account " + account + " of type " + account.getAccountType(), e);
						} catch (AttributeNotSupportedException e) {
							logger.error("Could not share item with account " + account + " of type " + account.getAccountType(), e);
						}
					}

				} catch (NotFoundException e) {
					logger.error("AccessSpace " + accessSpaceUri + " specified in PrivacyPreference " + preference + " could not be found.", e);
				}
			}
			accessSpaceIt.close();
		} else {
			logger.warn("Privacy preference " + preference + " doesn't specify any access space.");
		}
	}

}
