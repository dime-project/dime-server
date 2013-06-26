package eu.dime.ps.controllers.infosphere;

import java.util.Collection;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.EventManagerImpl;
import eu.dime.ps.semantic.model.pimo.SocialEvent;

/**
 * Tests {@link EventManagerImpl}.
 * 
 * @author Ismael Rivera
 */
public class EventManagerTest extends InfoSphereManagerTest {

	@Autowired
	private EventManagerImpl eventManager;
	
	@Test
	public void testExist() throws Exception {
		SocialEvent event = buildSocialEvent("ESWC12");
		eventManager.add(event);
		assertTrue(eventManager.exist(event.toString()));
	}

	@Test
	public void testGetEvent() throws Exception {
		SocialEvent event = buildSocialEvent("ESWC12");
		eventManager.add(event);
		SocialEvent another = eventManager.get(event.asResource().toString());
		assertEquals(event, another);
	}

	@Test(expected=InfosphereException.class)
	public void testGetUnknownEvent() throws Exception {
		eventManager.get("urn:12345");
	}

	@Test
	public void testGetAllEvents() throws Exception {
		eventManager.add(buildSocialEvent("di.me Board Meeting"));
		eventManager.add(buildSocialEvent("Codemania 2012"));
		assertEquals(2, eventManager.getAll().size());
	}
	
	@Test
	public void testAddEvent() throws Exception {
		SocialEvent event = buildSocialEvent("ESWC12");
		eventManager.add(event);
		Collection<SocialEvent> events = eventManager.getAll();
		assertEquals(1, events.size());
		assertTrue(events.contains(event));
	}

	@Test
	public void testUpdateEvent() throws Exception {
		SocialEvent event = buildSocialEvent("di.me review");
		eventManager.add(event);
		
		event.setPrefLabel("di.me 2nd review");
		eventManager.update(event);
		
		SocialEvent galaxy = eventManager.get(event.asResource().toString());
		assertEquals("di.me 2nd review", galaxy.getPrefLabel());
	}

	@Test
	public void testRemoveEvent() throws Exception {
		SocialEvent event1 = buildSocialEvent("ESWC12");
		SocialEvent event2 = buildSocialEvent("di.me Board Meeting");
		eventManager.add(event1);
		eventManager.add(event2);
		eventManager.remove(event1.asResource().toString());
		assertEquals(1, eventManager.getAll().size());
	}
	
}
