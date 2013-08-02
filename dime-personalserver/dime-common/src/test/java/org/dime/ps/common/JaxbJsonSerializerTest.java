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

package org.dime.ps.common;

import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import eu.dime.commons.dto.Data;
import eu.dime.commons.dto.Entry;
import eu.dime.commons.util.JaxbJsonSerializer;


public class JaxbJsonSerializerTest {

    
    @Test
    public void testSerializer(){
	
	Data<Entry> data = new Data<Entry>(0, 1, 1);

	Entry entry = new Entry();
	entry.setGuid("urn:uuid:group:f47ac10b-58cc");
	entry.setName("Business");
	entry.setType("group");
	entry.setImageUrl("/icons/group.png");
	Vector<String> items = new Vector<String>();
	items.add("urn:uuid:group:f47ac10b-58cc-c1");
	items.add("urn:uuid:group:f47ac10b-58cc-c2");
	items.add("urn:uuid:group:f47ac10b-58cc-c3");
	entry.setItems(items);
	
	data.getEntries().add(entry);
	
	String string = JaxbJsonSerializer.jsonValue(data);
	String json = "{\"startIndex\":0,\"itemsPerPage\":1,\"totalResults\":1,\"entry\":[{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"name\":\"Business\",\"imageUrl\":\"/icons/group.png\",\"type\":\"group\",\"items\":[\"urn:uuid:group:f47ac10b-58cc-c1\",\"urn:uuid:group:f47ac10b-58cc-c2\",\"urn:uuid:group:f47ac10b-58cc-c3\"]}]}";
	Assert.assertEquals(json, string);
	
    }
    
    @Test
    public void testDeserializer(){
	
	String json = "{\"startIndex\":0,\"itemsPerPage\":1,\"totalResults\":1,\"entry\":[{\"guid\":\"urn:uuid:group:f47ac10b-58cc\",\"name\":\"Business\",\"imageUrl\":\"/icons/group.png\",\"type\":\"group\",\"items\":[\"urn:uuid:group:f47ac10b-58cc-c1\",\"urn:uuid:group:f47ac10b-58cc-c2\",\"urn:uuid:group:f47ac10b-58cc-c3\"]}]}";
	
	Data<Entry> data = JaxbJsonSerializer.jaxbBean(json, Data.class);
	
	Assert.assertNotNull(data);
	
	String string = JaxbJsonSerializer.jsonValue(data);
	Assert.assertEquals(json, string);
	
    }
    
    
}
