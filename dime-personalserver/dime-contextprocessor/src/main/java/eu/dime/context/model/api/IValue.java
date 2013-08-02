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
