package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Manager for person groups management.
 * 
 * @author Ismael Rivera
 */
public interface PersonGroupManager extends InfoSphereManager<PersonGroup> {

	boolean isPersonGroup(String resourceId) throws InfosphereException;

	Collection<PersonGroup> getAdhocGroups() throws InfosphereException;

	Collection<PersonGroup> getAdhocGroups(List<URI> properties) throws InfosphereException;
	
	Collection<PersonGroup> getAll(Person person) throws InfosphereException;
	
	Collection<PersonGroup> getAll(Person person, List<URI> properties) throws InfosphereException;
	
	void addAdhocGroup(PersonGroup personGroup) throws InfosphereException;
	
}