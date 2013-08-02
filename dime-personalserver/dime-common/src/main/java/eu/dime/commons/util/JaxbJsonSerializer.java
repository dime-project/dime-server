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

package eu.dime.commons.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class JaxbJsonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JaxbJsonSerializer.class);
    
    public static String jsonValue(Object value){
	ObjectMapper mapper = new ObjectMapper();
	AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
	// make deserializer use JAXB annotations (only)
	mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
	// make serializer use JAXB annotations (only)
	mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
	StringWriter sw = new StringWriter();
	
	try {
	    mapper.writeValue(sw,value);
	} catch (JsonGenerationException e) {
	    return null;
	} catch (JsonMappingException e) {
	    return null;
	} catch (IOException e) {
	    return null;
	}
	
	String result = sw.toString();	
	return result;
    }
    
    public static <T> T jaxbBean(String json, Class<T> clazz){
	ObjectMapper mapper = new ObjectMapper();
	AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
	// make deserializer use JAXB annotations (only)
	mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
	// make serializer use JAXB annotations (only)
	mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
	StringWriter sw = new StringWriter();
	
	T result = null;
	try {
	    result = mapper.readValue(json, clazz);
	    
	} catch (JsonParseException e) {
	    return null;
	} catch (JsonMappingException e) {
	    return null;
	} catch (IOException e) {
	    return null;
	}
	
	return result;
    }

    /**
     * @param clazz The class to get the item type for
     * @return null if no valid item type is found
     */
    public static String getItemType(Class clazz) {
    	if (clazz.getName().equals("eu.dime.ps.semantic.model.nco.PersonContact")) {
    		return "profile";
    	}
    	else if (clazz.getName().equals("eu.dime.ps.semantic.model.dlpo.StatusMessage")) {
    		return "livepost";
    	}
    	else if (clazz.getName().equals("eu.dime.ps.semantic.model.dao.Account")) {
    		return "serviceaccount";
    	}
		return null;
    }
    
    public static LinkedHashMap getMapFromJSON(String json){

	
	LinkedHashMap payload;
	
	try {
	    payload = new ObjectMapper().readValue(json, LinkedHashMap.class);
	    return payload;
	    
	} catch (JsonParseException e) {
	    //e.printStackTrace();
	    logger.warn(e.getMessage());
	    return null;

	} catch (JsonMappingException e) {
	    //e.printStackTrace();
	    logger.warn(e.getMessage());
	    return null;

	} catch (IOException e) {
	    //e.printStackTrace();
	    logger.warn(e.getMessage());
	    return null;

	}
	
    }
    
}
