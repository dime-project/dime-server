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

package eu.dime.ps.semantic.search;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NFO;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.search.impl.SparqlSearcher;

/**
 * Tests {@link SparqlSearcher}.
 */
public final class SparqlSearcherTest extends SemanticTest {

	protected Searcher searcher;
	
	private TripleStore ts;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		if (ts == null) {
			ts = createTripleStore();
		}
		ts.clear();
		searcher = new SparqlSearcher(ts);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		searcher = null;
		super.tearDown();
	}
	
	protected void loadPIMO() throws ModelRuntimeException, IOException {
		Model sinkModel = RDF2Go.getModelFactory().createModel(PIMO.NS_PIMO).open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("pimo.trig"),
				Syntax.Trig, sinkModel);
		ts.addModel(sinkModel);
		sinkModel.close();

		Model dummyModel = RDF2Go.getModelFactory().createModel().open();
		dummyModel.addStatement(dummyModel.createURI("http://example.org/propertyExample"), RDF.type, RDF.Property);
		dummyModel.addStatement(dummyModel.createURI("http://example.org/propertyExample"), RDFS.label, dummyModel.createPlainLiteral("label contains 'tag'"));
		ts.addModel(dummyModel);
		dummyModel.close();
	}

	protected void loadData() throws ModelRuntimeException, IOException {
		Model sinkModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("files-data.nt"),
				Syntax.Ntriples, sinkModel);
		ts.addModel(sinkModel);
		sinkModel.close();
	}

	@Test
	public void testSearchClasses() throws Exception {
		loadPIMO();
		
		Collection<URI> results = null;
		results = searcher.search(PIMO.NS_PIMO, "Person", true, RDFS.Class, null, 20, 0);
		assertEquals(4, results.size());
		assertTrue(results.contains(PIMO.Person));
		assertTrue(results.contains(PIMO.PersonalInformationModel));
		assertTrue(results.contains(PIMO.PersonGroup));
		assertTrue(results.contains(PIMO.PersonRole));

		// 'location' is written in some value of a property of the class definition, but not in its URI
		results = searcher.search("location", false, RDFS.Class, null, 20, 0);
		assertEquals(7, results.size());
		assertTrue(results.contains(PIMO.Location));
		assertTrue(results.contains(PIMO.Room));
		assertTrue(results.contains(PIMO.State));
		assertTrue(results.contains(PIMO.Country));
		assertTrue(results.contains(PIMO.City));
		assertTrue(results.contains(PIMO.Locatable));
		assertTrue(results.contains(PIMO.Building));
	}
	
	@Test
	public void testSearchProperties() throws Exception {
		loadPIMO();
		
		Collection<URI> results = null;
		results = searcher.search(PIMO.NS_PIMO, "tag", RDF.Property, new URI[]{RDFS.label}, null, 20, 0);
		assertEquals(3, results.size());
		assertTrue(results.contains(PIMO.hasTag));
		assertTrue(results.contains(PIMO.isTagFor));
		assertTrue(results.contains(PIMO.tagLabel));
		
		// same search but no graph/context is specified should also return
		// the property http://example.org/propertyExample
		results = searcher.search("tag", RDF.Property, new URI[]{RDFS.label});
		assertEquals(4, results.size()); // using all RDFS entailment rules, this should be 7
		assertTrue(results.contains(new URIImpl("http://example.org/propertyExample")));
		
		// 'location' is written in some value of a property of the property definition, but not in its URI
		results = searcher.search("location", false, RDF.Property);
		assertEquals(4, results.size());
		assertTrue(results.contains(PIMO.locatedWithin));
	}

	@Test
	public void testSearchResources() throws Exception {
		loadData();
		
		Collection<URI> results = null;
		results = searcher.search("levels", NFO.FileDataObject, new URI[]{NFO.fileName, NFO.belongsToContainer});
		assertEquals(1, results.size());
		assertTrue(results.contains(new URIImpl("file:/home/ismriv/example/dir1/different-levels-of-composition.png")));
	}

	@Test
	public void testSearchWithOrder() throws Exception {
		loadData();
		
		List<URIImpl> expected = Arrays.asList(
				new URIImpl("file:/home/ismriv/example/dir1/screenshot-GVS.png"),
				new URIImpl("file:/home/ismriv/example/dir1/motivation-long-tail.png"),
				new URIImpl("file:/home/ismriv/example/dir2/lego.png"),
				new URIImpl("file:/home/ismriv/example/dir1/ComplexGadgetArchitecture.png"),
				new URIImpl("file:/home/ismriv/example/dir1/different-levels-of-composition.png"),
				new URIImpl("file:/home/ismriv/example/dir1/D3.1.2_FASTComplexGadgetArchitecture.pdf"),
				new URIImpl("file:/home/ismriv/example/dir1/D2.3.2_FAST_requirements_specification.v2.2.pdf"),
				new URIImpl("file:/home/ismriv/example/dir1/D2.4.2_mediation.pdf"),
				new URIImpl("file:/home/ismriv/example/dir1/D2.2.2_ontology.pdf"),
				new URIImpl("file:/home/ismriv/example/dir1/D2.1.2_StateOfTheArt_v1.pdf"),
				new URIImpl("file:/home/ismriv/example/dir1/FAST-a-lego-like-IDE-for-web-applications.pptx"));
		Collection<URI> results = null;
		results = searcher.search("a", NFO.FileDataObject, new URI[]{NFO.fileName, NFO.belongsToContainer}, "ASC(http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#fileSize)", Searcher.NONE, 0);
		assertEquals(expected, results);
	}

}
