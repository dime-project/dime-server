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

package eu.dime.ps.semantic.rdf.impl;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.rdf.RepositoryFactory;
import eu.dime.ps.semantic.rdf.inferencer.SimpleInferencingSail;

/**
 * Factory which creates Sesame memory repositories, with disk-based persistence
 * capabilities. It allows to delay writes for optimizing performance, which by
 * default is set to 5 seconds.
 * 
 * @author Ismael Rivera
 */
public class SesameMemoryRepositoryFactory implements RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(SesameMemoryRepositoryFactory.class);

	private final static int DEFAULT_SYNC_DELAY = 5000; // write delay in ms
	
	private final ConcurrentMap<String, Repository> pool = new ConcurrentHashMap<String, Repository>();
	
	private final String baseDir;
	private final int syncDelay;
	
	public SesameMemoryRepositoryFactory() {
		this.baseDir = null;
		this.syncDelay = 0;
	}

	public SesameMemoryRepositoryFactory(String repositoryDir) {
		this(repositoryDir, DEFAULT_SYNC_DELAY);
	}

	public SesameMemoryRepositoryFactory(String repositoryDir, int syncDelay) {
		String appDataDir = System.getProperty("dime.appdata.basedir");
		String homeDir = System.getProperty("user.home");
		
		if (appDataDir != null) {
			this.baseDir = appDataDir + repositoryDir + File.separator;
		} else if (homeDir != null) {
			this.baseDir = homeDir + File.separator + ".dime" + File.separator + repositoryDir + File.separator;
		} else {
			throw new IllegalArgumentException("di.me needs a directory to store user's data. " +
					"Please specify a directory in the 'dime.appdata.basedir' property (e.g. /path/to/dir/), " +
					"or at least 'user.home' must be set to be used as default (e.g. $HOME/.dime/), used as " +
					"default if 'dime.appdata.basedir' is not specified.");
		}
		
		this.syncDelay = syncDelay;
	}
	
	@Override
	public Repository get(String name) throws RepositoryException {
		Repository repository = pool.get(name);
		if (repository == null) {
			repository = getRepository(name, true);
			pool.putIfAbsent(name, repository);
		}
		return repository;
	}
	
	@Override
	public boolean remove(String name) throws RepositoryException {
		boolean deleted = false;
		
		// shutdown & remove repository from pool 
		Repository repository = pool.get(name);
		if (repository != null) {
			logger.info("Shutting down repository '" + name + "'");
			repository.shutDown();
			deleted = pool.remove(name) != null;
		}

		// delete directory with persistent data
		if (this.baseDir != null) {
			File path = new File(baseDir + name);
			if (path.exists()) {
				try {
					deleted = path.delete();
					if (deleted) {
						logger.info("Removed RDF repository '"+name+"' ["+path.getAbsolutePath()+"]");
					}
				} catch (SecurityException e) {
					throw new RepositoryException("Repository '"+name+"' couldn't be deleted.", e);
				}
			}
		}
		
		return deleted;
	}

	private synchronized Repository getRepository(String name, boolean inferencing) throws RepositoryException {
		Repository repository = null;
		MemoryStore store = null;
		
		if (this.baseDir == null) {
			store = new MemoryStore();
		} else {
			File path = new File(this.baseDir + name);
			logger.info("RDF repository at " +path.getAbsolutePath()+" [syncDelay="+syncDelay+", inferencing="+inferencing+"]");
			
			store = new MemoryStore(path);
			store.setSyncDelay(this.syncDelay);
		}
		
		if (inferencing) {
			// ForwardChainingRDFSInferencer is buggy: sometimes gets stuck in an almost infinite loop, and it's
			// also not thread-safe; we'll use a crappy inferencer which just gets the job done for what we need
//			repository = new SailRepository(new ForwardChainingRDFSInferencer(store));
			repository = new SailRepository(new SimpleInferencingSail(store));
		} else {
			repository = new SailRepository(store);
		}

		repository.initialize();
		
		return repository;
	}
	
}
