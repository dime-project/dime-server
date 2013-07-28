package eu.dime.context.model.impl;

import eu.dime.context.model.api.IContextData;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IContextValue;

abstract public class AbstractContextData implements IContextData // which extends Serializable
{
    public IValue getValue(IScope scopeKey)
    {
	final IContextValue contextValue = getContextValue(scopeKey);

	return contextValue == null ? null : contextValue.getValue();
    }
}
