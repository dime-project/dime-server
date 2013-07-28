package eu.dime.ps.contextprocessor;

import eu.dime.context.model.api.IScope;

/**
 * This is the point of interaction with the context providers.
 * Every provider should register the provided and the needed
 * scopes, and unregister when it becomes unavailable.
 *
 */
public interface IProviderManager {

	public void registerProvider(IScope outScope, IScope[] inScopeArr, IContextProvider provider);
	
	public void unregisterProvider(IContextProvider provider);
	
}
