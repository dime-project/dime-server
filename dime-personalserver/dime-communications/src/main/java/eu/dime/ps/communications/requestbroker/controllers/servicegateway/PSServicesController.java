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

package eu.dime.ps.communications.requestbroker.controllers.servicegateway;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import eu.dime.commons.dto.Entry;
import eu.dime.commons.dto.ExternalNotificationDTO;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.commons.dto.Response.Status;
import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.communications.utils.Base64encoding;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.UserManager;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.AccountManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableDataboxManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableFileManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableLivePostManager;
import eu.dime.ps.controllers.infosphere.manager.ShareableProfileManager;
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.dto.ProfileCard;
import eu.dime.ps.gateway.ServiceGateway;
import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.gateway.exception.AttributeNotSupportedException;
import eu.dime.ps.gateway.exception.InvalidLoginException;
import eu.dime.ps.gateway.exception.ServiceException;
import eu.dime.ps.gateway.exception.ServiceNotAvailableException;
import eu.dime.ps.gateway.service.ResourceAttributes;
import eu.dime.ps.gateway.service.internal.DimeServiceAdapter;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.storage.entities.User;

/**
 * Allows other di.me servers (or other services) to communicate with a di.me
 * personal server.
 * 
 * Local testing should use a URL like this:
 * https://localhost:8443/dime-communications
 * /api/services/get/notifications/@me/123
 * 
 * @author mplanaguma
 * @author Sophie.Wrobel
 * 
 */
@Controller
@Path("/services/{said}/")
public class PSServicesController {

	private static final Logger logger = LoggerFactory.getLogger(PSServicesController.class);

	private ServiceGateway serviceGateway;
	private TenantManager tenantManager;
	private AccountManager accountManager;
	private CredentialStore credentialStore;

	@Autowired
	private ShareableDataboxManager shareableDataboxManager;

	@Autowired
	private ShareableFileManager shareableFileManager;

	@Autowired
	private ShareableLivePostManager shareableLivePostManager;

	@Autowired
	private ShareableProfileManager shareableProfileManager;

	@Autowired
	private UserManager userManager;

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setShareableDataboxManager(
			ShareableDataboxManager shareableDataboxManager) {
		this.shareableDataboxManager = shareableDataboxManager;
	}

	public void setShareableFileManager(
			ShareableFileManager shareableFileManager) {
		this.shareableFileManager = shareableFileManager;
	}

	public void setShareableLivePostManager(
			ShareableLivePostManager shareableLivePostManager) {
		this.shareableLivePostManager = shareableLivePostManager;
	}

	public void setServiceGateway(ServiceGateway serviceGateway) {
		this.serviceGateway = serviceGateway;
	}

	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	public void setCredentialStore(CredentialStore credentialStore) {
		this.credentialStore = credentialStore;
	}

