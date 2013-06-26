package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Manager for profiles management.
 * 
 * @author Ismael Rivera
 */
public interface ProfileManager extends InfoSphereManager<PersonContact> {

	PersonContact getDefault()
			throws InfosphereException;
	
	PersonContact getDefault(List<URI> properties)
			throws InfosphereException;
	
	Collection<PersonContact> getAllByPerson(Person person)
			throws InfosphereException;

	Collection<PersonContact> getAllByPerson(Person person, List<URI> properties)
			throws InfosphereException;

	void add(Person person, PersonContact profile)
			throws InfosphereException;

	void add(Person person, PersonContact profile, boolean isDefault)
			throws InfosphereException;

}