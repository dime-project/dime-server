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
 * This interface serves as a logical "parent" for the {@link IEntity}, the
 * {@link IScope} concepts.
 *
 */
public interface IConcept
{
    /**
     * Returns the ontologyURL of this concept. For example, for the concept
     * represented by "http://www.myontology.com/ontology.xml#Thing...",
     * the returned URL will be "http://www.myontology.com/ontology.xml".
     *
     * @return a String representation of the corresponding ontology URL
     *
     */
    public String getOntologyURL();
}
