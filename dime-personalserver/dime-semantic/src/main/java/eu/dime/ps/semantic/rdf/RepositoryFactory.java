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

package eu.dime.ps.semantic.rdf;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

public interface RepositoryFactory {
	
	public Repository get(String name) throws RepositoryException;
	
	public boolean remove(String name) throws RepositoryException;
    
//    private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);
//
//    public Repository getSesameInMemoryRepository() throws RepositoryException {
//    	return getSesameInMemoryRepository(false);
//    }
//
//    public Repository getSesameInMemoryRepository(boolean inferencing) throws RepositoryException {
//		Repository repository = null;
//		if (inferencing) {
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
//		} else {
//		    repository = new SailRepository(new MemoryStore());
//		}
//		repository.initialize();
//		return repository;
//    }
//
//    public Repository getSesameInMemoryRepository(File dataDir) throws RepositoryException {
//    	return getSesameInMemoryRepository(dataDir, false);
//    }
//
//    public Repository getSesameInMemoryRepository(File dataDir, boolean inferencing)
//	    throws RepositoryException {
//		Repository repository = null;
//		if (inferencing) {
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore()));
//		} else {
//		    repository = new SailRepository(new MemoryStore());
//		}
//		repository.initialize();
//		return repository;
//    }
//
//    public Repository getSesameNativeRepository(String dataDir) throws RepositoryException {
//    	return getSesameNativeRepository(dataDir);
//    }
//
//    public Repository getSesameNativeRepository(String dataDir, boolean inferencing)
//	    throws RepositoryException {
//	
//		// dataDir relative path in user home 
//		File path = new File(System.getProperty("user.home") + File.separator + dataDir);
//		logger.info("RDF repository at " +path.getAbsolutePath()+" [inferencing="+inferencing+"]");
//
//		Repository repository = null;
//		if (inferencing) {
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(path)));
//		} else {
//		    repository = new SailRepository(new NativeStore(path));
//		}
//
//		repository.initialize();
//		
//		return repository;
//    }
//
//    public Repository getSesameNativeRepository(String dataDir, String indexes)
//    	    throws RepositoryException {
//    	return getSesameNativeRepository(dataDir, indexes, false);
//    }
//    
//    public Repository getSesameNativeRepository(String dataDir, String indexes, boolean inferencing)
//	    throws RepositoryException {
//	
//		// dataDir relative path in user home 
//		File path = new File(System.getProperty("user.home") + File.separator + dataDir);
//		logger.info("RDF repository at " +path.getAbsolutePath()+" [indexes="+indexes+", inferencing="+inferencing+"]");
//		
//		Repository repository = null;
//		if (inferencing) {
//		    repository = new SailRepository(new ForwardChainingRDFSInferencer(new NativeStore(path, indexes)));
//		} else {
//		    repository = new SailRepository(new NativeStore(path, indexes));
//		}
//
//		repository.initialize();
//		
//		return repository;
//	    }
//	
//    public Repository getSesameRemoteRepository(String sesameServer, String repositoryID)
//	    throws RepositoryException, RepositoryConfigException {
//	
//    	RepositoryManager manager = RemoteRepositoryManager.getInstance(sesameServer);
//		Repository repository = manager.getRepository(repositoryID);
//		repository.initialize();
//		return repository;
//    }
//
//    /**
//     * 
//     * Example:
//     * <ul>
//     * <li>jdbcUrl: jdbc:virtuoso://localhost:1111
//     * <li>username: dba
//     * <li>password: dba
//     * </ul>
//     * 
//     * @param jdbcUrl
//     *            jdbc:virtuoso://[host]:[port]
//     *            <ul>
//     *            <li>host: specifies the name of the machine that is running
//     *            the database
//     *            <li>port: specifies the port to use for communication with the
//     *            host machine
//     *            </ul>
//     * @param username
//     *            the user name or role that should be used to authenticate with
//     *            the RDBMS
//     * @param password
//     * @return
//     * @throws RepositoryException
//     */
//    public Repository getVirtuosoRepository(String jdbcUrl, String username, String password)
//	    throws RepositoryException {
//		Repository repository = new VirtuosoRepository(jdbcUrl, username, password);
//		repository.initialize();
//		return repository;
//    }

}
