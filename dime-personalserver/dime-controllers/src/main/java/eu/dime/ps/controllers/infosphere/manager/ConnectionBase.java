package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;

import org.openrdf.repository.RepositoryException;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceService;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.search.Searcher;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * 
 * @author Ismael Rivera
 */
public abstract class ConnectionBase {

	protected ConnectionProvider connectionProvider;

	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	
	private Connection getConnection() throws InfosphereException, RepositoryException {
		Long tenantId = TenantContextHolder.getTenant();
		if (tenantId == null) {
			throw new InfosphereException("No tenant identifier was found in TenantContextHolder: the connection could not be established");
		}
		return connectionProvider.getConnection(tenantId.toString());
	}
	
	protected TripleStore getTripleStore() throws InfosphereException {
		try {
			return getConnection().getTripleStore();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve TripleStore object: "+e.getMessage(), e);
		}
	}

	protected ResourceStore getResourceStore() throws InfosphereException {
		try {
			return getConnection().getResourceStore();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve ResourceStore object: "+e.getMessage(), e);
		}
	}

	protected PimoService getPimoService() throws InfosphereException {
		try {
			return getConnection().getPimoService();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve PimoService object: "+e.getMessage(), e);
		}
	}
	
	protected PrivacyPreferenceService getPrivacyPreferenceService() throws InfosphereException {
		try {
			return getConnection().getPrivacyPreferenceService();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve PrivacyPreferenceService object: "+e.getMessage(), e);
		}
	}
	
	protected LiveContextService getLiveContextService() throws InfosphereException {
		try {
			return getConnection().getLiveContextService();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve LiveContextService object: "+e.getMessage(), e);
		}
	}

	protected Searcher getSearcher() throws InfosphereException {
		try {
			return getConnection().getSearcher();
		} catch (RepositoryException e) {
			throw new InfosphereException("Cannot retrieve Searcher object: "+e.getMessage(), e);
		}
	}

}
