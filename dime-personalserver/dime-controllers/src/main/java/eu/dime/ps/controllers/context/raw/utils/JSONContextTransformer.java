package eu.dime.ps.controllers.context.raw.utils;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;
import eu.dime.commons.dto.ContextEntity;
import eu.dime.commons.dto.ContextSource;
import eu.dime.context.exceptions.JsonConversionException;
import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextData;
import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;
import eu.dime.context.model.impl.Factory;

public class JSONContextTransformer {
		
	private static Context contextElement2jsonObject(IContextElement ctxEl){
	
		Context ctxElObj = new Context();
		
		ctxElObj.setEntity(new ContextEntity(ctxEl.getEntity().getEntityTypeAsString(),
				ctxEl.getEntity().getEntityIDAsString()));
		
		ctxElObj.setScope(ctxEl.getScope().getScopeAsString());
		ctxElObj.setGuid(ctxEl.getScope().getScopeAsString());
		ctxElObj.setType("context");
		
		ctxElObj.setSource(new ContextSource(ctxEl.getSource(),"1.0"));
		
		IMetadata md = ctxEl.getMetadata();
		Set s = md.keySet();
		String timestamp = (String)md.getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue();
		ctxElObj.setTimestamp(timestamp);
		
		String expires = (String)md.getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue();
		ctxElObj.setExpires(expires);
		
		
		
		IContextData ctxData = ctxEl.getContextData();

		ctxElObj.setDataPart(createDatapart(ctxData));
		
		return ctxElObj;
	}
	
