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

import java.util.StringTokenizer;

public class ArrayOfStringsValue implements IValue
{
    private final String [] arrayOfStrings;

    public ArrayOfStringsValue(final String [] arrayOfStrings)
    {
	if(arrayOfStrings == null)
	{
	    throw new IllegalArgumentException("Illegal null input array");
	}

	this.arrayOfStrings = new String[arrayOfStrings.length];
	System.arraycopy(arrayOfStrings, 0, this.arrayOfStrings, 0, arrayOfStrings.length);
    }

    public ValueType getValueType()
    {
	return ValueType.ARRAY_OF_STRINGS_VALUE_TYPE;
    }

    public Object getValue()
    {
	return getValueAsArrayOfStrings();
    }

    public String [] getValueAsArrayOfStrings()
    {
	final String [] copy = new String[arrayOfStrings.length];

	System.arraycopy(arrayOfStrings, 0, copy, 0, arrayOfStrings.length);

	return copy;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer("[");

	for(int i = 0; i < arrayOfStrings.length; i++)
	{
	    stringBuffer.append(arrayOfStrings[i]);
	    stringBuffer.append(i == arrayOfStrings.length - 1 ? "]" : ", ");
	}

	return stringBuffer.toString();
    }

    public static ArrayOfStringsValue parse(final String valueS)
    {
	if(valueS == null)
	    throw new NullPointerException("Illegal null argument");

	if(!(valueS.startsWith("[") && valueS.endsWith("]")))
	    throw new IllegalArgumentException("Invalid input: " + valueS);

	final String content = valueS.substring(1, valueS.length() - 1);
	final StringTokenizer stringTokenizer = new StringTokenizer(content, ",");
	final String [] array = new String[stringTokenizer.countTokens()];
	int count = 0;
	while(stringTokenizer.hasMoreTokens())
	{
	    array[count++] = stringTokenizer.nextToken().trim();
	}

	return new ArrayOfStringsValue(array);
    }
}
