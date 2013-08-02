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

package eu.dime.ps.semantic.rdf;

import ie.deri.smile.rdf.impl.TripleStoreImpl;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import eu.dime.ps.semantic.SemanticTest;

/**
 * Test {@link TripleStoreImpl}.
 */
public class TripleStoreImplTest extends SemanticTest {

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRDFSReasoning() {
		URI ontGraph = new URIImpl("http://example.org#");
		URI person = new URIImpl("http://example.org#Person");
		URI director = new URIImpl("http://example.org#Director");
		URI actor = new URIImpl("http://example.org#Actor");
		tripleStore.addStatement(ontGraph, person, RDF.type, RDFS.Class);
		tripleStore.addStatement(ontGraph, director, RDF.type, RDFS.Class);
		tripleStore.addStatement(ontGraph, director, RDFS.subClassOf, person);
		tripleStore.addStatement(ontGraph, actor, RDF.type, RDFS.Class);
		tripleStore.addStatement(ontGraph, actor, RDFS.subClassOf, person);
		
		URI[] instances = new URI[]{new URIImpl("urn:actor:a"), new URIImpl("urn:director:a")};  
		tripleStore.addStatement(new URIImpl("urn:data-graph"), instances[0], RDF.type, actor);
		tripleStore.addStatement(new URIImpl("urn:data-graph"), instances[1], RDF.type, director);

		ClosableIterator<QueryRow> it = null;
		
		// urn:actor:a & urn:director:a are returned by the query, both are a instances of 
		// subclasses of 'person'
		it = tripleStore.sparqlSelect(
				"SELECT ?who WHERE { ?who "+RDF.type.toSPARQL()+" "+person.toSPARQL()+". }")
				.iterator();
		assertTrue(it.hasNext());
		while (it.hasNext()) {
			URI who = it.next().getValue("who").asURI();
			assertTrue(ArrayUtils.contains(instances, who));
		}
		it.close();
	}
	
//	@Test
//	public void testGetAndCast() throws Exception {
//		TestDataLoader loader = new TestDataLoader();
//		loader.preparePimoService("juan", tripleStore);
//		
//		Resource email = tripleStore.get(new URIImpl("mailto:jmartinez@gmail.com"));
//		assertTrue(email instanceof EmailAddress);
//		
//		Resource file = tripleStore.get(new URIImpl("file:/home/juamar/photos/Juan%20Martinez.png"));
//		assertFalse(file instanceof FileDataObject);
//	}

}
