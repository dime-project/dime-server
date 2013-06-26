/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class DoubleValue implements IValue
{
    private final java.lang.Double doubleValue;

    public DoubleValue(final double doubleValue)
    {
	this(new java.lang.Double(doubleValue));
    }

    public DoubleValue(final java.lang.Double doubleValue)
    {
	this.doubleValue = doubleValue;
    }

    public ValueType getValueType()
    {
	return ValueType.DOUBLE_VALUE_TYPE;
    }

    public Object getValue()
    {
	return doubleValue;
    }

    public Double getDoubleValue()
    {
	return doubleValue;
    }

    public String toString()
    {
	return doubleValue.toString();
    }
}
