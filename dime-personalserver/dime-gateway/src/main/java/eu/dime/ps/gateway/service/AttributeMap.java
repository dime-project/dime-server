package eu.dime.ps.gateway.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Accessor class to identify and extract information out of supported attributes passed to a service adapter.
 * 
 * Example of using this class:
 *   String input = "/events/@me/1234";
 *   AttributeMap attributeMap = new AttributeMap();
 *   String genericAttribute = attributeMap.getAttribute(input);
 *   Map<String, String> ids = attributeMap.extractIds(genericAttribute, input);
 *   if (genericAttribute.equals(AttributeMap.EVENT_DETAILS)) {
 *     system.out.println("Requested details for event ID: " + ids.get(AttributeMap.EVENT_ID));
 *   }
 * 
 * @author Sophie.Wrobel
 *
 */
public class AttributeMap {

	// Extractable IDs
	private ArrayList<String> idnames;
	public static final String EVENT_ID = "eventId";
	public static final String USER_ID = "userId";
	
	// Path names for attributes
	private ArrayList<String> attributes;
	public static final String EVENT_ALL = "/event/@all";
	public static final String EVENT_ALLMINE = "/event/@me/@all";
	public static final String EVENT_DETAILS = "/event/@me/{eventId}";
	public static final String EVENT_ATTENDEES = "/event/@me/{eventId}/@all";
	public static final String EVENT_ATTENDEEDETAILS = "/event/@me/{eventId}/{userId}";
	public static final String PROFILE_ME = "/profile/@me";
	public static final String PROFILE_MYDETAILS = "/profile/@me/@all";
	public static final String PROFILE_DETAILS = "/profile/{userId}/@all";
	public static final String PROFILEATTRIBUTE_MYDETAILS = "/profileattribute/@me/@all";
	public static final String PROFILEATTRIBUTE_DETAILS = "/profileattribute/{userId}/@all";
	public static final String FRIEND_ALL = "/person/@me/@all";
	public static final String FRIEND_DETAILS = "/person/@me/{userId}";
	public static final String GROUP_ALL = "/group/@all";
	public static final String LIVEPOST_ALL = "/livepost/@all";
	public static final String LIVEPOST_ALLMINE = "/livepost/@me/@all";
	public static final String LIVEPOST_ALLUSER = "/livepost/{userId}/@all";
	public static final String NOTIFICATION = "/notification";
	public static final String PLACE_ALL = "/place/@all";
	public static final String ACTIVITY_ALL = "/activity/@me/@all";
	public static final String ACTIVITY_DETAILS = "/activity/{userId}/@all";
	
	/**
	 * Constructor - initialize attributes
	 */
	public AttributeMap() {
		// Note: The add order is important - special @keywords must be added before ID placeholders! 
		this.attributes = new ArrayList<String>();
		this.attributes.add(EVENT_ATTENDEES);
		this.attributes.add(EVENT_ATTENDEEDETAILS);
		this.attributes.add(EVENT_ALLMINE);
		this.attributes.add(EVENT_DETAILS);
		this.attributes.add(EVENT_ALL);
		this.attributes.add(PROFILE_ME);
		this.attributes.add(PROFILE_MYDETAILS);
		this.attributes.add(PROFILE_DETAILS);
		this.attributes.add(PROFILEATTRIBUTE_MYDETAILS);
		this.attributes.add(PROFILEATTRIBUTE_DETAILS);
		this.attributes.add(FRIEND_ALL);
		this.attributes.add(FRIEND_DETAILS);
		this.attributes.add(GROUP_ALL);
		this.attributes.add(LIVEPOST_ALLMINE);
		this.attributes.add(LIVEPOST_ALLUSER);
		this.attributes.add(LIVEPOST_ALL);
		this.attributes.add(NOTIFICATION);
		this.attributes.add(PLACE_ALL);
		this.attributes.add(ACTIVITY_ALL);
		this.attributes.add(ACTIVITY_DETAILS);
		
		this.idnames = new ArrayList<String>();
		this.idnames.add(EVENT_ID);
		this.idnames.add(USER_ID);
	}
	
	/**
	 * Retrieves the generic form of the attribute.
	 * 
	 * @param input The attribute to verify with ID values
	 * @return The path for the attribute with generic ID placeholders. Returns null if no attribute was found.
	 */
	public String getAttribute (String input) {
		Iterator<String> iterator = this.attributes.iterator();
		while (iterator.hasNext()) {
			String candidate = iterator.next();
			String attr = candidate.replaceAll("\\{\\w+\\}", "([^/]+)");
			if (input.matches(attr)) {
				return candidate;
			}
		}
		return null;
	}
	
	/**
	 * Extracts IDs from an attribute.
	 * 
	 * @param attribute The attribute with generic ID placeholders
	 * @param input The attribute to extract ID values from
	 * @return Map with extracted ID names and values as a Map <IdName, Value>. Returns empty map if no IDs could be extracted.
	 */
	public Map<String, String> extractIds (String attribute, String input) {
		HashMap<String, String> idMap = new HashMap<String, String>();
		Iterator<String> iterator = this.idnames.iterator();
		while (iterator.hasNext()) {
			String candidate = iterator.next();
			String attr = attribute.replaceAll("\\{"+candidate+"\\}", "([^/]+)").replaceAll("\\{\\w+\\}", "[^/]+");
			Pattern pattern = Pattern.compile(attr);
			Matcher matcher = pattern.matcher(input);
			if (matcher.find() && matcher.groupCount() >= 1) {
			    idMap.put(candidate, matcher.group(1));
			}
			
		}
		
		return idMap;
	}

}
