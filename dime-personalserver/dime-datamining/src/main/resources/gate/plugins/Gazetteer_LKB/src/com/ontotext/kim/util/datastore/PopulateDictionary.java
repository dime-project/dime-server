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

package com.ontotext.kim.util.datastore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import com.ontotext.kim.client.KIMRuntimeException;
import com.ontotext.kim.client.semanticrepository.QueryResultListener;
import com.ontotext.kim.model.AliasCacheImpl;
import com.ontotext.kim.model.Options;
import com.ontotext.kim.semanticrepository.UnmanagedRepositoryFactory;

public class PopulateDictionary {

	private static class TempAliasCache extends AliasCacheImpl {
		public TempAliasCache(QueryResultListener.Feed dataFeed) {
			super(Options.SENSITIVE);
			List<String> el = Collections.emptyList(); 
			File dictionaryPath;
			try {
				dictionaryPath = new File("cache").getCanonicalFile();
				prepareOutputFolder(dictionaryPath);
				initCache(el, dataFeed, dictionaryPath, true);
			} catch (IOException e) {
				throw new KIMRuntimeException(e.getMessage());
			}			
		}

		private void prepareOutputFolder(File cachePath) {
			try {

				if (cachePath.exists() && !cachePath.isDirectory())
					FileUtils.forceDelete(cachePath);
				if (!cachePath.exists())
					FileUtils.forceMkdir(cachePath);
				File marker = new File(cachePath, "marker.flag");
				marker.createNewFile();
			} catch (IOException ex) {
				// Exceptions are logged by caller.
				throw new RuntimeException(
						"Could not initialize the storage folder for KIM. Check that KIM has write access to the folder" + 
						cachePath.toString() + 
						" or has the required permissions to create it. Note the exact cause below", ex);
			}

		}		
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		UnmanagedRepositoryFactory factory = new UnmanagedRepositoryFactory();
		Repository rep = factory.createRepository(new File(args.length > 1 ? args[1] : "owlim.ttl"));
		rep.initialize();
		try {
			RepositoryConnection conn = rep.getConnection();
			String queryString = FileUtils.readFileToString(new File(args[0]).getAbsoluteFile());
			Constructor<TupleQueryResultHandler> wrapperFactory = null;
			QueryResultListener.Feed dataFeed = new RepositoryFeed(conn, wrapperFactory, queryString);
			new TempAliasCache(dataFeed);
		}
		finally {
			rep.shutDown();
		}
	}

}
