/*
 * Copyright (c) 2011 Telecom Italia S.p.A.
 */

package eu.dime.context.model.api;

/**
 * Interface for the <i>entity</i> notion.
 *
 */
public interface IEntity extends IConcept
{
    public static String ENTITY_ID_SEPARATOR = "|";

    /**
     * Returns a string representation of this concept.
     *
     * @return returns a string representation of this concept
     */
    public String getEntityAsString();

    /**
     * @return a string corresponding to the ID represented by this entity
     */
    public String getEntityIDAsString();

    /**
     *
     * @return a string corresponding to the type of the this entity
     */
    public String getEntityTypeAsString();

    /**
     *
     * @return a string corresponding to the type of the this entity
     */
    public String getEntityTypeAsShortString();
}
