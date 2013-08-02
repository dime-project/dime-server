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

import eu.dime.context.model.Constants;
import eu.dime.context.model.api.IEntity;

import java.io.Serializable;

/**
 * Immutable implementation of the <i>entity</i> notion.
 */
public class Entity implements IEntity, Serializable
{

    /**
     * Package-only access to entities is achieved through this factory method.
     *
     * @param entity a string representation of the desired entity
     * @return an instance of {@link Entity}
     * corresponding to the specified string
     */
    public static IEntity createEntity(final String entity)
    {
	if(entity == null)
	{
	    throw new NullPointerException("Illegal null argument");
	}

	// assert entity starts with "http://" and contains one '#' symbol
	// REMOVED TO ALLOW ENTITIES WITHOUT REFERENCE TO ONTOLOGIES
/*	if(!entity.startsWith("http://"))
	{
	    throw new IllegalArgumentException("The specified entity does not "
		    + "start with 'http://'");
	}
	if(entity.indexOf('#') == -1)
	{
	    throw new IllegalArgumentException("The specified entity does not "
		    + "contain a '#' symbol");
	}
*/
    return new Entity(entity);
    }

    private final String entity;

    private Entity(final String entity)
    {
    	if (!entity.contains("|")) this.entity = Constants.ENTITY_USER + ENTITY_ID_SEPARATOR + entity;
    	else this.entity = entity;
    }

    /**
     * Returns a string representation of this {@link Entity}.
     *
     * @return a string representation of this {@link Entity}.
     */
    public String getEntityAsString()
    {
	return entity;
    }

    /**
     * Returns the entity ID (e.g. if the entity is
     * http://myontology.com/persons/#user|hisName, the entity ID is "hisName")
     *
     * @return a string corresponding to the ID represented by this entity
     */
    public String getEntityIDAsString()
    {
	// the entity can never be null
	// assert entity != null;

	int i = entity.indexOf(ENTITY_ID_SEPARATOR);
	if (i == -1)
	{
	    return entity;
	}
	else
	{
	    return entity.substring(i + 1);
	}
    }

    /**
     * Returns the entity type (e.g. if the entity is
     * http://myontology.com/persons/#user|hisName, the entity type
     * is "http://myontology.com/persons/#user")
     *
     * @return a string corresponding to the type of the this entity
     */
    public String getEntityTypeAsString()
    {
	int i = entity.indexOf(ENTITY_ID_SEPARATOR);
	if (i == -1)
	{
	    return "";
	}
	else
	{
	    return entity.substring(0, i);
	}
    }

    /**
     * Returns the entity type as a short string. For example, if the entity
     * is http://myontology.com/persons/#user|hisName, the entity type's short
     * string is "#user|hisName".
     *
     * @return a string corresponding to the type of the this entity
     */
    public String getEntityTypeAsShortString()
    {
	final int sharpPosition = entity.indexOf('#');

	return entity.substring(sharpPosition, entity.length());
    }

    /**
     * Returns the ontologyURL of this entity. For example, for the concept
     * represented by "http://www.myontology.com/ontology.xml#Thing...",
     * the returned URL will be "http://www.myontology.com/ontology.xml".
     *
     * @return a String representation of the corresponding ontology URL
     *
     */
    public String getOntologyURL()
    {
	final int sharpPosition = entity.indexOf('#');
	
	// ADDED TO SUPPORT LACK OF ONTOLOGY
	if (sharpPosition==-1)
		return "";

	return entity.substring(0, sharpPosition);
    }

    public String toShortString()
    {
	final int indexOfHash = entity.indexOf("#");
	return indexOfHash > 0 ? entity.substring(indexOfHash) : entity;
    }

    public String toString()
    {
	return getEntityAsString();
    }

    public int hashCode()
    {
	return entity.hashCode();
    }

    public boolean equals(final Object object)
    {
	if(object == null) return false;

	if(this == object) return true;

	if(this.getClass() != object.getClass()) return false;

	final Entity otherEntity = (Entity) object;

	return this.entity.equals(otherEntity.entity);
    }
}
