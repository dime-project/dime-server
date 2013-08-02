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
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.dlpo.LivePost;

/**
 * Manager for live post management.
 * 
 * @author Ismael Rivera
 */
public interface LivePostManager extends InfoSphereManager<LivePost> {

	Collection<LivePost> getAllByCreator(String creatorId)
			throws InfosphereException;

	Collection<LivePost> getAllByCreator(String creatorId, List<URI> properties)
			throws InfosphereException;
	
	<T extends LivePost> Collection<T> getAllByType(Class<T> returnType)
			throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByType(Class<T> returnType,
			List<URI> properties) throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId) throws InfosphereException;

	<T extends LivePost> Collection<T> getAllByTypeAndByCreator(Class<T> returnType,
			String creatorId, List<URI> properties) throws InfosphereException;

	Collection<LivePost> getAllByPerson(URI personId) throws InfosphereException;

	Collection<LivePost> getAllByPerson(URI personId, List<URI> properties)
			throws InfosphereException;

}