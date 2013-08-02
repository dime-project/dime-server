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

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.ForbiddenException;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.dto.Type;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.PrivacyPreferenceException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Provides a base implementation of the methods add/update/delete for adding shared resources
 * into the user's RDF store. Subclasses may override the methods to add extra functionality or
 * validations.
 * 
 * @author Ismael Rivera
 */
public abstract class ShareableManagerBase<T extends Resource> extends ConnectionBase implements ShareableManager<T> {

	private static final Logger logger = LoggerFactory.getLogger(ShareableManagerBase.class);

	/**
	 * Notifier manager is required for sending user notifications when resources are shared.
	 */
	protected NotifierManager notifierManager;

	public ShareableManagerBase() {}
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}

	protected void onCreate(T resource) {
		sendUserNotification(resource, UNRefToItem.OPERATION_SHARED);
	}

	protected void onUpdate(T resource) {
		// no-op
	}
	
	protected void onDelete(T resource) {
		sendUserNotification(resource, UNRefToItem.OPERATION_UNSHARED);
	}
	
	/**
	 * Sends a 'RefToItem' user notification to the UI.
	 * 
	 * @param resource item of the notification
	 * @param operation operation occurred to the item
	 */
	private void sendUserNotification(T resource, String operation) {
		Long tenant = TenantHelper.getCurrentTenantId();

		Type type = Type.get(resource);
		if (type == null) {
			logger.warn("Type is undefined for resource " + resource + ". User notification of shared item couldn't be sent.");
			return;
		}
		
		String name;                
                URI nameUri=NAO.prefLabel;

                //get name of resource
                Node prefLabel= ModelUtils.findObject(resource.getModel(), resource, nameUri);
                if (prefLabel!=null){
                    name = prefLabel.asLiteral().getValue();
                }else{
                    logger.error("Cannot find label in resource: " + resource) ;
                    name = "Name not found in model";
		}



                String userId;
                Node creator = ModelUtils.findObject(resource.getModel(), resource, NAO.creator);
		if (creator == null) {
                    logger.warn("Creator is undefined for resource " + resource + ". UserId set to @me");
                    userId="@me";
		}else{
                    userId=creator.toString();
                }
		
		UNRefToItem unRefToItem = new UNRefToItem(resource.toString(), name, type.toString(),  userId, operation);
		UserNotification notification = new UserNotification(tenant, unRefToItem);
		
		try {
			notifierManager.pushInternalNotification(notification);
                        //FIXME send a system notification as well
		} catch (NotifierException e) {
			logger.error("Error while pushing user notification ["+notification+"].", e);
		}
	}

	@Override
	public void add(T resource, String sharedBy, String sharedWith) throws InfosphereException {
		PimoService pimoService = getPimoService();
		
		// a shared resource is not directly related to the account, instead the
		// resource was shared by (nso:sharedBy) a person, and an occurrence
		// nco:Contact of the person comes (nie:dataSource) from that account

		URI creator = null;
		Account sharedByAccount = null;
		Account sharedWithAccount = null;
		try {
			sharedByAccount = pimoService.get(new URIImpl(sharedBy), Account.class);
			sharedWithAccount = pimoService.get(new URIImpl(sharedWith), Account.class);
			creator = sharedByAccount.getCreator_asNode().asURI();

			if (creator == null) {
				throw new InfosphereException("Resource "+resource+" cannot added: creator not found for account "+sharedBy+".");
			}
			
			if (creator.equals(pimoService.getUserUri())) {
				// account is one of owner of the PIM, should use the normal infosphere managers
				throw new InfosphereException("Resource "+resource+" cannot added: account "+sharedBy+
						" belongs to the PIM owner, cannot use shareable managers.");
			}
		} catch (NotFoundException e) {
			throw new InfosphereException("Resource "+resource+" cannot added: " + e.getMessage(), e);
		}

		// set the creator of the item
		resource.getModel().addStatement(resource, NAO.creator, creator);

		// set the data source of the item
		resource.getModel().addStatement(resource, NIE.dataSource, sharedByAccount);
		
		// set the person's account which shared the resource (sender)
		resource.getModel().addStatement(resource, NSO.sharedBy, sharedByAccount);
		
		// set the person's account which the resource was shared with (recipient)
		resource.getModel().addStatement(resource, NSO.sharedWith, sharedWithAccount);
		
		// relate person with resource if it's a privacy preference
		if (resource instanceof PrivacyPreference) {
			resource.getModel().addStatement(creator, NSO.hasPrivacyPreference, resource);
		}

		try {
			pimoService.create(resource);
			onCreate(resource);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Cannot add resource "+resource+" for account "+sharedBy, e);
		}
	}
	
	@Override
	public void update(T resource, String sharedBy, String sharedWith) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();

		try {
			assertExistence(resource, sharedBy);
			resourceStore.update(resource, true);
			onUpdate(resource);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot update resource "+resource+" for account "+sharedBy+": "+e.getMessage(), e);
		}
	}
	
	@Override
	public void remove(T resource, String sharedBy) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();

		try {
			assertExistence(resource, sharedBy);
			resourceStore.remove(resource);
			onDelete(resource);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot remove resource "+resource+" for account "+sharedBy+": "+e.getMessage(), e);
		}
	}

	private void assertExistence(T resource, String accountId) throws NotFoundException, InfosphereException {
		ResourceStore resourceStore = getResourceStore();

		URI accountUri = new URIImpl(accountId);
		Account account = resourceStore.get(accountUri, Account.class);
		Person person = resourceStore.find(Person.class)
				.where(PIMO.occurrence).is(Query.X)
				.where(Query.X, NIE.dataSource).is(account)
				.first();

		if (person == null) {
			throw new InfosphereException("Cannot update resource "+resource+" for account "+accountId+": there's no person associated with it.");
		} else {
			Resource sharedItem = resourceStore.get(resource);
			if (!sharedItem.getModel().contains(resource, NSO.sharedBy, accountUri)) {
				throw new InfosphereException("Cannot update resource "+resource+" for account "+accountId
						+": this resource has not been shared previously through that account.");
			}
		}
	}

	/**
	 * Checks if a resource can be accessed by a given person.
	 * 
	 * @param resource the resource that wants to be accessed
	 * @param requesterId account identifier of the person accessing the resource
	 * @throws InfosphereException
	 */
	protected void checkAuthorized(T resource, String requesterId) throws NotFoundException, ForbiddenException, InfosphereException {
		PimoService pimoService = getPimoService();
		PrivacyPreferenceService privacyPreferenceService = getPrivacyPreferenceService();

		try {
			// privacy preference service can deal with just the account, checking if it belongs to a person, etc.
			Account account = pimoService.get(new URIImpl(requesterId), Account.class);
			if (!privacyPreferenceService.hasAccessTo(resource, account)) {
				throw new ForbiddenException(account+" is not authorized to access "+resource);
			}
		} catch (PrivacyPreferenceException e) {
			throw new InfosphereException("Cannot check authorization [item="+resource+", agent="+requesterId+"]: "+e.getMessage(), e);
		}
	}
	
	/**
	 * Given a collection of resources, it filters out the resource that a person is not
	 * authorized to have access to.
	 * 
	 * @param resources collection of all resources
	 * @param requesterId account identifier of the person accessing the resources 
	 * @return the collection of authorized resources, after filtering out those which cannot be accessed
	 */
	protected Collection<T> filterAuthorized(Collection<T> resources, String requesterId) {
		Collection<T> filtered = new ArrayList<T>(resources.size());
		for (T item : resources) {
			try {
				checkAuthorized(item, requesterId);
			} catch (NotFoundException e) {
				logger.debug("Filtering out resource "+item+", not accessible by account "+requesterId+": " + e.getMessage(), e);
				continue; // skips rest of the logic if not authorized
			} catch (ForbiddenException e) {
				logger.debug("Filtering out resource "+item+", not accessible by account "+requesterId+": " + e.getMessage(), e);
				continue; // skips rest of the logic if not authorized
			} catch (InfosphereException e) {
				logger.debug("Filtering out resource "+item+", not accessible by account "+requesterId+": " + e.getMessage(), e);
				continue; // skips rest of the logic if not authorized
			}
			
			// if authorized, add preference to result list
			filtered.add(item);
		}
		return filtered;
	}
	
	/**
	 * Sets a resource as shared with a person.
	 * 
	 * @param resource resource shared
	 * @param requesterId account which creator is the person the resource was shared with
	 * @throws InfosphereException
	 */
	protected void setSharedWith(T resource, String requesterId) throws InfosphereException {
		TripleStore tripleStore = getTripleStore();
		PimoService pimoService = getPimoService();
		
//		try {
//			Account account = pimoService.get(new URIImpl(requesterId), Account.class);
//			if (!account.hasCreator()) {
//				throw new InfosphereException("Couldn't check accesibility: creator not found for account "+requesterId);
//			}

			// set resource as sharedWith the account
			URI account = new URIImpl(requesterId);
			if (!tripleStore.containsStatements(pimoService.getPimoUri(), resource, NSO.sharedWith, account)) {
				tripleStore.addStatement(pimoService.getPimoUri(), resource, NSO.sharedWith, account);
			}
//		} catch (NotFoundException e) {
//			logger.warn("Cannot set "+resource+" shared with information: "+e.getMessage(), e);
//		}
	}
	
	/**
	 * Same as {@link setSharedWith(T, String)}, except a collection of resources has been shared.
	 * 
	 * @param resources
	 * @param requesterId
	 * @throws InfosphereException
	 */
	protected void setSharedWith(Collection<T> resources, String requesterId) throws InfosphereException {
		for (T resource : resources) {
			setSharedWith(resource, requesterId);
		}
	}
	
}
