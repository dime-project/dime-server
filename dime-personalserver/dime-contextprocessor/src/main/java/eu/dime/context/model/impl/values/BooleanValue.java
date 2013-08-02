/*
* Copyright 2013 by the digital.me project (http://www.dime-project.eu).
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
