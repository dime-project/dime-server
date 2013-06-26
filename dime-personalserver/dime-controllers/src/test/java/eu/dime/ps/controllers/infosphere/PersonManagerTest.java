package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.PersonGroupManager;
import eu.dime.ps.controllers.infosphere.manager.PersonManagerImpl;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Tests {@link PersonManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class PersonManagerTest extends InfoSphereManagerTest {

	@Autowired
	private PersonManagerImpl personManager;
	
	@Autowired
	private PersonGroupManager personGroupManager;

	@Test
	public void testGetMe() throws Exception {
		assertNotNull(personManager.getMe());
	}
	
	@Test
	public void testExist() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		personManager.add(person);
		assertTrue(personManager.exist(person.toString()));
	}
	
	@Test
	public void testFindById() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		personManager.add(person);
		Person another = personManager.get(person.asURI().toString());
		assertEquals(person, another);
	}

	@Test(expected=InfosphereException.class)
	public void testFindUnknownById() throws Exception {
		personManager.get("urn:12345");
	}

	@Test
	public void testFind() throws Exception {
		Person ismael = buildPerson("Ismael Rivera");
		Person simon = buildPerson("S. Squerri");
		personManager.add(ismael);
		personManager.add(simon);
		Collection<Person> results = personManager.find("ismael");
		assertEquals(1, results.size());
		assertEquals(ismael.asURI(), results.iterator().next().asURI());
	}
        
	@Test
	public void testListPeople() throws Exception {
		personManager.add(buildPerson("Ismael Rivera"));
		personManager.add(buildPerson("S. Squerri"));
		assertEquals(2, personManager.getAll().size());
	}
	
	@Test
	public void testAdd() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		personManager.add(person);
		Collection<Person> people = personManager.getAll();
		assertEquals(1, people.size());
		assertTrue(people.contains(person));
	}

	@Test
	public void testUpdate() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		personManager.add(person);
		
		person.setPrefLabel("S. Squerri");
		personManager.update(person);
		
		Person squerri = personManager.get(person.asURI().toString());
		assertEquals("S. Squerri", squerri.getPrefLabel());
	}
	
	@Test
	public void testUpdatePersonTwice() throws Exception {
		Person person = buildPerson("Ismael Rivera");
		personManager.add(person);
		
		person.setPrefLabel("S. Squerri");
		personManager.update(person);		
		person.setPrefLabel("S. Squerri Again");
		personManager.update(person);		
		Person squerri = personManager.get(person.asURI().toString());
		assertEquals("S. Squerri Again", squerri.getPrefLabel());
	}

	@Test
	public void testRemove() throws Exception {
		Person p1 = buildPerson("Ismael Rivera");
		Person p2 = buildPerson("S. Squerri");
		personManager.add(p1);
		personManager.add(p2);
		personManager.remove(p1.asURI().toString());
		assertEquals(1, personManager.getAll().size());
	}

	@Test
	public void testSearch() throws Exception {
		Person raul = buildPerson("Raul Gonzalez");
		Person ismael = buildPerson("Ismael Rivera");
		personManager.add(raul);
		personManager.add(ismael);
		
		Collection<Person> results = personManager.find("rivera");
		assertEquals(1, results.size());
		assertEquals(ismael, results.iterator().next());
	}

	@Test
	public void testSearchReturnsName() throws Exception {
		Person ismael = buildPerson("Ismael Rivera");
		personManager.add(ismael);
		
		Collection<Person> results = personManager.find("rivera");
		Person other = results.iterator().next();
		assertEquals("Ismael Rivera", other.getPrefLabel());
	}
	
	@Test
	public void testGetAllByGroup() throws Exception {
		Person ismael = buildPerson("Ismael Rivera");
		Person mark = buildPerson("Mark Doe");
		personManager.add(ismael);
		personManager.add(mark);

		PersonGroup friends = buildPersonGroup("Friends", ismael, mark);
		personGroupManager.add(friends);
		
		Collection<Person> results = personManager.getAllByGroup(friends);
		assertEquals(2, results.size());
		assertTrue(results.contains(ismael));
		assertTrue(results.contains(mark));
	}

}
