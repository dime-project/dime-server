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

package eu.dime.ps.semantic.connection;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.impl.TripleStoreImpl;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.privacy.impl.PrivacyPreferenceServiceImpl;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.search.Searcher;
import eu.dime.ps.semantic.search.impl.SparqlSearcher;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.PersonMatchingService;
import eu.dime.ps.semantic.service.context.LiveContextServiceImpl;
import eu.dime.ps.semantic.service.context.SnapshotBasedStrategy;
import eu.dime.ps.semantic.service.impl.PersonMatchingConfiguration;
import eu.dime.ps.semantic.service.impl.PersonMatchingServiceImpl;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * A connection (session) with an RDF store.
 * <b>Note:</b> By default a Connection object is in auto-commit mode, thus every operation automatically commits any changes. auto-commit mode is the only strategy supported, but subclasses of Connection may implement other strategies.
 *
 * @author Ismael Rivera
 */
public class Connection {

	private static final Logger logger = LoggerFactory.getLogger(Connection.class);

	private boolean closed = false;
	
	private final String name;
	private final Repository repository;

	private TripleStore tripleStore;
	private ResourceStore resourceStore;
	private PimoService pimoService;
	private PrivacyPreferenceService privacyPreferenceService;
	private LiveContextService liveContextService;
	private PersonMatchingService personMatchingService;
	private Searcher searcher;
	
	public Connection(String name, Repository repository) {
		this.name = name;
		this.repository = repository;
	}
	
	/**
	 * Retrieves this Connection object's name
	 * @return the connection's name or null if not set
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves whether this Connection object is in writable mode
	 * @return true if this Connection object is writable; false otherwise
	 * @throws RepositoryException if a RDF repository access error occurs
	 */
	public boolean isWritable() throws RepositoryException {
		return repository.isWritable();
	}
	
	/**
	 * A new connection needs to be initialize when used for the first time.
	 * Internally, it configures the RDF services for a given user, using a
	 * unique username.
	 * 
	 * @param username
	 * @throws RepositoryException
	 */
	public void initialize(String username) throws RepositoryException {
		try {
			pimoService = new PimoService(username, username, getTripleStore());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}
	
	public TripleStore getTripleStore() throws RepositoryException {
		if (tripleStore == null)
			tripleStore = new TripleStoreImpl(name, repository);
		return tripleStore;
	}
	
	public ResourceStore getResourceStore() throws RepositoryException {
		if (resourceStore == null)
			resourceStore = new ResourceStoreImpl(getTripleStore());
		return resourceStore;
	}
	
	public PimoService getPimoService() throws RepositoryException {
		if (pimoService == null) {
			try {
				pimoService = new PimoService(getTripleStore());
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
		return pimoService;
	}
	
	public PrivacyPreferenceService getPrivacyPreferenceService() throws RepositoryException {
		if (privacyPreferenceService == null)
			privacyPreferenceService = new PrivacyPreferenceServiceImpl(getPimoService());
		return privacyPreferenceService;
	}

	public LiveContextService getLiveContextService() throws RepositoryException {
		if (liveContextService == null)
			liveContextService = new LiveContextServiceImpl(getPimoService(), SnapshotBasedStrategy.class);
		return liveContextService;
	}
	
	public PersonMatchingService getPersonMatchingService() throws RepositoryException {
		if (personMatchingService == null) {
			personMatchingService = new PersonMatchingServiceImpl(getResourceStore());
		}
		return personMatchingService;
	}
	
	public Searcher getSearcher() throws RepositoryException {
		if (searcher == null) {
			searcher = new SparqlSearcher(getTripleStore());
		}
		return searcher;
	}

	/**
	 * Releases this Connection object's RDF repository and other resources immediately
	 * instead of waiting for them to be automatically released.
	 * @throws RepositoryException if a RDF repository access error occurs
	 */
	public void close() throws RepositoryException {
		closed = true;
//		tripleStore.close();
//		pimoService.close();
//		liveContextService.close();
	}
	
	/**
	 * Retrieves whether this Connection object has been closed. A connection is closed if
	 * the method close has been called on it or if certain fatal errors have occurred. This
	 * method is guaranteed to return true only when it is called after the method
	 * Connection.close has been called.
	 * @return true if this Connection object is closed; false if it is still open
	 */
	public boolean isClosed() {
		return closed;
	}
	
}
