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

package eu.dime.ps.semantic.service;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NCO;
import ie.deri.smile.vocabulary.PIMO;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

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

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nfo.FileDataObject;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.rdf.impl.ResourceStoreImpl;
import eu.dime.ps.semantic.service.impl.ResourceMatchingServiceImpl;

/**
 * Tests {@link ResourceMatchingServiceImpl}.
 */
public final class ResourceMatchingServiceTest extends SemanticTest {

	private ResourceMatchingServiceImpl matchingService;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.tripleStore = createTripleStore();
		this.resourceStore = new ResourceStoreImpl(this.tripleStore);
		this.matchingService = new ResourceMatchingServiceImpl();
		this.matchingService.setTripleStore(tripleStore);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testCompareTwoModels() {
		Model modelA = RDF2Go.getModelFactory().createModel().open();
		Model modelB = RDF2Go.getModelFactory().createModel().open();
		
		URI a1 = new URIImpl("urn:a1");
		modelA.addStatement(a1, RDF.type, PIMO.Thing);
		modelA.addStatement(a1, NAO.prefLabel, "a");
		modelA.addStatement(a1, PIMO.partOf, new URIImpl("urn:a"));
		
		URI b1 = new URIImpl("urn:v1");
		modelB.addStatement(b1, PIMO.partOf, new URIImpl("urn:a"));
		modelB.addStatement(b1, RDF.type, PIMO.Thing);
		modelB.addStatement(b1, NAO.prefLabel, "a");

		assertFalse(modelA.isIsomorphicWith(modelB));
		assertTrue(matchingService.match(modelA, modelB));
	}
	
	@Test
	public void testMatchingBasedOnContactUID() throws ModelRuntimeException, IOException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/linkedin-connections-initial.ttl"),
				Syntax.Turtle, initial);
		tripleStore.addAll(initial.iterator());

		// loads the updated RDF data (it contains some changes resources matching to existing ones)
		TripleStore updatedData = createTripleStore(
				this.getClass().getClassLoader().getResourceAsStream("matching/linkedin-connections-updated.ttl"),
				Syntax.Turtle);
		ResourceStore updatedStore = new ResourceStoreImpl(updatedData);
		
		Collection<PersonContact> updatedContacts = updatedStore.find(PersonContact.class).results();
		Map<String, String> matchResult = matchingService.match(updatedContacts);

		PersonContact charlie = resourceStore.find(PersonContact.class).distinct().where(NCO.contactUID).is("7N49Odr3Dk").first();
		PersonContact carmelo = updatedStore.find(PersonContact.class).distinct().where(NCO.contactUID).is("7N49Odr3Dk").first();

		assertEquals(charlie.asResource().toString(), matchResult.get(carmelo.asResource().toString()));
		assertNull(matchResult.get(carmelo.getAllPersonName().next().asResource().toString()));

		PersonContact simonInitial = resourceStore.find(PersonContact.class).distinct().where(NCO.contactUID).is("czWR9bvFhQ").first();
		PersonContact simonUpdated = updatedStore.find(PersonContact.class).distinct().where(NCO.contactUID).is("czWR9bvFhQ").first();

		assertEquals(simonInitial.asResource().toString(), matchResult.get(simonUpdated.asResource().toString()));
		
		// FIXME this was buggy in the matching implementation, remove the comment once fixed 
//		assertEquals(simonInitial.getAllPersonName().next().asResource().toString(), matchResult.getMappings().get(simonUpdated.getAllPersonName().next().asResource().toString()));
	}

	@Test
	public void testMatchingBasedOnExternalIdentifier() throws ModelRuntimeException, IOException {
		// loads an example of RDF data for a service request (as coming from the transformer)
		Model initial = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(
				this.getClass().getClassLoader().getResourceAsStream("matching/externalIdentifier-initial.ttl"),
				Syntax.Turtle, initial);
		tripleStore.addAll(initial.iterator());

		// loads the updated RDF data (it contains some changes resources matching to existing ones)
		TripleStore updatedData = createTripleStore(
				this.getClass().getClassLoader().getResourceAsStream("matching/externalIdentifier-updated.ttl"),
				Syntax.Turtle);
		ResourceStore updatedStore = new ResourceStoreImpl(updatedData);

		Collection<FileDataObject> updatedFiles = updatedStore.find(FileDataObject.class).results();
		Map<String, String> matchResult = matchingService.match(updatedFiles);

		FileDataObject file1 = resourceStore.find(FileDataObject.class).distinct().where(NAO.externalIdentifier).is("12345").first();
		FileDataObject file1Updated = updatedStore.find(FileDataObject.class).distinct().where(NAO.externalIdentifier).is("12345").first();
		FileDataObject file2 = resourceStore.find(FileDataObject.class).distinct().where(NAO.externalIdentifier).is("67890").first();
		FileDataObject file2Updated = updatedStore.find(FileDataObject.class).distinct().where(NAO.externalIdentifier).is("67890").first();

		assertEquals(file1.asResource().toString(), matchResult.get(file1Updated.asResource().toString()));
		assertEquals(file2.asResource().toString(), matchResult.get(file2Updated.asResource().toString()));
	}
	
}