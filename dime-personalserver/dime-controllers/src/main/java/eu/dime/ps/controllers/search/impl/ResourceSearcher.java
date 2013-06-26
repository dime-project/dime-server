package eu.dime.ps.controllers.search.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.search.SearchResult;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.search.Searcher;
import eu.dime.ps.semantic.search.impl.SparqlSearcher;

public class ResourceSearcher implements eu.dime.ps.controllers.search.Searcher {

	private static final Logger logger = LoggerFactory.getLogger(ResourceSearcher.class);

	private Searcher searcher;
	private ResourceStore resourceStore;
	
	public ResourceSearcher(ResourceStore resourceStore) {
		this.resourceStore = resourceStore;
		this.searcher = new SparqlSearcher(resourceStore);
	}
	
	@Override
	public Collection<SearchResult> search(String searchExpr) {
 		return search(searchExpr, null, null, Searcher.NONE, Searcher.NONE);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, long limit, long offset) {
		return search(searchExpr, null, null, limit, offset);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, URI type) {
		return search(searchExpr, type, null, Searcher.NONE, Searcher.NONE);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, URI type, long limit, long offset) {
		return search(searchExpr, type, null, limit, offset);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, URI type, String order) {
		return search(searchExpr, type, order, Searcher.NONE, Searcher.NONE);
	}
	
	@Override
	public Collection<SearchResult> search(String searchExpr, String order) {
		return search(searchExpr, null, order, Searcher.NONE, Searcher.NONE);
	}
	
	@Override
	public Collection<SearchResult> search(String searchExpr, String order, long limit, long offset) {
		return search(searchExpr, null, order, limit, offset);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, URI type, String order, long limit, long offset) {
		Collection<URI> results = searcher.search(null, searchExpr, type, null, order, limit, offset);
		Collection<SearchResult> searchResults = new ArrayList<SearchResult>(results.size());
		for (URI result : results) {
			try {
				searchResults.add(new SearchResult(resourceStore.get(result).asResource()));
			} catch (NotFoundException e) {
				logger.error("search result " + result + " cannot be found: " + e, e);
			}
		}
		return searchResults;
	}

}
