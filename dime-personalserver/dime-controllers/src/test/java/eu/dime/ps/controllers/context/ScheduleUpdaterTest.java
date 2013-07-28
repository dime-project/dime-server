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

package eu.dime.ps.controllers.context;

import ie.deri.smile.rdf.TripleStore;
import ie.deri.smile.vocabulary.NCAL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.Schedule;
import eu.dime.ps.semantic.util.DateUtils;
import eu.dime.ps.storage.entities.Tenant;

/**
 * Tests {@link ScheduleUpdater}.
 */
@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduleUpdaterTest extends TestCase {

	private final static Calendar MAY18AT1339 = Calendar.getInstance();
	private final static Calendar MAY20AT0839 = Calendar.getInstance();
	static {
		MAY18AT1339.setTimeInMillis(1337344732634L); // 18 May 2012 13:39
		MAY20AT0839.setTimeInMillis(1337499532634L); // 20 May 2012 08:39
	}

	private ScheduleUpdater scheduleUpdater;
	
	@Autowired
	private Connection connection;
	
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		scheduleUpdater = new ScheduleUpdater();
		
		ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);
		Mockito.when(connectionProvider.getConnection("12345")).thenReturn(connection);
		
		List<Tenant> tenantList = new ArrayList<Tenant>();
		Tenant tenant = new Tenant("12345", null);
		tenant.setId(12345L);
		tenantList.add(tenant);
		TenantManager tenantManager = Mockito.mock(TenantManager.class);
		Mockito.when(tenantManager.getAll()).thenReturn(tenantList);
		
		scheduleUpdater.setConnectionProvider(connectionProvider);
		scheduleUpdater.setTenantManager(tenantManager);
	}
	
	private void createTestData(URI type) throws Exception {
		TripleStore ts = this.connection.getTripleStore();
		
		URI graph = new URIImpl("urn:example");
		long hour = 1000 * 60 * 60;

		Calendar dtStartA = new GregorianCalendar(), dtEndA = new GregorianCalendar();
		dtStartA.setTimeInMillis(dtStartA.getTimeInMillis() - 6 * hour);
		dtEndA.setTimeInMillis(dtEndA.getTimeInMillis() - 5 * hour);

		ts.addStatement(graph, new URIImpl("urn:A"), RDF.type, type);
		ts.addStatement(graph, new URIImpl("urn:A"), NCAL.dtstart, new URIImpl("urn:Astart"));
		ts.addStatement(graph, new URIImpl("urn:Astart"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtStartA));
		ts.addStatement(graph, new URIImpl("urn:A"), NCAL.dtend, new URIImpl("urn:Aend"));
		ts.addStatement(graph, new URIImpl("urn:Aend"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtEndA));

		Calendar dtStartB = new GregorianCalendar(), dtEndB = new GregorianCalendar();
		dtStartB.setTimeInMillis(dtStartB.getTimeInMillis() - 1 * hour);
		dtEndB.setTimeInMillis(dtEndB.getTimeInMillis() + 1 * hour);

		ts.addStatement(graph, new URIImpl("urn:B"), RDF.type, type);
		ts.addStatement(graph, new URIImpl("urn:B"), NCAL.dtstart, new URIImpl("urn:Bstart"));
		ts.addStatement(graph, new URIImpl("urn:Bstart"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtStartB));
		ts.addStatement(graph, new URIImpl("urn:B"), NCAL.dtend, new URIImpl("urn:Bend"));
		ts.addStatement(graph, new URIImpl("urn:Bend"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtEndB));

		Calendar dtStartC = new GregorianCalendar(), dtEndC = new GregorianCalendar();
		dtStartC.setTimeInMillis(dtStartC.getTimeInMillis() + 3 * hour);
		dtEndC.setTimeInMillis(dtEndC.getTimeInMillis() + 4 * hour);

		ts.addStatement(graph, new URIImpl("urn:C"), RDF.type, type);
		ts.addStatement(graph, new URIImpl("urn:C"), NCAL.dtstart, new URIImpl("urn:Cstart"));
		ts.addStatement(graph, new URIImpl("urn:Cstart"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtStartC));
		ts.addStatement(graph, new URIImpl("urn:C"), NCAL.dtend, new URIImpl("urn:Cend"));
		ts.addStatement(graph, new URIImpl("urn:Cend"), NCAL.dateTime, DateUtils.dateTimeAsLiteral(dtEndC));
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testCurrentEvents() throws Exception {
		createTestData(NCAL.Event);
		
		scheduleUpdater.refresh();
		scheduleUpdater.update();
		
		Schedule schedule = connection.getLiveContextService().get(Schedule.class);
		List<Node> events = schedule.getAllCurrentEvent_asNode_().asList();
		assertEquals(1, events.size());
		assertEquals(new URIImpl("urn:B"), events.get(0));
	}
	
	@Test
	public void testUpcomingEvents() throws Exception {
		createTestData(NCAL.Event);

		scheduleUpdater.refresh();
		scheduleUpdater.update();
		
		Schedule schedule = connection.getLiveContextService().get(Schedule.class);
		List<Node> events = schedule.getAllUpcomingEvent_asNode_().asList();
		assertEquals(1, events.size());
		assertEquals(new URIImpl("urn:C"), events.get(0));
	}
	
	@Test
	public void testCurrentTasks() throws Exception {
		createTestData(NCAL.Todo);
		
		scheduleUpdater.refresh();
		scheduleUpdater.update();
		
		Schedule schedule = connection.getLiveContextService().get(Schedule.class);
		List<Node> tasks = schedule.getAllCurrentTask_asNode_().asList();
		assertEquals(1, tasks.size());
		assertEquals(new URIImpl("urn:B"), tasks.get(0));
	}
	
	@Test
	public void testUpcomingTasks() throws Exception {
		createTestData(NCAL.Todo);

		scheduleUpdater.refresh();
		scheduleUpdater.update();
		
		Schedule schedule = connection.getLiveContextService().get(Schedule.class);
		List<Node> tasks = schedule.getAllUpcomingTask_asNode_().asList();
		assertEquals(1, tasks.size());
		assertEquals(new URIImpl("urn:C"), tasks.get(0));
	}
	
}
