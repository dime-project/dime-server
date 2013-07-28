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

package eu.dime.ps.controllers.context.raw.utils;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;
import eu.dime.commons.util.JaxbJsonSerializer;
import eu.dime.context.exceptions.JsonConversionException;
import eu.dime.context.model.api.IContextDataset;

public class JSONContextTransformerTest {
	
	@Test
	public void testClient(){
		try{
			boolean test = false;
			String jsonCtx = "{\"timeRef\" : \"2009-12-04T10:00:00+01:00\", \"entry\" : [ {\"guid\" : \"cell\", \"type\" : \"context\",  \"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : \"222-1-61101-7065\", \"cgi2\" : 222.3}} ]}";
			
			ContextData ctxData = JaxbJsonSerializer.jaxbBean(jsonCtx, ContextData.class);
			IContextDataset ctxds = JSONContextTransformer.jsonContextData2contextDataset(ctxData);
			List<Context> ctxLst = JSONContextTransformer.contextDataset2jsonContextEntry(ctxds);
			String jsonStr = JaxbJsonSerializer.jsonValue(ctxLst);
			System.out.println("Json context dataset: \n" + jsonStr);
			
			// Removes spaces and newlines
			jsonStr = jsonStr.replaceAll("\\r\\n", "").replaceAll(" ", "");
			jsonCtx = jsonCtx.replaceAll(" ","");
			String chkStr = jsonCtx.substring((jsonCtx.indexOf("\"entry\":")+"\"entry\":".length()),jsonCtx.length()-1);
			test = jsonStr.equals(chkStr);
			assertTrue(test);
			System.out.println("Check json serialization/deserialization: " + (test?"OK":"FAILED") + "\n");
			
		} catch (JsonConversionException e) {
			System.out.println("Json parsing error: " + e.getMessage());
			assert(false);
		} catch (Exception e) {
			System.out.println("Json error: " + e.getMessage());
			assert(false);
		}
	}

}
