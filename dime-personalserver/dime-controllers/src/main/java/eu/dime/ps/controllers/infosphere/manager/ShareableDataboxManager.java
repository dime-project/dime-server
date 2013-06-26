package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nfo.DataContainer;

/**
 * Implementation of ShareableManager for databoxes, complaint with the
 * privacy preferences of the user.
 * 
 * @author Ismael Rivera
 */
public class ShareableDataboxManager extends ShareableManagerBase<DataContainer> implements ShareableManager<DataContainer> {

	public static final List<URI> SHAREABLE_DATABOX_PROPERTIES;
	static {
		SHAREABLE_DATABOX_PROPERTIES = new ArrayList<URI>(5);
		SHAREABLE_DATABOX_PROPERTIES.add(NAO.created);
		SHAREABLE_DATABOX_PROPERTIES.add(NAO.lastModified);
		SHAREABLE_DATABOX_PROPERTIES.add(NAO.prefLabel);
		SHAREABLE_DATABOX_PROPERTIES.add(NAO.prefSymbol);
		SHAREABLE_DATABOX_PROPERTIES.add(NIE.hasPart);
	};

	private DataboxManager databoxManager;

	public void setDataboxManager(DataboxManager databoxManager) {
		this.databoxManager = databoxManager;
	}
	
	@Override
	public boolean exist(String resourceId) throws InfosphereException {
		return databoxManager.exist(resourceId);
	}

	@Override
	public DataContainer get(String databoxId, String requesterId) throws InfosphereException {
		DataContainer databox = databoxManager.get(databoxId, SHAREABLE_DATABOX_PROPERTIES);
		checkAuthorized(databox, requesterId);

		// sets the databox as sharedWith the requester user
		setSharedWith(databox, requesterId);
		
		return databox;
	}

	@Override
	public Collection<DataContainer> getAll(String accountId, String requesterId) throws InfosphereException {
		Collection<DataContainer> all = databoxManager.getAllByCreator(databoxManager.getMe().asURI(), SHAREABLE_DATABOX_PROPERTIES);
		
		// filtering out those the user is not authorized to access
		Collection<DataContainer> authorized = filterAuthorized(all, requesterId);
	
		// sets the databoxes as sharedWith the requester user
		setSharedWith(authorized, requesterId);

		return authorized;
	}
	
	@Override
	public void add(DataContainer databox, String sharedBy, String sharedWith) throws InfosphereException {

		// force the type to be only nfo:DataContainer
		databox.getModel().removeStatements(databox, RDF.type, Variable.ANY);
		databox.getModel().addStatement(databox, RDF.type, NFO.DataContainer);
		
		// remove any information about access spaces, etc. if provided
		databox.getModel().removeStatements(databox, PPO.hasAccessSpace, Variable.ANY);
		databox.getModel().removeStatements(Variable.ANY, RDF.type, PPO.AccessSpace);
		databox.getModel().removeStatements(Variable.ANY, RDF.type, NSO.AccessSpace);
		databox.getModel().removeStatements(Variable.ANY, NSO.includes, Variable.ANY);
		databox.getModel().removeStatements(Variable.ANY, NSO.excludes, Variable.ANY);
		
		// adding the databox
		super.add(databox, sharedBy, sharedWith);
	}
	
}