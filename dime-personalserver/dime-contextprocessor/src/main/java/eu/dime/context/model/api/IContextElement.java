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
 * Provides the specification for a Context Element. A context element can be
 * associated with an arbitrary set of
 * {@link eu.dime.context.model.impl.ContextValue}s and an arbitrary
 * set of {@link IMetadata}.
 *
 * @see IContextData
 */
public interface IContextElement extends Serializable
{
    public static final IContextElement [] EMPTY_CONTEXT_ELEMENT_ARRAY = new IContextElement [0];

    /**
     * Accesses the entity assigned to this context element.
     *
     * @return the entity assigned to this context element
     */
    public IEntity getEntity();

    /**
     * Accesses the scope assigned to this context element.
     *
     * @return the scope assigned to this context element
     */
    public IScope getScope();

    /**
     * Returns a string representation of the source of this context element.
     * Typically, the source is a
     * {@link eu.dime.context.plugins.IContextPlugin} and the string
     * representation is its ID (accessible via
     * {@link eu.dime.context.plugins.IContextPlugin#getID()}).
     *
     * @return a string representation of the source of this context element
     */
    public String getSource();

    /**
     * Returns the context values associated to this {@link IContextElement}.
     * The returned {@link IContextData} is typically a {@link java.util.Map}
     * implementation. The returned map is guaranteed to never be null.
     *
     * @return the {@link IContextData} object containing all context values
     * related to this IContextElement object
     * @see eu.dime.context.model.impl.ContextValueMap
     */
    public IContextData getContextData();

    /**
     * Returns the metadata associated to this {@link IContextElement}. The
     * returned {@link IMetadata} is typically a {@link java.util.Map}
     * implementation. The returned map is guaranteed to never be null.
     *
     * @return the {@link IMetadata} object containing all metadata
     * related to this IContextElement object
     */
    public IMetadata getMetadata();
    
    /**
     * Returns the timestamp associated to this {@link IContextElement}.
     *
     * @return timestamp in timemillis
     */
    public long getTimestampAsLong();
    
    /**
     * Returns the expires associated to this {@link IContextElement}.
     *
     * @return expires in timemillis
     */
    public long getExpiresAsLong();

    /**
     * Returns the timestamp string associated to this {@link IContextElement}.
     *
     * @return timestamp string
     */
    public String getTimestampAsString();

    /**
     * Returns the expires string associated to this {@link IContextElement}.
     *
     * @return expires string
     */
    public String getExpiresAsString();
    
    /**
     * Check that the context element is valid (not expired, i.e. timestamp<now<expires)
     *
     * @return boolean
     */
    public boolean isValid();
}
