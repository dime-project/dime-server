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

package eu.dime.ps.semantic.service.context;

import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DPO;

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
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.service.exception.LiveContextException;

/**
 * Tests {@link AspectBasedStrategy}.
 */
public class AspectBasedStrategyTest extends SemanticTest {
	
	private Model previousContext;
	private Model liveContext;
	private UpdateStrategy strategy;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.previousContext = RDF2Go.getModelFactory().createModel().open();
		this.liveContext = RDF2Go.getModelFactory().createModel().open();
		this.strategy = new AspectBasedStrategy(previousContext, liveContext);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testUpdateSeveralResources() throws LiveContextException {
		List<Statement> toAdd = new ArrayList<Statement>();
		List<Statement> toRemove = new ArrayList<Statement>();
		
		// creates an aspect, and some metadata for it
		URI environment = new URIImpl("urn:uuid:"+UUID.randomUUID());
		URI temp = new URIImpl("urn:uuid:"+UUID.randomUUID());
		URI weather = new URIImpl("urn:uuid:"+UUID.randomUUID());
		toAdd.add(new StatementImpl(null, environment, RDF.type, DCON.Environment));
		toAdd.add(new StatementImpl(null, environment, DCON.currentTemperature, temp));
		toAdd.add(new StatementImpl(null, environment, DCON.currentWeather, weather));
		toAdd.add(new StatementImpl(null, temp, RDF.type, DPO.Temperature));
		toAdd.add(new StatementImpl(null, temp, DCON.temperature, new DatatypeLiteralImpl("34.0", XSD._double)));
		toAdd.add(new StatementImpl(null, weather, RDF.type, DPO.WeatherConditions));
		toAdd.add(new StatementImpl(null, weather, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)));
		
		// updates the graphs
		strategy.update(toAdd, toRemove);
		
		Resource aspectURI = null;
		URI weatherConditionsURI = null;
		URI temperatureURI = null;
		
		// checks if the graphs contains the appropriate data
		assertEquals(0, this.previousContext.size());
		
		assertEquals(7, this.liveContext.size());
		assertTrue(this.liveContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.liveContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.liveContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("34.0", XSD._double)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		
		// change something and check again
		toAdd.clear();
		toRemove.clear();
		toAdd.add(new StatementImpl(null, temp, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)));
		toRemove.add(new StatementImpl(null, temp, DCON.temperature, new DatatypeLiteralImpl("34.0", XSD._double)));
		strategy.update(toAdd, toRemove);
		
		assertEquals(5, this.previousContext.size());
		assertTrue(this.previousContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.previousContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("34.0", XSD._double)).hasNext());
		
		assertEquals(7, this.liveContext.size());
		assertTrue(this.liveContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.liveContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.liveContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		
		// change something and check again
		toAdd.clear();
		toRemove.clear();
		toAdd.add(new StatementImpl(null, weather, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)));
		strategy.update(toAdd, toRemove);
		
		assertEquals(7, this.previousContext.size());
		assertTrue(this.previousContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.previousContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.previousContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("34.0", XSD._double)).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		
		assertEquals(8, this.liveContext.size());
		assertTrue(this.liveContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.liveContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.liveContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)).hasNext());
		
		// change and update two different aspects separately
		// This strategy should result in the previous context containing both previous values of the aspect changed
		toAdd.clear();
		toRemove.clear();
		toAdd.add(new StatementImpl(null, temp, DCON.temperature, new DatatypeLiteralImpl("20.0", XSD._double)));
		toRemove.add(new StatementImpl(null, temp, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)));
		strategy.update(toAdd, toRemove);
		
		toAdd.clear();
		toRemove.clear();
		toAdd.add(new StatementImpl(null, weather, DCON.humidity, new DatatypeLiteralImpl("80", XSD._int)));
		toRemove.add(new StatementImpl(null, weather, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)));
		strategy.update(toAdd, toRemove);
		
		assertEquals(8, this.previousContext.size());
		assertTrue(this.previousContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.previousContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.previousContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)).hasNext());		
		
		assertEquals(8, this.liveContext.size());
		assertTrue(this.liveContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.liveContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.liveContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("20.0", XSD._double)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.humidity, new DatatypeLiteralImpl("80", XSD._int)).hasNext());
				
		// change something and check again
		toAdd.clear();
		toRemove.clear();
		toAdd.add(new StatementImpl(null, weather, DCON.cloudcover, new DatatypeLiteralImpl("10", XSD._int)));
		toAdd.add(new StatementImpl(null, weather, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)));
		toRemove.add(new StatementImpl(null, weather, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)));
		toRemove.add(new StatementImpl(null, weather, DCON.humidity, new DatatypeLiteralImpl("80", XSD._int)));
		strategy.update(toAdd, toRemove);
		
		assertEquals(8, this.previousContext.size());
		assertTrue(this.previousContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.previousContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.previousContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.previousContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.previousContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.previousContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("36.0", XSD._double)).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("5", XSD._int)).hasNext());
		assertTrue(this.previousContext.findStatements(weatherConditionsURI, DCON.humidity, new DatatypeLiteralImpl("80", XSD._int)).hasNext());	
		
		assertEquals(8, this.liveContext.size());
		assertTrue(this.liveContext.findStatements(environment, RDF.type, DCON.Environment).hasNext());	
		aspectURI = this.liveContext.findStatements(environment, RDF.type, DCON.Environment).next().getSubject();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).hasNext());
		weatherConditionsURI = this.liveContext.findStatements(aspectURI, DCON.currentWeather, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).hasNext());
		temperatureURI = this.liveContext.findStatements(aspectURI, DCON.currentTemperature, Variable.ANY).next().getObject().asURI();
		assertTrue(this.liveContext.findStatements(temperatureURI, RDF.type, DPO.Temperature).hasNext());
		assertTrue(this.liveContext.findStatements(temperatureURI, DCON.temperature, new DatatypeLiteralImpl("20.0", XSD._double)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, RDF.type, DPO.WeatherConditions).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.cloudcover, new DatatypeLiteralImpl("10", XSD._int)).hasNext());
		assertTrue(this.liveContext.findStatements(weatherConditionsURI, DCON.humidity, new DatatypeLiteralImpl("70", XSD._int)).hasNext());
	}
	
}
