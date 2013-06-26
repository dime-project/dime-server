/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.api;

import java.io.Serializable;

/**
 * Abstracts a low-level context value. The possible value types are:
 * <ul>
 *   <li>{@link ValueType#BOOLEAN_VALUE_TYPE}</li>
 *   <li>{@link ValueType#INTEGER_VALUE_TYPE}</li>
 *   <li>{@link ValueType#LONG_VALUE_TYPE}</li>
 *   <li>{@link ValueType#DOUBLE_VALUE_TYPE}</li>
 *   <li>{@link ValueType#STRING_VALUE_TYPE}</li>
 * </ul>
 *
 * @see ValueType
 */
public interface IValue extends Serializable
{
    public ValueType getValueType();

    public Object getValue();
}
