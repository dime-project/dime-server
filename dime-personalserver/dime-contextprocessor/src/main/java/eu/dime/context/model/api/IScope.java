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
