package eu.dime.ps.controllers.trustengine.impl;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.notifications.user.UNRefToItem;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
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
	
	public void setConnectionProvider(ConnectionProvider conectionProvider){
		this.connectionProvider = conectionProvider;
	}
	
	protected ResourceStore getResourceStore() throws RepositoryException{
		long tenant = TenantContextHolder.getTenant();
		if (tenant > 0){
			return connectionProvider.getConnection(String.valueOf(tenant)).getResourceStore();
		}
		return null;
	}
	
	protected PimoService getPimoService() throws RepositoryException {
		long tenant = TenantContextHolder.getTenant();
		if (tenant > 0){
			return connectionProvider.getConnection(String.valueOf(tenant)).getPimoService();
		}
		return null;
	}
	

	protected PrivacyPreferenceService getPrivPrefService() throws RepositoryException {
		long tenant = TenantContextHolder.getTenant();
		if (tenant > 0){
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
}
