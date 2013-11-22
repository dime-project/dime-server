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

package eu.dime.ps.semantic.service;

import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.OntologyInvalidException;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nie.InformationElement;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.exception.PimoConfigurationException;
import eu.dime.ps.semantic.service.exception.PimoException;
import eu.dime.ps.semantic.service.impl.PimoService;
import org.junit.Ignore;

/**
 * Tests {@link PimoService}.
 * 
 */
@Ignore
public class PimoServiceTest extends SemanticTest {

	@Autowired
	protected PimoService pimoService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		pimoService.getTripleStore().clear();
		pimoService.createNewPimo();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPimoCreation() throws PimoException, PimoConfigurationException {
		// clears the triple store, to be able to test the PIM initialization
		pimoService.getTripleStore().clear();
		
		assertTrue(pimoService.checkPimoNeedsCreation());
		pimoService.createNewPimo();
		assertFalse(pimoService.checkPimoNeedsCreation());
	}
	
	@Test
	public void testCreateUri() {
		assertNotNull(pimoService.createUri());
	}
	
	@Test
	public void testCreateUriWithName() throws OntologyInvalidException {
		URI test1 = pimoService.createUriWithName("semantic web");
		assertNotNull(test1);
		assertEquals(pimoService.getUserNamespace() + "names:semantic+web", test1.toString());
		
		URI test2 = pimoService.createUriWithName("test:", "JUnit");
		assertNotNull(test2);
		assertEquals(pimoService.getUserNamespace() + "test:JUnit", test2.toString());
	}
	
	@Test(expected=OntologyInvalidException.class)
	public void testCreateClass() throws OntologyInvalidException {
		try {
			URI sport = pimoService.createClass("Sport", PIMO.Thing);
			assertNotNull(sport);
			assertEquals(pimoService.getUserNamespace() + "classes:Sport", sport.toString());

			// sets a proper name
			URI furniture = pimoService.createClass("furniture", PIMO.Thing);
			assertNotNull(furniture);
			assertEquals(pimoService.getUserNamespace() + "classes:Furniture", furniture.toString());
		} catch (OntologyInvalidException e) {
			fail(e.getMessage());
		}
		// tries to create a class which already exists
		pimoService.createClass("Sport", PIMO.Thing);
	}
	
	@Test(expected=OntologyInvalidException.class)
	public void testCreateProperty() throws OntologyInvalidException {
		try {
			URI rating = pimoService.createProperty("rating", null);
			assertNotNull(rating);
			assertEquals(pimoService.getUserNamespace() + "properties:rating", rating.toString());
			
			// sets a proper name
			URI importance = pimoService.createProperty("Importance", null);
			assertNotNull(importance);
			assertEquals(pimoService.getUserNamespace() + "properties:importance", importance.toString());
		} catch (OntologyInvalidException e) {
			fail(e.getMessage());
		}
		// tries to create a property which already exists
		pimoService.createProperty("rating", null);
	}
	
	@Test
	public void testUpdateThing() throws ResourceExistsException, NotFoundException {
		Person person = modelFactory.getPIMOFactory().createPerson();
		person.setPrefLabel("Ismael");
		pimoService.createThing(person);
		
		// makes a first update
		person.setPrefLabel("Ismael R.");
		pimoService.updateThing(person);
		
		// makes a second update
		person.setPrefLabel("Ismael Rivera");
		pimoService.updateThing(person);
	}
	
