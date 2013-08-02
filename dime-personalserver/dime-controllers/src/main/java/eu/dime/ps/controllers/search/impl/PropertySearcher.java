/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.controllers.search.impl;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.controllers.search.SearchResult;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.search.Searcher;

public class PropertySearcher extends ResourceSearcher {

	public PropertySearcher(ResourceStore resourceStore) {
		super(resourceStore);
	}
	
	@Override
	public Collection<SearchResult> search(String searchExpr) {
 		return search(searchExpr, RDF.Property, null, Searcher.NONE, Searcher.NONE);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, long limit, long offset) {
		return search(searchExpr, RDF.Property, null, limit, offset);
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
	public Collection<SearchResult> search(String searchExpr, String order) {
		return search(searchExpr, RDF.Property, order, Searcher.NONE, Searcher.NONE);
	}

	@Override
	public Collection<SearchResult> search(String searchExpr, String order, long limit, long offset) {
		return search(searchExpr, RDF.Property, order, Searcher.NONE, Searcher.NONE);
	}

}
