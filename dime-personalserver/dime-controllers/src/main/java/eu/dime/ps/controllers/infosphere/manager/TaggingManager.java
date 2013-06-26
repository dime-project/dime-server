package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nao.Tag;

/**
 * Manager for tagging resources.
 * 
 * @author Ismael Rivera
 */
public interface TaggingManager {

	/**
	 * Retrieves all tags associated with a resource.
	 * @param resource
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> getAllByResource(Resource resource)
			throws InfosphereException;

	/**
	 * Retrieves all tags which label (nao:prefLabel) is like (partially equals) to a given one.
	 * @param label
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> getAllByLabelLike(String label)
			throws InfosphereException;

	/**
	 * Creates nao:Tag instances with the given labels, and attach the tags to the resource.
	 * @param resource
	 * @param labels
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> add(Resource resource, String... labels) throws InfosphereException;
	
}