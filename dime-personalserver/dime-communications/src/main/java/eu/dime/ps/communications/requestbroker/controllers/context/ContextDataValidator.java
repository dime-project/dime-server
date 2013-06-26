package eu.dime.ps.communications.requestbroker.controllers.context;

import java.util.Collection;
import java.util.Iterator;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;

public class ContextDataValidator {
    /**
     * Validates that the ContextData is well-formed
     */
    public static void validateCtxData(ContextData ctxdata) throws IllegalArgumentException {
    	Collection<Context> ctxColl = ctxdata.getEntries();
    	Iterator<Context> it = ctxColl.iterator();
    	while (it.hasNext()){
    		if (!checkContext(it.next()))
    			throw new IllegalArgumentException("Wrong JSON Context Data");
    	}
    }
    
    private static boolean checkContext(Context ctx){
    	return (ctx.getEntity()!=null) && (ctx.getScope()!=null) &&
    			(ctx.getTimestamp()!=null) && (ctx.getExpires()!=null) &&
    			(ctx.getDataPart()!=null) && (!ctx.getDataPart().isEmpty());
    }

}
