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

import static org.mockito.Matchers.anyObject;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.dcon.State;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityContextUpdaterTest extends TestCase {

	@Autowired
	private LiveContextService liveContextService;

	@Autowired
	private Connection connection;
	
	private EntityFactory entityFactory;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}
	
	private ActivityContextUpdater rawContextUpdater;
	
	private IScope currentActivity = Factory.createScope(Constants.SCOPE_ACTIVITY);
	private IEntity user1 = Factory.createEntity("user1");
	private String testActivity = "@Conference_Booth";
	
	private IContextDataset rawContextActivity;
	
	@Override
	@Before
	public void setUp() throws Exception {

		super.setUp();

		rawContextUpdater = new ActivityContextUpdater();

		ConnectionProvider connectionProvider = mock(ConnectionProvider.class);
		when(connectionProvider.getConnection(anyString())).thenReturn(connection);

		Tenant t1 = entityFactory.buildTenant();
		t1.setName(user1.getEntityIDAsString());
		t1.setId(new Long(1));

		TenantManager tenantManager = mock(TenantManager.class);
		when(tenantManager.getByAccountName(anyString())).thenReturn(t1);

		IContextProcessor contextProcessor = mock(IContextProcessor.class);
		rawContextActivity = createRawContext(user1, currentActivity,testActivity);
		when(contextProcessor.getContext((Tenant) anyObject(),(IEntity) anyObject(),(IScope) anyObject())).thenReturn(
				rawContextActivity);

		rawContextUpdater.setConnectionProvider(connectionProvider);
		rawContextUpdater.setTenantManager(tenantManager);
		rawContextUpdater.setContextProcessor(contextProcessor);

	}
	
	@Test
	public void testUpdateActivity() {
		try {
			
			List<Node> activities = new ArrayList<Node>();
			RawContextNotification notification = createNotification(user1,currentActivity);
			rawContextUpdater.contextChanged(notification);
			State state = liveContextService.get(State.class);
			ClosableIterator<Statement> currentIt = state.getModel().findStatements(state, DCON.currentActivity, Variable.ANY);
			while (currentIt.hasNext()) {
				Node object = currentIt.next().getObject();
				if (object instanceof URI) {
					activities.add(object);
				}
			}
			currentIt.close();
			assertTrue(activities.size() == 1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private RawContextNotification createNotification(IEntity entity, IScope scope) {
		RawContextNotification notification = new RawContextNotification();
		notification.setTenant(new Long(1));
		notification.setItemID("");
		notification.setName(entity.getEntityAsString() + ","
				+ scope.getScopeAsString());
		notification.setItemType("context");
		notification.setOperation("fixed");
		notification.setSender("ContextProcessor");
		notification.setTarget("@me");
		return notification;
	}

	private IContextDataset createRawContext(IEntity entity, IScope scope,
			String value) {

		HashMap<IScope, IContextValue> contVal = new HashMap<IScope, IContextValue>();
		IMetadata metadata = Factory.createDefaultMetadata(180000);

		if (scope.getScopeAsString().equalsIgnoreCase(
				Constants.SCOPE_ACTIVITY)) {
			IScope currentActivityScope = Factory
					.createScope(Constants.SCOPE_ACTIVITY_CURRENT);
			IContextValue activity = Factory.createContextValue(currentActivityScope,
					Factory.createValue(value));
			contVal.put(currentActivityScope, activity);
		} else {
			// others scopes
		}

		IContextElement ce = Factory.createContextElement(entity, scope,
				"Dime", Factory.createContextValueMap(contVal), metadata);

		IContextDataset dataset = Factory.createContextDataset(ce);

		return dataset;
	}
	
	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

}
