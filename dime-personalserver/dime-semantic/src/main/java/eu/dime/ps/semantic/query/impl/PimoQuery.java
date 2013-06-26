package eu.dime.ps.semantic.query.impl;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.service.impl.PimoService;

/**
 * Extends {@link BasicQuery}, and only return resources which are in
 * the user's PIM graph.
 * 
 * @author Ismael Rivera
 */
public class PimoQuery<T extends org.ontoware.rdfreactor.schema.rdfs.Resource> extends BasicQuery<T> {

	public PimoQuery(PimoService pimoService, Class<T> returnType) {
		super(pimoService, returnType);
		this.from(pimoService.getPimoUri());
	}

	public PimoQuery(PimoService pimoService, Class<T> returnType, URI... types) {
		super(pimoService, returnType, types);
		this.from(pimoService.getPimoUri());
	}

}
