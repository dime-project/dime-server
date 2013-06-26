package eu.dime.ps.contextprocessor.utils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IContextData;
import eu.dime.context.model.api.IContextElement;
import eu.dime.context.model.api.IMetadata;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;
import eu.dime.context.model.impl.Factory;

public class ContextUtility {
	
	private static Logger logger = Logger.getLogger(ContextUtility.class);
	
	private static ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
	
	/*
	 * This is NOT the class to be used for JSON serialization from IContextElement to JSON
	 * It's just used to serialize the object in the context database to improve data readability
	 */
	public static String contextElement2JSON(IContextElement el) {
			
		String jsonStr = null;
		try {
			IContextData ctxData = el.getContextData();
			IMetadata md = el.getMetadata();
			String timestamp = (String)md.getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_TIMESTAMP)).getValue();
			String exp = (String)md.getMetadatumValue(Factory.createScope(Constants.SCOPE_METADATA_EXPIRES)).getValue();
			LinkedHashMap<String, Object> datapart = createDatapart(timestamp,exp,ctxData);
			ObjectWriter writer = mapper.defaultPrettyPrintingWriter(); 
			jsonStr = writer.writeValueAsString(datapart);
		} catch (JsonGenerationException e) {
			return ""; //throw new JsonConversionException(e.getMessage(),e);
		} catch (JsonMappingException e) {
			return ""; //throw new JsonConversionException(e.getMessage(),e);
		} catch (IOException e) {
			return ""; //throw new JsonConversionException(e.getMessage(),e);
		}
		return jsonStr;
		
	}
	
	private static LinkedHashMap<String,Object> createDatapart(String ts, String exp, final IContextData ctxData) {

		LinkedHashMap<String,Object> dataPart = new LinkedHashMap<String,Object>();
		
		dataPart.put("timestamp",ts);
		dataPart.put("expires",exp);

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
    	    	(valueType.toString().equals(ValueType.ARRAY_OF_LONGS_VALUE_TYPE.toString())))
    	    	/* does not work, to be checked:
    	    	 if ((valueType.equals(ValueType.STRING_VALUE_TYPE))||
    	    	 (valueType.equals(ValueType.BOOLEAN_VALUE_TYPE))||
    	    	 (valueType.equals(ValueType.DOUBLE_VALUE_TYPE)) ||
    	    	 (valueType.equals(ValueType.FLOAT_VALUE_TYPE)) ||
    	    	 (valueType.equals(ValueType.INTEGER_VALUE_TYPE)) ||
    	    	 (valueType.equals(ValueType.LONG_VALUE_TYPE)))
    	    	 */ {
    	    	dataPart.put(scope, objValue);
    	    } else {
    	    	
    	      // [TI] Note: Arrays not supported (since currently not needed)
    	    	
    	    }
    	}
    	
    	return dataPart;
    }
	
	public static String convertCtxtDT2CacheDT(String ctxtdt) {
		long date = Factory.timestampFromXMLString(ctxtdt);
		return Factory.cacheDateFormat.format(date);
	}

}
