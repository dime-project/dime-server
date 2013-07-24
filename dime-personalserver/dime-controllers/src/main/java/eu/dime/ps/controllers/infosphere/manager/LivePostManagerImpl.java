package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.DLPO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.datamining.LivePostDecomposer;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dlpo.LivePost;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link LivePostManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class LivePostManagerImpl extends InfoSphereManagerBase<LivePost> implements LivePostManager {

	private static final Logger logger = LoggerFactory.getLogger(LivePostManagerImpl.class);
	
	private final LivePostDecomposer decomposer;
	
	public LivePostManagerImpl() {
		this.decomposer = new LivePostDecomposer();
	}
	
	@Override
	public Collection<LivePost> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
		
	@Override
	public Collection<LivePost> getAll(List<URI> properties)
				throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(LivePost.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}
	
	@Override
	public Collection<LivePost> getAllByPerson(URI personId) throws InfosphereException {
		return getAllByPerson(personId, new ArrayList<URI>(0));
	}

	@Override
	public Collection<LivePost> getAllByPerson(URI personId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		URI me = pimoService.getUserUri();
		
		Query<LivePost> query = resourceStore
				.find(LivePost.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]));
		if (me.equals(personId)) {
			query.where(NSO.sharedBy).isNull();
		} else {
			query.where(NSO.sharedBy).is(personId);
		}

		return query.results();
	}

	@Override
	public Collection<LivePost> getAllByCreator(String creatorId)
			throws InfosphereException {
		return getAllByCreator(creatorId, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<LivePost> getAllByCreator(String creatorId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(LivePost.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(NAO.creator).is(new URIImpl(creatorId))
				.results();
	}

	public <T extends LivePost> Collection<T> getAllByType(Class<T> returnType)
			throws InfosphereException {
		return getAllByType(returnType, new ArrayList<URI>(0));
	}

	public <T extends LivePost> Collection<T> getAllByType(Class<T> returnType,
			List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(returnType)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}

	@Override
	public <T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId) throws InfosphereException {
		return getAllByTypeAndByCreator(returnType, creatorId, new ArrayList<URI>(0));
	}
	
	@Override
	public <T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId, List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(returnType)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(NAO.creator).is(new URIImpl(creatorId))
				.results();
	}
	
	@Override
	public LivePost get(String livePostId) throws InfosphereException {
		return get(livePostId, new ArrayList<URI>(0));
	}
	
	@Override
	public LivePost get(String livePostId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		try {
			return resourceStore.get(new URIImpl(livePostId), LivePost.class, properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("LivePost "+livePostId+" not found");
		}
	}
	
//	@Override
//	public LivePost getByCreator(String livePostId, String personId)
//			throws NotFoundException, InfosphereException {
//		Person person = resourceStore.get(new URIImpl(personId), Person.class);
//		LivePost livepost =
//			resourceStore.find(LivePost.class)
//				.distinct()
//				.where(NAO.creator).is(person)
//				.first();
//		if (livepost == null) {
//			throw new InfosphereException("LivePost "+livePostId+" not found");
//		}
//		return livepost;
//	}

	@Override
	public void add(LivePost livepost) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			livepost.getModel().addStatement(livepost, RDF.type, DLPO.Status);
			resourceStore.create(pimoService.getPimoUri(), livepost);
		} catch (ResourceExistsException e) {
			logger.error("Cannot save livepost "+livepost.asResource());
		}
		
		// TODO commented out for ametic event, put this back after
//		List<LivePost> liveposts;
//		try {
//			liveposts = decomposer.decompose(livepost);
//			for (LivePost lv : liveposts) {
//				System.out.println("DECOMPOSITION");
//				lv.getModel().dump();
//				if (lv.asResource().equals(livepost.asResource())) {
//					super.add(livepost);
//				} else {
//					try {
//						resourceStore.create(pimoService.getPimoUri(), lv);
//					} catch (ResourceExistsException e) {
//						logger.error("Cannot save livepost "+lv.asResource()
//								+" decomposed from "+livepost.asResource(), e);
//					}
//				}
//			}
//		} catch (DataMiningException e) {
//			throw new InfosphereException("Error while decomposing/analysing livepost "+livepost.asResource(), e);
//		}
	}
	
	@Override
	public void update(LivePost livepost) throws InfosphereException {
		// FIXME hack for 2nd year review, API controller expects type Status
		livepost.getModel().addStatement(livepost, RDF.type, DLPO.Status);
		super.update(livepost);
	}
	
}