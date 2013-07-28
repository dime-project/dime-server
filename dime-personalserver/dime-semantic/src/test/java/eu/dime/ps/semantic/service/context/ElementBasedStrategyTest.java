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

package eu.dime.ps.semantic.service.context;

import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.impl.StatementImpl;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.service.exception.LiveContextException;

/**
 * Tests {@link ElementBasedStrategy}.
 */
public class ElementBasedStrategyTest extends SemanticTest {

	private Model previousContext;
	private Model liveContext;
	private UpdateStrategy strategy;

	private static final URI ENVIRONMENT = new URIImpl("urn:environment");
	private static final URI PEERS = new URIImpl("urn:peers");
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		previousContext = RDF2Go.getModelFactory().createModel().open();
		liveContext = RDF2Go.getModelFactory().createModel().open();
		strategy = new ElementBasedStrategy(previousContext, liveContext);
		
		liveContext.addStatement(ENVIRONMENT, RDF.type, DCON.Environment);
		liveContext.addStatement(PEERS, RDF.type, DCON.Peers);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testAddElementToLiveContext() throws Exception {
		
		// a new element is added to the live context; previous context is empty
		
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI ismael = new URIImpl("urn:ismael");
		toAdd.add(new StatementImpl(null, PEERS, DCON.nearbyPerson, ismael));

		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		assertTrue(liveContext.contains(PEERS, DCON.nearbyPerson, ismael));
		assertFalse(previousContext.contains(PEERS, DCON.nearbyPerson, Variable.ANY));
	}

	@Test
	public void testAddSecondElementToLiveContext() throws Exception {
		
		// if a new element goes into the live context, the existing elements
		// are copied to the previous context if they don't yet exist.
		
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI ismael = new URIImpl("urn:ismael");
		toAdd.add(new StatementImpl(null, PEERS, DCON.nearbyPerson, ismael));

		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		URI simon = new URIImpl("urn:simon");
		toAdd.add(new StatementImpl(null, PEERS, DCON.nearbyPerson, simon));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(liveContext.contains(PEERS, DCON.nearbyPerson, ismael));
		assertTrue(liveContext.contains(PEERS, DCON.nearbyPerson, simon));
		assertTrue(previousContext.contains(PEERS, DCON.nearbyPerson, ismael));
		assertFalse(previousContext.contains(PEERS, DCON.nearbyPerson, simon));
	}

	@Test
	public void testAddRemoveElementFromLiveContext() throws Exception {
		
		// an element is removed from the live context, it didn't exist before in the
		// previous context, but should exist after being removed
		
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI ismael = new URIImpl("urn:ismael");
		toAdd.add(new StatementImpl(null, PEERS, DCON.nearbyPerson, ismael));

		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		toRemove.add(new StatementImpl(null, PEERS, DCON.nearbyPerson, ismael));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(previousContext.contains(PEERS, DCON.nearbyPerson, ismael));
		assertFalse(liveContext.contains(PEERS, DCON.nearbyPerson, Variable.ANY));
	}
	
	@Test
	public void testAddUpdateRemoveElementFromLiveContext() throws Exception {
	
		// an element is removed from the live context, it had a past value in the previous context,
		// and it gets overriden by the value of the live context before being removed

		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI temperature = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentTemperature, temperature));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));

		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertFalse(previousContext.contains(ENVIRONMENT, DCON.currentTemperature, temperature));
		assertFalse(previousContext.contains(temperature, Variable.ANY, Variable.ANY));

		toRemove.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("39")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("39")));
		assertTrue(previousContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
	}


	@Test
	public void testCombinedAddition() throws LiveContextException {
		
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI temperature = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentTemperature, temperature));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));

		URI weather = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentWeather, weather));
		toAdd.add(new StatementImpl(null, weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(liveContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertTrue(liveContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertTrue(liveContext.contains(weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		assertFalse(previousContext.contains(ENVIRONMENT, DCON.currentWeather, Variable.ANY));
		assertFalse(previousContext.contains(temperature, DCON.temperature, Variable.ANY));
		assertFalse(previousContext.contains(weather, DCON.cloudcover, Variable.ANY));
	}

	@Test
	public void testCombinedAdditionAndUpdate() throws LiveContextException {
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI temperature = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentTemperature, temperature));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));

		URI weather = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentWeather, weather));
		toAdd.add(new StatementImpl(null, weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		// above code is tested in testCombinedAddition

		toRemove.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("36")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("36")));
		assertTrue(previousContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertFalse(previousContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertFalse(previousContext.contains(weather, DCON.cloudcover, Variable.ANY));
	}
	
	@Test
	public void testSeveralAddAndUpdate() throws LiveContextException {
		
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();

		URI temperature = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentTemperature, temperature));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		URI weather = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentWeather, weather));
		toAdd.add(new StatementImpl(null, weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(liveContext.contains(ENVIRONMENT, DCON.currentTemperature, temperature));
		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertTrue(liveContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertTrue(liveContext.contains(weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		assertTrue(previousContext.contains(ENVIRONMENT, DCON.currentTemperature, temperature));
		assertTrue(previousContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertFalse(previousContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertFalse(previousContext.contains(weather, DCON.cloudcover, Variable.ANY));

		toRemove.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("34")));
		toAdd.add(new StatementImpl(null, temperature, DCON.temperature, new PlainLiteralImpl("36")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();
		
		assertTrue(liveContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("36")));
		assertTrue(liveContext.contains(weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		assertTrue(previousContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertFalse(previousContext.contains(ENVIRONMENT, DCON.currentWeather, weather));
		assertFalse(previousContext.contains(weather, DCON.cloudcover, new PlainLiteralImpl("5")));
		
		URI altitude = new URIImpl("urn:uuid:" + UUID.randomUUID());
		toAdd.add(new StatementImpl(null, ENVIRONMENT, DCON.currentAbsoluteAltitude, altitude));
		toAdd.add(new StatementImpl(null, altitude, DCON.altitude, new PlainLiteralImpl("628")));
		
		strategy.update(toAdd, toRemove);
		toAdd.clear();
		toRemove.clear();

		assertTrue(liveContext.contains(ENVIRONMENT, DCON.currentAbsoluteAltitude, altitude));
		assertTrue(liveContext.contains(altitude, DCON.altitude, new PlainLiteralImpl("628")));
		assertTrue(previousContext.contains(temperature, DCON.temperature, new PlainLiteralImpl("34")));
		assertTrue(previousContext.contains(weather, DCON.cloudcover, new PlainLiteralImpl("5")));
	}
	
}
