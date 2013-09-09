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

import ie.deri.smile.rdf.util.ModelUtils;

import java.io.IOException;

import org.junit.Test;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.RuleManagerImpl;
import eu.dime.ps.semantic.model.drmo.Rule;

/**
 * Tests {@link RuleManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class RuleManagerTest extends InfoSphereManagerTest {

	@Autowired
	private RuleManagerImpl ruleManager;

	private static Rule RULE_1 = null;
	static {
		try {
			Model ruleModel = RDF2Go.getModelFactory().createModel().open();
			ModelUtils.loadFromInputStream(
					RuleManagerTest.class.getClassLoader().getResourceAsStream("infosphere/rules/rule1.trig"),
					Syntax.Trig, ruleModel);
			RULE_1 = new Rule(ruleModel, new URIImpl("urn:usecase:changingTrust"), false);
		} catch (IOException e) {
			throw new RuntimeException("Test initialization failed!", e);
		}
	}
	
	private static Rule RULE_2 = null;
	static {
		try {
			Model ruleModel = RDF2Go.getModelFactory().createModel().open();
			ModelUtils.loadFromInputStream(
					RuleManagerTest.class.getClassLoader().getResourceAsStream("infosphere/rules/rule2.trig"),
					Syntax.Trig, ruleModel);
			RULE_2 = new Rule(ruleModel, new URIImpl("urn:usecase:sharing"), false);
		} catch (IOException e) {
			throw new RuntimeException("Test initialization failed!", e);
		}
	}
	
	@Test
	public void testExist() throws Exception {
		ruleManager.add(RULE_1);
		ruleManager.add(RULE_2);
		assertTrue(ruleManager.exist(RULE_1.toString()));
		assertTrue(ruleManager.exist(RULE_2.toString()));
	}
	
	@Test
	public void testFindById() throws Exception {
		ruleManager.add(RULE_1);
		Rule rule = ruleManager.get(RULE_1.toString());
		assertNotNull(rule);
		assertEquals(rule.asURI(), RULE_1.asURI());
	}

	@Test(expected=InfosphereException.class)
	public void testFindUnknownById() throws Exception {
		ruleManager.get("urn:12345");
	}

	@Test
	public void testFindByIdFullMetadata() throws Exception {
		ruleManager.add(RULE_1);
		Rule rule = ruleManager.get(RULE_1.toString());
		Model expected = rule.getModel();
		Model metadata = rule.getModel();
		
		assertEquals(expected.size(), metadata.size());
		ClosableIterator<Statement> statements = expected.iterator();
		while (statements.hasNext()) {
			assertTrue(metadata.contains(statements.next()));
		}
		statements.close();
	}

	@Test
	public void testGetAll() throws Exception {
		ruleManager.add(RULE_1);
		ruleManager.add(RULE_2);
		assertEquals(2, ruleManager.getAll().size());
	}
	
	@Test
	public void testAdd() throws Exception {
		assertFalse(ruleManager.exist(RULE_1.toString()));
		ruleManager.add(RULE_1);
		assertTrue(ruleManager.exist(RULE_1.toString()));
	}

	@Test
	public void testUpdate() throws Exception {
		ruleManager.add(RULE_1);

		Rule expected = ruleManager.get(RULE_1.toString());
		expected.setPrefLabel("My rule");
		ruleManager.update(expected);
		
		Rule rule = ruleManager.get(RULE_1.toString());
		assertEquals(expected.getPrefLabel(), rule.getPrefLabel());
	}
	
	@Test
	public void testRemove() throws Exception {
		ruleManager.add(RULE_1);
		assertTrue(ruleManager.exist(RULE_1.toString()));
		ruleManager.remove(RULE_1.toString());
		assertFalse(ruleManager.exist(RULE_1.toString()));
	}

	@Test
	public void testRemoveAllMetadata() throws Exception {
		ruleManager.add(RULE_1);
		ruleManager.remove(RULE_1.toString());
		assertFalse(ruleManager.exist(RULE_1.toString()));
		
		ClosableIterator<Statement> statements = RULE_1.getModel().iterator();
		while (statements.hasNext()) {
			Statement stmt = statements.next();
			assertFalse("Contains triple from rule metadata: " + stmt, tripleStore.contains(stmt));
		}
		statements.close();
	}

}
