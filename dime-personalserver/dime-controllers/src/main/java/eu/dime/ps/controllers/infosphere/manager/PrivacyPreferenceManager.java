/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NIE;
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
import eu.dime.ps.semantic.service.impl.PimoService;

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
		TripleStore tripleStore = getTripleStore();
		PimoService pimoService = getPimoService();

		// if nso:hasPrivacyPreference is missing, we set the owner of the store
		// but only for resources of type ppo:PrivacyPreference
		if (model.contains(privacyPreference, RDF.type, PPO.PrivacyPreference)
				&& !model.contains(me, NSO.hasPrivacyPreference, privacyPreference)) {
			model.addStatement(me, NSO.hasPrivacyPreference, privacyPreference);
		}
		
		// remove all files from databox (to detect a databox is empty, etc.) 
		tripleStore.removeStatements(pimoService.getPimoUri(), privacyPreference, NIE.hasPart, Variable.ANY);
		tripleStore.removeStatements(pimoService.getPimoUri(), privacyPreference, PPO.appliesToResource, Variable.ANY);
		
		// save the PrivacyPreference instance
		try {
			pimoService.update(privacyPreference, true);
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
