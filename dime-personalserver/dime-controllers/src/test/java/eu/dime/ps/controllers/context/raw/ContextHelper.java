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

package eu.dime.ps.controllers.context.raw;

import java.util.HashMap;


import java.util.Vector;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.ContextElement;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.MetadataMap;
import eu.dime.context.model.impl.Util;

public class ContextHelper {
	
	public static IEntity TEST_ENTITY = Factory.createEntity("testuser");
	
	public static IContextElement createCivilAddress(long ts, int duration, String placeName) {
		HashMap<IScope,IMetadatum> metad = createMetadata(ts,duration);
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
		IScope scopePlaceName = Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS_PLACE_NAME);
		IContextValue placeNameValue = Factory.createContextValue(scopePlaceName, Factory.createValue(placeName));
		contVal.put(scopePlaceName, placeNameValue);
		IContextElement ctxEl = Factory.createContextElement(TEST_ENTITY,
				Factory.createScope(Constants.SCOPE_LOCATION_CIVILADDRESS),
				"JUnit test",
				Factory.createContextValueMap(contVal),
				Factory.createMetadata(metad)
				);
		return ctxEl;
	}
	
	public static IContextDataset createTestWiFiDataset(String wfList, String wfSignal) {
		
		HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
		IContextElement wfCtxEl = createWiFiContextElement(wfList,wfSignal,metad);
		return Factory.createContextDataset(wfCtxEl);
		
	}
	
	public static IContextDataset createTestWiFiDataset(String wfList[], String wfSignal[]) {
		
		
		Vector<IContextElement> wfs = new Vector<IContextElement>();
		
		for (int i=0; i<wfList.length; i++) {
			HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
			IContextElement ce = createWiFiContextElement(wfList[i],wfSignal[i],metad);
			wfs.add(ce);
		}
		
		IContextElement[] wfce = new ContextElement[wfs.size()];
		return Factory.createContextDataset(wfs.toArray(wfce));
		
	}
	
	private static HashMap<IScope, IMetadatum> createMetadata(long ts, int duration) {
		HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();

		IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
		metad.put(timestampScope,
			Factory.createMetadatum(timestampScope,
				Factory.createValue(Util.getDateTime(ts))));
		IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
		metad.put(expireScope,
			Factory.createMetadatum(expireScope,
					Factory.createValue(Util.getDateTime(ts + (duration * 1000)))));
		return metad;
	}

	public static IContextDataset createTestWiFiAndBtDataset(String wfList, String wfSignal, String btList) {
		
		HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
		
		IContextElement wfCtxEl = createWiFiContextElement(wfList,wfSignal,metad);
		IContextElement btCtxEl = createBtContextElement(btList,metad);
		
		IContextElement[] ctxEls = {wfCtxEl,btCtxEl};
		return Factory.createContextDataset(ctxEls);
		
	}
	
	private static IContextElement createBtContextElement(String btList,
			HashMap<IScope, IMetadatum> metad) {
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
		IScope scopeBtList = Factory.createScope(Constants.SCOPE_BT_LIST);
		IContextValue btListValue = Factory.createContextValue(scopeBtList,
				Factory.createValue(btList));
		contVal.put(scopeBtList, btListValue);
		IContextElement ctxEl = Factory.createContextElement(TEST_ENTITY,
				Factory.createScope(Constants.SCOPE_BT),
				"JUnit test",
				Factory.createContextValueMap(contVal),
				Factory.createMetadata(metad)
				);
		return ctxEl;
	}

	private static IContextElement createWiFiContextElement(String wfList, String wfSignal, HashMap<IScope,IMetadatum> metad) {
		
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
		
		IScope scopeWfNames = Factory.createScope(Constants.SCOPE_WF_LIST);
		String[] wfNames = wfList.split(";");
		IContextValue cvWfNames = Factory.createContextValue(scopeWfNames,
				Factory.createValue(wfNames));
		
		String[] wfSignalsStr = wfSignal.split(";");
		int[] wfSignals = new int[wfSignalsStr.length];
		for (int i=0; i<wfSignalsStr.length; i++) {
			wfSignals[i] = Integer.parseInt(wfSignalsStr[i]);
		}
		
		IScope scopeWfSignals = Factory.createScope(Constants.SCOPE_WF_SIGNALS);
		IContextValue cvWfSignals = Factory.createContextValue(scopeWfSignals,
				Factory.createValue(wfSignals));
		
		contVal.put(scopeWfNames, cvWfNames);
		contVal.put(scopeWfSignals, cvWfSignals);
		
		IContextElement ctxEl = Factory.createContextElement(TEST_ENTITY,
				Factory.createScope(Constants.SCOPE_WF),
				"JUnit test",
				Factory.createContextValueMap(contVal),
				Factory.createMetadata(metad)
				);
		return ctxEl;
	}
	
	public static IContextDataset createTestContextDataset(boolean pos, boolean wf, boolean bt) {
		IScope scope = null;
    	Vector<IContextElement> ctxEls = new Vector<IContextElement>();
    	HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
    	
    	HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();

		IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
		metad.put(timestampScope,
			Factory.createMetadatum(timestampScope,
				Factory.createValue(Util.getDateTime(System.currentTimeMillis()))));
		IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
		metad.put(expireScope,
			Factory.createMetadatum(expireScope,
					Factory.createValue(Util.getDateTime(System.currentTimeMillis() + 600000))));
    	
    	if (pos) {
    		scope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION);
    		

    		IScope latitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
    		IContextValue cv = Factory.createContextValue(latitudeScope,
    			Factory.createValue("45.11"));

    		IScope longitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
    		IContextValue cv1 = Factory.createContextValue(longitudeScope,
    			Factory.createValue("7.67"));

    		contVal.put(latitudeScope, cv);
    		contVal.put(longitudeScope, cv1);

    		ctxEls.add(Factory.createContextElement(TEST_ENTITY,
    			scope,
    			"sourceName",
    			Factory.createContextValueMap(contVal),
    			(MetadataMap) Factory.createMetadata(metad)));
    		
    	}
    	
    	if (wf) {
    		scope = Factory.createScope(Constants.SCOPE_WF);
    		

    		IScope latitudeScope = Factory.createScope(Constants.SCOPE_WF_LIST);
    		IContextValue cv = Factory.createContextValue(latitudeScope,
    			Factory.createValue("AAAA;BBBB"));

    		contVal.put(latitudeScope, cv);

    		ctxEls.add(Factory.createContextElement(TEST_ENTITY,
    			scope,
    			"sourceName",
    			Factory.createContextValueMap(contVal),
    			(MetadataMap) Factory.createMetadata(metad)));
    	}
    	
    	if (bt) {
    		scope = Factory.createScope(Constants.SCOPE_BT);
    		

    		IScope latitudeScope = Factory.createScope(Constants.SCOPE_BT_LIST);
    		IContextValue cv = Factory.createContextValue(latitudeScope,
    			Factory.createValue("CCCC;DDDD"));

    		contVal.put(latitudeScope, cv);

    		ctxEls.add(Factory.createContextElement(TEST_ENTITY,
    			scope,
    			"sourceName",
    			Factory.createContextValueMap(contVal),
    			(MetadataMap) Factory.createMetadata(metad)));
    	}
    	
    	IContextElement[] els = new ContextElement[ctxEls.size()];
    	if (ctxEls.size() > 0) return Factory.createContextDataset(ctxEls.toArray(els));
    	else return null;
        
	}

	public static IContextDataset createTestW3CDataset(Double latitude,
			Double longitude, String[] wfList, String[] wfSignals,
			String placeId, String placeName) {
		
		Vector<IContextElement> ces = new Vector<IContextElement>();
		
		if (wfList != null) {
			for (int i=0; i<wfList.length; i++) {
				HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
				IContextElement ce = createWiFiContextElement(wfList[i],wfSignals[i],metad);
				ces.add(ce);
			}
		}
		
		if (latitude != null) {
			HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
			IContextElement ce = createPositionContextElement(latitude,longitude,metad);
			ces.add(ce);
		}
		
		if (placeId != null) {
			HashMap<IScope,IMetadatum> metad = createMetadata(System.currentTimeMillis(),600);
			IContextElement ce = createPlaceContextElement(placeId,placeName,metad);
			ces.add(ce);
		}
		
		IContextElement[] ce = new ContextElement[ces.size()];
		return Factory.createContextDataset(ces.toArray(ce));
	}

	private static IContextElement createPositionContextElement(
			Double latitude, Double longitude, HashMap<IScope, IMetadatum> metad) {
		
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
		IScope scopeLat = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
		IContextValue latValue = Factory.createContextValue(scopeLat,Factory.createValue(latitude));
		contVal.put(scopeLat, latValue);
		IScope scopeLon = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
		IContextValue lonValue = Factory.createContextValue(scopeLon,Factory.createValue(longitude));
		contVal.put(scopeLon, lonValue);
		IContextElement ctxEl = Factory.createContextElement(TEST_ENTITY,
				Factory.createScope(Constants.SCOPE_LOCATION_POSITION),
				"JUnit test",
				Factory.createContextValueMap(contVal),
				Factory.createMetadata(metad)
				);
		return ctxEl;
	}
	
	private static IContextElement createPlaceContextElement(
			String placeId, String placeName, HashMap<IScope, IMetadatum> metad) {
		
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();
		IScope scopePid = Factory.createScope(Constants.SCOPE_CURRENT_PLACE_ID);
		IContextValue pidValue = Factory.createContextValue(scopePid,Factory.createValue(placeId));
		contVal.put(scopePid, pidValue);
		IScope scopePn = Factory.createScope(Constants.SCOPE_CURRENT_PLACE_NAME);
		IContextValue pnValue = Factory.createContextValue(scopePn,Factory.createValue(placeName));
		contVal.put(scopePn, pnValue);
		IContextElement ctxEl = Factory.createContextElement(TEST_ENTITY,
				Factory.createScope(Constants.SCOPE_CURRENT_PLACE),
				"JUnit test",
				Factory.createContextValueMap(contVal),
				Factory.createMetadata(metad)
				);
		return ctxEl;
	}
	
	

}
