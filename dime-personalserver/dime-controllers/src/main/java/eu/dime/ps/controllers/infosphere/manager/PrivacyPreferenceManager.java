package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.ppo.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;
import eu.dime.ps.semantic.privacy.PrivacyPreferenceType;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;

/**
 * Base implementation of a manager for PrivacyPreference objects.
 * It handles the common functionality required for all PrivacyPreferences resources
 * and its subtypes (e.g. Databox, ProfileCard, etc.).
 *  
 * @author Ismael Rivera
 */
public abstract class PrivacyPreferenceManager<T extends Resource> extends InfoSphereManagerBase<T> {

	private static final Logger logger = LoggerFactory.getLogger(DataboxManagerImpl.class);

	@Override
	public void add(T privacyPreference) throws InfosphereException {
		Person me = getMe();
		Model model = privacyPreference.getModel();

		// if nso:hasPrivacyPreference is missing, we set the owner of the store
		// but only for resources of type ppo:PrivacyPreference
		if (model.contains(privacyPreference, RDF.type, PPO.PrivacyPreference)
				&& !model.contains(me, NSO.hasPrivacyPreference, privacyPreference)) {
			model.addStatement(me, NSO.hasPrivacyPreference, privacyPreference);
		}
		
		// save the PrivacyPreference instance
		try {
			getPimoService().create(privacyPreference);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Could not create privacy preference: " + e.getMessage(), e);
		}
	}

	@Override
	public void update(T privacyPreference) throws InfosphereException {
		Person me = getMe();
		Model model = privacyPreference.getModel();

		// if nso:hasPrivacyPreference is missing, we set the owner of the store
		// but only for resources of type ppo:PrivacyPreference
		if (model.contains(privacyPreference, RDF.type, PPO.PrivacyPreference)
				&& !model.contains(me, NSO.hasPrivacyPreference, privacyPreference)) {
			model.addStatement(me, NSO.hasPrivacyPreference, privacyPreference);
		}
		
		// save the PrivacyPreference instance
		try {
			getPimoService().update(privacyPreference);
		} catch (NotFoundException e) {
			throw new InfosphereException("Could not update privacy preference: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void remove(String databoxId)  throws InfosphereException {
		try {
			getPimoService().remove(new URIImpl(databoxId));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot remove databox "+databoxId, e);
		}
	}

	protected Collection<PrivacyPreference> getAll(PrivacyPreferenceType type, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		
		Collection<PrivacyPreference> profileCards =
			resourceStore.find(PrivacyPreference.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(RDFS.label).is(type.toString())
				.results();
		
		// load metadata for each AccessSpace resource
		for (PrivacyPreference profileCard : profileCards) {
			prefetchAccessSpace(profileCard);
		}
		
		return profileCards;
	}

	protected Collection<PrivacyPreference> getAllByPerson(PrivacyPreferenceType type, Person person, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		
		if (!resourceStore.exists(person)) {
			throw new InfosphereException("Person "+person.asURI()+" does not exist.");
		}
		
		Collection<PrivacyPreference> profileCards = 
			resourceStore.find(PrivacyPreference.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(RDFS.label).is(type.toString())
				.where(person.asURI(), NSO.hasPrivacyPreference, Query.THIS)
				.results();
		
		// load metadata for each AccessSpace resource
		for (PrivacyPreference profileCard : profileCards) {
			prefetchAccessSpace(profileCard);
		}

		return profileCards;
	}
	
	/**
	 * Inject metadata about the access spaces inside the privacy preference model.
	 * @param privacyPreference
	 * @throws InfosphereException
	 */
	protected void prefetchAccessSpace(Resource privacyPreference) throws InfosphereException {
		URI pimGraph = getPimoService().getPimoUri();
		ResourceStore resourceStore = getResourceStore();
		ClosableIterator<Statement> it = privacyPreference.getModel().findStatements(privacyPreference, PPO.hasAccessSpace, Variable.ANY);
		while (it.hasNext()) {
			Statement stmt = it.next();
			Node object = stmt.getObject();
			if (object instanceof URI) {
				try {
					AccessSpace accessSpace = resourceStore.get(pimGraph, object.asURI(), AccessSpace.class);
					privacyPreference.getModel().addModel(accessSpace.getModel());
				} catch (NotFoundException e) {
					logger.error("Access space "+object.asURI()+" belonging to privacy preference "+privacyPreference.asURI()+" could not be found.", e);
				}
			}
		}
		it.close();
	}
	
}
