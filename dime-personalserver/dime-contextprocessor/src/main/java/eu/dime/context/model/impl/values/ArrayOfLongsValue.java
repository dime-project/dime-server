/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

import java.util.StringTokenizer;

public class ArrayOfLongsValue implements IValue
{
    private final long [] arrayOfLongs;

    public ArrayOfLongsValue(final long [] arrayOfLongs)
    {
	if(arrayOfLongs == null)
	{
	    throw new IllegalArgumentException("Illegal null input array");
	}

	this.arrayOfLongs = new long[arrayOfLongs.length];
	System.arraycopy(arrayOfLongs, 0, this.arrayOfLongs, 0, arrayOfLongs.length);
    }

    public ValueType getValueType()
    {
	return ValueType.ARRAY_OF_LONGS_VALUE_TYPE;
    }

    public Object getValue()
    {
	return getValueAsArrayOfLongs();
    }

    public long [] getValueAsArrayOfLongs()
    {
	final long [] copy = new long[arrayOfLongs.length];

	System.arraycopy(arrayOfLongs, 0, copy, 0, arrayOfLongs.length);

	return copy;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer("[");

	for(int i = 0; i < arrayOfLongs.length; i++)
	{
	    stringBuffer.append(arrayOfLongs[i]);
	    stringBuffer.append(i == arrayOfLongs.length - 1 ? "]" : ", ");
	}

	return stringBuffer.toString();
    }

    public static ArrayOfLongsValue parse(final String valueS)
    {
	if(valueS == null)
	    throw new NullPointerException("Illegal null argument");

	if(!(valueS.startsWith("[") && valueS.endsWith("]")))
	    throw new IllegalArgumentException("Invalid input: " + valueS);

	final String content = valueS.substring(1, valueS.length() - 1);
	final StringTokenizer stringTokenizer = new StringTokenizer(content, ",");
	final long [] array = new long[stringTokenizer.countTokens()];
	int count = 0;
	while(stringTokenizer.hasMoreTokens())
	{
	    array[count++] = Long.parseLong(stringTokenizer.nextToken().trim());
	}

	return new ArrayOfLongsValue(array);
    }
}
