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

package eu.dime.context.model.impl;

import eu.dime.context.model.api.*;
import eu.dime.context.model.impl.values.*;

public class DefaultContextValue extends AbstractContextValue
{
    final IValue value;

    public DefaultContextValue(final IScope scope,
			       final boolean value)
    {
	super(scope);

	this.value = new BooleanValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final double value)
    {
	super(scope);

	this.value = new DoubleValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final float value)
    {
	super(scope);

	this.value = new FloatValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final int value)
    {
	super(scope);

	this.value = new IntegerValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final long value)
    {
	super(scope);

	this.value = new LongValue(value);
    }

    public DefaultContextValue(final IScope scope,
			       final String value)
    {
	super(scope);

	this.value = new StringValue(value);
    }

    public IValue getValue()
    {
	return value;
    }

    public String toString()
    {
	return value.toString();
    }
}
