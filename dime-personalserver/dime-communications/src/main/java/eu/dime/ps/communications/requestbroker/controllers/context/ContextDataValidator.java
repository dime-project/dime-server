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
