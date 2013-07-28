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

package eu.dime.ps.controllers.infosphere.manager;

import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.semantic.model.nfo.DataContainer;
import eu.dime.ps.semantic.model.nie.DataObject;

/**
 * Manager for databox management.
 * 
 * @author Ismael Rivera
 */
public interface DataboxManager extends InfoSphereManager<DataContainer> {
	
	public Collection<DataContainer> getAllByCreator(URI creatorId)
			throws InfosphereException;

	public Collection<DataContainer> getAllByCreator(URI creatorId, List<URI> properties)
			throws InfosphereException;

	public Collection<DataObject> getDataboxItems(String databoxId)
			throws InfosphereException;
	
	public Collection<DataContainer> getAllByPerson(URI personId) throws InfosphereException;

	public Collection<DataContainer> getAllByPerson(URI personId, List<URI> properties)
			throws InfosphereException;

}