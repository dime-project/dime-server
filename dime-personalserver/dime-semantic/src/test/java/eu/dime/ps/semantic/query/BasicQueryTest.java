package eu.dime.ps.semantic.query;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.ontoware.rdfreactor.schema.rdfs.Resource;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.model.nfo.Folder;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.query.impl.BasicQuery;

/**
 * Tests {@link BasicQuery}.
 */
public final class BasicQueryTest extends SemanticTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		tripleStore.clear();
		loadData();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected void loadData() throws ModelRuntimeException, IOException {
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("files-data.nt"),
				Syntax.Ntriples, sinkModel);
		tripleStore.addModel(sinkModel);
		sinkModel.close();
	}

	@Test
	public void testNumericalComparison() throws Exception {
		Collection<FileDataObject> results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileSize).gt(100000l)
				.where(NFO.fileSize).lt(200000l).results();
		assertEquals(3, results.size());
		URI[] files = new URI[] {
				new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png"),
				new URIImpl("file:/home/ismriv/example/dir1/screenshot-GVS.png"),
				new URIImpl("file:/home/ismriv/example/dir2/lego.png")
		};
		for (FileDataObject f : results) {
			assertTrue(ArrayUtils.contains(files, f.asURI()));
		}
	}

	@Test
	public void testLikeExpression() throws Exception {
		Collection<FileDataObject> results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like("motivation").results();
		assertEquals(1, results.size());
		URI[] files = new URI[] {
				new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png"),
		};
		for (FileDataObject f : results) {
			assertTrue(ArrayUtils.contains(files, f.asURI()));
		}
	}
	
	@Test
	public void testOrWhere() throws Exception {
		Collection<FileDataObject> results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like("motivation")
				.orWhere(NFO.fileName).like("composition").results();
		assertEquals(2, results.size());
		URI[] files = new URI[] {
				new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png"),
				new URIImpl("file:/home/ismriv/example/dir1/different-levels-of-composition.png"),
		};
		for (FileDataObject f : results) {
			assertTrue(ArrayUtils.contains(files, f.asURI()));
		}
	}
	
	@Test
	public void testWhereWithVariables() throws Exception {
		Collection<Folder> results = 
			resourceStore.find(Folder.class).results();
		assertEquals(2, results.size());
		results = 
			resourceStore.find(Folder.class)
				.where(BasicQuery.THIS, NIE.hasPart, BasicQuery.X)
				.where(BasicQuery.X, NFO.fileName).like("motivation").results();
		assertEquals(1, results.size());
		assertEquals(new URIImpl("file:/home/ismriv/example/dir1/"), results.iterator().next().asURI());
	}

	@Test
	public void testOrder() throws Exception {
		FileDataObject[] files =  
			resourceStore.find(FileDataObject.class)
				.orderBy(NFO.fileLastModified, BasicQuery.ORDER_DESCENDING).results().toArray(new FileDataObject[0]);
		assertEquals("file:/home/ismriv/example/dir1/FAST-presentation.pptx", files[0].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/FAST-a-lego-like-IDE-for-web-applications.pptx", files[1].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/screenshot-GVS.png", files[10].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/ComplexGadgetArchitecture.png", files[11].asURI().toString());

		files = resourceStore.find(FileDataObject.class)
			.orderBy(NFO.fileLastModified, BasicQuery.ORDER_ASCENDING).results().toArray(new FileDataObject[0]);
		assertEquals("file:/home/ismriv/example/dir1/FAST-presentation.pptx", files[11].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/FAST-a-lego-like-IDE-for-web-applications.pptx", files[10].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/screenshot-GVS.png", files[1].asURI().toString());
		assertEquals("file:/home/ismriv/example/dir1/ComplexGadgetArchitecture.png", files[0].asURI().toString());
	}
	
	@Test
	public void testCount() throws Exception {
		long count = 0;
		count =	resourceStore.find(FileDataObject.class).count();
		assertEquals(12, count);
		count =	resourceStore.find(FileDataObject.class)
			.where(NFO.fileName).like("motivation")
			.orWhere(NFO.fileName).like("composition").count();
		assertEquals(2, count);
	}

	@Test
	public void testMultipleResourceTypes() throws Exception {
		Collection<Resource> results = new ArrayList<Resource>();
		results.addAll(resourceStore.find(Folder.class).results());
		results.addAll(resourceStore.find(FileDataObject.class).results());
		assertEquals(14, results.size());
	}

	@Test
	public void testLikeIsRegEx() throws Exception {
		Collection<FileDataObject> results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like("^Complex.").results(); // filename starts with 'Complex'
		assertEquals(1, results.size());
		
		results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like("^complex.").results(); // by default is case-insensitive
		assertEquals(1, results.size());

		results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like("^complex.", true).results(); // should return nothing, now it's case-sensitive
		assertEquals(0, results.size());

		results = 
			resourceStore.find(FileDataObject.class)
				.where(NFO.fileName).like(".pptx$").results(); // filename ends with pptx
		assertEquals(2, results.size());
	}
	
	@Test
	public void testBound() {
		tripleStore.addStatement(null, new URIImpl("urn:person1"), RDF.type, PIMO.Person);
		tripleStore.addStatement(null, new URIImpl("urn:person1"), NAO.prefLabel, new PlainLiteralImpl("Ismael Morales"));
		tripleStore.addStatement(null, new URIImpl("urn:person1"), NAO.prefSymbol, new URIImpl("urn:symbol1"));
		tripleStore.addStatement(null, new URIImpl("urn:person2"), RDF.type, PIMO.Person);
		tripleStore.addStatement(null, new URIImpl("urn:person2"), NAO.prefLabel, new PlainLiteralImpl("Ismael Rivera"));

		Query<Person> unboundQuery = 
			resourceStore.find(Person.class)
				.where(NAO.prefLabel).like("ael")
				.where(NAO.prefSymbol).isNull();
		assertEquals("urn:person2", unboundQuery.first().asURI().toString());
		
		Query<Person> boundQuery = 
			resourceStore.find(Person.class)
				.where(NAO.prefSymbol).is(BasicQuery.ANY);
		assertEquals("urn:person1", boundQuery.first().asURI().toString());
	}

	@Test
	public void testEqURI() {
		URI file1 = new URIImpl("file://somewhere/Paris.jpg");
		URI file2 = new URIImpl("file://somewhere/London.jpg");
		tripleStore.addStatement(null, file1, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file1, NIE.url, file1);
		tripleStore.addStatement(null, file2, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file2, NIE.url, file2);
		
		Query<FileDataObject> query = 
				resourceStore.find(FileDataObject.class)
					.distinct()
					.where(NIE.url).eq(file2);

		Collection<FileDataObject> results = query.results();
		assertEquals(1, results.size());
		assertEquals(file2, results.iterator().next().asURI());
	}
	
	@Test
	public void testEqString() {
		URI file1 = new URIImpl("file://somewhere/Paris.jpg");
		URI file2 = new URIImpl("file://somewhere/London.jpg");
		tripleStore.addStatement(null, file1, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file1, NAO.prefLabel, new PlainLiteralImpl("Paris.jpg"));
		tripleStore.addStatement(null, file2, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file2, NAO.prefLabel, new PlainLiteralImpl("London.jpg"));

		Query<FileDataObject> query = 
				resourceStore.find(FileDataObject.class)
					.distinct()
					.where(NAO.prefLabel).eq("Paris.jpg");

		Collection<FileDataObject> results = query.results();
		assertEquals(1, results.size());
		assertEquals(file1, results.iterator().next().asURI());
	}
	
	@Test
	public void testEqNumber() {
		URI file1 = new URIImpl("file://somewhere/Paris.jpg");
		URI file2 = new URIImpl("file://somewhere/London.jpg");
		tripleStore.addStatement(null, file1, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file1, NFO.fileSize, new DatatypeLiteralImpl("3304", XSD._double));
		tripleStore.addStatement(null, file2, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file2, NFO.fileSize, new DatatypeLiteralImpl("7273", XSD._double));
	
		Query<FileDataObject> query = 
				resourceStore.find(FileDataObject.class)
					.distinct()
					.where(NFO.fileSize).eq(7273d);

		Collection<FileDataObject> results = query.results();
		assertEquals(1, results.size());
		assertEquals(file2, results.iterator().next().asURI());
	}
	
	@Test
	public void testNeqURI() {
		URI p1 = new URIImpl("urn:person1");
		URI p2 = new URIImpl("urn:person2");
		tripleStore.addStatement(null, p1, RDF.type, PIMO.Person);
		tripleStore.addStatement(null, p1, NAO.prefLabel, new PlainLiteralImpl("Ismael Morales"));
		tripleStore.addStatement(null, p1, NAO.prefSymbol, new URIImpl("urn:symbol1"));
		tripleStore.addStatement(null, p2, RDF.type, PIMO.Person);
		tripleStore.addStatement(null, p2, NAO.prefLabel, new PlainLiteralImpl("Ismael Rivera"));

		Query<Person> query = 
			resourceStore.find(Person.class)
				.distinct()
				.where(NAO.prefLabel).like("ael")
				.where(NAO.prefSymbol).isNot(new URIImpl("urn:symbol1"))
				.orWhere(NAO.prefLabel).like("ael")
				.where(NAO.prefSymbol).isNull();
				
		Collection<Person> results = query.results();
		assertEquals(1, results.size());
		assertEquals(p2, results.iterator().next().asURI());
	}
	
	@Test
	public void testNeqLiteral() {
		URI file1 = new URIImpl("file://somewhere/Paris.jpg");
		URI file2 = new URIImpl("file://somewhere/London.jpg");
		tripleStore.addStatement(null, file1, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file1, NAO.prefLabel, new PlainLiteralImpl("Paris.jpg"));
		tripleStore.addStatement(null, file1, NFO.fileSize, new DatatypeLiteralImpl("3304", XSD._double));
		tripleStore.addStatement(null, file2, RDF.type, NFO.FileDataObject);
		tripleStore.addStatement(null, file1, NAO.prefLabel, new PlainLiteralImpl("London.jpg"));
		tripleStore.addStatement(null, file1, NFO.fileSize, new DatatypeLiteralImpl("7273", XSD._double));

		Query<FileDataObject> query = 
			resourceStore.find(FileDataObject.class)
				.distinct()
				.where(NFO.fileSize).gt(100d)
				.where(NAO.prefLabel).isNot("Paris.jpg");
				
		Collection<FileDataObject> results = query.results();
		assertEquals(1, results.size());
	}
	
	@Test
	public void testQueryIds() {
		tripleStore.addStatement(null, new URIImpl("urn:person1"), RDF.type, PIMO.Person);
		tripleStore.addStatement(null, new URIImpl("urn:person2"), RDF.type, PIMO.Person);
		tripleStore.addStatement(null, new URIImpl("urn:person3"), RDF.type, PIMO.Person);
		
		Collection<org.ontoware.rdf2go.model.node.Resource> ids = resourceStore.find(Person.class).ids();
		assertTrue(ids.contains(new URIImpl("urn:person1")));
		assertTrue(ids.contains(new URIImpl("urn:person2")));
		assertTrue(ids.contains(new URIImpl("urn:person3")));
	}

}