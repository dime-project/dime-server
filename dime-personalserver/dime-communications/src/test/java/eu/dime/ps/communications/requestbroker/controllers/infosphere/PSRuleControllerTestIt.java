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
package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.rules.transformer.RuleInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Syntax;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Message;
import eu.dime.commons.dto.Request;
import eu.dime.commons.dto.Response;
import eu.dime.ps.controllers.infosphere.manager.RuleManager;
import eu.dime.ps.semantic.model.drmo.Rule;

public class PSRuleControllerTestIt extends PSInfosphereControllerTestIt {

	@Autowired
	private RuleManager ruleManager;

	private PSRuleController controller;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// remove all data
		pimoService.clear();
		
		// set up PSRuleController
		controller = new PSRuleController();
		controller.setRuleManager(ruleManager);
	}
	
	@Test
	public void testCreate() throws Exception {
		RuleInstance expected = buildRuleInstance("controllers/rule/rule1.json");
		URI originalGuid = expected.getGuid();

		Response<RuleInstance> response = controller.create(buildRequest(expected), "@me");

		RuleInstance actual = response.getMessage().getData().getEntries().iterator().next();
		assertEquals(expected.getType(), actual.getType());
		assertEquals(expected.getName(), actual.getName());
		
		// guid should be ignored by create(), instead a new unique id should be generated
		assertTrue("GUID should not be the one from the request", !originalGuid.equals(actual.getGuid()));
	}
	
	@Test
	public void testGetAll() throws Exception {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("controllers/rule/rule1.trig");
		Model ruleModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(is, Syntax.Trig, ruleModel);
		Rule expected = new Rule(ruleModel, "http://test.org/event1", false);
		ruleManager.add(expected);
		
		Response<RuleInstance> response = controller.getAll("@me");

		Collection<RuleInstance> entries = response.getMessage().getData().getEntries();
		assertEquals(1, entries.size());
		RuleInstance actual = entries.iterator().next();
		assertEquals("http://test.org/event1", actual.getGuid().toString());
		assertEquals("rule", actual.getType());
		assertEquals("Rule 1", actual.getName());
	}
	
	@Test
	public void testGetById() throws Exception {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("controllers/rule/rule1.trig");
		Model ruleModel = RDF2Go.getModelFactory().createModel().open();
		ModelUtils.loadFromInputStream(is, Syntax.Trig, ruleModel);
		Rule expected = new Rule(ruleModel, "http://test.org/event1", false);
		ruleManager.add(expected);
		
		Response<RuleInstance> response = controller.getById("@me", "http://test.org/event1");

		Collection<RuleInstance> entries = response.getMessage().getData().getEntries();
		assertEquals(1, entries.size());
		RuleInstance actual = entries.iterator().next();
		assertEquals("http://test.org/event1", actual.getGuid().toString());
		assertEquals("rule", actual.getType());
		assertEquals("Rule 1", actual.getName());
	}

	protected RuleInstance buildRuleInstance(String file) throws JsonParseException, JsonMappingException, IOException {
		InputStream json = this.getClass().getClassLoader().getResourceAsStream(file);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, RuleInstance.class);
	}
	
	protected Request<RuleInstance> buildRequest(RuleInstance rule) {
		Request<RuleInstance> request = new Request<RuleInstance>();
		Message<RuleInstance> message = new Message<RuleInstance>();
		Data<RuleInstance> data = new Data<RuleInstance>();
		data.getEntries().add(rule);
		message.setData(data);
		request.setMessage(message);
		return request;
	}
	
}
