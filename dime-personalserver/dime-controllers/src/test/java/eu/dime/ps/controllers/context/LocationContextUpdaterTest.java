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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.notifications.DimeInternalNotification;
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
import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.infosphere.manager.LocationManager;
import eu.dime.ps.controllers.infosphere.manager.PlacemarkManager;
import eu.dime.ps.controllers.placeprocessor.PlaceKey;
import eu.dime.ps.controllers.placeprocessor.PlaceProcessor;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.dcon.SpaTem;
import eu.dime.ps.semantic.model.dpo.Place;
import eu.dime.ps.semantic.model.nfo.Placemark;
import eu.dime.ps.semantic.service.LiveContextService;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class LocationContextUpdaterTest extends TestCase {

	@Autowired
	private LiveContextService liveContextService;

	@Autowired
	private Connection connection;

	@Autowired
	private PlacemarkManager placemarkManager;

	@Autowired
	private LocationManager locationManager;

	@Autowired
	private EntityFactory entityFactory;

	private LocationContextUpdater rawContextUpdater;

	private IContextDataset rawContextCurrentPlace;

	private IScope currentPlace = Factory
			.createScope(Constants.SCOPE_CURRENT_PLACE);
	private IEntity user1 = Factory.createEntity("user1");
	private IEntity user2 = Factory.createEntity("user2");
	private String testPlaceId = "ametic:xyz123jkh";

	@Override
	@Before
	public void setUp() throws Exception {

		super.setUp();

		rawContextUpdater = new LocationContextUpdater();

		ConnectionProvider connectionProvider = mock(ConnectionProvider.class);
		when(connectionProvider.getConnection(anyString())).thenReturn(connection);

		Tenant t1 = entityFactory.buildTenant();
		t1.setName(user1.getEntityIDAsString());
		t1.setId(new Long(1));
		TenantContextHolder.setTenant(t1.getId());

		TenantManager tenantManager = mock(TenantManager.class);
		when(tenantManager.getByAccountName(anyString())).thenReturn(t1);

		IContextProcessor contextProcessor = mock(IContextProcessor.class);
		rawContextCurrentPlace = createRawContext(user1, currentPlace,
				testPlaceId);
		when(contextProcessor.getContext((Tenant) anyObject(),(IEntity) anyObject(),(IScope) anyObject())).thenReturn(
				rawContextCurrentPlace);

		rawContextUpdater.setConnectionProvider(connectionProvider);
		rawContextUpdater.setTenantManager(tenantManager);
		rawContextUpdater.setLocationManager(locationManager);
		rawContextUpdater.setContextProcessor(contextProcessor);

		Placemark pmk = buildPlacemark(testPlaceId);
		placemarkManager.add(pmk);
		
		PlaceProcessor pp = new PlaceProcessor();
		pp.RDFPlaceReferences = new HashMap<PlaceKey, String>();
		pp.RDFPlaceReferences.put(new PlaceKey(t1.getId(),testPlaceId), pmk.asURI().toString());
		rawContextUpdater.setPlaceProcessor(pp);

	}

	@Test
	public void testUpdateCurrentPlace() {
		try {
			RawContextNotification notification = createNotification(user1,
					currentPlace);
			SpaTem spatem = liveContextService.get(SpaTem.class);
			ClosableIterator<Place> places = spatem.getAllCurrentPlace();
			//assertFalse(places.hasNext());
			rawContextUpdater.contextChanged(notification);
			spatem = liveContextService.get(SpaTem.class);
			places = spatem.getAllCurrentPlace();
			assertTrue(places.hasNext());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Override
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	private Placemark buildPlacemark(String placeId) {
		Placemark placemark = new ModelFactory().getNFOFactory()
				.createPlacemark();
		placemark.setPrefLabel(placeId);
		placemark.setLat(new Float(45.213));
		placemark.setLong(new Float(7.678));
		return placemark;
	}

	private RawContextNotification createNotification(IEntity entity, IScope scope) {
		RawContextNotification notification = new RawContextNotification();
		notification.setTenant(new Long(1));
		notification.setItemID("");
		notification.setName(entity.getEntityAsString() + ","
				+ scope.getScopeAsString());
		notification.setItemType("context");
		notification.setOperation("create");
		notification.setSender("ContextProcessor");
		notification.setTarget("@me");
		return notification;
	}

	private IContextDataset createRawContext(IEntity entity, IScope scope,
			String value) {

		HashMap<IScope, IContextValue> contVal = new HashMap<IScope, IContextValue>();
		IMetadata metadata = Factory.createDefaultMetadata(180000);

		if (scope.getScopeAsString().equalsIgnoreCase(
				Constants.SCOPE_CURRENT_PLACE)) {
			IScope placeIdScope = Factory
					.createScope(Constants.SCOPE_CURRENT_PLACE_ID);
			IContextValue placeId = Factory.createContextValue(placeIdScope,
					Factory.createValue(value));
			contVal.put(placeIdScope, placeId);
		} else {
			// others scopes
		}

		IContextElement ce = Factory.createContextElement(entity, scope,
				"Dime", Factory.createContextValueMap(contVal), metadata);

		IContextDataset dataset = Factory.createContextDataset(ce);

		return dataset;
	}

}
