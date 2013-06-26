package eu.dime.ps.gateway.service;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import eu.dime.ps.gateway.service.AttributeMap;

/**
 * @author Sophie.Wrobel
 *
 */
public class AttributeMapTest {

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.AttributeMap#getAttribute()}.
	 */
	@Test
	public void testGetAttribute() {
		AttributeMap attributeMap = new AttributeMap();
		assertTrue("Wrong mapping found for getAttribute(/event/@all).", 
				attributeMap.getAttribute("/event/@all").equals(AttributeMap.EVENT_ALL));
		assertTrue("Wrong mapping found for getAttribute(/event/@me/123).", 
				attributeMap.getAttribute("/event/@me/123").equals(AttributeMap.EVENT_DETAILS));
	}

	/**
	 * Test method for {@link eu.dime.ps.controllers.service.AttributeMap#extractIds()}.
	 */
	@Test
	public void testExtractIds() {
		AttributeMap attributeMap = new AttributeMap();

		Map<String, String> details = attributeMap.extractIds(AttributeMap.EVENT_ALL, "/event/@all");
		assertTrue("Wrong eventId detected for /event/@all",
				details.get(AttributeMap.EVENT_ID) == null);
		assertTrue("Wrong userId detected for /event/@all",
				details.get(AttributeMap.USER_ID) == null);
		
		details = attributeMap.extractIds(AttributeMap.EVENT_DETAILS, "/event/@me/123");
		assertTrue("Wrong eventId detected for /event/@me/123",
				details.get(AttributeMap.EVENT_ID).equals("123"));
		assertTrue("Wrong userId detected for /event/@me/123",
				details.get(AttributeMap.USER_ID) == null);
		
		details = attributeMap.extractIds(AttributeMap.EVENT_ATTENDEEDETAILS, "/event/@me/123/456");
		assertTrue("Wrong eventId detected for /event/@me/123/456",
				details.get(AttributeMap.EVENT_ID).equals("123"));
		assertTrue("Wrong userId detected for /event/@me/123/456",
				details.get(AttributeMap.USER_ID).equals("456"));
	}

}
