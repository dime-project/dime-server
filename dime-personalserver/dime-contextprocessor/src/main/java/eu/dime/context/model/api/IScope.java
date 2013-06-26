/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.api;

/**
 * Interface for the <i>scope</i> notion.
 *
 */
public interface IScope extends IConcept
{
    /**
     * Returns a string representation of this concept.
     *
     * @return returns a string representation of this concept
     */
    public String getScopeAsString();

    /**
     * Returns the scoping path without the ontology. For example, for the
     * scope represented by
     * "http://www.myontology.com/ontology.xml#Thing.Concept.Scope.Resource.Memory",
     * the returned URL will be "#Thing.Concept.Scope.Resource.Memory".
     *
     * @return a String representation of the scope without the ontology
     */
    public String getScopeAsShortString();
}
