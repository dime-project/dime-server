package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class StringValue implements IValue
{
    private final String string;

    public StringValue(final String string)
    {
	this.string = string;
    }

    public ValueType getValueType()
    {
	return ValueType.STRING_VALUE_TYPE;
    }

    public Object getValue()
    {
	return string;
    }

    public String getStringValue()
    {
	return string;
    }

    public String toString()
    {
	return string;
    }
}
