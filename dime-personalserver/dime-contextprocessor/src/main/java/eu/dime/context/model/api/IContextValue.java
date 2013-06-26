/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

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
