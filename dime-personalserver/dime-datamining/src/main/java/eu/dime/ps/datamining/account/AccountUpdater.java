package eu.dime.ps.datamining.account;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

public interface AccountUpdater<T extends Resource> {

	void update(URI accountUri, String path, T resource)
			throws AccountIntegrationException;

	void update(URI accountUri, String path, Collection<T> resources)
			throws AccountIntegrationException;

}
