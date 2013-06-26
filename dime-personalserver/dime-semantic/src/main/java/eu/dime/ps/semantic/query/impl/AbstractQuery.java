package eu.dime.ps.semantic.query.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.query.Query;
import eu.dime.ps.semantic.query.Queryable;

/**
 * The abstract base class for queries.
 * <p>Instantiable subclasses are:
 * <ul>
 * <li> {@link BasicQuery}
 * <li> {@link PimoQuery}
 * </ul>
 *
 * @author Ismael Rivera
 */
public abstract class AbstractQuery<T extends Resource> implements Query<T> {

	/** Query is executed on a data source or RDF store */
	protected Queryable rdfStore;
	
	/** Underlying Jena Query, which abstracts the creation of the SPARQL query */
	protected com.hp.hpl.jena.query.Query jenaQuery;
	
	/** Class of the type of object/s to return as result of the execution of the query */
	protected Class<T> returnType;

	protected URI[] selectedProperties;
	protected URI[] discardedProperties;
	
	protected AbstractQuery(Queryable queryable, Class<T> returnType) {
		this.rdfStore = queryable;
		this.returnType = returnType;
		this.jenaQuery = new com.hp.hpl.jena.query.Query();
	}

	@Override
	public Query<T> from(URI... uris) {
		for (URI uri : uris) {
			this.jenaQuery.addGraphURI(uri.toString());
		}
		return this;
	}
	
	@Override
	public Query<T> fromNamed(URI... uris) {
		for (URI uri : uris) {
			this.jenaQuery.addNamedGraphURI(uri.toString());
		}
		return this;
	}
	
	@Override
	public Query<T> distinct() {
		this.jenaQuery.setDistinct(true);
		return this;
	}
	
	@Override
	public Query<T> select(URI... properties) {
		this.selectedProperties = (URI[]) ArrayUtils.addAll(this.selectedProperties, properties);
		return this;
	}

//	@Override
//	public Query<T> discard(URI... properties) {
//		this.discardedProperties = (URI[]) ArrayUtils.addAll(this.discardedProperties, properties);
//		return this;
//	}

	@Override
	public Query<T> limit(Long limit) {
		this.jenaQuery.setLimit(limit);
		return this;
	}

	@Override
	public Query<T> offset(Long offset) {
		this.jenaQuery.setOffset(offset);
		return this;
	}

	@Override
	public String toString() {
		return jenaQuery.toString();
	}
	
	/**
	 * Retrieves a resource from the knowledge base or model set provided.
	 * 
	 * @param resource
	 * @return
	 * @throws NotFoundException
	 */
	protected T get(org.ontoware.rdf2go.model.node.Resource resource) throws NotFoundException {
		if (selectedProperties != null && selectedProperties.length > 0
				&& discardedProperties != null && discardedProperties.length > 0) {
			Set<URI> properties = new HashSet<URI>();
			properties.addAll(Arrays.asList(selectedProperties));
			properties.removeAll(Arrays.asList(discardedProperties));
			return rdfStore.get(resource, returnType, properties.toArray(new URI[properties.size()]));
		} else if (selectedProperties != null && selectedProperties.length > 0) {
			return rdfStore.get(resource, returnType, selectedProperties);
//		} else if (discardedProperties != null && discardedProperties.length > 0) {
//			return rdfStore.get(resource, returnType, true, discardedProperties);
		} else {
			return rdfStore.get(resource, returnType);
		}
	}

}