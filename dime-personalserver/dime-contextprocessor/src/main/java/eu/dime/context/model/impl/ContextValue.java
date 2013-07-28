package eu.dime.context.model.impl;

import java.util.Map;

import eu.dime.context.model.api.*;

/**
 * Immutable implementation. Abstracts a context value, the actual value is of type
 * {@link eu.dime.context.model.api.IValue}.
 */
class ContextValue extends AbstractContextValue
{
    private final IValue value;

    /**
     * Package-private constructor prohibits instantiation outside the package.
     * Constructs an instance of {@link ContextValue} with the specified
     * values.
     *
     * @param scope the context of the context value
     * @param value the value of the context value
     */
    ContextValue(
	    final IScope scope,
	    final IValue value)
    {
	super(scope);

	this.value = value;
    }

    public IValue getValue()
    {
	return value;
    }

    public String toString()
    {
	return value.toString();
    }
}
