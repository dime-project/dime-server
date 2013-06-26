package eu.dime.ps.semantic.rdf.impl;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import eu.dime.ps.semantic.rdf.RepositoryFactory;

/**
 * Factory to create remote Sesame HTTP repositories. Remote repositories are exposed through a SPARQL
 * endpoint, and they are configured using the Sesame OpenRDF Workbench.
 * 
 * @see http://www.openrdf.org/doc/sesame2/users/ch06.html
 * @author Ismael Rivera
 */
public class SesameHTTPRepositoryFactory implements RepositoryFactory {
    
    private final String serverURL;
    
    public SesameHTTPRepositoryFactory(String serverURL) {
    	this.serverURL = serverURL;
    }

    @Override
    public Repository get(String name) throws RepositoryException {
    	Repository repository = null;
		repository = new HTTPRepository(this.serverURL, name); 
		repository.initialize();
		return repository;
    }
    
    @Override
    public boolean remove(String name) throws RepositoryException {
    	throw new RepositoryException("Remote HTTP repositories can only be removed from the OpenRDF Workbench.");
    }

}
