package eu.dime.context.model.api;

import java.io.Serializable;

/**
 *
 */
public interface IMetadatum extends Serializable
{
    public IScope getScope();

    public IValue getValue();
}