	public void setShareableProfileManager(ShareableProfileManager shareableProfileManager) {
		this.shareableProfileManager = shareableProfileManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@POST
	@Path("set/notification")
	@Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response setNotification(Request<ExternalNotificationDTO> json,
			@PathParam("said") String said) throws InfosphereException,
			RepositoryStorageException, UnsupportedEncodingException {

		ExternalNotificationDTO jsonNotification = null;
		
		try {
			// Deserialize notification
			jsonNotification = json.getMessage().getData().getEntries().iterator().next();

			String saidNameReceiver = jsonNotification.getSaidReciever();

			// Check Tenant and said
			if (!saidNameReceiver.equals(said)) {
				return Response.badRequest();
			} else {
				TenantContextHolder.setTenant(this.tenantManager.getByAccountName(saidNameReceiver).getId());
			}

			if (ExternalNotificationDTO.OPERATION_SHARE.equals(jsonNotification.getOperation())) {
				return this.requestSharedObject(jsonNotification);
			}
		} catch (Exception e) {
			// Catch all - no failing allowed!
			return Response.serverError("Unknown Error! Details: " + e.getMessage(), e);
		} finally {
			// clearing up tenant from TenantContextHolder
			TenantContextHolder.clear();
		}

		return Response.okEmpty();
	}

	private Response requestSharedObject(ExternalNotificationDTO jsonNotification)
			throws UnsupportedEncodingException {

		String saidNameSender = jsonNotification.getSaidSender();
		String saidNameReceiver = jsonNotification.getSaidReciever();
		String saidUriReceiver = null;
		String saidUriSender = null;
		String password = null;

		// Get URI Receiver
		try {
			saidUriReceiver = credentialStore.getUriForName(saidNameReceiver);
		} catch (NoResultException e) {
			logger.error("Could not find URI for own SAIDname. Received notification may be corrupt: " + e.getMessage(), e);
			return Response.badRequest(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Very bad unknown error! Received notification may be corrupt: " + e.getMessage(), e);
			return Response.serverError(e.getMessage(), e);
		}

		// Get URI Sender
		saidUriSender = credentialStore.getUriForAccountName(saidNameReceiver, saidNameSender);

		if (saidUriSender != null) {
			try {
				password = credentialStore.getPassword(saidUriReceiver,
						saidUriSender, TenantHelper.getCurrentTenant());
			} catch (NoResultException e) {
				logger.info("Could not find password to authenticate request [sender=" + saidUriSender
						+ ", receiver=" + saidUriReceiver + "]. A password will be requested to access the other PS.");
			}
		}

		if (password == null || password.equals("")) {
			try {
				// get credentials from other PS
				saidUriSender = this.requestCredentialsAndProfile(
						saidNameSender, saidNameReceiver, saidUriReceiver);
			} catch (Exception e) {
				// FIXME
				logger.warn("Catched exception when trying to retrieve credentials. Maybe no problem. ;-)", e);
			}
		} else {
			Token token = new Token(saidNameReceiver, password);
			try {
				// saidNameSender is unknown, thus the profile is requested for this sender
				requestProfile(token, saidNameSender, saidUriSender, saidUriReceiver);
			} catch (ServiceNotAvailableException e) {
				logger.error("Error obtaining profile: " + e.getMessage(), e);
				return Response.serverError(e.getMessage(), e);
			} catch (AttributeNotSupportedException e) {
				logger.error("Error obtaining profile: " + e.getMessage(), e);
				return Response.badRequest(e.getMessage(), e);
			} catch (InfosphereException e) {
				logger.error("Error obtaining profile: " + e.getMessage(), e);
				return Response.serverError(e.getMessage(), e);
			}
		}

		// Get shared object

		UNRefToItem unEntry = null;

		try {
			unEntry = getAndSaveSharedObject(jsonNotification, saidUriSender, saidUriReceiver);
		} catch (AttributeNotSupportedException e) {
			logger.warn("Error obtaining the resources shared: " + e.getMessage());
			return Response.badRequest(e.getMessage(), e);
		} catch (ServiceNotAvailableException e) {
			logger.warn("Error obtaining the resources shared: " + e.getMessage());
			return Response.serverError(e.getMessage(), e);
		} catch (InvalidLoginException e) {
			logger.warn("Error obtaining the resources shared: " + e.getMessage());
			return Response.badRequest(e.getMessage(), e);
		} catch (InfosphereException e) {
			logger.warn("Error obtaining the resources shared: " + e.getMessage());
			return Response.serverError(e.getMessage(), e);
		} catch (ServiceException e) {
			return Response.status(Status.get(Integer.parseInt(e.getDetailCode())), e.getMessage(), e);
		}

		// Notify to UI
		try {
			Long tenant = tenantManager.getByAccountName(saidNameReceiver).getId();
			UserNotification notification = new UserNotification(tenant, unEntry);
			// TODO remove when the Manager will do it
			// this.notifierManager.pushInternalNotification(notification);

		} catch (Exception e) {
			return Response.serverError("Notifier Exception: " + e.getMessage(), e);
		}

		// TODO return NotificationDTO
		return Response.okEmpty();
	}
	
	private UNRefToItem getAndSaveSharedObject(
			ExternalNotificationDTO jsonNotification, String saidUriSender,
			String saidUriReceiver) throws UnsupportedEncodingException,
			AttributeNotSupportedException, ServiceNotAvailableException,
			InvalidLoginException, InfosphereException, ServiceException {

		String saidNameSender = jsonNotification.getSaidSender();
		String objectSharedType = ResourceAttributes.ATTR_RESOURCE;
		Class returnType = null;
		String path = null;

		// Notification RefToItem
		UNRefToItem unEntry = new UNRefToItem();
		unEntry.setOperation(UNRefToItem.OPERATION_SHARED);
		unEntry.setUserID(saidUriSender);

		DimeServiceAdapter adapter = serviceGateway.getDimeServiceAdapter(saidNameSender);

		Entry entry = jsonNotification.getElement();
		String entryType = entry.getType();

		if (DimeInternalNotification.ITEM_TYPE_RESOURCE.equals(entryType)) {
			objectSharedType = ResourceAttributes.ATTR_RESOURCE;
			returnType = FileDataObject.class;
		} else if (DimeInternalNotification.ITEM_TYPE_DATABOX.equals(entryType)) {
			objectSharedType = ResourceAttributes.ATTR_DATABOX;
			returnType = DataContainer.class;
		} else if (DimeInternalNotification.ITEM_TYPE_LIVEPOST.equals(entryType)) {
			objectSharedType = ResourceAttributes.ATTR_LIVEPOST;
			returnType = LivePost.class;
		} else if (DimeInternalNotification.ITEM_TYPE_PROFILE.equals(entryType)) {
			objectSharedType = ResourceAttributes.ATTR_PROFILE;
			returnType = ProfileCard.class;
		}

		if (objectSharedType == null) {
			throw new InfosphereException("Shared resource's [" + entry.getGuid() + "] type '" + entryType + "' is unknown or not supported.");
		} else {
			// Add type on the Notification
			unEntry.setType(objectSharedType);

			// inceding the id to send as a path variable without '/' caracter
			String resourceID = entry.getGuid();
			String resourceIDBase64Encoded = Base64encoding.encode(resourceID);

			// Add guid on the Notification
			unEntry.setGuid(resourceID);

			// building the path
			path = "/" + objectSharedType + "/"
					+ jsonNotification.getSender() + "/"
					+ resourceIDBase64Encoded;

			// Sending the call to obtain the Resource
			Collection<org.ontoware.rdfreactor.schema.rdfs.Resource> resources = adapter
					.get(saidUriSender, saidUriReceiver, path, returnType, TenantHelper.getCurrentTenant());
			logger.info("GET request to: " + path + " to retrieve shared resource: " + resources.size() + " objects received.");

			// Saving the Resource
			for (org.ontoware.rdfreactor.schema.rdfs.Resource resource : resources) {

				// if DataBox
				if (resource instanceof DataContainer) {
					logger.info("adding a shared Databox from: " + saidUriSender);

					DataContainer databox = (DataContainer) resource;
					ClosableIterator<Node> files = databox.getAllPart_asNode();
					Collection<FileDataObject> fileResources = new ArrayList<FileDataObject>();
					while (files.hasNext()) {
						String fileId = files.next().asURI().toString();
						String fileIDBase64Encoded = Base64encoding.encode(fileId);

						path = "/resource/" + saidNameSender + "/" + fileIDBase64Encoded;
						Collection<FileDataObject> dbFiles = adapter.get(
								saidUriSender, saidUriReceiver, path,
								FileDataObject.class, TenantHelper.getCurrentTenant());

						fileResources.addAll(dbFiles);
					}
					files.close();

					// adds databox and all files metadata to the managers
					if (shareableDataboxManager.exist(databox.toString())) {
						logger.info("updating a databox from: " + saidUriSender+" to: "+saidUriReceiver);
						shareableDataboxManager.update(databox, saidUriSender, saidUriReceiver);
					} else {
						logger.info("adding a databox from: " + saidUriSender+" to: "+saidUriReceiver);
						shareableDataboxManager.add(databox, saidUriSender, saidUriReceiver);
					}

					for (FileDataObject fdo : fileResources) {
						if (shareableFileManager.exist(fdo.toString())) {
							logger.info("updating a shared File or Resource from: " + saidUriSender);
							shareableFileManager.update(fdo, saidUriSender, saidUriReceiver);
						} else {
							logger.info("adding a shared File or Resource from: " + saidUriSender);
							shareableFileManager.add(fdo, saidUriSender, saidUriReceiver);
						}
					}
				}

				// if File
				if (resource instanceof FileDataObject) {
					FileDataObject fdo = (FileDataObject) resource;
					if (shareableFileManager.exist(resource.toString())) {
						logger.info("updating a shared File or Resource from: " + saidUriSender);
						shareableFileManager.update(fdo, saidUriSender, saidUriReceiver);
					} else {
						logger.info("adding a shared File or Resource from: " + saidUriSender);
						shareableFileManager.add(fdo, saidUriSender, saidUriReceiver);
					}
				}

				// if LivePost
				if (resource instanceof LivePost) {
					LivePost livepost = (LivePost) resource;
					if(shareableLivePostManager.exist(resource.toString())){
						logger.info("updating a shared LivePost from: " + saidUriSender);	
						shareableLivePostManager.update(livepost, saidUriSender, saidUriReceiver);
					} else{
						logger.info("adding a shared LivePost from: " + saidUriSender);
						shareableLivePostManager.add(livepost, saidUriSender, saidUriReceiver);
					}
				}

				// if Profile
				if (resource instanceof PersonContact) {
					PersonContact profile = (PersonContact) resource;
					if (shareableProfileManager.exist(resource.toString())){
						logger.info("updating a shared Profile from: " + saidUriSender);
						shareableProfileManager.update(profile, saidUriSender, saidUriReceiver);
					} else {
						logger.info("adding a shared Profile from: " + saidUriSender);
						shareableProfileManager.add(profile, saidUriSender, saidUriReceiver);
					}
				}
			}
			
			return unEntry;
		}
	}

	private PersonContact requestProfile(Token token, String saidNameSender,
			String saidUriSender, String saidUriReceiver)
					throws ServiceNotAvailableException,
					AttributeNotSupportedException, InfosphereException {

		DimeServiceAdapter adapter = serviceGateway.getDimeServiceAdapter(saidNameSender);

		// try to find Account instance for sender URI, if not found request the
		// profile information to sender PS for that account
		Account account = null;
		try {
			if (accountManager.exist(saidUriSender)) {
				account = accountManager.get(saidUriSender);
			}
		} catch (InfosphereException e) {
			// this can happen if accountManager.get() cannot find the account,
			// but in this case we just want account to be null
		}
		
		if (account == null) {
			logger.info("Sender is unknown: no di.me account (and therefore a profile) has been found with " +
					"URI " + saidUriSender + ". The profile shared through that account will be retrieved from the sender PS.");

			PersonContact profile = adapter.getProfile(saidNameSender, token);
			if (profile != null) {
				userManager.addProfile(new URIImpl(saidUriSender), profile);
				return profile;
			} else {
				logger.warn("failed to add Profile for User " + saidNameSender
						+ " to the infosphere. May be already there");
			}
		}
		
		return null;
	}

	private String requestCredentialsAndProfile(String saidNameSender,
			String saidNameReceiver, String saidUriReceiver)
					throws RepositoryStorageException, InfosphereException,
					AttributeNotSupportedException, ServiceNotAvailableException, ServiceException {

		String saidUriSender = null;
		DimeServiceAdapter adapter = serviceGateway.getDimeServiceAdapter(saidNameSender);
		Token token = adapter.getUserToken(saidNameReceiver); // <-- http request to other PS for credentials

		// HTTP request to other PS for the profile
		User user = null;
		if (token != null) {
			try {
				// add contact & create guest account
				saidUriSender = "urn:uuid:" + UUID.randomUUID();
				user = userManager.add(saidNameSender, new URIImpl(saidUriSender));
			} catch (Exception e) {
				logger.info("Could not create user. Maybe already exists. But will still try to update credentials");
				saidUriSender = credentialStore.getUriForAccountName(saidNameReceiver, saidNameSender);
			}
			credentialStore.updateCredentialsForAccount(saidUriReceiver,
					saidUriSender, saidNameSender, token.getSecret(), TenantHelper.getCurrentTenant());
		}

		// request profile from sender
		PersonContact profile = requestProfile(token, saidNameSender, saidUriSender, saidUriReceiver);
		if (profile != null) {
			adapter.confirmToken(token);
		}
		
		// do authenticated post to other PS to confirm that credentials are
		// stored
		// FIXME [Marcel] what happens if confirmToken returns false? shouldn't
		// we do
		// something here? some simple recovery mechanishm or at least showing
		// an error
		// to the user??

		return user.getAccountUri();
	}

}
