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

package eu.dime.context.model;

import org.apache.log4j.Logger;

import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.impl.Factory;

/**
 * Includes the strings for the main context types (entities and scopes).
 */
public class Constants
{
	
	//public static IEntity ENTITY_ME = Factory.createEntity("device|12345");
	public static IEntity ENTITY_DEV12345 = Factory.createEntity("device|12345");
	
	public static final String DIME_ONTOLOGY_V0_1_ID = "http://servername/Ontology_v0_1.xml";
	
	public static final String DEFAULT_ONTOLOGY = DIME_ONTOLOGY_V0_1_ID;
	

    public static final String SCOPE_METADATA_TIMESTAMP
	    = "timestamp";

    public static final String SCOPE_METADATA_EXPIRES
	    = "expires";


    
    public static final String ENTITY_DEVICE
	    = "device";

    public static final String ENTITY_USER
	    = "username";

	public static IEntity ENTITY_ME = Factory.createEntity(ENTITY_USER + "|@me");
	
	public static IEntity ENTITY_ALL_USERS = Factory.createEntity(ENTITY_USER + "|*");

	
    public static final String SCOPE_WF
    	= "wf";
    
    public static final String SCOPE_WF_LIST
    	= "wfList";
    
    public static final String SCOPE_WF_NAMES
		= "wfNames";
    
    public static final String SCOPE_WF_SIGNALS
		= "wfSignals";
    
    public static final String SCOPE_BT
    	= "bt";
    
    public static final String SCOPE_BT_LIST
    	= "btList";
    
    public static final String SCOPE_BT_LOCAL		
    	= "btLocal";
    
    public static final String SCOPE_PROXIMITY
		= "userProximity";
    
    public static final String SCOPE_PROXIMITY_USERS
		= "users";
    
    public static final String SCOPE_LOCATION_POSITION
	    = "position";

    public static final String SCOPE_LOCATION_POSITION_LATITUDE
	    = "latitude";

    public static final String SCOPE_LOCATION_POSITION_LONGITUDE
	    = "longitude";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS
    	= "civilAddress";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_COUNTRY
		= "country";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_CITY
		= "city";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_STREET
		= "street";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_BUILDING
		= "building";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_FLOOR
		= "floor";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_CORRIDOR
		= "corridor";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_ROOM
		= "room";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_PLACE_TYPE
		= "placeType";
    
    public static final String SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME
		= "placeName";
    
    
    public static final String SCOPE_CURRENT_PLACE	= "currentPlace";
	public static final String SCOPE_CURRENT_PLACE_ID	= "placeId";
	public static final String SCOPE_CURRENT_PLACE_NAME	= "placeName";
	
	public static final String SCOPE_CURRENT_EVENT	= "currentEvent";
	public static final String SCOPE_CURRENT_EVENT_ID	= "eventId";
	public static final String SCOPE_CURRENT_EVENT_NAME	= "eventName";
	
	public static final String SCOPE_STATUS = "deviceStatus";
	public static final String SCOPE_STATUS_KEEPALIVE = "keepAlive";
    public static final String SCOPE_STATUS_IS_ALIVE = "isAlive";
    public static final String SCOPE_STATUS_LAST_ACTIVE = "lastActive";

	public static final String SCOPE_CELL 
		= "cell";
	
	public static final String SCOPE_CELL_CGI
		= "cgi";
	
	public static final String SCOPE_ACTIVITY 
		= "situation";

	public static final String SCOPE_ACTIVITY_CURRENT 
		= "current";

    }
