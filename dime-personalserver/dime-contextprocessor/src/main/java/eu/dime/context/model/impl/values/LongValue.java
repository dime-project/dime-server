/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class LongValue implements IValue
{
    private final Long longValue;

    public LongValue(final long longValue)
    {
	this(new Long(longValue));
    }

    public LongValue(final Long longValue)
    {
	this.longValue = longValue;
    }

    public ValueType getValueType()
    {
	return ValueType.LONG_VALUE_TYPE;
    }

    public Object getValue()
    {
	return longValue;
    }

    public Long getLongValue()
    {
	return longValue;
    }

    public String toString()
    {
	return longValue.toString();
    }
}
