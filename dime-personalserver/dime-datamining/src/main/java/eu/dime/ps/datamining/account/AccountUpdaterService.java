package eu.dime.ps.datamining.account;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

/**
 * Provides means for updating the data gathered from different social
 * services such as LinkedIn, Twitter, etc.
 * 
 * @author Ismael Rivera
 */
public interface AccountUpdaterService {

	public static final String ACTION_RESOURCE_NEW = "eu.dime.ps.controllers.account.RESOURCE_NEW";
	public static final String ACTION_RESOURCE_MODIFY = "eu.dime.ps.controllers.account.RESOURCE_MODIFY";
	public static final String ACTION_RESOURCE_DELETE = "eu.dime.ps.controllers.account.RESOURCE_DELETE";

	<T extends Resource> void updateResources(URI accountUri, String path, Collection<T> resources)
			throws AccountIntegrationException;
	
	void removeResources(URI accountUri)
			throws AccountIntegrationException;
	
	void removeResources(URI accountUri, String path)
			throws AccountIntegrationException;

}
