/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

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
