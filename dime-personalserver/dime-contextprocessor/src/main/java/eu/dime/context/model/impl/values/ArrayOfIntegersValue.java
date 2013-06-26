/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
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
