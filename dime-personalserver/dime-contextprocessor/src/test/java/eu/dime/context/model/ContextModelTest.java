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

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.Constants;

import eu.dime.ps.contextprocessor.helper.ContextDataPrinter;
import eu.dime.ps.contextprocessor.helper.Util;

/**
 * Unit test for some Context Model features.
 */
public class ContextModelTest {
	
	@Test
	public void testClient()
	{
		//String jsonCtx = "{\"data\" : {\"ctxEl\" : [ {\"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : \"222-1-61101-7065\", \"cgi2\" : 222.3}} ]} }";
		//			String jsonCtx = "{\"entry\" : [ {\"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : \"222-1-61101-7065\", \"cgi2\" : 222.3}} ]}";
		//			String jsonCtx = "{\"entry\" : [ {\"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : [\"222-1-61101-7065\",\"222-1-61101-7066\"], \"cgi2\" : [222.3,222.0]}} ]}";
		/*			String jsonCtx = "{\"timeRef\" : \"2009-12-04T10:00:00+01:00\", \"entry\" : [ {\"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : \"222-1-61101-7065\", \"cgi2\" : 222.3}} ]}";
		 */

		IEntity entity = Factory.createEntity(Constants.ENTITY_USER + "|mvalla");
		IContextElement ctxEl = Util.createCtxElCellCgiCgi2(entity, "2009-12-04T09:00:00+01:00", "2009-12-04T09:01:00+01:00", "222-1-61101-7065", 222.3, false);
		String timeRef = "2009-12-04T10:00:00+01:00";
		IContextDataset ctxds = Factory.createContextDataset(ctxEl,timeRef);

		System.out.println("Json context element: \n");
		ContextDataPrinter.printContextElement(ctxEl);

		// Test resynch
		long now = System.currentTimeMillis();
		IContextElement resynchCtxEl = null;
		try {
			//				resynchCtxEl = Factory.cloneResynchdContextElement(ctxds.getContextElements()[0],Factory.timestampFromXMLString(ctxds.getTimeRef()),now);
			resynchCtxEl = Factory.cloneResynchdContextElement(ctxds.getContextElements()[0],now);

			System.out.println("Json resynchd context dataset: \n");// + jsonStr);
			ContextDataPrinter.printContextElement(resynchCtxEl);
			assertTrue((resynchCtxEl.getTimestampAsLong()/1000==now/1000)&&
					(resynchCtxEl.getTimestampAsLong()-ctxds.getContextElements()[0].getTimestampAsLong())==
						((resynchCtxEl.getExpiresAsLong()-ctxds.getContextElements()[0].getExpiresAsLong())));

		} catch (Exception e2) {
			System.out.println("Error resynchronizing context dataset: " + e2.getMessage());
		}

		// Test of new methods of IContextElement/IContextDataset
		IContextDataset ctxdsTest = Factory.createContextDataset(new IContextElement[]{ctxds.getContextElements()[0],resynchCtxEl});
		IScope scopeCell = Factory.createScope(Constants.SCOPE_CELL);
		IContextElement ctxel = ctxdsTest.getContextElements()[0];
		IScope paramCgi = Factory.createScope(Constants.SCOPE_CELL_CGI);
		IContextElement lastCE = ctxdsTest.getLastContextElement(entity, scopeCell);
		IContextElement currCE = ctxdsTest.getCurrentContextElement(entity, scopeCell);
		System.out.println("Last context element: \n");
		ContextDataPrinter.printContextElement(lastCE);
		assertTrue(lastCE.getTimestampAsLong()==resynchCtxEl.getTimestampAsLong());
		System.out.println("Current context element: \n");
		ContextDataPrinter.printContextElement(currCE);
		assertTrue(currCE.getTimestampAsLong()==resynchCtxEl.getTimestampAsLong());

		boolean valid = ctxel.isValid();
		String s1 = ctxel.getExpiresAsString();
		long l1 = ctxel.getExpiresAsLong();
		String s2 = ctxel.getTimestampAsString();
		long l2 = ctxel.getTimestampAsLong();
		IValue val1 = ctxdsTest.getLastValue(entity, scopeCell, paramCgi);
		IValue val2 = ctxdsTest.getCurrentValue(entity, scopeCell, paramCgi);
		assertTrue(val1.toString().equals(lastCE.getContextData().getContextValue(paramCgi).getValue().toString()));
		assertTrue(val2.toString().equals(currCE.getContextData().getContextValue(paramCgi).getValue().toString()));
		System.out.println("Parameter/tag values: \n" + 
				"isValid = " + valid + "\n" +
				"timestamp = " + s2 + "\n" +
				"timestamp (long) = " + l2 +  "\n" +
				"expires = " + s1 + "\n" +
				"expires (long) = " + l1 + "\n" +
				"getLast val = " + ((val1!=null)?val1.getValue():null) +  "\n" +
				"getCurrent val = " + ((val2!=null)?val2.getValue():null) +  "\n");

	}
    


}
