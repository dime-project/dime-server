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
import eu.dime.ps.controllers.util.TenantHelper;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.Tenant;

public class PersonMatchManagerImpl implements PersonMatchManager {

	public static final double THRESHOLD = 0.75;
	public static final int DEFAULT_TECHNIQUE = 3;
	
	@Override
	public List<PersonMatch> getAll() throws InfosphereException {
		
		Tenant tenant = TenantHelper.getCurrentTenant();
			
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByThreshold(tenant, DEFAULT_TECHNIQUE, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByThreshold(double threshold) throws InfosphereException {
		
		Tenant tenant = TenantHelper.getCurrentTenant();
		
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByThreshold(tenant, DEFAULT_TECHNIQUE, threshold);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByStatus(String status) throws InfosphereException {
		
		Tenant tenant = TenantHelper.getCurrentTenant();
		
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByStatusAndByThreshold(tenant, DEFAULT_TECHNIQUE, status, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByPerson(URI person) throws InfosphereException {
		Tenant tenant = TenantHelper.getCurrentTenant();
	
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndBySourceAndByThreshold(tenant, DEFAULT_TECHNIQUE, person.asURI().toString(), THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches for person "+person, e);
		}
	}

	@Override
	public List<PersonMatch> getAllByPersonAndByStatus(URI person, String status) throws InfosphereException {
		Tenant tenant = TenantHelper.getCurrentTenant();
	
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndBySourceAndByStatusAndByThreshold(tenant, DEFAULT_TECHNIQUE, person.asURI().toString(), status, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches for person "+person, e);
		}
	}

}
