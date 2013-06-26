/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class IntegerValue implements IValue
{
    private final Integer integerValue;

    public IntegerValue(final int integerValue)
    {
	this(new Integer(integerValue));
    }

    public IntegerValue(final Integer integerValue)
    {
	this.integerValue = integerValue;
    }

    public ValueType getValueType()
    {
	return ValueType.INTEGER_VALUE_TYPE;
    }

    public Object getValue()
    {
	return integerValue;
    }

    public Integer getIntegerValue()
    {
	return integerValue;
    }

    public String toString()
    {
	return integerValue.toString();
    }
}
