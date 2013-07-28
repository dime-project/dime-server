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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

public abstract class AdvisoryBase {
	
	Logger logger = Logger.getLogger(getClass());

	protected ConnectionProvider connectionProvider;
	
	@Autowired
	protected NotifierManager notifierManager;
	
	public void setNotifyManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	public void setConnectionProvider(ConnectionProvider connectionProvider){
		this.connectionProvider = connectionProvider;
	}
	
	protected ResourceStore getResourceStore() throws RepositoryException{
		Long tenant = TenantContextHolder.getTenant();
		if (tenant != null){
			return connectionProvider.getConnection(String.valueOf(tenant)).getResourceStore();
		}
		return null;
	}
	
	protected PimoService getPimoService() throws RepositoryException {
		Long tenant = TenantContextHolder.getTenant();
		if (tenant != null){
			return connectionProvider.getConnection(String.valueOf(tenant)).getPimoService();
		}
		return null;
	}
	

	protected PrivacyPreferenceService getPrivPrefService() throws RepositoryException {
		Long tenant = TenantContextHolder.getTenant();
		if (tenant != null){
			return connectionProvider.getConnection(String.valueOf(tenant)).getPrivacyPreferenceService();
		}
		return null;
	}
	
	protected void notifyUI(String operation, String type, String guid, String name){
		UNRefToItem refToItem = new UNRefToItem(guid, name, type,  "@me", operation);
		UserNotification notification = new UserNotification(TenantContextHolder.getTenant(), refToItem);
		try {
			notifierManager.pushInternalNotification(notification);
		} catch (NotifierException e) {
			logger.warn("Failed to push UserNotificaton.", e);
		}
	}
	
	/**
	 * Retrieves all the items contained in a databox.
	 * 
	 * @param databoxUri
	 * @return
	 * @throws RepositoryException 
	 */
	protected Collection<String> getAllItemsInDataboxAsString(URI databoxUri)
			throws NotFoundException, RepositoryException {
		PrivacyPreference databox = getResourceStore().get(databoxUri, PrivacyPreference.class);
		
		// only the URIs of the things are shared in the databox
		Collection<Resource> itemUris = databox.getAllAppliesToResource_as().asList();
		
		// loading all the metadata from the triple store
		Collection<String> items = new ArrayList<String>();
		for (Resource item : itemUris) {
			items.add(item.toString());
		}
		return items;
	}
	
	/**
	 * Retrieves all the items contained in a databox.
	 * 
	 * @param databoxUri
	 * @return
	 * @throws RepositoryException 
	 */
	protected Collection<DataObject> getAllItemsInDatabox(URI databoxUri)
			throws NotFoundException, RepositoryException {
		PrivacyPreference databox = getResourceStore().get(databoxUri, PrivacyPreference.class);
		
		// only the URIs of the things are shared in the databox
		Collection<Resource> itemUris = databox.getAllAppliesToResource_as().asList();
		
		// loading all the metadata from the triple store
		Collection<DataObject> items = new ArrayList<DataObject>();
		for (Resource item : itemUris) {
			try {
				items.add(this.getResourceStore().get(item.asURI(), DataObject.class));
			} catch (NotFoundException e) {
				logger.warn("Item " + item.asURI() + " is in databox " + databox.asURI() + " but it is does not exist.");
			}
		}
		return items;
	}
}
