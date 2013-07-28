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

import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;
import eu.dime.context.model.impl.values.*;

public class DefaultMetadatum implements IMetadatum
{
    private final IScope scope;
    private final IValue value;

    public DefaultMetadatum(final IScope scope,
			    final IValue value)
    {
	this.scope = scope;
	this.value = value;
    }

    public DefaultMetadatum(final IScope scope,
			    final boolean value)
    {
	this.scope = scope;
	this.value = new BooleanValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final int value)
    {
	this.scope = scope;
	this.value = new IntegerValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final long value)
    {
	this.scope = scope;
	this.value = new LongValue(value);
    }


    public DefaultMetadatum(final IScope scope,
			    final float value)
    {
	this.scope = scope;
	this.value = new FloatValue(value);
    }

    public DefaultMetadatum(final IScope scope,
			    final double value)
    {
	this.scope = scope;
	this.value = new DoubleValue(value);
    }
    public DefaultMetadatum(final IScope scope,
			    final String value)
    {
	this.scope = scope;
	this.value = new StringValue(value);
    }

    public IScope getScope()
    {
	return scope;
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
