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

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextElement;
import eu.dime.ps.controllers.context.raw.ActivityDetector;
import eu.dime.ps.controllers.context.raw.utils.Defaults;

public class ActivityDetectorTest {
	
	private ActivityDetector activityDetector = new ActivityDetector();
	
	@Test
	public void testActivityNoRecentData() {
		
		IContextElement[] addrs = {
			// 15 secs ago
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((15) * 1000), 600, "place1"),
			// 10 secs out of monitored period (before)
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE + 10) * 1000), 600, "place1")
		};
		
		String activity = activityDetector.getActivity(Constants.ENTITY_ME,addrs);
		assertFalse((activity == null) || (!activity.equalsIgnoreCase("")));
		
	}
	
	@Test
	public void testActivityDifferentPlaces() {
		
		IContextElement[] addrs = {
			// 15 secs ago
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((15) * 1000), 600, "place1"),
			// 180 secs ago
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((180) * 1000), 600, "place2"),
			// 20 secs in the monitored period
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE - 20) * 1000), 600, "place1"),
			// 10 secs out of monitored period (before)
			ContextHelper.createCivilAddress(System.currentTimeMillis() - ((Defaults.ACTIVITY_PERIOD + Defaults.ACTIVITY_TOLERANCE + 10) * 1000), 600, "place1")
		};
		
		String situation = activityDetector.getActivity(Constants.ENTITY_ME,addrs);
		assertFalse((situation == null) || (!situation.equalsIgnoreCase("")));
		
	}
	
	@Test
	public void testActivityOK() {
		
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
		
		String situation = activityDetector.getActivity(Constants.ENTITY_ME,addrs);
		assertFalse((situation == null) || (!situation.equalsIgnoreCase("@place1")));
		
	}

}
