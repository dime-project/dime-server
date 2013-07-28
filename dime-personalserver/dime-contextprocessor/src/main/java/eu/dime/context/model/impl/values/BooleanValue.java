package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class BooleanValue implements IValue
{
    private final Boolean booleanValue;

    public BooleanValue(final boolean booleanValue)
    {
	this((booleanValue) ? Boolean.TRUE : Boolean.FALSE);
    }

    public BooleanValue(final Boolean booleanValue)
    {
	this.booleanValue = booleanValue;
    }

    public ValueType getValueType()
    {
	return ValueType.BOOLEAN_VALUE_TYPE;
    }

    public Object getValue()
    {
	return booleanValue;
    }

    public Boolean getBooleanValue()
    {
	return booleanValue;
    }

    public String toString()
    {
	return booleanValue.toString();
    }
}
