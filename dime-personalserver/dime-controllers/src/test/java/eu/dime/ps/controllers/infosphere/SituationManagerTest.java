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

import ie.deri.smile.vocabulary.DCON;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.SituationManagerImpl;
import eu.dime.ps.semantic.model.dcon.Situation;

/**
 * Tests {@link SituationManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class SituationManagerTest extends InfoSphereManagerTest {

	@Autowired
	private SituationManagerImpl situationManager;
	
	protected Situation buildSituation(String name) {
		Situation situation = modelFactory.getDCONFactory().createSituation();
		situation.setPrefLabel(name);
		return situation;
	}

	@Test
	public void testExist() throws Exception {
		Situation situation = buildSituation("di.me meeting");
		situationManager.add(situation);
		assertTrue(situationManager.exist(situation.toString()));
	}

	@Test
	public void testAddGetByName() throws Exception {
		Situation s1 = buildSituation("di.me meeting");
		Situation s2 = buildSituation("Conference");
		situationManager.add(s1);
		situationManager.add(s2);
		
		Situation result = situationManager.getByName("Conference");
		assertEquals(s2.asURI(), result.asURI());
	}

	@Test
	public void testAddGetAll() throws Exception {
		Situation s1 = buildSituation("di.me meeting");
		Situation s2 = buildSituation("Conference");
		situationManager.add(s1);
		situationManager.add(s2);

		assertEquals(2, situationManager.getAll().size());
		assertTrue(situationManager.getAll().contains(s1));
		assertTrue(situationManager.getAll().contains(s2));
	}
	
	@Test
	public void testActivate() throws Exception {
		Situation s1 = buildSituation("di.me meeting");
		Situation s2 = buildSituation("Conference");
		situationManager.add(s1);
		situationManager.add(s2);
		
		situationManager.activate(s1.asURI().toString());

		s1 = situationManager.get(s1.asURI().toString());
		assertTrue(s1.getModel().contains(situationManager.getMe(), DCON.hasSituation, s1.asURI()));

		s2 = situationManager.get(s2.asURI().toString());
		assertFalse(s2.getModel().contains(situationManager.getMe(), DCON.hasSituation, s2.asURI()));
	}

	@Test
	public void testMultipleActive() throws Exception {
		Situation s1 = buildSituation("di.me meeting");
		Situation s2 = buildSituation("Conference");
		situationManager.add(s1);
		situationManager.add(s2);
		
		situationManager.activate(s1.asURI().toString());
		situationManager.activate(s2.asURI().toString());

		s1 = situationManager.get(s1.asURI().toString());
		assertTrue(s1.getModel().contains(situationManager.getMe(), DCON.hasSituation, s1.asURI()));

		s2 = situationManager.get(s2.asURI().toString());
		assertTrue(s2.getModel().contains(situationManager.getMe(), DCON.hasSituation, s2.asURI()));
	}

	@Test
	public void testDeactivate() throws Exception {
		Situation s1 = buildSituation("di.me meeting");
		Situation s2 = buildSituation("Conference");
		situationManager.add(s1);
		situationManager.add(s2);
		
		situationManager.activate(s1.asURI().toString());
		situationManager.deactivate(s1.asURI().toString());
		
		s1 = situationManager.get(s1.asURI().toString());
		assertFalse(s1.getModel().contains(situationManager.getMe(), DCON.hasSituation, s1.asURI()));

		s2 = situationManager.get(s2.asURI().toString());
		assertFalse(s2.getModel().contains(situationManager.getMe(), DCON.hasSituation, s2.asURI()));
	}

	@Test
	public void testUpdate() throws Exception {
		Situation situation = buildSituation("di.me mee");
		situationManager.add(situation);

		// change situation prefLabel and update
		Situation saved = situationManager.get(situation.toString());
		saved.setPrefLabel("di.me meeting");
		situationManager.update(saved);
		
		// get situation from manager and assert its label has changed
		Situation updated = situationManager.get(situation.toString());
		assertEquals("di.me meeting", updated.getPrefLabel());
	}
	
	@Test
	public void testUpdateSameLabel() throws Exception {
		Situation situation = buildSituation("di.me meeting");
		situationManager.add(situation);

		// change nothing and update
		Situation saved = situationManager.get(situation.toString());
		situationManager.update(saved);
		
		// get situation from manager and assert its label is as expected
		Situation updated = situationManager.get(situation.toString());
		assertEquals("di.me meeting", updated.getPrefLabel());
	}
	
	@Test
	@ExpectedException(InfosphereException.class)
	public void testRemove() throws Exception {
		Situation situation = buildSituation("di.me meeting");
		try {
			situationManager.add(situation);
			situationManager.remove(situation.toString());
		} catch (InfosphereException e) {
			fail(e.getMessage());
		}
		
		// getting a deleted situation should throw an InfosphereException
		situationManager.get(situation.toString());
	}
	
}