package eu.dime.ps.contextprocessor.helper;

import java.util.HashMap;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IContextValue;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.impl.Factory;
import eu.dime.context.model.impl.MetadataMap;

public class Util {

    static public boolean isContextDataExpired(
    	    final IContextElement contextElement)
        {
    	if(contextElement == null)
    	{
    	    throw new NullPointerException("Illegal null argument");
    	}

    	final IMetadata metadata = contextElement.getMetadata();

    	final IMetadatum metadatum
    		= metadata.getMetadatum(Factory.METADATA_EXPIRES_SCOPE);
    	final String expiresStr = (String) metadatum.getValue().getValue();
    	final long expires = Factory.timestampFromXMLString(expiresStr);

    	return expires < System.currentTimeMillis();
        }
    
    static public IContextElement createCtxElPosition(IEntity entity, 
    		String timestampStr, String expireStr, double lat, double lon){
    	return createCtxElPosition(entity, timestampStr, expireStr, lat, lon, false);
    }
    
    static public IContextElement createCtxElPositionValid(IEntity entity, 
    		double lat, double lon){
    	return createCtxElPosition(entity, "", "", lat, lon, true);
    }
    
    static public IContextElement createCtxElPosition(IEntity entity, 
    		String timestampStr, String expireStr, double lat, double lon, boolean nowValid){
    	
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();

		IScope latitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
		IContextValue cv = Factory.createContextValue(latitudeScope,
			Factory.createValue(lat));

		IScope longitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
		IContextValue cv1 = Factory.createContextValue(longitudeScope,
			Factory.createValue(lon));

		contVal.put(latitudeScope, cv);
		contVal.put(longitudeScope, cv1);

		IMetadata metadata = null;
		if (!nowValid){
			HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();
	
			IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
			metad.put(timestampScope,
				Factory.createMetadatum(timestampScope,
					Factory.createValue(timestampStr)));
			IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
			metad.put(expireScope,
				Factory.createMetadatum(expireScope,
					Factory.createValue(expireStr)));
			metadata = (MetadataMap) Factory.createMetadata(metad);
		}else{
			metadata = Factory.createDefaultMetadata(180000);
		}
		IContextElement ctxEl = Factory.createContextElement(entity,
			Factory.createScope(Constants.SCOPE_LOCATION_POSITION),
			"Dime",
			Factory.createContextValueMap(contVal),
			metadata
			);
		
		return ctxEl;
    }
    
    static public IContextElement createCtxElWithArray(IEntity entity, 
    		String timestampStr, String expireStr, double[] lat, boolean nowValid){
    	
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();

		IScope latitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LATITUDE);
		IContextValue cv = Factory.createContextValue(latitudeScope,
			Factory.createValue(lat));

/*		IScope longitudeScope = Factory.createScope(Constants.SCOPE_LOCATION_POSITION_LONGITUDE);
		IContextValue cv1 = Factory.createContextValue(longitudeScope,
			Factory.createValue(lon));*/

		contVal.put(latitudeScope, cv);
//		contVal.put(longitudeScope, cv1);

		IMetadata metadata = null;
		if (!nowValid){
			HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();
	
			IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
			metad.put(timestampScope,
				Factory.createMetadatum(timestampScope,
					Factory.createValue(timestampStr)));
			IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
			metad.put(expireScope,
				Factory.createMetadatum(expireScope,
					Factory.createValue(expireStr)));
			metadata = (MetadataMap) Factory.createMetadata(metad);
		}else{
			metadata = Factory.createDefaultMetadata(180000);
		}
		IContextElement ctxEl = Factory.createContextElement(entity,
			Factory.createScope(Constants.SCOPE_LOCATION_POSITION),
			"Dime",
			Factory.createContextValueMap(contVal),
			metadata
			);
		
		return ctxEl;
    }
    
    /*
     String jsonCtx = "{\"timeRef\" : \"2009-12-04T10:00:00+01:00\", \"entry\" : [ {\"entity\" : {\"type\" : \"user\",\"id\" : \"mvalla\"},\"scope\":\"cell\",\"source\" : {\"id\" : \"Dime\",\"v\" : \"1.0\"},\"timestamp\" : \"2009-12-04T09:00:00+01:00\",\"expires\" : \"2009-12-04T09:01:00+01:00\",\"dataPart\" : {\"cgi\" : \"222-1-61101-7065\", \"cgi2\" : 222.3}} ]}";
    */
    static public IContextElement createCtxElCellCgiCgi2(IEntity entity, 
    		String timestampStr, String expireStr, String cgi, double cgi2, boolean nowValid){
    	
		HashMap<IScope,IContextValue> contVal = new HashMap<IScope,IContextValue>();

		IScope cgiScope = Factory.createScope(Constants.SCOPE_CELL_CGI);
		IContextValue cv = Factory.createContextValue(cgiScope,
			Factory.createValue(cgi));

		IScope cgi2Scope = Factory.createScope("cgi2");
		IContextValue cv1 = Factory.createContextValue(cgi2Scope,
			Factory.createValue(cgi2));

		contVal.put(cgiScope, cv);
		contVal.put(cgi2Scope, cv1);

		IMetadata metadata = null;
		if (!nowValid){
			HashMap<IScope,IMetadatum> metad = new HashMap<IScope,IMetadatum>();
	
			IScope timestampScope = Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP);
			metad.put(timestampScope,
				Factory.createMetadatum(timestampScope,
					Factory.createValue(timestampStr)));
			IScope expireScope = Factory.createScope(Constants.SCOPE_METADATA_EXPIRES);
			metad.put(expireScope,
				Factory.createMetadatum(expireScope,
					Factory.createValue(expireStr)));
			metadata = (MetadataMap) Factory.createMetadata(metad);
		}else{
			metadata = Factory.createDefaultMetadata(180000);
		}
		IContextElement ctxEl = Factory.createContextElement(entity,
			Factory.createScope(Constants.SCOPE_CELL),
			"Dime",
			Factory.createContextValueMap(contVal),
			metadata
			);
		
		return ctxEl;
    }
    
}
