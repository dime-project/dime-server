package eu.dime.context.model.api;

import java.io.Serializable;

/**
 *
 * @see IValue
 */
public interface IContextValue extends Serializable
{
    public IScope getScope();

    public IValue getValue();
}
