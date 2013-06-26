/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl;

import eu.dime.context.model.api.*;
import eu.dime.context.model.impl.values.*;

public class DefaultContextValue extends AbstractContextValue
{
    final IValue value;

    public DefaultContextValue(final IScope scope,
			       final boolean value)
    {
	super(scope);

	this.value = new BooleanValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final double value)
    {
	super(scope);

	this.value = new DoubleValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final float value)
    {
	super(scope);

	this.value = new FloatValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final int value)
    {
	super(scope);

	this.value = new IntegerValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final long value)
    {
	super(scope);

	this.value = new LongValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final String value)
    {
	super(scope);

	this.value = new StringValue(value);
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
