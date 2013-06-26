package eu.dime.ps.semantic.search.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.Sparqlable;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.openjena.atlas.lib.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.semantic.search.Searcher;

/**
 * Implementation of {@link Searcher} based on SPARQL queries over an RDF store. 
 * 
 * @author Ismael Rivera
 */
public class SparqlSearcher implements Searcher {

	private static final Logger logger = LoggerFactory.getLogger(SparqlSearcher.class);

	private Sparqlable sparqlable;
	
	public SparqlSearcher(Sparqlable sparqlable) {
		this.sparqlable = sparqlable;
	}
	
	@Override
	public Collection<URI> search(String searchExpr) {
		return search(null, searchExpr, false, null, null, null, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI) {
		return search(null, searchExpr, onlyURI, null, null, null, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, URI type) {
		return search(null, searchExpr, false, type, null, null, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI,
			URI type) {
		return search(null, searchExpr, onlyURI, type, null, null, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI,
			String order) {
		return search(null, searchExpr, onlyURI, null, null, order, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI,
			long limit, long offset) {
		return search(null, searchExpr, onlyURI, null, null, null, limit, offset);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI,
			String order, long limit, long offset) {
		return search(null, searchExpr, onlyURI, null, null, order, limit, offset);
	}

	@Override
	public Collection<URI> search(String searchExpr, boolean onlyURI,
			URI type, String order, long limit, long offset) {
		return search(null, searchExpr, onlyURI, type, null, order, limit, offset);
	}

	@Override
	public Collection<URI> search(String searchExpr, URI type,
			URI[] properties) {
		return search(null, searchExpr, false, type, properties, null, NONE, NONE);
	}

	@Override
	public Collection<URI> search(String searchExpr, URI type, URI[] properties,
			String order, long limit, long offset) {
		return search(null, searchExpr, false, type, properties, order, limit, offset);
	}

	@Override
	public Collection<URI> search(URI context, String searchExpr, boolean onlyURI,
			URI type, String order, long limit, long offset) {
		return search(context, searchExpr, onlyURI, type, null, order, limit, offset);
	}

	@Override
	public Collection<URI> search(URI context, String searchExpr, URI type,
			URI[] properties, String order, long limit, long offset) {
		return search(context, searchExpr, false, type, properties, order, limit, offset);
	}
		
	private Collection<URI> search(URI context, String searchExpr, boolean onlyURI,
			URI type, URI[] properties, String order, long limit, long offset) {
		
		String sparqlQuery = StrUtils.strjoin("\n",
				"SELECT DISTINCT ?resource WHERE {",
				(context != null ? " GRAPH "+context.toSPARQL()+" {" : ""),
				" { ?resource " + RDF.type.toSPARQL() + " " + (type == ANY ? "?type" : type.toSPARQL()) + ".",
				" FILTER (regex(str(?resource), \""+searchExpr+"\", \"i\")). }");
		
		if (!onlyURI && (properties == null || properties.length == 0)) {
			sparqlQuery = StrUtils.strjoin("\n",
					sparqlQuery,
					" UNION {",
					" ?resource " + RDF.type.toSPARQL() + " " + (type == ANY ? "?type" : type.toSPARQL()) + ".",
					" ?resource ?p ?value.",
					" FILTER (regex(str(?value), \""+searchExpr+"\", \"i\")). }");
		} else if (!onlyURI) {
			for (URI property : properties) {
				sparqlQuery = StrUtils.strjoin("\n",
						sparqlQuery,
						" UNION {",
						" ?resource " + RDF.type.toSPARQL() + " " + (type == ANY ? "?type" : type.toSPARQL()) + ".",
						" ?resource " + property.toSPARQL() + " ?value.",
						" FILTER (regex(str(?value), \""+searchExpr+"\", \"i\")). }");
			}
		}
		
		String orderClause = null;
		if (order != null) {
			String[] orderAry = order.split("(\\()|(\\))");
			String direction = null;
			
			if (orderAry.length == 2) {
				direction = orderAry[0].trim();
				if (!direction.equals("ASC") && !direction.equals("DESC")) {
					throw new IllegalArgumentException("Order direction must be ASC or DESC");
				}
			} else {
				throw new IllegalArgumentException("Parameter 'order' is not grammatically correct. " +
						"Must be ASC or DESC followed by a property URI between parenthesis, " +
						"e.g.: DESC(http://www.semanticdesktop.org/ontologies/2007/08/15/nao#prefLabel)");
			}
			
			sparqlQuery = StrUtils.strjoinNL(
					sparqlQuery,
					"?resource <" + orderAry[1] + "> ?order .");
			orderClause = "ORDER BY" + direction + "(?order)";
		}
		
		sparqlQuery = StrUtils.strjoin("\n",
				sparqlQuery,
				(context != null ? "}" : ""),
				"}",
				(orderClause != null ? orderClause : ""),
				(limit != NONE ? "LIMIT " + limit : ""),
				(offset != NONE ? "OFFSET " + offset : ""));
		
		Collection<URI> results = new ArrayList<URI>();
		ClosableIterator<QueryRow> iterator = sparqlable.sparqlSelect(sparqlQuery).iterator();
		URI result = null;
		logger.debug("QUERY:\n" + sparqlQuery);
		while (iterator.hasNext()) {
			result = iterator.next().getValue("resource").asURI();
			logger.debug("RESULT: " + result);
			results.add(result);
		}
		iterator.close();

		return results;
	}

}
