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

package eu.dime.context.model.impl;

import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;

import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

/**
 * DefaultContextDataset contains the data retrieved from the context data,
 * as a result of a context query.
 *
 * Also, this class implements the {@link java.io.Serializable} interface.
 */
public class ContextDataset implements IContextDataset
{
    public static final IContextDataset EMPTY_CONTEXT_DATASET = new ContextDataset();

    final private IContextElement [] contextElements;
    
    final private String timeRef;

    private ContextDataset()
    {
	this(new IContextElement [0]);
    }

    ContextDataset(final IContextElement contextElement)
    {
	this(new IContextElement [] {contextElement});
    }
    
    ContextDataset(final IContextElement contextElement, String timeRef)
    {
	this(new IContextElement [] {contextElement}, timeRef);
    }
    
    ContextDataset(final IContextElement [] contextElements)
    {
	this.contextElements = contextElements;
	this.timeRef = null;
    }

    ContextDataset(final IContextElement [] contextElements, String timeRef)
    {
	this.contextElements = contextElements;
	this.timeRef = timeRef;
    }
    
    public IContextElement [] getContextElements()
    {
	return contextElements;
    }

    /*public IContextElement [] getContextElements(IEntity entity, IScope scope)
    {
	final Set contextElementsSet = new HashSet();

	for(int i = 0; i < contextElements.length; i++)
	{
	    final IContextElement contextElement = contextElements[i];
	    if(contextElement.getEntity().equals(entity)
		    && contextElement.getScope().equals(scope))
	    {
		contextElementsSet.add(contextElement);
	    }
	}

	final IContextElement [] contextElements = new IContextElement[contextElementsSet.size()];

	return (IContextElement []) contextElementsSet.toArray(contextElements);
    }*/

    public IContextElement [] getContextElements(IEntity entity, IScope scope)
    {
	final Vector contextElementsVect = new Vector();

	for(int i = 0; i < contextElements.length; i++)
	{
	    final IContextElement contextElement = contextElements[i];
	    if(contextElement.getEntity().equals(entity)
		    && contextElement.getScope().equals(scope))
	    {
	    	contextElementsVect.add(contextElement);
	    }
	}

	final IContextElement [] contextElements = new IContextElement[contextElementsVect.size()];

	return (IContextElement []) contextElementsVect.toArray(contextElements);
    }

    public IContextElement [] getContextElements(IScope scope)
    {
	final Vector contextElementsVect = new Vector();

	for(int i = 0; i < contextElements.length; i++)
	{
	    final IContextElement contextElement = contextElements[i];
	    if(contextElement.getScope().equals(scope))
	    {
	    	contextElementsVect.add(contextElement);
	    }
	}

	final IContextElement [] contextElements = new IContextElement[contextElementsVect.size()];

	return (IContextElement []) contextElementsVect.toArray(contextElements);
    }
    
    public String getTimeRef(){
    	return timeRef;
    }
    
    public IValue getLastValue(final IEntity entity, final IScope scope, final IScope param){
    	IContextElement[] ctxElArr = getContextElements(entity, scope);
    	if (ctxElArr.length==0)
    		return null;
    	
    	return ctxElArr[getMostRecentCtxElIdx(ctxElArr)].getContextData().getValue(param);
    }
    
    public IValue getCurrentValue(final IEntity entity, final IScope scope, final IScope param){
    	IContextElement[] ctxElArr = getContextElements(entity, scope);
    	if (ctxElArr.length==0)
    		return null;
    	int lastIdx = getMostRecentCtxElIdx(ctxElArr);
    	if (ctxElArr[lastIdx].isValid())
    		return ctxElArr[lastIdx].getContextData().getValue(param);
    	else
    		return null;
    }
    
    public IContextElement getLastContextElement(final IEntity entity, final IScope scope){
    	IContextElement[] ctxElArr = getContextElements(entity, scope);
    	if (ctxElArr.length==0)
    		return null;
    	
    	return ctxElArr[getMostRecentCtxElIdx(ctxElArr)];
    }
    
    public IContextElement getCurrentContextElement(final IEntity entity, final IScope scope){
    	IContextElement[] ctxElArr = getContextElements(entity, scope);
    	if (ctxElArr.length==0)
    		return null;
    	int lastIdx = getMostRecentCtxElIdx(ctxElArr);
    	if (ctxElArr[lastIdx].isValid())
    		return ctxElArr[lastIdx];
    	else
    		return null;
    }
    
    public boolean isEmpty()
    {
	return contextElements.length == 0;
    }
    
    static private int getMostRecentCtxElIdx(IContextElement[] ctxElArr){
    	// Search the most recent element, and return it
    	int lastIdx = 0;
    	long currLastTimestamp = ctxElArr[0].getTimestampAsLong();
    	for (int i=1; i<ctxElArr.length; i++){
    		long newTimestamp = ctxElArr[i].getTimestampAsLong();
    		if (newTimestamp>currLastTimestamp){
    			currLastTimestamp = newTimestamp;
    			lastIdx = i;
    		}
    	}
    	return lastIdx;
    }
}
