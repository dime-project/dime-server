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

package eu.dime.ps.controllers.context;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.DPO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.rdf.ResourceStore;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Tests {@link TimePeriodUpdater}.
 */
@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TimePeriodUpdaterTest extends TestCase {

	private final static URI AFTERNOON = new URIImpl("urn:afternoon");
	private final static URI FRIDAY = new URIImpl("urn:friday");
	private final static URI WEEK20 = new URIImpl("urn:week20");
	private final static URI WEEKEND = new URIImpl("urn:weekend");

	private final static List<URI> RESULTS = new ArrayList<URI>();
	static {
		RESULTS.add(AFTERNOON);
		RESULTS.add(FRIDAY);
		RESULTS.add(WEEK20);
		RESULTS.add(WEEKEND);
	}

	private final static Calendar MAY18AT1339 = Calendar.getInstance();
	private final static Calendar MAY20AT0839 = Calendar.getInstance();
	static {
		MAY18AT1339.setTimeInMillis(1337344732634L); // 18 May 2012 13:39
		MAY20AT0839.setTimeInMillis(1337499532634L); // 20 May 2012 08:39
	}
	
	@Autowired
	private Connection connection;
	
	private TimePeriodUpdater timePeriodUpdater;
	
	private ConnectionProvider connectionProvider;
	private TenantManager tenantManager;

	private ResourceStore resourceStore;
	private LiveContextService liveContextService;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// disabling org.openrdf.rdf2go.RepositoryModel warnings
		org.apache.log4j.Logger.getLogger("org.openrdf.rdf2go").setLevel(org.apache.log4j.Level.OFF);        
		java.util.logging.Logger.getLogger("org.openrdf.rdf2go").setLevel(java.util.logging.Level.OFF);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		createTestData();
		
		connectionProvider = Mockito.mock(ConnectionProvider.class);
		Mockito.when(connectionProvider.getConnection("12345")).thenReturn(connection);
		
		resourceStore = this.connection.getResourceStore();
		liveContextService = this.connection.getLiveContextService();
		
		List<Tenant> tenantList = new ArrayList<Tenant>();
		Tenant tenant = new Tenant("12345");
		tenant.setId(12345L);
		tenantList.add(tenant);
		tenantManager = Mockito.mock(TenantManager.class);
		Mockito.when(tenantManager.getAll()).thenReturn(tenantList);

		timePeriodUpdater = new TimePeriodUpdater();
		timePeriodUpdater.setConnectionProvider(connectionProvider);
		timePeriodUpdater.setTenantManager(tenantManager);
	}
	
	private void createTestData() throws Exception {
		TripleStore ts = this.connection.getTripleStore();
		
		URI graph = new URIImpl("urn:example");
		ts.addStatement(graph, new URIImpl("urn:morning"), RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, new URIImpl("urn:morning"), DPO.minHour, ts.createLiteral(6));
		ts.addStatement(graph, new URIImpl("urn:morning"), DPO.maxHour, ts.createLiteral(11));
		ts.addStatement(graph, AFTERNOON, RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, AFTERNOON, DPO.minHour, ts.createLiteral(12));
		ts.addStatement(graph, AFTERNOON, DPO.maxHour, ts.createLiteral(15));
		ts.addStatement(graph, new URIImpl("urn:evening"), RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, new URIImpl("urn:evening"), DPO.minHour, ts.createLiteral(16));
		ts.addStatement(graph, new URIImpl("urn:evening"), DPO.maxHour, ts.createLiteral(21));

		ts.addStatement(graph, new URIImpl("urn:thursday"), RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, new URIImpl("urn:thursday"), DPO.minDayOfWeek, ts.createLiteral(4));
		ts.addStatement(graph, new URIImpl("urn:thursday"), DPO.maxDayOfWeek, ts.createLiteral(4));
		ts.addStatement(graph, FRIDAY, RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, FRIDAY, DPO.minDayOfWeek, ts.createLiteral(5));
		ts.addStatement(graph, FRIDAY, DPO.maxDayOfWeek, ts.createLiteral(5));
		ts.addStatement(graph, new URIImpl("urn:saturday"), RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, new URIImpl("urn:saturday"), DPO.minDayOfWeek, ts.createLiteral(6));
		ts.addStatement(graph, new URIImpl("urn:saturday"), DPO.maxDayOfWeek, ts.createLiteral(6));

		ts.addStatement(graph, WEEK20, RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, WEEK20, DPO.minWeek, ts.createLiteral(20));
		ts.addStatement(graph, WEEK20, DPO.maxWeek, ts.createLiteral(20));

		ts.addStatement(graph, WEEKEND, RDF.type, DPO.TimePeriod);
		ts.addStatement(graph, WEEKEND, DPO.minDayOfWeek, ts.createLiteral(6));
		ts.addStatement(graph, WEEKEND, DPO.maxDayOfWeek, ts.createLiteral(7));
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testHour() throws Exception {
		assertTrue(timePeriodUpdater.findTimePeriods(this.resourceStore, MAY18AT1339).contains(AFTERNOON));
	}
	
	@Test
	public void testDayOfWeek() throws Exception {
		assertTrue(timePeriodUpdater.findTimePeriods(this.resourceStore, MAY18AT1339).contains(FRIDAY));
	}
	
	@Test
	public void testWeekend() throws Exception {
		assertFalse(timePeriodUpdater.findTimePeriods(this.resourceStore, MAY18AT1339).contains(WEEKEND));
		assertTrue(timePeriodUpdater.findTimePeriods(this.resourceStore, MAY20AT0839).contains(WEEKEND));
	}
	
	@Test
	public void testFindTimePeriodsByWeek() throws Exception {
		assertTrue(timePeriodUpdater.findTimePeriods(this.resourceStore, MAY18AT1339).contains(WEEK20));
	}
	
	@Test
	public void testUpdateLiveContext() throws Exception {
		TimePeriodUpdater mock = new TimePeriodUpdater() {
			@Override
			protected List<URI> findTimePeriods(ResourceStore resourceStore, Calendar when) {
				return RESULTS;
			}
		};
		mock.setConnectionProvider(connectionProvider);
		mock.setTenantManager(tenantManager);
		
		// update live context
		mock.update();
		
		// verify that the time periods where attached to the SpaTem aspect
		SpaTem spatem = this.liveContextService.get(SpaTem.class);
		assertNotNull(spatem);
		assertTrue(spatem.getModel().contains(spatem.asResource(), DCON.currentTime, AFTERNOON));
		assertTrue(spatem.getModel().contains(spatem.asResource(), DCON.currentTime, FRIDAY));
		assertTrue(spatem.getModel().contains(spatem.asResource(), DCON.currentTime, WEEKEND));
		assertTrue(spatem.getModel().contains(spatem.asResource(), DCON.currentTime, WEEK20));
	}

}
