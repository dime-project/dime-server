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

package eu.dime.ps.contextprocessor;

import java.util.HashMap;

import org.junit.Ignore;

import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.MetadataMap;
import eu.dime.ps.contextprocessor.ContextProcessorMockup;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.ps.contextprocessor.ContextListener;
import eu.dime.ps.contextprocessor.helper.ContextDataPrinter;
import eu.dime.ps.storage.entities.Tenant;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Context Processor.
 */
@Ignore 
public class ContextProcessorMockupTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ContextProcessorMockupTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ContextProcessorMockupTest.class );
    }

    @Ignore
    public static void testClient()
    {
    	IContextProcessor cp = new ContextProcessorMockup();
    	IEntity entity = Factory.createEntity(Constants.ENTITY_USER + "|mvalla");
    	IScope scope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION);
    	Tenant t = new Tenant();
    	IContextDataset ctxDs = null;
    	try {
			ctxDs = cp.getContext(t, entity, scope);
		} catch (ContextException e) {
			System.out.println(e.getMessage());
		}
		assertTrue(ctxDs.equals(IContextDataset.EMPTY_CONTEXT_DATASET));
		//assertTrue(ctxDs==null);
		
		/*
		 * Creation of a context dataset to test context update
		 */
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();

		IScope latitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
		IContextValue cv = Factory.createContextValue(latitudeScope,
			Factory.createValue("45.11"));

		IScope longitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
		IContextValue cv1 = Factory.createContextValue(longitudeScope,
			Factory.createValue("7.67"));

		contVal.put(latitudeScope, cv);
		contVal.put(longitudeScope, cv1);

		HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();

		IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
		metad.put(timestampScope,
			Factory.createMetadatum(timestampScope,
				Factory.createValue("2011-07-07T10:30:00+01:00")));
		IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
		metad.put(expireScope,
			Factory.createMetadatum(expireScope,
				Factory.createValue("2011-07-07T12:35:00+01:00")));

		IContextElement ctxEl = Factory.createContextElement(entity,
			scope,
			"sourceName",
			Factory.createContextValueMap(contVal),
			(MetadataMap) Factory.createMetadata(metad));

        IContextDataset ctxDataset = Factory.createContextDataset(ctxEl);
        
        /*
         * Test of contextUpdate
         */
        try {
			cp.contextUpdate(t,ctxDataset);
		} catch (ContextException e) {
			System.out.println(e.getMessage());
		}
		
		/*
		 * Test of getContext
		 */
        try {
			ctxDs = cp.getContext(t,entity, scope);
		} catch (ContextException e) {
			System.out.println(e.getMessage());
			return;
		}
		
		assertTrue(ctxDs.getContextElements()[0].equals(ctxDataset.getContextElements()[0])); //ctxDs!=null);
		
		/*
		 * Visualization of the context element 
		 */
		if (ctxDs.getContextElements().length!=0){
			ContextDataPrinter.printContextElement(ctxDs.getContextElements()[0]);
		}
				
		/*
		 * Test of subscription&notification
		 */
		IContextListener listener = new ContextListener();
		try {
			cp.subscribeContext(entity,scope, listener);
			
			cp.contextUpdate(t,ctxDataset);
			
		} catch (ContextException e) {}
		
    }
    
    /*public static void main (String[] args) {
    	testClient();
    }*/
}
