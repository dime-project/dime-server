package eu.dime.ps.semantic.service;

import java.util.List;

import org.ontoware.rdf2go.model.node.Resource;

public interface PersonMatchingService {

	/**
	 * Finds a set of person instances which match, based on some
	 * qualities or features, to a given person.
	 * @param person the person used to find matches
	 * @return a PersonMatch which contains a set of people
	 *         matching the given one, and the confidence or degree
	 *         of similarity for each
	 */
	List<PersonMatch> match(Resource person);

	List<PersonMatch> match(Resource person, double threshold);

	/**
	 * Same as {@link #match(Resource)}, but performs the matching
	 * for a set of people.
	 * @param people
	 * @return
	 */
	List<List<PersonMatch>> match(List<Resource> people);
	
	List<List<PersonMatch>> match(List<Resource> people, double threshold);
	
}
