package eu.dime.ps.controllers.search;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

public interface Searcher {

	public Collection<SearchResult> search(String searchExpr);

	public Collection<SearchResult> search(String searchExpr, long limit, long offset);
	
	public Collection<SearchResult> search(String searchExpr, URI type);
	
	public Collection<SearchResult> search(String searchExpr, URI type, long limit, long offset);
	
	public Collection<SearchResult> search(String searchExpr, String order);
	
	public Collection<SearchResult> search(String searchExpr, String order, long limit, long offset);

	public Collection<SearchResult> search(String searchExpr, URI type, String order);
	
	public Collection<SearchResult> search(String searchExpr, URI type, String order, long limit, long offset);
	
}
