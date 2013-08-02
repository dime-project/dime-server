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

package eu.dime.ps.semantic.service.logging;

import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DUHO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.exception.ResourceExistsException;
import eu.dime.ps.semantic.model.dcon.Environment;
import eu.dime.ps.semantic.model.nso.AccessSpace;
import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

public class HistoryLogServiceTest extends SemanticTest {

	@Autowired
	protected HistoryLogService historyLogService;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testCreateLogForContext() throws ResourceExistsException {
		URI liveContext = new URIImpl("urn:live-context");
		Environment environment = modelFactory.getDCONFactory().createEnvironment();
		URI temperatureUri = new URIImpl("urn:temperature:123");
		environment.setCurrentTemperature(temperatureUri);
		
		resourceStore.createGraph(liveContext);
		resourceStore.create(liveContext, environment);
		
		URI logUri = historyLogService.createLogForContext(liveContext);
		URI metadataUri = tripleStore.getOrCreateMetadataGraph(logUri);

		assertTrue(tripleStore.containsStatements(metadataUri, logUri, RDF.type, DUHO.ContextLog));
		assertTrue(tripleStore.containsStatements(metadataUri, logUri, DUHO.timestamp, Variable.ANY));
		assertFalse(tripleStore.containsStatements(logUri, liveContext, Variable.ANY, Variable.ANY));
		assertTrue(tripleStore.containsStatements(logUri, environment.asResource(), RDF.type, DCON.Environment));
		assertTrue(tripleStore.containsStatements(logUri, environment.asResource(), DCON.currentTemperature, temperatureUri));
		assertFalse(tripleStore.containsStatements(logUri, Variable.ANY, NAO.hasDataGraph, Variable.ANY));
		assertFalse(tripleStore.containsStatements(logUri, Variable.ANY, NAO.isDataGraphFor, Variable.ANY));
	}
	
	@Test
	public void testCreateLogForPrivacyPreference() throws ResourceExistsException {
		URI testGraph = new URIImpl("test:graph");
		URI group1 = new URIImpl("urn:group:1");
		URI person3 = new URIImpl("urn:person:3");
		URI person14 = new URIImpl("urn:person:14");
		PrivacyPreference privacyPref = modelFactory.getPPOFactory().createPrivacyPreference();
		privacyPref.setPrefLabel("example");
		AccessSpace accessSpace = modelFactory.getNSOFactory().createAccessSpace();
		accessSpace.addIncludes(group1);
		accessSpace.addExcludes(person3);
		privacyPref.setAccessSpace(accessSpace);
		resourceStore.createOrUpdate(testGraph, privacyPref);
		resourceStore.createOrUpdate(testGraph, accessSpace);
		
		URI logUri = historyLogService.createLogForPrivacyPreference(privacyPref);
		URI metadataUri = tripleStore.getOrCreateMetadataGraph(logUri);

		accessSpace.addIncludes(person14);
		resourceStore.createOrUpdate(testGraph, accessSpace);
		
		assertTrue(tripleStore.containsStatements(metadataUri, logUri, RDF.type, DUHO.PrivacyPreferenceLog));
		assertTrue(tripleStore.containsStatements(logUri, privacyPref, RDF.type, PPO.PrivacyPreference));
		assertTrue(tripleStore.containsStatements(logUri, privacyPref, PPO.hasAccessSpace, accessSpace));
		assertTrue(tripleStore.containsStatements(logUri, accessSpace, NSO.includes, group1));
		assertTrue(tripleStore.containsStatements(logUri, accessSpace, NSO.excludes, person3));
		assertFalse(tripleStore.containsStatements(logUri, accessSpace, NSO.includes, person14));
		assertTrue(tripleStore.containsStatements(testGraph, accessSpace, NSO.includes, person14));
	}
	
}