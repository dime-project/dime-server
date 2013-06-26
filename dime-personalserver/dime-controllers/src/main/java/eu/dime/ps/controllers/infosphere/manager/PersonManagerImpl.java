package eu.dime.ps.controllers.infosphere.manager;

import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.PIMO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.trustengine.utils.AdvisoryConstants;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.impl.BasicQuery;
import eu.dime.ps.semantic.query.impl.PimoQuery;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.impl.PimoService;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Implements {@link PersonManager} using a RDF services offered by
 * {@link ConnectionProvider} for persistence.
 * 
 * @author Ismael Rivera
 */
public class PersonManagerImpl extends InfoSphereManagerBase<Person> implements PersonManager {

	@Override
	public boolean isPerson(String resourceId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.isTypedAs(new URIImpl(resourceId), PIMO.Person);
		} catch (NotFoundException e) {
			throw new InfosphereException("Couldn't check if "+resourceId+" was a pimo:Person.", e);
		}
	}
	
	@Override
	public void add(Person person) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			person.getModel().addStatement(pimoService.getUserUri(), FOAF.knows, person.asResource());
			pimoService.create(person);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("cannot add person: "+e, e);
		}
	}

	@Override
	public void update(Person person) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			pimoService.update(person, true);
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot update person: "+e, e);
		}
	}

	@Override
	public void remove(String personId) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			pimoService.remove(new URIImpl(personId));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot remove person: "+e, e);
		}
	}

	@Override
	public Person get(String personId) throws InfosphereException {
		return get(personId, new ArrayList<URI>(0));
	}
	
	@Override
	public Person get(String personId, List<URI> properties) throws InfosphereException {
		PimoService pimoService = getPimoService();
		try {
			return pimoService.get(new URIImpl(personId), Person.class,
					properties.toArray(new URI[properties.size()]));
		} catch (NotFoundException e) {
			throw new InfosphereException("cannot find person by id "+personId+": "+e, e);
		}
	}

	@Override
	public Collection<Person> find(String searchTerm)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		Collection<Person> results = new ArrayList<Person>();
		
		for (URI personUri : getSearcher().search(searchTerm, PIMO.Person)) {
			try {
				Person result = pimoService.get(personUri, Person.class);
				if (result.getModel().contains(pimoService.getUserUri(), FOAF.knows, personUri)) {
					results.add(result);
				}
			} catch (NotFoundException e) {
				throw new InfosphereException("cannot find person "+personUri+" returned by the search");
			}
		}
		
		return results;
	}

	@Override
	public Collection<Person> getAll() throws InfosphereException {
		return getAll(new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<Person> getAll(List<URI> properties) throws InfosphereException {
		PimoService pimoService = getPimoService();
		Collection<Person> people = 
			pimoService.find(Person.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(pimoService.getUserUri(), FOAF.knows, PimoQuery.THIS)
				.results();
		return people;
	}
	
	@Override
	public Collection<Person> getAllByGroup(PersonGroup group)
			throws InfosphereException {
		return getAllByGroup(group, new ArrayList<URI>(0));
	}
	
	@Override
	public Collection<Person> getAllByGroup(PersonGroup group, List<URI> properties)
			throws InfosphereException {
		PimoService pimoService = getPimoService();
		Collection<Person> people =
			pimoService.find(Person.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(PIMO.memberOf).is(group)
				.results();
		return people;
	}

	@Override
	public Collection<Person> getAllByAccount(String accountId)
			throws InfosphereException {
		return getAllByAccount(accountId, new ArrayList<URI>(0));
	}

	@Override
	public Collection<Person> getAllByAccount(String accountId, List<URI> properties)
			throws InfosphereException {
		ResourceStore resourceStore = getResourceStore();
		return resourceStore.find(Person.class)
				.distinct()
				.select(properties.toArray(new URI[properties.size()]))
				.where(PIMO.createdPimo).is(Query.X)
				.where(new URIImpl(accountId), PIMO.isDefinedBy).is(BasicQuery.X)
				.results();
	}

	@Override
	public Person create(PersonContact contact) throws InfosphereException {
		PimoService pimoService = getPimoService();
		
		try {
			pimoService.create(contact);
		} catch (ResourceExistsException e) {
			throw new InfosphereException("The person couldn't be created: "+e.getMessage(), e);
		}
		
		Person person = pimoService.getOrCreatePersonForGroundingOccurrence(contact);
		if (person == null) {
			throw new InfosphereException("The person couldn't be created.");
		}
		person.setTrustLevel(AdvisoryConstants.DEFAULT_TRUST_VALUE);
		try {
			pimoService.update(person);
		} catch (NotFoundException e) { //very unlikely to happen
			throw new InfosphereException("Could not set default TrustValue for created Person, because Person not found.", e);
		}
		return person;
	}

	@Override
	public Person merge(URI master, URI... targets) throws InfosphereException {
		PimoService pimoService = getPimoService();
		Tenant tenant = Tenant.find(TenantContextHolder.getTenant());
		try {
			Person mergedPerson = pimoService.merge(master, targets);
			// flag matches as 'accepted' 
			for (URI target : targets) {
				for (PersonMatch match : PersonMatch.findAllByTenantAndBySourceAndByTarget(tenant, master.toString(), target.toString())) {
					match.setStatus(PersonMatch.ACCEPTED);
					match.merge();
				}
			}
			return mergedPerson;
		} catch (NotFoundException e) {
			throw new InfosphereException("Merge operation failed: "+e.getLocalizedMessage(), e);
		}
	}
	
}