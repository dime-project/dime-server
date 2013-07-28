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
