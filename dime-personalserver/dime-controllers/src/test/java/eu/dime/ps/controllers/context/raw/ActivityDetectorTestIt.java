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

package eu.dime.ps.controllers.context.raw;

import junit.framework.TestCase;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.controllers.TenantManager;
import eu.dime.ps.controllers.context.raw.ActivityDetector;
import eu.dime.ps.controllers.context.raw.utils.Defaults;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.storage.IStorage;
import eu.dime.ps.storage.entities.Tenant;
import static org.mockito.Mockito.*;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-activity-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ActivityDetectorTestIt extends TestCase {
	
	@Autowired
	private ActivityDetector activityDetector;
	private static IContextProcessor contextProcessor = mock(IContextProcessor.class);
	private TenantManager tenantManager = mock(TenantManager.class);
	
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		activityDetector.setContextProcessor(contextProcessor);
		activityDetector.setTenantManager(tenantManager);
	}
	
	@Test
	public void testProcessActivity() {
		
		IContextElement[] addrs = {
			// 15 secs ago
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((15) * 1000), 600, "place1"),
			// 180 secs ago
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((180) * 1000), 600, "place1"),
			// 20 secs in the monitored period
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE - 20) * 1000), 600, "place1"),
			// 10 secs out of monitored period (before)
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE + 10) * 1000), 600, "place2")
		};
		
		IContextDataset dataset = Factory.createContextDataset(addrs);
		
		try {
			when(contextProcessor.getContext((Tenant)anyObject(),(IEntity)anyObject(),(IScope)anyObject(),anyString(),anyString())).thenReturn(dataset);
		} catch (ContextException e) {
			assertFalse(true);
		}
		
		when(tenantManager.getByAccountName(anyString())).thenReturn(new Tenant());
		
		IContextDataset situation = this.activityDetector.getContext(new Tenant(), Constants.ENTITY_ME, Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS), null);
		assertFalse((situation == null) || (!this.activityDetector.getActivityValue(situation).equalsIgnoreCase("@place1")));
	
	}

}
