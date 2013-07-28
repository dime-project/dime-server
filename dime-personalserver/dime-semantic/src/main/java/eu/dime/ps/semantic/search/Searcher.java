/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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

package eu.dime.ps.semantic.search;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

/**
 * Interface for search functionality which to be provided by the semantic module.
 * 
 * @author Ismael Rivera
 */
public interface Searcher {

	/** It should be used when no specific 'type' is required */
	public static final URI ANY = null;

	/** It should be used to specify no limit or no offset in a query */
	public static final int NONE = -1;

	/**
	 * Searches for all items related a given search expression.
	 * 
	 * @param searchExpr is a piece of text or string used to describe the item
	 * @return collection of the URIs of the resources as result of the search
	 */
	Collection<URI> search(String searchExpr);

	/**
	 * Performs the same as {@link #search(String)}, except the
	 * search expression must appears in the URI of the resource.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI);

	/**
	 * Performs the same as {@link #search(String)}, except the
	 * results may be filter by their type.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param type specifies the type of the results
	 * @return
	 */
	Collection<URI> search(String searchExpr, URI type);

	/**
	 * Performs the same as {@link #search(String, boolean)},
	 * except the results may be filter by their type.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param type specifies the type of the results
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI,
			URI type);

	/**
	 * Performs the same as {@link #search(String, boolean)},
	 * except the results are returned in a specific order.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param order by clause (e.g. "DESC(http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel)")
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI,
			String order);

	/**
	 * Performs the same as {@link #search(String, boolean)},
	 * except a limit and an offset may be specified to retrieve just a portion of
	 * the results.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param limit used to limit the number of results returned
	 * @param offset used to skip a number of results
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI,
			long limit, long offset);

	/**
	 * Performs the same as {@link #search(String, boolean, String)}, except the results are returned
	 * in a specific order, and a limit and an offset may be specified to retrieve
	 * just a portion of the results.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param order by clause (e.g. "DESC(http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel)")
	 * @param limit used to limit the number of results returned
	 * @param offset used to skip a number of results
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI,
			String order, long limit, long offset);

	/**
	 * Performs the same as {@link #search(String, boolean, URI)}, except a limit and offset may 
	 * be specified to retrieve just a portion of the results.
	 * 
	 * @param searchExpr
	 * @param onlyURI
	 * @param type
	 * @param order
	 * @param limit
	 * @param offset
	 * @return
	 */
	Collection<URI> search(String searchExpr, boolean onlyURI,
			URI type, String order, long limit, long offset);

	/**
	 * Performs the same as {@link #search(String, URI)}, except
	 * the search expression must be match to the values of a set of properies.
	 * 
	 * @param searchExpr
	 * @param type
	 * @param properties list of property URused to restrict the search
	 * @return
	 */
	Collection<URI> search(String searchExpr, URI type,
			URI[] properties);

	/**
	 * Performs the same as {@link #search(String, URI, URI[])},
	 * except the results are returned in a specific order, and a limit and an offset may
	 * be specified to retrieve just a portion of the results.
	 * 
	 * @param searchExpr
	 * @param type
	 * @param properties
	 * @param order by clause (e.g. "DESC(http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel)")
	 * @param limit used to limit the number of results returned
	 * @param offset used to skip a number of results
	 * @return
	 */
	Collection<URI> search(String searchExpr, URI type,
			URI[] properties, String order, long limit, long offset);

	/**
	 * Performs the same as {@link #search(String, boolean, URI, String, long, long)},
	 * except a context may be specified.
	 * 
	 * @param context URI representing the context of a given resource
	 * @param searchExpr
	 * @param onlyURI
	 * @param type
	 * @param order
	 * @param limit
	 * @param offset
	 * @return
	 */
	Collection<URI> search(URI context, String searchExpr,
			boolean onlyURI, URI type, String order, long limit, long offset);

	/**
	 * Performs the same as {@link #search(String, URI, URI[], String, long, long)}, except a context 
	 * may be specified.
	 * 
	 * @param context URI representing the context of a given resource
	 * @param searchExpr
	 * @param type
	 * @param properties
	 * @param order
	 * @param limit
	 * @param offset
	 * @return
	 */
	Collection<URI> search(URI context, String searchExpr,
			URI type, URI[] properties, String order, long limit, long offset);

}
