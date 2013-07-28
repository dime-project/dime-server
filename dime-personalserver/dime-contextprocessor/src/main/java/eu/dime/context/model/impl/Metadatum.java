package eu.dime.context.model.impl;

import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;

import java.io.Serializable;

/**
 * Immutable implementation of the Metadatum data-structure.
 */
class Metadatum implements IMetadatum, Serializable
{
    private final IScope scope;
    private final IValue value;

    /**
     * Package-private constructor prohibits instantiation outside the package.
     * Constructs an instance of {@link ContextValue} with the specified
     * values.
     *
     * @param scope the metadata element's scope
     * @param value the {@link IValue} of this metadata element
     */
    Metadatum(
	    final IScope scope,
	    final IValue value)
    {
	this.scope = scope;
	this.value = value;
    }

    public IScope getScope()
    {
	return scope;
    }

    public IValue getValue()
    {
	return value;
    }
}
