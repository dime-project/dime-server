/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.api;

import java.util.Set;
import java.io.Serializable;

/**
 * Provides the specification for a set of context values. Typically, an
 * instance of this data-structure is associated to an instance of a
 * {@link IContextElement}.
 *
 * Implementations of the {@link IContextData} <b>must be immutable</b>.
 *
 * @see eu.dime.context.model.api.IContextElement
 *
 * @see IContextValue
 */
public interface IContextData extends Serializable
{
    /**
     * Retrieves the {@link eu.dime.context.model.impl.ContextValue}
     * which is associated to the specified {@link IScope}.
     * It returns null if no value is associated to the specified
     * {@link IScope}.
     *
     * @param scopeKey the scope name of the requested
     * {@link eu.dime.context.model.impl.ContextValue}
     * @return the {@link eu.dime.context.model.impl.ContextValue}
     * identified by the specified contextValueKey, or null if no
     * {@link eu.dime.context.model.impl.ContextValue} is associated to
     * the specified {@link IScope}
     * @throws NullPointerException when a null argument is passed
     */
    public IContextValue getContextValue(final IScope scopeKey);

    /**
     * Retrieves the {@link IValue} associated to the
     * {@link eu.dime.context.model.impl.ContextValue} which is
     * associated to the specified {@link IScope}.
     * In practice, this method call is equivalent to the invocation of
     * {@link #getContextValue(IScope)} and then
     * {@link eu.dime.context.model.impl.ContextValue#getValue()}
     * assuming that the first invocation does not return null.
     * It returns null if no value is associated to the specified
     * {@link IScope}.
     *
     * @param scopeKey the scope name of the requested {@link IValue}
     * @return the {@link IValue} of the context value identified by the
     * specified contextValueKey, or null if no
     * {@link eu.dime.context.model.impl.ContextValue} is associated to
     * the specified {@link IScope}
     * @throws NullPointerException when a null argument is passed
     */
    public IValue getValue(final IScope scopeKey);

    /**
     * Returns the set of the stored keys. This set contains objects of type
     * {@link IScope}.
     *
     * @return an instance of {@link Set} containing the {@link IScope}s which
     * name the stored data
     */
    public Set keySet();
}
