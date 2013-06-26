package eu.dime.ps.contextprocessor;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import javax.validation.constraints.AssertFalse;

import eu.dime.ps.contextprocessor.ContextListener;
import eu.dime.ps.contextprocessor.IContextProcessor;
import eu.dime.context.IContextListener;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.MetadataMap;
import eu.dime.context.model.Constants;

import eu.dime.ps.contextprocessor.helper.ContextDataPrinter;
import eu.dime.ps.contextprocessor.helper.Util;
import eu.dime.ps.contextprocessor.impl.ContextProcessorImpl;
import eu.dime.ps.contextprocessor.impl.RawContextNotification;
//import eu.dime.ps.storage.DimeStorageImpl;
import eu.dime.ps.storage.IStorage;
import eu.dime.ps.storage.entities.Tenant;

import static org.mockito.Mockito.*;

/**
 * Unit test for Context Processor.
 */
public class ContextProcessorTest {
	
	private IContextProcessor contextProcessor;
	
    @Test
    public void testClient()
    {
    	try {
			
			IEntity entity = Factory.createEntity(Constants.ENTITY_USER + "|mvalla");
			IContextElement ctxEl1 = Util.createCtxElCellCgiCgi2(entity, "2009-12-04T09:00:00+01:00", "2009-12-04T09:01:00+01:00", "222-1-61101-7065", 222.3, false);
			String timeRef = "2009-12-04T10:00:00+01:00";
			IContextDataset ctxds = Factory.createContextDataset(ctxEl1,timeRef);
			Tenant t = new Tenant();

			System.out.println("Json context element: \n");
			ContextDataPrinter.printContextElement(ctxEl1);
			
			IStorage mockStorage = mock(IStorage.class);

			contextProcessor = new ContextProcessorImpl(mockStorage);

			IScope scope = Factory.createScope(Constants.SCOPE_CELL);
			
			IContextDataset ctxDs = null;
			try {
				contextProcessor.contextUpdate(t,ctxds);
				verify(mockStorage).storeContextElement((Tenant)anyObject(),(IContextElement)anyObject());
				
				contextProcessor.deleteContext(entity, scope);
				verify(mockStorage).deleteContextElements(entity, scope);

				when(mockStorage.getCurrentContextElements((Tenant)anyObject(), (IEntity)anyObject(), (IScope)anyObject())).thenReturn(IContextElement.EMPTY_CONTEXT_ELEMENT_ARRAY);
				ctxDs = contextProcessor.getContext(t,entity, scope);
				verify(mockStorage).getCurrentContextElements(t,entity, scope);
			} catch (ContextException e) {
				System.out.println(e.getMessage());
			}
			System.out.println("Check of update/delete/get context element performed!");// + (test?"OK":"FAILED") + "\n");
								
			/*
			 * Test of subscription&notification
			 */
			IContextListener listener = mock(IContextListener.class);
			try {
				contextProcessor.subscribeContext(entity, scope, listener);
				contextProcessor.contextUpdate(t,ctxds);
				// Sleep needed, since notification is done in a separate thread
				Thread.sleep(200);
				verify(listener).contextChanged((RawContextNotification)anyObject());
				System.out.println("Check of notification performed!");
			} catch (ContextException e) {
				assertFalse(true);
			}
			
    	} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			assertFalse(true);
		}
		
    }
    
}
