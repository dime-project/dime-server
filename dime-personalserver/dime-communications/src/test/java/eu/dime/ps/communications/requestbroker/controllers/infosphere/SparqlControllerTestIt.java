package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.PIMO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.openrdf.repository.Repository;

import eu.dime.ps.communications.SingleConnectionProviderMock;
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.rdf.impl.SesameMemoryRepositoryFactory;

/**
 * Tests {@link SparqlController}.
 * 
 * @author Ismael Rivera
 */
public class SparqlControllerTestIt extends Assert {

	private SingleConnectionProviderMock connectionProvider = null;
	private SparqlController sparqlController = null;

	@Before
	public void setUp() throws Exception {
		SesameMemoryRepositoryFactory repositoryFactory = new SesameMemoryRepositoryFactory();
		Repository repository = repositoryFactory.get("12345");
		Connection connection = new Connection("12345", repository);
		connectionProvider = new SingleConnectionProviderMock();
		connectionProvider.setConnection(connection);
		
		sparqlController = new SparqlController();
		sparqlController.setConnectionProvider(connectionProvider);
		
		// any value is ok, connectionProvider will return always the same connection
		TenantContextHolder.setTenant(0L);
	}
	
	@Test
	public void testSimpleQuery() throws Exception {
		// put some test data in the triple store
		TripleStore tripleStore = connectionProvider.getConnection("12345").getTripleStore();
		tripleStore.addStatement(new URIImpl("urn:graph:test"), new URIImpl("urn:ismael"), RDF.type, PIMO.Person);

		// simple query to retrieve all statements
		String response = sparqlController.query("SELECT * WHERE { GRAPH <urn:graph:test> { ?s ?p ?o .} }").getEntity().toString();
		assertNotNull(response);
		
		JSONObject json = new JSONObject(response);

		JSONObject head = (JSONObject) json.get("head");
		JSONArray vars = (JSONArray) head.get("vars");
		assertEquals("s", vars.get(0));
		assertEquals("p", vars.get(1));
		assertEquals("o", vars.get(2));
		
		JSONObject results = (JSONObject) json.get("results");
		JSONArray bindings = (JSONArray) results.get("bindings");
		assertEquals(1, bindings.length()); // 1 triple
	}
	
}
