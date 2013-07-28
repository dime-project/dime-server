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
