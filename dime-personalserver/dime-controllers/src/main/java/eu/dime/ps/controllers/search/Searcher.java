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
