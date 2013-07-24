package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dlpo.LivePost;

/**
 * Manager for live post management.
 * 
 * @author Ismael Rivera
 */
public interface LivePostManager extends InfoSphereManager<LivePost> {

	Collection<LivePost> getAllByCreator(String creatorId)
			throws InfosphereException;

	Collection<LivePost> getAllByCreator(String creatorId, List<URI> properties)
			throws InfosphereException;
	
	<T extends LivePost> Collection<T> getAllByType(Class<T> returnType)
			throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByType(Class<T> returnType,
			List<URI> properties) throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId) throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId, List<URI> properties) throws InfosphereException;

	Collection<LivePost> getAllByPerson(URI personId) throws InfosphereException;

	Collection<LivePost> getAllByPerson(URI personId, List<URI> properties)
			throws InfosphereException;

}