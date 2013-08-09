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

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Implements {@link PersonGroupManager} using the RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class PersonGroupManagerImpl extends InfoSphereManagerBase<PersonGroup> implements PersonGroupManager {

	@Override
	public boolean isPersonGroup(String resourceId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.isTypedAs(new URIImpl(resourceId), PIMO.PersonGroup);
		} catch (NotFoundException e) {
			throw new InfosphereException("Couldn't check if "+resourceId+" was a pimo:PersonGroup.", e);
		}
	}

	@Override
	public Collection<PersonGroup> getAdhocGroups()
			throws InfosphereException {
		return getAdhocGroups(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PersonGroup> getAdhocGroups(List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		return pimoService.find(PersonGroup.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(NAO.creator).is(new URIImpl("urn:auto-generated")) // FIXME the creator of an adhoc group should be the 'dime account'
				.results();
	}
	
	
	@Override
	public Collection<PersonGroup> getAll()
			throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PersonGroup> getAll(List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		
		// returns all person groups (adhoc and manually created)
		return pimoService.find(PersonGroup.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.results();
	}

	@Override
	public Collection<PersonGroup> getAll(Person person)
		throws InfosphereException {
		return getAll(person, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<PersonGroup> getAll(Person person, List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();

		// returns all person groups (adhoc and manually created)
		return pimoService.find(PersonGroup.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(PIMO.hasMember).is(person)
				.results();
	}

	@Override
	public PersonGroup get(String groupId) throws InfosphereException {
		return get(groupId, new ArrayList<URI>(0));
	}
	
	@Override
	public PersonGroup get(String groupId, List<URI> properties) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.get(new URIImpl(groupId), PersonGroup.class, 
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("PersonGroup "+groupId+" not found.", e);
		}
	}
	
	@Override
	public void addAdhocGroup(PersonGroup personGroup) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			// an adhoc group is a person group with no creator, it's automatically
			// created by the system, thus all creators are removed to ensure it
			// FIXME the creator of an adhoc group should be the 'dime account'
			personGroup.setCreator(new URIImpl("urn:auto-generated"));
			pimoService.create(personGroup);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Cannot add person group "+personGroup, e);
		}
	}
	
	@Override
	public void add(PersonGroup personGroup) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			personGroup.setCreator(pimoService.getUserUri());
			pimoService.create(personGroup);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("Cannot add person group "+personGroup, e);
		}
	}
	
	@Override
	public void update(PersonGroup personGroup) throws InfosphereException {
		PimoService pimoService = getPimoService();
		TripleStore tripleStore = pimoService.getTripleStore();
		try {
			// remove always all members from the group (in case group is now empty) 
			tripleStore.removeStatements(pimoService.getPimoUri(), personGroup, PIMO.hasMember, Variable.ANY);
			
			pimoService.update(personGroup, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot update person group "+personGroup, e);
		}
	}
	
	@Override
	public void remove(String groupId) throws InfosphereException{
		PimoService pimoService = getPimoService();
		try {
			pimoService.remove(new URIImpl(groupId));
		} catch (NotFoundException e) {
			throw new InfosphereException("Cannot delete person group "+groupId, e);
		}
	}

}