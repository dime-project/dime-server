package eu.dime.ps.semantic.connection;

import ie.deri.smile.vocabulary.FOAF;
import ie.deri.smile.vocabulary.NFO;

import java.util.UUID;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.rdf.impl.SesameNativeRepositoryFactory;

/**
 * Tests {@link ConnectionProvider}.
 * 
 * @author Ismael Rivera
 */
public class ConnectionProviderTestIt extends TestCase {

	private ConnectionProvider connectionProvider;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Before
	public void setUp() throws Exception {
		connectionProvider = new ConnectionProvider(new SesameNativeRepositoryFactory());
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testNewConnection() throws Exception {
		String repository = UUID.randomUUID().toString();
		Connection conn = connectionProvider.newConnection(repository, "test");
		
		assertNotNull(conn.getTripleStore());
		assertNotNull(conn.getResourceStore());
		assertNotNull(conn.getPimoService());
		assertNotNull(conn.getLiveContextService());
		assertNotNull(conn.getPersonMatchingService());
		assertNotNull(conn.getPrivacyPreferenceService());
		assertNotNull(conn.getSearcher());
	}
	
	@Test
	public void testSamePIMUri() throws Exception {
		String repository = UUID.randomUUID().toString();
		Connection conn = connectionProvider.newConnection(repository, "test");
		
		assertEquals(conn.getPimoService().getPimoUri(), conn.getPimoService().getPimoUri());
	}

	@Test
	public void testPIMOPersistance() throws Exception {
		String repository = UUID.randomUUID().toString();
		Connection conn = connectionProvider.newConnection(repository, "test");
		ModelFactory modelFactory = new ModelFactory();
		Person person = modelFactory.getPIMOFactory().createPerson("urn:ismael");
		conn.getPimoService().create(person);
		
		// there should be me & ismael
		assertEquals(2L, conn.getPimoService().find(Person.class).distinct().count().longValue());
		assertTrue(conn.getPimoService().find(Person.class).ids().contains(new URIImpl("urn:ismael")));
	}

	@Test
	public void testMultipleGetConnection() throws Exception {
		String repository = UUID.randomUUID().toString();
		connectionProvider.newConnection(repository, "test");
		
		assertEquals(
				connectionProvider.getConnection(repository).getPimoService().getPimoUri(),
				connectionProvider.getConnection(repository).getPimoService().getPimoUri());
	}
	
	@Test
	public void testPersistanceDifferentRepositories() throws Exception {
		String rep1 = UUID.randomUUID().toString();
		String rep2 = UUID.randomUUID().toString();
		Connection conn1 = connectionProvider.newConnection(rep1, "test1");
		Connection conn2 = connectionProvider.newConnection(rep2, "test2");
		
		URI g1 = new URIImpl("urn:test1");
		URI g2 = new URIImpl("urn:test2");
		URI ismael = new URIImpl("urn:ismael");
		URI photo = new URIImpl("urn:photo123");
		
		conn1.getTripleStore().addStatement(g1, ismael, RDF.type, FOAF.Person);
		conn2.getTripleStore().addStatement(g2, photo, RDF.type, NFO.Document);
		conn1.close();
		conn2.close();
		
		conn1 = connectionProvider.getConnection(rep1);
		assertTrue(conn1.getTripleStore().containsStatements(g1, ismael, RDF.type, FOAF.Person));
		assertFalse(conn1.getTripleStore().containsStatements(g2, photo, RDF.type, NFO.Document));

		conn2 = connectionProvider.getConnection(rep2);
		assertFalse(conn2.getTripleStore().containsStatements(g1, ismael, RDF.type, FOAF.Person));
		assertTrue(conn2.getTripleStore().containsStatements(g2, photo, RDF.type, NFO.Document));
	}

}
