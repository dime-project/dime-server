package eu.dime.ps.controllers.infosphere.manager;

import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.storage.entities.PersonMatch;

public interface PersonMatchManager {
	
	List<PersonMatch> getAll() throws InfosphereException;
	List<PersonMatch> getAllByThreshold(double threshold) throws InfosphereException;
	List<PersonMatch> getAllByStatus(String status) throws InfosphereException;
	List<PersonMatch> getAllByPerson(URI person) throws InfosphereException;	
	List<PersonMatch> getAllByPersonAndByStatus(URI person, String status) throws InfosphereException;	

}
