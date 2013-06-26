package eu.dime.ps.storage.datastore.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.db4o.ObjectContainer;

import eu.dime.ps.storage.datastore.DataStore;
import eu.dime.ps.storage.datastore.engine.Db4oPersistenceEngine;
import eu.dime.ps.storage.util.CMSInitHelper;
/**
 * 
 * @author marcel
 *
 */
public class DataStoreProvider {

	private Logger logger = Logger.getLogger(DataStoreProvider.class);
	
	private static HashMap <Long, DataStore> dataStores = new HashMap<Long, DataStore>();
	private Db4oPersistenceEngine db4oPersistenceEngine;
	
	public void setDb4oPersistenceEngine(Db4oPersistenceEngine db4oPersistenceEngine) {
		this.db4oPersistenceEngine = db4oPersistenceEngine;
	}

	/** 
	 * retrieves a tenantStore
	 * @param tenantId
	 * @param create If true a new tenantStore will be created if not existing
	 * @return
	 */
	public DataStore getTenantStore(long tenantId, boolean create){
		DataStore dataStore = dataStores.get(tenantId);
		if (dataStore == null && create){
			dataStore = createTenantStore(tenantId);
			//dataStores.put(tenantId, dataStore);
		}
		return dataStore;
	}

	private DataStore createTenantStore(long tenantId) {
		ObjectContainer container = db4oPersistenceEngine.openConnection(String.valueOf(tenantId));
		DataStore store = new DataStoreImpl(container, tenantId);
		return store;
	}

	public void closeTenantStore(long tenantId){
		DataStore store = dataStores.get(tenantId);
		if (store!=null){
			store.close();
		}
		dataStores.remove(tenantId);
		db4oPersistenceEngine.closeConnection(String.valueOf(tenantId));
	}

	public boolean deleteTenantStore(long tenantId) {
		closeTenantStore(tenantId);
		dataStores.remove(tenantId);
		String path = CMSInitHelper.getCMSFolder() + File.separator + tenantId;
		try {
			FileUtils.deleteDirectory(new File(path));
		} catch (IOException e) {
			logger.error("Could not delete directory.",e);
			return false;
		}
		return true;
	}

	public DataStore getTenantStore(long id) {
		return getTenantStore(id, true);
	}
	
}