	private static LinkedHashMap<String,Object> createDatapart(final IContextData ctxData) {

		LinkedHashMap<String,Object> dataPart = new LinkedHashMap<String,Object>();

    	Set s = ctxData.keySet();
    	Object[] keys = s.toArray();

    	for (int i = 0; i < s.size(); i++) {
    		IScope ctxValScope = (IScope) keys[i];
    		String scope = ctxValScope.getScopeAsString();
    		IValue value = ctxData.getContextValue(ctxValScope).getValue();
    		Object objValue = value.getValue();
    	    ValueType valueType = value.getValueType();

    	    if ((valueType.toString().equals(ValueType.STRING_VALUE_TYPE.toString()))||
    	    	(valueType.toString().equals(ValueType.BOOLEAN_VALUE_TYPE.toString()))||
    	    	(valueType.toString().equals(ValueType.DOUBLE_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.FLOAT_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.INTEGER_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.LONG_VALUE_TYPE.toString())) || //)
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_STRINGS_VALUE_TYPE.toString()))||
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_BOOLEANS_VALUE_TYPE.toString()))||
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_DOUBLES_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_FLOATS_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_INTEGERS_VALUE_TYPE.toString())) ||
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_LONGS_VALUE_TYPE.toString()))) {
    	    		dataPart.put(scope, objValue);
    	        }
    	}
    	
    	return dataPart;
    }

	/**
	 * Transform the IContextDataset object in a JSON object, ready to be serialized
	 * IMPORTANT: timeRef field of IContextDataset object is NOT serialized, since it
	 * should be put out of the JSONContextElement list.
	 * @param ctxDataset
	 * @return JSON object list to be linked to "entry" field of eu.dime.ps.communications.requestbroker.json.JSONData
	 * @throws JsonConversionException
	 */
	public static List<Context> contextDataset2jsonContextEntry(IContextDataset ctxDataset) throws JsonConversionException{
		
		List<Context> jsonCtxElObjList = new ArrayList<Context>(); 
		try {
			IContextElement[] ctxElArr = ctxDataset.getContextElements();
			
			for (int i=0; i<ctxElArr.length; i++){
				Context ctxElObj = contextElement2jsonObject(ctxElArr[i]);
				jsonCtxElObjList.add(ctxElObj);
			}			
		} catch (Exception e) {
			throw new JsonConversionException(e.getMessage(),e);
		}
		
		return jsonCtxElObjList;
	
	}
	
	public static IContextDataset jsonContextData2contextDataset(ContextData ctxData) throws JsonConversionException{

		Vector<IContextElement> ctxElements = new Vector<IContextElement>();
		IContextDataset ctxDataset = IContextDataset.EMPTY_CONTEXT_DATASET;

		try {
			
			String timeRef = ctxData.getTimeRef();
						
			Collection<Context> ctxColl = ctxData.getEntries();
			Iterator<Context> ctxElIt = ctxColl.iterator();

			while (ctxElIt.hasNext()){
				Context ctxEl = ctxElIt.next();//(LinkedHashMap<String,Object>)ctxElIt.next();

				String source = ctxEl.getSource().getId();
				
				String entityId = ctxEl.getEntity().getId();
				String entityType = ctxEl.getEntity().getType();
				IEntity entity = Factory.createEntity(entityType + IEntity.ENTITY_ID_SEPARATOR + entityId);

				String scopeName = ctxEl.getScope();
				IScope scope = Factory.createScope(scopeName);

				HashMap metadataMap = new HashMap();
				String timestamp = ctxEl.getTimestamp();
				metadataMap.put(Factory.METADATA_TIMESTAMP_SCOPE,
						Factory.createMetadatum(Factory.METADATA_TIMESTAMP_SCOPE,
								Factory.createValue(timestamp)));

				String expires = ctxEl.getExpires();
				metadataMap.put(Factory.METADATA_EXPIRES_SCOPE,
						Factory.createMetadatum(Factory.METADATA_EXPIRES_SCOPE,
								Factory.createValue(expires)));

				IMetadata metadata = Factory.createMetadata(metadataMap);

				HashMap ctxValuesMap = new HashMap();
				Map<String,Object> dataPart = ctxEl.getDataPart(); // dataPart
				Set<String> genParNames = dataPart.keySet();
				Iterator<String> genParNamesIt = genParNames.iterator();
				while (genParNamesIt.hasNext()){
					String genParN = genParNamesIt.next();
					Object genPar = dataPart.get(genParN);
					if ((genPar instanceof String)||
							(genPar instanceof Double)||
							(genPar instanceof Long)||
							(genPar instanceof Integer)||
							(genPar instanceof Boolean)){ // par
						IContextValue ctxValue = Factory.createContextValue(Factory.createScope(genParN),
								Factory.createValue(genPar));
						ctxValuesMap.put(Factory.createScope(genParN), ctxValue);

					}else if (genPar instanceof ArrayList){ // parA
						Object obj = ((ArrayList)genPar).get(0);
						Object[] objArr = null;
						if (obj instanceof String)
							objArr = ((ArrayList)genPar).toArray(new String[0]);
						else if (obj instanceof Double)
							objArr = ((ArrayList)genPar).toArray(new Double[0]);
						else if (obj instanceof Long)
							objArr = ((ArrayList)genPar).toArray(new Long[0]);
						else if (obj instanceof Integer)
							objArr = ((ArrayList)genPar).toArray(new Integer[0]);
						else if (obj instanceof Boolean)
							objArr = ((ArrayList)genPar).toArray(new Boolean[0]);
						else
							throw new Exception("Type not supported in array");
						IContextValue ctxValue = Factory.createContextValue(Factory.createScope(genParN),
								Factory.createValue(objArr));
						ctxValuesMap.put(Factory.createScope(genParN), ctxValue);
							
					} else {
							throw new Exception();// error
					}

				}
				IContextElement contextElement = Factory.createContextElement(entity,
						scope,
						source,
						Factory.createContextValueMap(ctxValuesMap),
						metadata);
				ctxElements.add(contextElement);
			}
			ctxDataset = Factory.createContextDataset(ctxElements.toArray(new IContextElement[0]),timeRef);

			return ctxDataset;
		} catch (Exception e) {
			throw new JsonConversionException(e.getMessage(),e);
		}

	}

}
