/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl;

import java.lang.StringBuffer;

import eu.dime.context.model.api.*;

/**
 * Immutable implementation of the {@link IContextElement} data structure.
 */
public class ContextElement extends AbstractContextElement
{
    public static final ContextElement [] EMPTY_CONTEXT_ELEMENT_ARRAY
	    = new ContextElement [] {};

    private final ContextValueMap contextValueMap;

    private final String contextOntologyURL;

    /**
     * Empty constructor needed for serialization purposes.
     */
    public ContextElement()
    {
	super();

	this.contextValueMap = null;
	this.contextOntologyURL = null;
    }

    /**
     * Package-private constructor prohibits instantiation outside the package.
     * Constructs an instance of {@link ContextElement} with the specified
     * values.
     *
     * @param entity the element's entity
     * @param scope the element's scope
     * @param source the element's source (i.e. component which generated it)
     * @param contextValueMap the element's value map (i.e. a map of its value
     * IDs pointing to the {@link IContextData}s themselves
     * @param metadata the element's metadata mapping (i.e. a map of its
     * metadata IDs pointing to the {@link IMetadata} themselves
     */
    ContextElement(
	    final IEntity entity,
	    final IScope scope,
	    final String source,
	    final ContextValueMap contextValueMap,
	    final IMetadata metadata)
    {
	super(entity, scope, source, metadata);

	// assert that the entity and the scope all refer
	// to the same ontology
	if(!(entity.getOntologyURL().equals(scope.getOntologyURL())))
	{
	    throw new IllegalArgumentException("The specified entity and scope" +
		    " do not refer to the same" +
		    " ontology ID!");
	}

	this.contextValueMap = contextValueMap;

	this.contextOntologyURL = scope.getOntologyURL();
    }

    // From IContextElement

    public IContextData getContextData()
    {
	return this.contextValueMap;
    }

    public String getContextOntologyURL()
    {
	return contextOntologyURL;
    }

    public String toString()
    {
	final StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append("ContextElement{entity=").append(getEntity())
		.append(", scope=").append(getScope()).append("}");

	return stringBuffer.toString();
    }

    public String toExtendedString()
    {
	final StringBuffer stringBuffer = new StringBuffer();
	stringBuffer.append("ContextElement{entity=").append(getEntity()).append("\n")
		.append(", scope=").append(getScope()).append("\n")
		.append(", size-of-contextValueMap=").append(contextValueMap.size()).append("\n")
		.append(", contextValueMap=").append(contextValueMap).append("\n")
		.append(", metadata=").append(getMetadata())
		.append("}");

	return stringBuffer.toString();
    }

    public boolean isSameType(final IContextElement contextElement)
    {
	return contextElement != null
		&& getEntity().equals(contextElement.getEntity())
		&& getScope().equals(contextElement.getScope());
    }

    static public boolean sameEntityScope(IContextElement contextElement1, IContextElement contextElement2)
    {
	return !((contextElement1 == null) || (contextElement2 == null))
		&& contextElement1.getEntity().equals(contextElement2.getEntity())
		&& contextElement1.getScope().equals(contextElement2.getScope());
    }
}
