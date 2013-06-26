package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;

/**
 * Manager for databox management.
 * 
 * @author Ismael Rivera
 */
public interface DataboxManager extends InfoSphereManager<DataContainer> {
	
	public Collection<DataContainer> getAllByCreator(URI creatorId)
			throws InfosphereException;

	public Collection<DataContainer> getAllByCreator(URI creatorId, List<URI> properties)
			throws InfosphereException;

	public Collection<DataObject> getDataboxItems(String databoxId)
			throws InfosphereException;

}