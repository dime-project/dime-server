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
