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

/**
 * 
 */
package com.ontotext.kim.util.datastore;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.parser.QueryParserFactory;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.repository.RepositoryConnection;

import com.ontotext.kim.client.model.WKBConstants;
import com.ontotext.kim.client.query.KIMQueryException;
import com.ontotext.kim.client.semanticrepository.QueryResultListener;
import com.ontotext.kim.semanticrepository.ListenerAdapter;

/**
 * Feed implementation over an instance of a Sesame 2 repository and
 * a given SPARQL or SeRQL query
 * 
 * @see QueryResultListener.Feed
 * @author mnozchev
 */
public class RepositoryFeed implements QueryResultListener.Feed {

	private final RepositoryConnection conn;
	private final Constructor<TupleQueryResultHandler> wrapperFactory;
	private final String query;

	public RepositoryFeed(RepositoryConnection conn, Constructor<TupleQueryResultHandler> wrapperFactory, String queryString) {			
		this.conn = conn;
		this.wrapperFactory = wrapperFactory;
		this.query = queryString;
	}

	public void feedTo(QueryResultListener listener)
	throws KIMQueryException {
		TupleQueryResultHandler handler = new ListenerAdapter(listener);
		if (wrapperFactory != null) {
			try {
				handler = wrapperFactory.newInstance(handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {			 
			// Note that conn.prepareTupleQuery(ql, query) may not actually parse the query 
			// if the repository is remote.
			
			QueryLanguage lang = findLanguage(query);
			TupleQuery tq = conn.prepareTupleQuery(lang, query);
			tq.evaluate(handler);
		} catch (Exception e) {
			throw new KIMQueryException(e, query);
		}
	}

	private final QueryLanguage findLanguage(String query) throws MalformedQueryException {
		String message = "";
		for (QueryParserFactory qp : Arrays.asList(
				QueryParserRegistry.getInstance().get(QueryLanguage.SERQL),
				QueryParserRegistry.getInstance().get(QueryLanguage.SPARQL))) {
			try {
				qp.getParser().parseQuery(query, WKBConstants.WKB_NS);
				return qp.getQueryLanguage();
			} catch (MalformedQueryException e) {
				message += e.getMessage() + "\n";
			}
		}
		throw new MalformedQueryException(message);
	}
}