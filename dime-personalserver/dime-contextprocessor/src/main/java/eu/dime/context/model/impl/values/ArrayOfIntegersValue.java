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

import java.util.StringTokenizer;

public class ArrayOfIntegersValue implements IValue
{
    private final int [] arrayOfIntegerValues;

    public ArrayOfIntegersValue(final int [] arrayOfIntegerValues)
    {
	if(arrayOfIntegerValues == null)
	{
	    throw new IllegalArgumentException("Illegal null input array");
	}

	this.arrayOfIntegerValues = new int[arrayOfIntegerValues.length];
	System.arraycopy(arrayOfIntegerValues, 0, this.arrayOfIntegerValues, 0, arrayOfIntegerValues.length);
    }

    public ValueType getValueType()
    {
	return ValueType.ARRAY_OF_INTEGERS_VALUE_TYPE;
    }

    public Object getValue()
    {
	return getValueAsArrayOfIntegers();
    }

    public int [] getValueAsArrayOfIntegers()
    {
	final int [] copy = new int[arrayOfIntegerValues.length];

	System.arraycopy(arrayOfIntegerValues, 0, copy, 0, arrayOfIntegerValues.length);

	return copy;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer("[");

	for(int i = 0; i < arrayOfIntegerValues.length; i++)
	{
	    stringBuffer.append(arrayOfIntegerValues[i]);
	    stringBuffer.append(i == arrayOfIntegerValues.length - 1 ? "]" : ", ");
	}

	return stringBuffer.toString();
    }

    public static ArrayOfIntegersValue parse(final String valueS)
    {
	if(valueS == null)
	    throw new NullPointerException("Illegal null argument");

	if(!(valueS.startsWith("[") && valueS.endsWith("]")))
	    throw new IllegalArgumentException("Invalid input: " + valueS);

	final String content = valueS.substring(1, valueS.length() - 1);
	final StringTokenizer stringTokenizer = new StringTokenizer(content, ",");
	final int [] array = new int[stringTokenizer.countTokens()];
	int count = 0;
	while(stringTokenizer.hasMoreTokens())
	{
	    array[count++] = Integer.parseInt(stringTokenizer.nextToken().trim());
	}

	return new ArrayOfIntegersValue(array);
    }
}
