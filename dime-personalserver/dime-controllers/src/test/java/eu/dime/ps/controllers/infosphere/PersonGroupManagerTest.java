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

package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.infosphere.manager.PersonGroupManagerImpl;
import eu.dime.ps.controllers.infosphere.manager.PersonManager;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.model.pimo.PersonGroup;

/**
 * Tests {@link PersonGroupManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class PersonGroupManagerTest extends InfoSphereManagerTest {

	@Autowired
	private PersonManager personManager;
	
	@Autowired
	private PersonGroupManagerImpl personGroupManager;

	@Test
	public void testExist() throws Exception {
		PersonGroup group = buildPersonGroup("Friends");
		personGroupManager.add(group);
		assertTrue(personGroupManager.exist(group.toString()));
	}

	@Test
	public void testAddAdhocGroup() throws Exception {
		Person p1 = buildPerson("Ismael");
		Person p2 = buildPerson("Will");
		personManager.add(p1);
		personManager.add(p2);
		PersonGroup g1 = buildPersonGroup("International Semantic Web Conference", p1, p2);
		personGroupManager.addAdhocGroup(g1);
		
		Collection<PersonGroup> groups = personGroupManager.getAdhocGroups();  
		assertEquals(1, groups.size());
		assertEquals(g1.asURI(), groups.iterator().next().asURI());
		assertEquals(2, groups.iterator().next().getAllMembers_as().count());

		// also call to get all person groups should return this group
		assertEquals(1, personGroupManager.getAll().size());
	}

	@Test
	public void testAdd() throws Exception {
		Person p1 = buildPerson("Ismael");
		Person p2 = buildPerson("Will");
		personManager.add(p1);
		personManager.add(p2);
		PersonGroup g1 = buildPersonGroup("Friends", p1, p2);
		personGroupManager.add(g1);
		
		Collection<PersonGroup> groups = personGroupManager.getAll();  
		assertEquals(1, groups.size());
		assertEquals(g1.asURI(), groups.iterator().next().asURI());
		assertEquals(2, groups.iterator().next().getAllMembers_as().count());
		// checks who's the creator of the group
		assertEquals(personManager.getMe().asURI(), groups.iterator().next().getCreator().asURI());
	}

	@Test
	public void testUpdate() throws Exception {
		Person p1 = buildPerson("Ismael");
		Person p2 = buildPerson("Will");
		personManager.add(p1);
		personManager.add(p2);
		PersonGroup g1 = buildPersonGroup("F...", p1, p2);
		personGroupManager.add(g1);

		g1.setPrefLabel("Friends");
		g1.addMember(buildPerson("Keith"));
		personGroupManager.update(g1);
		PersonGroup other = personGroupManager.get(g1.asURI().toString());
		assertEquals("Friends", other.getPrefLabel());
		assertEquals(3, other.getAllMembers_as().count());
	}

	@Test
	public void testUpdateTwice() throws Exception {
		Person p1 = buildPerson("Ismael");
		Person p2 = buildPerson("Will");
		personManager.add(p1);
		personManager.add(p2);
		PersonGroup g1 = buildPersonGroup("F...", p1, p2);
		personGroupManager.add(g1);

		g1.setPrefLabel("Friends");
		g1.addMember(buildPerson("Keith"));
		personGroupManager.update(g1);
		
		g1.setPrefLabel("Friends2");
		personGroupManager.update(g1);
		PersonGroup other2 = personGroupManager.get(g1.asURI().toString());
		assertEquals("Friends2", other2.getPrefLabel());
		assertEquals(3, other2.getAllMembers_as().count());
	}

	@Test
	public void testRemove() throws Exception {
		PersonGroup g1 = buildPersonGroup("Friends", buildPerson("Ismael"));
		PersonGroup g2 = buildPersonGroup("Co-workers", buildPerson("Will"));
		personGroupManager.add(g1);
		personGroupManager.add(g2);
		personGroupManager.remove(g1.asURI().toString());
		assertEquals(1, personGroupManager.getAll().size());
	}
	
}
