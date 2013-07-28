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

package eu.dime.ps.gateway.service;

/**
 * @author Sophie.Wrobel
 */
public class ResourceAttributes {

	public static final String ABBR_ALL = "@all";
	public static final String ABBR_ME = "@me";
	public static final String ABBR_FRIENDS = "@friends";
	public static final String ABBR_OWNGROUP = "@self";
	
	public static final String ATTR_PERSON = "person";
	public static final String ATTR_GROUP = "group";
	public static final String ATTR_LIVESTREAM = "livestream";
	public static final String ATTR_SITUATION = "situation";
	public static final String ATTR_EVENT = "event";
	public static final String ATTR_RESOURCE = "resource";
	public static final String ATTR_PROFILECARD = "profilecard";
	public static final String ATTR_PROFILE = "profile";
	public static final String ATTR_APPOINTMENT = "appointment";
	public static final String ATTR_ACTIVITY = "activity";
	public static final String ATTR_PROFILEATTRIBUTE = "profileattribute";
	public static final String ATTR_LIVEPOST = "livepost";
	public static final String ATTR_DATABOX = "databox";
	public static final String ATTR_PLACE ="place";
	
	private String resourceType = null;
	private String person = null;
	private String queryObject = null;
	
	public ResourceAttributes(String resource) {
		String[] input = resource.split("/");
		this.resourceType = input[1];
		if (input.length > 3) {
			this.person = input[2];
			this.queryObject = input[3];
		} else if (input.length > 2) {
			this.queryObject = input[2];
		}
	}
	
	public String getResourceType() {
		return resourceType;
	}
	
	public String getPerson() {
		return person;
	}
	
	public String getQueryObject() {
		return queryObject;
	}
	
}
