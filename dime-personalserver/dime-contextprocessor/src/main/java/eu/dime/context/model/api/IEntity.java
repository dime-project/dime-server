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
