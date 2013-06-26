/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

import java.util.StringTokenizer;

public class ArrayOfBooleansValue implements IValue
{
    private final boolean [] arrayOfBooleans;

    public ArrayOfBooleansValue(final boolean [] arrayOfBooleans)
    {
	if(arrayOfBooleans == null)
	{
	    throw new IllegalArgumentException("Illegal null input array");
	}

	this.arrayOfBooleans = new boolean[arrayOfBooleans.length];
	System.arraycopy(arrayOfBooleans, 0, this.arrayOfBooleans, 0, arrayOfBooleans.length);
    }

    public ValueType getValueType()
    {
	return ValueType.ARRAY_OF_BOOLEANS_VALUE_TYPE;
    }

    public Object getValue()
    {
	return getValueAsArrayOfBooleans();
    }

    public boolean [] getValueAsArrayOfBooleans()
    {
	final boolean [] copy = new boolean[arrayOfBooleans.length];

	System.arraycopy(arrayOfBooleans, 0, copy, 0, arrayOfBooleans.length);

	return copy;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer("[");

	for(int i = 0; i < arrayOfBooleans.length; i++)
	{
	    stringBuffer.append(arrayOfBooleans[i]);
	    stringBuffer.append(i == arrayOfBooleans.length - 1 ? "]" : ", ");
	}

	return stringBuffer.toString();
    }

    public static ArrayOfBooleansValue parse(final String valueS)
    {
	if(valueS == null)
	    throw new NullPointerException("Illegal null argument");

	if(!(valueS.startsWith("[") && valueS.endsWith("]")))
	    throw new IllegalArgumentException("Invalid input: " + valueS);

	final String content = valueS.substring(1, valueS.length() - 1);
	final StringTokenizer stringTokenizer = new StringTokenizer(content, ",");
	final boolean [] array = new boolean[stringTokenizer.countTokens()];
	int count = 0;
	while(stringTokenizer.hasMoreTokens())
	{
	    array[count++] = Boolean.valueOf(stringTokenizer.nextToken().trim()).booleanValue();
	}

	return new ArrayOfBooleansValue(array);
    }
}
