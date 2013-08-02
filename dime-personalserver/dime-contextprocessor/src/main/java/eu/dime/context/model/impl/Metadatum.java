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

package eu.dime.context.model.impl;

import eu.dime.context.model.api.IMetadatum;
import eu.dime.context.model.api.IScope;
import eu.dime.context.model.api.IValue;

import java.io.Serializable;

/**
 * Immutable implementation of the Metadatum data-structure.
 */
class Metadatum implements IMetadatum, Serializable
{
    private final IScope scope;
    private final IValue value;

    /**
     * Package-private constructor prohibits instantiation outside the package.
     * Constructs an instance of {@link ContextValue} with the specified
     * values.
     *
     * @param scope the metadata element's scope
     * @param value the {@link IValue} of this metadata element
     */
    Metadatum(
	    final IScope scope,
	    final IValue value)
    {
	this.scope = scope;
	this.value = value;
    }

    public IScope getScope()
    {
	return scope;
    }

    public IValue getValue()
    {
	return value;
    }
}
