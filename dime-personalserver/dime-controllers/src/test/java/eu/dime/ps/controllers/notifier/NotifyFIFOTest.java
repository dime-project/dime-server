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

package eu.dime.ps.controllers.notifier;

import junit.framework.Assert;

import org.junit.Test;

import eu.dime.commons.notifications.DimeExternalNotification;
import eu.dime.commons.notifications.DimeInternalNotification;

public class NotifyFIFOTest {

	NotifyFIFOExternal fifo;
	
	@Test
	public void testPopPushNotification() {
		
		Long tenant = 1l;
		
		fifo = new NotifyFIFOExternal();

		fifo.pushNotification(new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		
		Assert.assertEquals(1, fifo.size());
		
		fifo.popNotification();
		
		Assert.assertEquals(0, fifo.size());
		
	}

	@Test
	public void testOrderFIFOhNotification() {
		
		Long tenant = 1l;

		fifo = new NotifyFIFOExternal();

		DimeExternalNotification n1 = (new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		n1.setItemID("n1");
		
		fifo.pushNotification(n1);
		
		DimeExternalNotification n2 = (new DimeExternalNotification("target", "sender", DimeExternalNotification.OP_CREATE, "id", "name", "type",tenant));
		n2.setItemID("n2");
		
		fifo.pushNotification(n2);
		
		Assert.assertEquals(n1.getItemID(),fifo.popNotification().getItemID());
		Assert.assertEquals(n2.getItemID(),fifo.popNotification().getItemID());
		
	}

}
