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

package eu.dime.context.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IScope;

/**
 * Utility methods for the model.
 */
public class Util
{
	
	private static String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
	
    /**
     * Static method that is used to test if the timestamp of the given
     * {@link IContextElement} is within the boundary specified by the
     * fromTimestamp and the toTimestamp arguments.
     *
     * @param contextElement the element to check its timestamp
     * @param fromTimestamp the lower timestamp boundary
     * @param toTimestamp the upper timestamp boundary
     * @return true if and only if the timestamp of the specified context
     * element is within the range [fromTimestamp, toTimestamp]
     */
    static public boolean testIfInTimestampRange(
	    final IContextElement contextElement,
	    final long fromTimestamp,
	    final long toTimestamp)
    {
	if(contextElement == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	if(fromTimestamp > toTimestamp)
	{
	    throw new IllegalArgumentException("The fromTimestamp must be" +
		    " smaller than the toTimestamp argument");
	}

	final IMetadata metadata = contextElement.getMetadata();

	final IMetadatum metadatum
		= metadata.getMetadatum(Factory.METADATA_TIMESTAMP_SCOPE);
	final String timestampS = (String) metadatum.getValue().getValue();
	final long timestamp = Factory.timestampFromXMLString(timestampS);

	return fromTimestamp < timestamp && timestamp < toTimestamp;
    }
    
    static public String getDateTime(long millis) {
    	DateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
		Date expDate = new Date(millis);
		String expires = dateFormat.format(expDate);
		String left = expires.substring(0,expires.length()-2);
		String right = expires.substring(expires.length()-2);
		expires = left + ":" + right;
		return expires;
    }
    
    static public IContextDataset createSimpleContextDataset(IEntity entity, IScope scope, String param, String value, int duration) {
    	
    	HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
    	
    	HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();

		IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
		metad.put(timestampScope,
			Factory.createMetadatum(timestampScope,
				Factory.createValue(Util.getDateTime(System.currentTimeMillis()))));
		IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
		metad.put(expireScope,
			Factory.createMetadatum(expireScope,
					Factory.createValue(Util.getDateTime(System.currentTimeMillis() + (duration * 1000)))));
    
		IScope par = Factory.createScope(param);
		IContextValue val = Factory.createContextValue(par,Factory.createValue(value));
		contVal.put(par, val);
		
		IContextElement ctxEl = Factory.createContextElement(entity,
    			scope,
    			"sourceName",
    			Factory.createContextValueMap(contVal),
    			(MetadataMap) Factory.createMetadata(metad));
		
		return Factory.createContextDataset(ctxEl);
    }
    
    static public IContextDataset createComplexContextDataset(IEntity entity, IScope scope, String source, String[] param, String[] value, int duration) {
    	
    	HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
    	
    	HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();
    	
    	Vector<IContextElement> ctxEls = new Vector<IContextElement>();

		IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
		metad.put(timestampScope,
			Factory.createMetadatum(timestampScope,
				Factory.createValue(Util.getDateTime(System.currentTimeMillis()))));
		IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
		metad.put(expireScope,
			Factory.createMetadatum(expireScope,
					Factory.createValue(Util.getDateTime(System.currentTimeMillis() + (duration * 1000)))));
		
		contVal = new HashMap<IScope,IContextValue>();
		for (int i=0; i<param.length; i++) {
			IScope par = Factory.createScope(param[i]);
			IContextValue val = Factory.createContextValue(par,Factory.createValue(value[i]));
			contVal.put(par, val);
		}
		
		ctxEls.add(Factory.createContextElement(entity,
    			scope,
    			source,
    			Factory.createContextValueMap(contVal),
    			(MetadataMap) Factory.createMetadata(metad)));
    
		IContextElement[] els = new ContextElement[ctxEls.size()];
		return Factory.createContextDataset(ctxEls.toArray(els));
		
    }
    
}
