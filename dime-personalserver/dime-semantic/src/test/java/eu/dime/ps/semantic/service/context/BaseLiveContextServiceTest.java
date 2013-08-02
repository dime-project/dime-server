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

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.semantic.SemanticTest;
import eu.dime.ps.semantic.model.dcon.Attention;
import eu.dime.ps.semantic.model.dcon.Connectivity;
import eu.dime.ps.semantic.model.dcon.Environment;
import eu.dime.ps.semantic.model.dcon.Peers;
import eu.dime.ps.semantic.model.dcon.Schedule;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.model.ddo.Bluetooth;
import eu.dime.ps.semantic.model.dpo.Temperature;
import eu.dime.ps.semantic.model.pimo.Person;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.semantic.service.exception.LiveContextException;
import eu.dime.ps.semantic.service.impl.PimoService;


/**
 * Tests {@link LiveContextServiceBase}.
 * 
 * Tests the basic operations all LiveContextService implementations
 * should provide: retrieval of information in the context graphs,
 * and updating the information of the live context graph.
 */
public class BaseLiveContextServiceTest extends SemanticTest {

	@Autowired
	protected PimoService pimoService;

	protected LiveContextService lcs;

	private static URI TEST_DATASOURCE = new URIImpl("urn:test:datasource");
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		lcs = new LiveContextServiceImpl(pimoService, SnapshotBasedStrategy.class);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testGetAspects() {
		Schedule schedule = lcs.get(Schedule.class);
		assertNotNull(schedule);
		// different calls should return same instance
		assertEquals(schedule, lcs.get(Schedule.class));

		Peers peers = lcs.get(Peers.class);
		assertNotNull(peers);
		assertEquals(peers, lcs.get(Peers.class));
		
		Environment environment = lcs.get(Environment.class);
		assertNotNull(environment);
		assertEquals(environment, lcs.get(Environment.class));
		
		Attention attention = lcs.get(Attention.class);
		assertNotNull(attention);
		assertEquals(attention, lcs.get(Attention.class));
		
		SpaTem spatem = lcs.get(SpaTem.class);
		assertNotNull(spatem);
		assertEquals(spatem, lcs.get(SpaTem.class));
		
		Connectivity connectivity = lcs.get(Connectivity.class);
		assertNotNull(connectivity);
		assertEquals(connectivity, lcs.get(Connectivity.class));
		
		State state = lcs.get(State.class);
		assertNotNull(state);
		assertEquals(state, lcs.get(State.class));
	}

	@Test
	public void testUpdatePeers() throws LiveContextException {
		// two people for testing
		Person ismael = modelFactory.getPIMOFactory().createPerson();
		Person simon = modelFactory.getPIMOFactory().createPerson();
		
		// get the Peers instance
		Peers peers = lcs.get(Peers.class);
		// verifies it has no nearby peers
		assertEquals(0, peers.getAllNearbyPerson_as().count());
		
		// adds 2 peers, updates the live context and assert the
		// peers have been attached
		lcs.getSession(TEST_DATASOURCE).add(Peers.class, DCON.nearbyPerson, ismael);
		lcs.getSession(TEST_DATASOURCE).add(Peers.class, DCON.nearbyPerson, simon);

		Peers updatedPeers = lcs.get(Peers.class);
		assertEquals(2, updatedPeers.getAllNearbyPerson_as().count());
	}
	
	@Test
	public void testUpdateConnectivity() throws Exception {
		Bluetooth bluetooth = modelFactory.getDDOFactory().createBluetooth();
		bluetooth.setPrefLabel("494dj3Z13");
		bluetooth.setSignal(new Float(4f));
		
		Connectivity connectivity = lcs.get(Connectivity.class);
		assertNotNull(connectivity);

		lcs.getSession(TEST_DATASOURCE).add(Connectivity.class, DCON.connection, bluetooth);

		connectivity = lcs.get(Connectivity.class);
		assertTrue(connectivity.hasConnection());
		assertEquals(bluetooth, connectivity.getAllConnection().next());
		
		Bluetooth bt = lcs.get(bluetooth.asURI(), Bluetooth.class, TEST_DATASOURCE);
		assertEquals("494dj3Z13", bt.getPrefLabel());
		assertEquals(new Float(4f), bt.getAllSignal().next());
	}

	@Test
	public void testRelationBetweenAspectAndResource() throws LiveContextException {
		Temperature temp = modelFactory.getDPOFactory().createTemperature();
		temp.setTemperature(34f);
		lcs.getSession(TEST_DATASOURCE).set(Environment.class, DCON.currentTemperature, temp);

		// updating the temp resource should remove all previous data about the temp
		temp.setTemperature(36f); // temp changes
		lcs.getSession(TEST_DATASOURCE).set(Environment.class, DCON.currentTemperature, temp);
		
		Environment environment = lcs.get(Environment.class);
		assertTrue(environment.hasCurrentTemperature());
		assertEquals(temp, environment.getAllCurrentTemperature().next());
	}

	@Ignore
	@Test
	public void testConsecutiveUpdates() throws Exception {
		Bluetooth bluetooth = modelFactory.getDDOFactory().createBluetooth();
		bluetooth.setPrefLabel("494dj3Z13");
		bluetooth.setSignal(new Float(4f));
		lcs.getSession(TEST_DATASOURCE).add(Connectivity.class, DCON.connection, bluetooth);

		// modify signal
		bluetooth.setSignal(new Float(-3f));
		lcs.getSession(TEST_DATASOURCE).remove(Connectivity.class, DCON.connection);
		lcs.getSession(TEST_DATASOURCE).add(Connectivity.class, DCON.connection, bluetooth);

		Bluetooth bt = lcs.get(bluetooth.asURI(), Bluetooth.class, TEST_DATASOURCE);
		assertEquals("494dj3Z13", bt.getPrefLabel());
		assertEquals(new Float(-3f), bt.getAllSignal().next());
	}

}