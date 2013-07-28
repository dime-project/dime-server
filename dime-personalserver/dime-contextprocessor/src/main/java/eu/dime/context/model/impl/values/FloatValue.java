package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

public class FloatValue implements IValue
{
    private final Float floatValue;

    public FloatValue(final float floatValue)
    {
	this(new Float(floatValue));
    }

    public FloatValue(final Float floatValue)
    {
	this.floatValue = floatValue;
    }

    public ValueType getValueType()
    {
	return ValueType.FLOAT_VALUE_TYPE;
    }

    public Object getValue()
    {
	return floatValue;
    }

    public Float getFloatValue()
    {
	return floatValue;
    }

    public String toString()
    {
	return floatValue.toString();
    }
}
