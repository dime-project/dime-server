package eu.dime.context.model.impl;

import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.values.*;

public class DefaultMetadatum implements IMetadatum
{
    private final IScope scope;
    private final IValue value;

    public DefaultMetadatum(final IScope scope,
			    final IValue value)
    {
	this.scope = scope;
	this.value = value;
    }

    public DefaultMetadatum(final IScope scope,
			    final boolean value)
    {
	this.scope = scope;
	this.value = new BooleanValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final int value)
    {
	this.scope = scope;
	this.value = new IntegerValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final long value)
    {
	this.scope = scope;
	this.value = new LongValue(value);
    }


    public DefaultMetadatum(final IScope scope,
			    final float value)
    {
	this.scope = scope;
	this.value = new FloatValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final double value)
    {
	this.scope = scope;
	this.value = new DoubleValue(value);
    }
    public DefaultMetadatum(final IScope scope,
			    final String value)
    {
	this.scope = scope;
	this.value = new StringValue(value);
    }

    public IScope getScope()
    {
	return scope;
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
