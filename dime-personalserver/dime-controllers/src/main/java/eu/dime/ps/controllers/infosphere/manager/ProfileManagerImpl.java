/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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

import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nie.InformationElement;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link ProfileManager} using a RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class ProfileManagerImpl extends InfoSphereManagerBase<PersonContact> implements ProfileManager {

	private static final Logger logger = LoggerFactory.getLogger(ProfileManagerImpl.class);

	@Override
	public void add(PersonContact profile) throws InfosphereException {
		add(getMe(), profile, false);
	}

	@Override
	public void add(Person person, PersonContact profile)
			throws InfosphereException {
		add(person, profile, false);
	}

	@Override
	public void add(Person person, PersonContact profile, boolean isDefault)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PimoService pimoService = getPimoService();
		try {
			// adds the relation to the profile:
			// - pimo:groundingOccurrence for the digital.me default profile
			// - pimo:occurrence for the rest
			if (isDefault) {
				person.addGroundingOccurrence(profile);
			} else {
				person.addOccurrence(profile);
			}
			resourceStore.createOrUpdate(pimoService.getPimoUri(), profile);
			pimoService.update(person, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("person does not exist, profile cannot be added to an unexisting person: "+e, e);
		}
	}

	@Override
	public Collection<PersonContact> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PersonContact> getAll(List<URI> properties) throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(PersonContact.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(Query.ANY, PIMO.occurrence, Query.THIS)
				.results();
	}
	
	@Override
	public Collection<PersonContact> getAllByPerson(Person person) throws InfosphereException {
		return getAllByPerson(person, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PersonContact> getAllByPerson(Person person, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		
		if (!resourceStore.exists(person)) {
			throw new InfosphereException("Person "+person.asURI()+" does not exist.");
		}
		
		return resourceStore.find(PersonContact.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(person.asURI(), PIMO.occurrence, Query.THIS)
				.results();
	}
	
	@Override
	public PersonContact get(String profileId) throws InfosphereException {
		return get(profileId, new ArrayList<URI>(0));
	}

	@Override
	public PersonContact get(String profileId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		PersonContact profile;
		try {
			profile = resourceStore.get(new URIImpl(profileId), PersonContact.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot retrieve the profile: "+e, e);
		}
		
		// TODO we should do a sanity check, to actually be sure this instance of Contact is a user's profile
			
		return profile;
	}

	@Override
	public PersonContact getDefault() throws InfosphereException {
		return getDefault(new ArrayList<URI>(0));
	}
	
	@Override
	public PersonContact getDefault(List<URI> properties)
			throws InfosphereException {
		PersonContact profile = null; 
		Person me = getMe();
		ClosableIterator<InformationElement> occurrences = me.getAllGroundingOccurrence();
		if (occurrences.hasNext()) {
			profile = get(occurrences.next().asResource().toString(), properties);
			if (occurrences.hasNext()) {
				logger.warn(me.asResource()+" has several nco:Contact as grounding occurrences but it should only have one.");
			}
		} else {
			logger.error("no profile (nco:Contact instance) was found for "+me.asResource());
		}
		return profile;
	}

}