/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ValueType implements Serializable
{
    public static final ValueType BOOLEAN_VALUE_TYPE = new ValueType("boolean");
    public static final ValueType INTEGER_VALUE_TYPE = new ValueType("integer");
    public static final ValueType LONG_VALUE_TYPE = new ValueType("long");
    public static final ValueType FLOAT_VALUE_TYPE = new ValueType("float");
    public static final ValueType DOUBLE_VALUE_TYPE = new ValueType("double");
    public static final ValueType STRING_VALUE_TYPE = new ValueType("string");

    public static final ValueType ARRAY_OF_BOOLEANS_VALUE_TYPE = new ValueType("array_of_booleans");
    public static final ValueType ARRAY_OF_INTEGERS_VALUE_TYPE = new ValueType("array_of_integers");
    public static final ValueType ARRAY_OF_LONGS_VALUE_TYPE = new ValueType("array_of_longs");
    public static final ValueType ARRAY_OF_FLOATS_VALUE_TYPE = new ValueType("array_of_floats");
    public static final ValueType ARRAY_OF_DOUBLES_VALUE_TYPE = new ValueType("array_of_doubles");
    public static final ValueType ARRAY_OF_STRINGS_VALUE_TYPE = new ValueType("array_of_strings");

    private final String name;

    private ValueType(final String name)
    {
	this.name = name;
    }

    public String toString()
    {
	return name;
    }

    private static Map valueTypes = new HashMap();

    static
    {
	valueTypes.put(BOOLEAN_VALUE_TYPE.toString(), BOOLEAN_VALUE_TYPE);
	valueTypes.put(INTEGER_VALUE_TYPE.toString(), INTEGER_VALUE_TYPE);
	valueTypes.put(LONG_VALUE_TYPE.toString(), LONG_VALUE_TYPE);
	valueTypes.put(FLOAT_VALUE_TYPE.toString(), FLOAT_VALUE_TYPE);
	valueTypes.put(DOUBLE_VALUE_TYPE.toString(), DOUBLE_VALUE_TYPE);
	valueTypes.put(STRING_VALUE_TYPE.toString(), STRING_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_BOOLEANS_VALUE_TYPE.toString(), ARRAY_OF_BOOLEANS_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_INTEGERS_VALUE_TYPE.toString(), ARRAY_OF_INTEGERS_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_LONGS_VALUE_TYPE.toString(), ARRAY_OF_LONGS_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_FLOATS_VALUE_TYPE.toString(), ARRAY_OF_FLOATS_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_DOUBLES_VALUE_TYPE.toString(), ARRAY_OF_DOUBLES_VALUE_TYPE);
	valueTypes.put(ARRAY_OF_STRINGS_VALUE_TYPE.toString(), ARRAY_OF_STRINGS_VALUE_TYPE);
    }

    static public ValueType parse(final String valueTypeS)
    {
	return (ValueType) valueTypes.get(valueTypeS);
    }
}
