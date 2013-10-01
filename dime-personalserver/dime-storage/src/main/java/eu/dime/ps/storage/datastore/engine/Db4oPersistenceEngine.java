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

package eu.dime.ps.storage.datastore.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.io.FileStorage;

import eu.dime.ps.storage.datastore.types.DimeBinary;
import eu.dime.ps.storage.datastore.types.PersistentDimeObject;
import eu.dime.ps.storage.util.CMSInitHelper;

/**
 * 
 * @author marcel
 *
 */
public class Db4oPersistenceEngine {

	Logger logger = Logger.getLogger(Db4oPersistenceEngine.class);
	
	private String CMS_ROOT_FOLDER;
	private String BLOB_FOLDER;
	
	private static int recursionCounter = 0;
	
	private static HashMap<String, ObjectServer> serverMap = new HashMap<String, ObjectServer>();
	
	public Db4oPersistenceEngine() {
		CMS_ROOT_FOLDER = CMSInitHelper.getCMSFolder();
		BLOB_FOLDER = CMSInitHelper.getBlobFolder();

	}

	public Db4oPersistenceEngine(String path){
		CMS_ROOT_FOLDER = path;
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
		//config.common().callConstructors(false);
		//config.file().freespace().useRamSystem();
		config.file().storage(new FileStorage());
		config.file().blockSize(8);
		config.common().diagnostic().addListener(new DiagnosticToConsole());
		config.common().bTreeNodeSize(100);
		config.common().callbacks(false);
		config.common().objectClass(DimeBinary.class).objectField("hash").indexed(true);
		config.common().objectClass(PersistentDimeObject.class).objectField("id").indexed(true);

		config.file().recoveryMode(true);
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
