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
