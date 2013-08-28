package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.DatatypeLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.openrdf.model.Value;
import org.openrdf.model.impl.BooleanLiteralImpl;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.DecimalLiteralImpl;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.impl.MapBindingSet;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;

/**
 * Allows arbitrary SPARQL queries to be executed on the user's data store.
 * 
 * @author Ismael Rivera
 */
@Path("/dime/rest/{said}/sparql")
public class SparqlController {
	
	private static final Logger logger = LoggerFactory.getLogger(SparqlController.class);
	
	private ConnectionProvider connectionProvider;
	
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
    	this.connectionProvider = connectionProvider;
    }

    /**
     * Performs a SPARQL query in the triple store, and returns the results in JSON.
     * 
     * @param queryString a string containing the SPARQL query
     * @return the SPARQL query results formatted in sparql-results+json
     * <br/>Example:
     * <pre>
     * {
     *   "head": { "vars": [ "book" , "title" ] } ,
     *   "results": {
     *     "bindings": [
     *       {
     *         "book": { "type": "uri" , "value": "http://example.org/book/book6" } ,
     *         "title": { "type": "literal" , "value": "Harry Potter and the Half-Blood Prince" }
     *       },
     *       {
     *         "book": { "type": "uri" , "value": "http://example.org/book/book1" } ,
     *         "title": { "type": "literal" , "value": "Harry Potter and the Philosopher's Stone" }
     *       }
     *     ]
     *   }
     * }
     * </pre>
     */
	@GET
	@Produces("application/sparql-results+json;charset=UTF-8")
	public Response query(@QueryParam("query") String queryString) {
		Connection connection = null;
		TripleStore tripleStore = null;
		
		try {
			connection = connectionProvider.getConnection(TenantHelper.getCurrentTenantId().toString());
			tripleStore = connection.getTripleStore();
			
			// perform SPARQL query
			QueryResultTable queryResults = tripleStore.sparqlSelect(queryString);
			ClosableIterator<QueryRow> resultRows = queryResults.iterator();
			
			// build the JSON response
			List<String> bindingNames = queryResults.getVariables();
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			SPARQLResultsJSONWriter writer = new SPARQLResultsJSONWriter(output);
			writer.startQueryResult(bindingNames);

			try {
				while (resultRows.hasNext()) {
					QueryRow result = resultRows.next();
					
					// construct a binding set for every result row
					MapBindingSet bindingSet = new MapBindingSet();
					for (String bindingName : bindingNames) {
						Node node = result.getValue(bindingName);
						Value value = null;
						
						if (node instanceof URI) {
							value = new URIImpl(node.toString());
						} else if (node instanceof DatatypeLiteral) {
							DatatypeLiteral literal = (DatatypeLiteral) node;
							URI datatype = literal.getDatatype();
							if (datatype.equals(XSD._boolean)) {
								value = new BooleanLiteralImpl(Boolean.parseBoolean(literal.getValue()));
							} else if (datatype.equals(XSD._date)
									|| datatype.equals(XSD._dateTime)) {
								XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(literal.getValue());
								value = new CalendarLiteralImpl(calendar);
							} else if (datatype.equals(XSD._decimal)
									|| datatype.equals(XSD._float)
									|| datatype.equals(XSD._double)) {
								value = new DecimalLiteralImpl(new BigDecimal(literal.getValue()), new URIImpl(datatype.toString()));
							} else if (datatype.equals(XSD._int)
									|| datatype.equals(XSD._integer)
									|| datatype.equals(XSD._negativeInteger)
									|| datatype.equals(XSD._nonNegativeInteger)
									|| datatype.equals(XSD._nonPositiveInteger)) {
								value = new IntegerLiteralImpl(new BigInteger(literal.getValue()));
							} else if (datatype.equals(XSD._long)) {
								value = new NumericLiteralImpl(Long.parseLong(literal.getValue()));
							} else {
								logger.error("");
								continue;
							}
						} else if (node instanceof Literal) {
							value = new LiteralImpl(((Literal) node).getValue());
						} else {
							logger.error("");
							continue;
						}
						
						bindingSet.addBinding(bindingName, value);
					}
					
					// pass the binding set to the solution list
					writer.handleSolution(bindingSet);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			writer.endQueryResult();
			
			// write query results to the HTTP response
			return Response.ok(new String(output.toByteArray(), "UTF-8")).build();
			
		} catch (RepositoryException e) {
			logger.error("Couldn't connect to RDF services: " + e.getMessage(), e);
			return Response.serverError().build();
		} catch (UnsupportedEncodingException e) {
			logger.error("Couldn't build response due to unsupported encoding: " + e.getMessage(), e);
			return Response.serverError().build();
		} catch (TupleQueryResultHandlerException e) {
			logger.error("Couldn't parse query results and construct the response: " + e.getMessage(), e);
			return Response.serverError().build();
		} finally {
			if (connection != null) {
                try {
                    connection.close();
                } catch (RepositoryException e) {}
            }
		}
	}

	public static void main(String[] args) throws Exception {
//		List<String> bindingNames = new ArrayList<String>();
//		bindingNames.add("book");
//		bindingNames.add("title");
//		
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		SPARQLResultsJSONWriter writer = new SPARQLResultsJSONWriter(output);
//		writer.startQueryResult(bindingNames);
//
//		MapBindingSet bs1 = new MapBindingSet();
//		bs1.addBinding("book", new URIImpl("http://example.org/book/book6"));
//		bs1.addBinding("title", new LiteralImpl("Harry Potter and the Half-Blood Prince"));
//
//		MapBindingSet bs2 = new MapBindingSet();
//		bs2.addBinding("title", new LiteralImpl("Chuck Norris in Action"));
//		bs2.addBinding("book", new URIImpl("http://example.org/book/book123"));
//		
//
//		writer.handleSolution(bs1);
//		writer.handleSolution(bs2);
//		
//		writer.endQueryResult();
//
//		
//		System.out.println(output.toString("UTF-8"));
	}
}
