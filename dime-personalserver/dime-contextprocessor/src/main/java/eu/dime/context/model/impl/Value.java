/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.context.model.impl;

import java.io.Serializable;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

class Value implements IValue // IValue extends Serializable
{
    private final Serializable value;
    private final ValueType valueType;

    Value(final boolean booleanValue)
    {
	this(Boolean.valueOf(booleanValue));
    }

    Value(final Boolean booleanValue)
    {
	this.value = booleanValue;
	this.valueType = ValueType.BOOLEAN_VALUE_TYPE;
    }

    Value(final int integerValue)
    {
	this(new Integer(integerValue));
    }

    Value(final Integer integerValue)
    {
	this.value = integerValue;
	this.valueType = ValueType.INTEGER_VALUE_TYPE;
    }

    Value(final long longValue)
    {
	this(new Long(longValue));
    }

    Value(final Long longValue)
    {
	this.value = longValue;
	this.valueType = ValueType.LONG_VALUE_TYPE;
    }

    Value(final float floatValue)
    {
	this(new Float(floatValue));
    }

    Value(final Float floatValue)
    {
	this.value = floatValue;
	this.valueType = ValueType.FLOAT_VALUE_TYPE;
    }

    Value(final double doubleValue)
    {
	this(new Double(doubleValue));
    }

    Value(final Double doubleValue)
    {
	this.value = doubleValue;
	this.valueType = ValueType.DOUBLE_VALUE_TYPE;
    }

    Value(final String stringValue)
    {
	this.value = stringValue;
	this.valueType = ValueType.STRING_VALUE_TYPE;
    }

    public ValueType getValueType()
    {
	return valueType;
    }

    public Object getValue()
    {
	return value;
    }

    public String toString()
    {
	return "IValue{type=" + valueType + ", value=" + value + "}";
    }
}
