package eu.dime.ps.controllers.context.raw.ifc;

import eu.dime.commons.dto.Context;
import eu.dime.commons.dto.ContextData;
import eu.dime.commons.dto.Data;
import eu.dime.context.exceptions.ContextException;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;

public interface RawContextManager {
	
	public Data<Context> getContext(
		    final String said,
		    final String scope)
		    throws ContextException;
	
	public void contextUpdate(String said,
	    	final ContextData contextData)
	    	throws ContextException;
	
	public void deleteContext(
		    final String said,
		    final String scope)
		    throws ContextException;

}
