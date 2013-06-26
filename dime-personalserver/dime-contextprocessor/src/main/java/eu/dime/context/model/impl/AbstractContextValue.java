/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl;

import eu.dime.context.model.api.*;

import java.io.Serializable;

abstract public class AbstractContextValue implements IContextValue, Serializable
{
    private final IScope scope;

    public AbstractContextValue()
    {
	this(null);
    }

    public AbstractContextValue(
	    final IScope scope)
    {
	super();

	this.scope = scope;
    }

    public IScope getScope()
    {
	return scope;
    }

}
