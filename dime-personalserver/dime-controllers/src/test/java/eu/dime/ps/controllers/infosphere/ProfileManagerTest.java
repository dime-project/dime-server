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

package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import ie.deri.smile.vocabulary.NIE;
import ie.deri.smile.vocabulary.PIMO;

import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Variable;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.controllers.infosphere.manager.ProfileManagerImpl;
import eu.dime.ps.semantic.model.nco.Contact;
import eu.dime.ps.semantic.model.nco.Hobby;
import eu.dime.ps.semantic.model.nco.PersonContact;
import eu.dime.ps.semantic.model.nie.DataSource;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Tests {@link ProfileManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class ProfileManagerTest extends InfoSphereManagerTest {

	@Autowired
	private PersonManager personManager;

	@Autowired
	private ProfileManagerImpl profileManager;

	@Test(expected=InfosphereException.class)
	public void testGetUnknown() throws Exception {
		profileManager.get("urn:12345");
	}

	@Test
	public void testExist() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		
		personManager.add(person);
		profileManager.add(person, profile, false);
		assertTrue(profileManager.exist(profile.toString()));
	}

	@Test
	public void testAdd() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		
		personManager.add(person);
		profileManager.add(person, profile, false);
		Contact another = profileManager.get(profile.asURI().toString());
		assertEquals(profile.asURI(), another.asURI());
		assertTrue(profile.hasEmailAddress());
		assertTrue(profile.hasPhoneNumber());
	}
	
	@Test
	public void testGetAllByPerson() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		
		personManager.add(person);
		profileManager.add(person, profile, false);
		Collection<PersonContact> another = profileManager.getAllByPerson(person);
		assertEquals(profile.asURI(), another.iterator().next().asURI());
		assertTrue(profile.hasEmailAddress());
		assertTrue(profile.hasPhoneNumber());
	}


	@Test
	public void testAddNotDuplicatingPerson() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		personManager.add(person);
		profileManager.add(person, profile, false);

		PersonContact result = profileManager.get(profile.asResource().toString());
		ClosableIterator<Statement> it = result.getModel().findStatements(Variable.ANY, PIMO.occurrence, profile.asResource());
		while(it.hasNext()) {
			assertEquals(person.asResource(), it.next().getSubject().asResource()); 
		}
		it.close();
	}

	@Test
	public void testGetWithPimoOcurrence() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		personManager.add(person);
		profileManager.add(person, profile, false);

		PersonContact result = profileManager.get(profile.asResource().toString());
		result.getModel().contains(person.asResource(), PIMO.occurrence, profile.asResource());
	}
	
	@Test
	public void testUpdate() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		personManager.add(person);
		profileManager.add(person, profile, false);
		
		Hobby football = modelFactory.getNCOFactory().createHobby();
		football.setPrefLabel("Football");
		profile.addHobby(football);

		profileManager.update(profile);
		PersonContact saved = profileManager.get(profile.toString());
		assertNotNull(saved);
		assertEquals(football.asURI(), profile.getAllHobby_asNode().next());
		assertTrue(profile.hasPersonName());
		assertTrue(profile.hasEmailAddress());
		assertTrue(profile.hasPhoneNumber());
	}

	@Test
	public void testUpdateKeepRelationToPerson() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		personManager.add(person);
		profileManager.add(person, profile, false);
		
		profile.setPrefLabel("Other");
		profileManager.update(profile);
		
		Person saved = personManager.get(person.toString());
		assertEquals(1, saved.getAllOccurrence_asNode_().count());
		assertEquals(profile.asURI(), saved.getAllOccurrence_asNode().next().asURI());
	}

	@Test(expected=InfosphereException.class)
	public void testRemove() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		
		try {
			personManager.add(person);
			profileManager.add(person, profile, false);
		
			assertNotNull(profileManager.get(profile.toString()));
			profileManager.remove(profile.toString());
		} catch (InfosphereException e) {
			fail(e.getMessage());
		}
		
		assertNull(profileManager.get(profile.toString()));
	}
	
	@Test(expected=InfosphereException.class)
	public void testUpdateFromDataSource() throws Exception {
		DataSource twitter = buildDataSource("Twitter");
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		profile.getModel().addStatement(profile, NIE.dataSource, twitter);
		
		resourceStore.create(twitter);
		personManager.add(person);
		profileManager.add(person, profile, false);
		
		profileManager.update(profile); // should fail
	}

	@Test(expected=InfosphereException.class)
	public void testRemoveFromDataSource() throws Exception {
		DataSource twitter = buildDataSource("Twitter");
		Person person = buildPerson("Ismael Rivera");
		PersonContact profile = buildProfile("Ismael Rivera", "ismael@email.com", "0913333");
		profile.getModel().addStatement(profile, NIE.dataSource, twitter);
		
		try {
			resourceStore.create(twitter);
			personManager.add(person);
			profileManager.add(person, profile, false);
		
			assertNotNull(profileManager.get(profile.toString()));
		} catch (InfosphereException e) {
			fail(e.getMessage());
		}
		
		profileManager.remove(profile.toString()); // should fail
	}

}
