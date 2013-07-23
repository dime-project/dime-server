package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link DataboxManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class DataboxManagerImpl extends PrivacyPreferenceManager<DataContainer> implements DataboxManager {

	private static final Logger logger = LoggerFactory.getLogger(DataboxManagerImpl.class);

	@Override
	public Collection<DataContainer> getAll() throws InfosphereException {
		return getAllByCreator(null, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<DataContainer> getAll(List<URI> properties) throws InfosphereException {
		return getAllByCreator(null, properties);
	}
	
	public Collection<DataContainer> getAllByCreator(URI creatorId)
			throws InfosphereException {
		return getAllByCreator(creatorId, new ArrayList<URI>(0));
	}

	public Collection<DataContainer> getAllByCreator(URI creatorId, List<URI> properties)
			throws InfosphereException {
		URI pimGraph = getPimoService().getPimoUri();
		ResourceStore resourceStore = getResourceStore();
		
		Query<DataContainer> query = resourceStore
				.find(DataContainer.class)
				.from(pimGraph)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]));
		if (creatorId != null) {
			query.where(NAO.creator).is(creatorId);
		}
		
		Collection<DataContainer> databoxes = query.results();
		for (DataContainer databox : databoxes) {
			prefetchAccessSpace(databox);
		}
		
		return databoxes;
	}

	@Override
	public DataContainer get(String databoxId) throws InfosphereException {
		return get(databoxId, new ArrayList<URI>(0));
	}
	
	@Override
	public DataContainer get(String databoxId, List<URI> properties) throws InfosphereException {
		DataContainer databox = null;
		
//		databox = getPrivacyPreferenceService().get(new URIImpl(databoxId), properties.toArray(new URI[properties.size()]));
		try {
			databox = getPimoService().get(new URIImpl(databoxId), DataContainer.class, properties.toArray(new URI[properties.size()]));
//			if (!CollectionUtils.contains(databox.getAllLabel(), PrivacyPreferenceType.DATABOX.toString())) {
//				throw new InfosphereException("cannot get databox "+databoxId+": privacy preference not defined as databox.");
//			}
			prefetchAccessSpace(databox);
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot get databox "+databoxId+": not found.", e);
		}

		return databox;
	}
	
	@Override
	public Collection<DataObject> getDataboxItems(String databoxId) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		Collection<DataObject> items = new ArrayList<DataObject>();
		try {
			// loads databox and all its items (+ metadata)
			DataContainer databox = resourceStore.get(new URIImpl(databoxId), DataContainer.class);
			for (DataObject item : databox.getAllPart_as().asList()) {
				try {
					items.add(resourceStore.get(item, DataObject.class));
				} catch (NotFoundException e) {
					logger.warn("Item "+item.asURI()+" is in databox "+databox.asURI()+
							" but it is does not exist.");
				}
			}
			
			return items;
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot get resources from databox [id="+databoxId+"]: "+e, e);
		}
	}
	
	@Override
	public void add(DataContainer databox) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		URI owner = pimoService.getUserUri();

		ClosableIterator<DataObject> rIt = null;
		try {
			rIt = databox.getAllPart();
			while (rIt.hasNext()) {
				Resource resource = rIt.next();
				if (!resourceStore.isTypedAs(resource.asURI(), NIE.DataObject)) {
					throw new InfosphereException("cannot add databox: "+resource.asURI()+
							" is not of type nie:DataObject");
				}
			}
			
			// if no creator provided, the creator is the owner of the PIM
			if (!databox.hasCreator()) {
				databox.setCreator(owner);
			}
			
			// if the owner of the PIM creates a databox, this is a PrivacyPreference as well
			if (owner.equals(databox.getCreator_asNode())) {
				databox.getModel().addStatement(databox, RDF.type, PPO.PrivacyPreference);
				databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
				
				// add all files (nie:hasPart) as ppo:appliesToResource
				ClosableIterator<DataObject> parts = databox.getAllPart();
				while (parts.hasNext()) {
					databox.getModel().addStatement(databox, PPO.appliesToResource, parts.next());
				}
			}
			
			super.add(databox);
			
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot add databox: "+databox.asResource(), e);
		} finally {
			if (rIt != null) {
				rIt.close();
			}
		}
	}

	@Override
	public void update(DataContainer databox) throws InfosphereException {
		PimoService pimoService = getPimoService();
		URI owner = pimoService.getUserUri();

		// if the owner of the PIM creates a databox, this is a PrivacyPreference as well
		if (owner.equals(databox.getCreator_asNode())) {
			databox.getModel().addStatement(databox, RDF.type, PPO.PrivacyPreference);
			databox.setLabel(PrivacyPreferenceType.DATABOX.toString());
			
			// add all files (nie:hasPart) as ppo:appliesToResource
			databox.getModel().removeStatements(databox, PPO.appliesToResource, Variable.ANY);
			ClosableIterator<DataObject> parts = databox.getAllPart();
			while (parts.hasNext()) {
				databox.getModel().addStatement(databox, PPO.appliesToResource, parts.next());
			}
		}
		
		super.update(databox);
	}

	@Override
	public void remove(String databoxId)  throws InfosphereException {
		super.remove(databoxId);
	}

}