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

import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.storage.entities.PersonMatch;

public interface PersonMatchManager {
	
	List<PersonMatch> getAll() throws InfosphereException;
	List<PersonMatch> getAllByThreshold(double threshold) throws InfosphereException;
	List<PersonMatch> getAllByStatus(String status) throws InfosphereException;
	List<PersonMatch> getAllByPerson(URI person) throws InfosphereException;	
	List<PersonMatch> getAllByPersonAndByStatus(URI person, String status) throws InfosphereException;	

}
