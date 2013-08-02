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

package eu.dime.ps.contextprocessor;

import eu.dime.context.model.api.IContextDataset;
import eu.dime.context.model.api.IEntity;
import eu.dime.context.model.api.IScope;
import eu.dime.ps.storage.entities.Tenant;

/**
 * This interface contains the methods to be exposed by a
 * context provider. It is used by the Context Processor, which
 * invokes these methods in order to retrieve context from them.
 *
 */
public interface IContextProvider {
	
	public IContextDataset getContext(Tenant t, IEntity entity, IScope scope, IContextDataset inputContextDataset);
	
}
