package eu.dime.ps.semantic.service.context;

import java.util.List;

import org.ontoware.rdf2go.model.Statement;

/**
 * Provides a strategy for updates on the live context, and how these 
 * changes are propagated to the previous context.
 * 
 * @author Ismael Rivera
 */
public interface UpdateStrategy {

	/**
	 * Update live context.
	 * 
	 * @param toAdd set of triples to be added to the live context
	 * @param toRemove sets of triples to be removed from the live context
	 */
	public void update(List<Statement> toAdd, List<Statement> toRemove);
	
}
