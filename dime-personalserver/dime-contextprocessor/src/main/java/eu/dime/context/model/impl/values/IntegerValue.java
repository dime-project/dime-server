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
