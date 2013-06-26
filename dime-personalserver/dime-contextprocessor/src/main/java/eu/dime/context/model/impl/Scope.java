/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.impl;

import eu.dime.context.model.api.IScope;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * Immutable implementation of the <i>scope</i> notion.
 */
class Scope implements IScope, Serializable
{
    /**
     * This map implements the cache. It is marked as transient to make sure
     * that it is not communicated when serialized or deserialized.
     */
    static transient private final Map scopes = new HashMap();

    /**
     * Package-only access to scopes is done through this factory method. This
     * enables caching of the scope objects, which is possible because they are
     * designed to be <i>immutable</i>.
     *
     * A sample scope is "http://someurl.org/somefile.xml#scopeID".
     *
     * @param scope a string representation of the desired scope, including its
     * ontology URL and scope ID
     * @return an instance of {@link Scope}
     * corresponding to the specified string
     */
    static Scope createScope(final String scope)
    {
	if(scope == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	// assert scope starts with "http://" and contains one '#' symbol
	// REMOVED TO ALLOW SCOPES WITHOUT REFERENCE TO ONTOLOGIES
/*	if(!scope.startsWith("http://"))
	{
	    throw new IllegalArgumentException("The specified scope does not "
		    + "start with 'http://'");
	}
	if(scope.indexOf('#') == -1)
	{
	    throw new IllegalArgumentException("The specified scope does not "
		    + "contain a '#' symbol");
	}
*/
	synchronized (scopes)
	{
	    // if the specified scope is already cached, then return it...
	    if(scopes.containsKey(scope))
	    {
		return (Scope) scopes.get(scope);
	    }

	    // ...else create a new one, cache it, and then return it
	    final Scope newScope = new Scope(scope);
	    scopes.put(scope, newScope);
	    return newScope;
	}
    }

    private final String scope;

    private Scope(final String scope)
    {
	this.scope = scope;
    }

    public String getScopeAsString()
    {
	return scope;
    }

    /**
     * Returns the ontologyURL of this scope. For example, for the concept
     * represented by "http://www.myontology.com/ontology.xml#Thing...",
     * the returned URL will be "http://www.myontology.com/ontology.xml".
     *
     * @return a String representation of the corresponding ontology URL
     *
     */
    public String getOntologyURL()
    {
	final int sharpPosition = scope.indexOf('#');

	// ADDED TO SUPPORT LACK OF ONTOLOGY
	if (sharpPosition==-1)
		return "";
	
	return scope.substring(0, sharpPosition);
    }

    /**
     * Returns the scoping path without the ontology. For example, for the
     * scope represented by
     * "http://www.myontology.com/ontology.xml#Thing.Concept.Scope.Resource.Memory",
     * the returned URL will be "#Thing.Concept.Scope.Resource.Memory".
     *
     * @return a String representation of the scope without the ontology
     */
    public String getScopeAsShortString()
    {
	final int sharpPosition = scope.indexOf('#');

	return scope.substring(sharpPosition, scope.length());
    }

    public String toString()
    {
	return getScopeAsString();
    }

    public int hashCode()
    {
	return scope.hashCode();
    }

    public boolean equals(final Object object)
    {
	if(object == null) return false;

	if(this == object) return true;

	if(this.getClass() != object.getClass()) return false;

	final Scope otherScope = (Scope) object;

	return this.scope.equals(otherScope.scope);
    }
}
