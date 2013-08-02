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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import eu.dime.ps.semantic.model.dcon.Connectivity;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class WiFiContextUpdaterTest extends TestCase {

	@Autowired
	private LiveContextService liveContextService;

	@Autowired
	private Connection connection;
	
	private EntityFactory entityFactory;
	
	private WiFiContextUpdater rawContextUpdater;
	
	private IContextDataset rawContextWifi;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}
	
	private IScope wifi = Factory.createScope(Constants.SCOPE_WF);
	private IEntity user1 = Factory.createEntity("user1");
	
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

		rawContextUpdater = new WiFiContextUpdater();

		ConnectionProvider connectionProvider = mock(ConnectionProvider.class);
		when(connectionProvider.getConnection(anyString())).thenReturn(connection);
		
		Tenant t1 = entityFactory.buildTenant();
		t1.setName(user1.getEntityIDAsString());
		t1.setId(new Long(2));

		TenantManager tenantManager = mock(TenantManager.class);
		when(tenantManager.getByAccountName(anyString())).thenReturn(t1);

		IContextProcessor contextProcessor = mock(IContextProcessor.class);
		rawContextWifi = createRawContext(user1, "net1,ne2,net1", "AAA,BBB,CCC", "-50,-60,-70");
		when(contextProcessor.getContext((Tenant)anyObject(),(IEntity) anyObject(),
						(IScope) anyObject())).thenReturn(rawContextWifi);

		rawContextUpdater.setConnectionProvider(connectionProvider);
		rawContextUpdater.setTenantManager(tenantManager);
		rawContextUpdater.setContextProcessor(contextProcessor);
	}
	
	@Test
	public void testUpdateWifi() {
		try {
			Tenant t1 = entityFactory.buildTenant();
			t1.setName(user1.getEntityIDAsString());
			t1.setId(new Long(2));
			RawContextNotification notification = createNotification(t1,user1,wifi);
			rawContextUpdater.contextChanged(notification);
			Connectivity conn = liveContextService.get(Connectivity.class);
			assertTrue(conn.getAllConnection().hasNext());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private RawContextNotification createNotification(Tenant t, IEntity entity, IScope scope) {
		RawContextNotification notification = new RawContextNotification();
		notification.setTenant(t.getId());
		notification.setItemID("");
		notification.setName(entity.getEntityAsString() + ","
				+ scope.getScopeAsString());
		notification.setItemType("context");
		notification.setOperation("create");
		notification.setSender("ContextProcessor");
		notification.setTarget("@me");
		return notification;
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	private IContextDataset createRawContext(IEntity entity, String ssids, String macs, String sstrength) {

		HashMap<IScope, IContextValue> contVal = new HashMap<IScope, IContextValue>();
		IMetadata metadata = Factory.createDefaultMetadata(180000);
		
		IContextValue ssid = Factory.createContextValue(Factory.createScope(Constants.SCOPE_WF_NAMES),Factory.createValue(ssids.split(",")));
		IContextValue mac = Factory.createContextValue(Factory.createScope(Constants.SCOPE_WF_LIST),Factory.createValue(macs.split(",")));
		String[] sss = sstrength.split(",");
		int[] intss = new int[sss.length];
		for (int i=0; i<sss.length; i++) {
			intss[i] = Integer.parseInt(sss[i]);
		}
		IContextValue ss = Factory.createContextValue(Factory.createScope(Constants.SCOPE_WF_SIGNALS),Factory.createValue(intss));
		
		contVal.put(Factory.createScope(Constants.SCOPE_WF_NAMES), ssid);
		contVal.put(Factory.createScope(Constants.SCOPE_WF_LIST), mac);
		contVal.put(Factory.createScope(Constants.SCOPE_WF_SIGNALS), ss);

		IContextElement ce = Factory.createContextElement(entity, wifi,
				"Dime", Factory.createContextValueMap(contVal), metadata);

		IContextDataset dataset = Factory.createContextDataset(ce);

		return dataset;
	}
	
	

}
