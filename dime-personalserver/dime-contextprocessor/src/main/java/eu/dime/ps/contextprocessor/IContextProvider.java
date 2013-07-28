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
