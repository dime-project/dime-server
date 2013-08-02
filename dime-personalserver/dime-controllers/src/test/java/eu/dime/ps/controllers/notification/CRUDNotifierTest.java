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

package eu.dime.ps.controllers.notification;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.dime.commons.notifications.DimeInternalNotification;
import eu.dime.ps.semantic.Event;
import eu.dime.ps.semantic.connection.Connection;
import eu.dime.ps.semantic.model.ModelFactory;
import eu.dime.ps.semantic.model.pimo.PersonGroup;
import eu.dime.ps.dto.Type;

@ContextConfiguration(locations = { "classpath*:**/applicationContext-infosphere-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class CRUDNotifierTest extends TestCase {

	private ModelFactory modelFactory = new ModelFactory();
	
	private CRUDNotifier notifier;
	
	private NotifierManagerMock notifierManager = new NotifierManagerMock();
	
	@Autowired
	private Connection connection;
	
	@Override
	@Before
	public void setUp() throws Exception {
		notifierManager.internal.clear();
		
		notifier = new CRUDNotifier();
		notifier.setNotifierManager(notifierManager);
	}
	
	@Test
	public void testCreatePersonGroup() {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setCreator(new URIImpl("urn:test"));
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_ADD, group);
		notifier.onReceive(event);
		
		assertEquals(1, notifierManager.internal.size());
		DimeInternalNotification notification = notifierManager.internal.get(0);
		assertEquals(group.asURI().toString(), notification.getItemID());
		assertEquals(DimeInternalNotification.OP_CREATE, notification.getOperation());
		assertEquals(Type.get(group).getLabel(), notification.getItemType());
	}
	
	@Test
	public void testUpdatePersonGroup() {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setCreator(new URIImpl("urn:test"));
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_MODIFY, group);
		notifier.onReceive(event);
		
		assertEquals(1, notifierManager.internal.size());
		DimeInternalNotification notification = notifierManager.internal.get(0);
		assertEquals(group.asURI().toString(), notification.getItemID());
		assertEquals(DimeInternalNotification.OP_UPDATE, notification.getOperation());
		assertEquals(Type.get(group).getLabel(), notification.getItemType());
	}
	
	@Test
	public void testRemovePersonGroup() {
		PersonGroup group = modelFactory.getPIMOFactory().createPersonGroup();
		group.setCreator(new URIImpl("urn:test"));
		Event event = new Event(connection.getName(), Event.ACTION_RESOURCE_DELETE, group);
		notifier.onReceive(event);
		
		assertEquals(1, notifierManager.internal.size());
		DimeInternalNotification notification = notifierManager.internal.get(0);
		assertEquals(group.toString(), notification.getItemID());
		assertEquals(DimeInternalNotification.OP_REMOVE, notification.getOperation());
		assertEquals(Type.get(group).getLabel(), notification.getItemType());
	}

}
