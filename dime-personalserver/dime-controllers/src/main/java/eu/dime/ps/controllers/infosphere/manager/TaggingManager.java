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

package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;

import org.ontoware.rdf2go.model.node.Resource;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nao.Tag;

/**
 * Manager for tagging resources.
 * 
 * @author Ismael Rivera
 */
public interface TaggingManager {

	/**
	 * Retrieves all tags associated with a resource.
	 * @param resource
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> getAllByResource(Resource resource)
			throws InfosphereException;

	/**
	 * Retrieves all tags which label (nao:prefLabel) is like (partially equals) to a given one.
	 * @param label
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> getAllByLabelLike(String label)
			throws InfosphereException;

	/**
	 * Creates nao:Tag instances with the given labels, and attach the tags to the resource.
	 * @param resource
	 * @param labels
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Tag> add(Resource resource, String... labels) throws InfosphereException;
	
}