package eu.dime.context.model.impl.values;

import eu.dime.context.model.api.IValue;
import eu.dime.context.model.api.ValueType;

import java.util.StringTokenizer;

public class ArrayOfDoublesValue implements IValue
{
    private final double [] arrayOfDoubles;

    public ArrayOfDoublesValue(final double [] arrayOfDoubles)
    {
	if(arrayOfDoubles == null)
	{
	    throw new IllegalArgumentException("Illegal null input array");
	}

	this.arrayOfDoubles = new double[arrayOfDoubles.length];
	System.arraycopy(arrayOfDoubles, 0, this.arrayOfDoubles, 0, arrayOfDoubles.length);
    }

    public ValueType getValueType()
    {
	return ValueType.ARRAY_OF_DOUBLES_VALUE_TYPE;
    }

    public Object getValue()
    {
	return getValueAsArrayOfDoubles();
    }

    public double [] getValueAsArrayOfDoubles()
    {
	final double [] copy = new double[arrayOfDoubles.length];

	System.arraycopy(arrayOfDoubles, 0, copy, 0, arrayOfDoubles.length);

	return copy;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer("[");

	for(int i = 0; i < arrayOfDoubles.length; i++)
	{
	    stringBuffer.append(arrayOfDoubles[i]);
	    stringBuffer.append(i == arrayOfDoubles.length - 1 ? "]" : ", ");
	}

	return stringBuffer.toString();
    }

    public static ArrayOfDoublesValue parse(final String valueS)
    {
	if(valueS == null)
	    throw new NullPointerException("Illegal null argument");

	if(!(valueS.startsWith("[") && valueS.endsWith("]")))
	    throw new IllegalArgumentException("Invalid input: " + valueS);

	final String content = valueS.substring(1, valueS.length() - 1);
	final StringTokenizer stringTokenizer = new StringTokenizer(content, ",");
	final double [] array = new double[stringTokenizer.countTokens()];
	int count = 0;
	while(stringTokenizer.hasMoreTokens())
	{
	    array[count++] = Double.parseDouble(stringTokenizer.nextToken().trim());
	}

	return new ArrayOfDoublesValue(array);
    }
}
