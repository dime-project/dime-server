package eu.dime.ps.semantic.query;

import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.model.pimo.Agent;

/**
 * The abstract base class for queries.
 *
 * @author Ismael Rivera
 */
public interface AccessibleQuery<T extends Resource> extends Query<T> {

	public AccessibleQuery<T> accessibleBy(Agent agent);

}