	@Test
	public void testMerge() throws Exception {
		PersonContact c11 = buildProfile("Ismael Rivera", "irivera@email.com", "123");
		PersonContact c12 = buildProfile("Ismael R.", "iissmm@email.com", "456");
		PersonContact c21 = buildProfile("ismriv", "ismriv@email.com", "000");
		PersonContact c22 = buildProfile("Ismael Rivera", "irivera@email.com", "789");
		Person p1 = buildPerson("Ismael Rivera");
		p1.setGroundingOccurrence(c11);
		p1.setOccurrence(c12);
		Person p2 = buildPerson("ismriv");
		p2.setGroundingOccurrence(c21);
		p2.setOccurrence(c22);

		resourceStore.createOrUpdate(pimoService.getPimoUri(), c11);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c12);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c21);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c22);
		pimoService.createOrUpdate(p1);
		pimoService.createOrUpdate(p2);
		
		Person merged = pimoService.merge(p1.asURI(), p2.asURI());

		assertEquals(p1.asURI(), merged.asURI());
		assertEquals("Ismael Rivera", merged.getPrefLabel());
		assertEquals(1, merged.getAllGroundingOccurrence_as().count());
		assertEquals(c11, merged.getAllGroundingOccurrence().next());
		assertEquals(4, merged.getAllOccurrence_as().count());
		
		ClosableIterator<InformationElement> occs = merged.getAllGroundingOccurrence();
		PersonContact groundingOccurrence = null;
		while (occs.hasNext()) {
			groundingOccurrence = resourceStore.get(occs.next().asURI(), PersonContact.class);
			assertEquals(1, groundingOccurrence.getAllPersonName_as().count());
			assertEquals(1, groundingOccurrence.getAllPhoneNumber_as().count());
			assertEquals(1, groundingOccurrence.getAllEmailAddress_as().count());
		}
		occs.close();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMergeNoTarget() throws Exception {
		pimoService.merge(new URIImpl("urn:person1"));
	}
	
	@Test(expected=NotFoundException.class)
	public void testMergeUnknownPerson() throws Exception {
		pimoService.merge(new URIImpl("urn:person1"), new URIImpl("urn:person2"));
	}

	@Test
	public void testMergeKeepingDataSource() throws Exception {
		Account account1 = buildAccount("Account 1");
		Account account2 = buildAccount("Account 2");
		PersonContact c1 = buildProfile("Ismael Rivera", "irivera@email.com", "123");
		c1.getModel().addStatement(c1, NIE.dataSource, account1);
		PersonContact c2 = buildProfile("ismriv", "ismriv@email.com", "000");
		c2.getModel().addStatement(c2, NIE.dataSource, account2);
		Person p1 = buildPerson("Ismael Rivera");
		p1.setGroundingOccurrence(c1);
		Person p2 = buildPerson("ismriv");
		p2.setGroundingOccurrence(c2);

		resourceStore.createOrUpdate(pimoService.getPimoUri(), account1);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), account2);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c1);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c2);
		pimoService.createOrUpdate(p1);
		pimoService.createOrUpdate(p2);

		Person merged = pimoService.merge(p1.asURI(), p2.asURI());
		
		ClosableIterator<Resource> occs = merged.getAllOccurrence();
		PersonContact occurrence = null;
		while (occs.hasNext()) {
			occurrence = resourceStore.get(occs.next().asURI(), PersonContact.class);
			assertTrue(occurrence.getModel().contains(occurrence, NIE.dataSource, Variable.ANY));
		}
		occs.close();
	}
	
	@Test
	public void testMergePreserveMasterTrustLevel() throws Exception {
		PersonContact c1 = buildProfile("Ismael Rivera", "irivera@email.com", "123");
		PersonContact c2 = buildProfile("ismriv", "ismriv@email.com", "000");
		Person p1 = buildPerson("Ismael Rivera");
		p1.setGroundingOccurrence(c1);
		p1.setTrustLevel(0.7);
		Person p2 = buildPerson("ismriv");
		p2.setGroundingOccurrence(c2);
		p2.setTrustLevel(0.55);

		resourceStore.createOrUpdate(pimoService.getPimoUri(), c1);
		resourceStore.createOrUpdate(pimoService.getPimoUri(), c2);
		pimoService.createOrUpdate(p1);
		pimoService.createOrUpdate(p2);
		
		Person merged = pimoService.merge(p1.asURI(), p2.asURI());
		assertEquals(0.7, merged.getAllTrustLevel().next().doubleValue(), 0.01);
	}
	
}