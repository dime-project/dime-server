package eu.dime.ps.storage.datastore.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.io.FileStorage;
import eu.dime.ps.storage.util.CMSInitHelper;

/**
 * 
 * @author marcel
 *
 */
public class Db4oPersistenceEngine {

	Logger logger = Logger.getLogger(Db4oPersistenceEngine.class);
	
	private static String CMS_ROOT_FOLDER;
	private static String BLOB_FOLDER;
	
	private static int recursionCounter = 0;
	
	private static HashMap<String, ObjectServer> serverMap = new HashMap<String, ObjectServer>();
	
	public Db4oPersistenceEngine() {
		CMS_ROOT_FOLDER = CMSInitHelper.getCMSFolder();
		BLOB_FOLDER = CMSInitHelper.getBlobFolder();

	}
	
		public Db4oPersistenceEngine(String folder){
			CMS_ROOT_FOLDER = folder;
			BLOB_FOLDER = CMSInitHelper.getBlobFolder();
		}

	public ObjectContainer openConnection(String dbName){

		if (serverMap.containsKey(dbName)){
			return serverMap.get(dbName).openClient();
		}
		
		String path = CMS_ROOT_FOLDER + File.separator + dbName + File.separator;
		
		if (recursionCounter>10){
			throw new DatabaseFileLockedException("Could not open database file: <"+path + "> Database file locked.");
		}
		
		new File(path).mkdirs();
		
		ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		config.common().weakReferences(false);
		config.file().freespace().useRamSystem();
		config.file().storage(new FileStorage());
		try {
			config.file().blobPath(path + "."+ BLOB_FOLDER);
		} catch (IOException e) {
			logger.error("Could not configure default (temp) blob path.", e);
		}
		
		ObjectServer server = null;
		Exception tmpException = null;
		try {
			server = Db4oClientServer.openServer(
					config, path + "db4o.yap", 0);
		} catch (DatabaseFileLockedException e) {
			tmpException = e;
			//probably concurrent accesses at the same time...
			// wait a bit and try again
			try {
				Thread.sleep(10);
				recursionCounter++;
				ObjectContainer c = openConnection(dbName);
				recursionCounter--;
				return c;
			} catch (InterruptedException e1) {
				return null;
			}
		}
		
		serverMap.put(dbName, server);
		
		return server.openClient();
	}
	
	public void closeConnection(String dbName){
		serverMap.get(dbName).close();
		serverMap.remove(dbName);
	}

	
	public ObjectServer getServerConnection(long tenantId) {
		return serverMap.get(String.valueOf(tenantId));
	}
	
	public ObjectContainer getClientConnection (long tenantId) {
		return serverMap.get(String.valueOf(tenantId)).openClient();
	}

}
