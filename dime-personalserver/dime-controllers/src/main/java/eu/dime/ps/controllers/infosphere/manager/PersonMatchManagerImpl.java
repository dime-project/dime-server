package eu.dime.ps.controllers.infosphere.manager;

import java.util.List;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.storage.entities.PersonMatch;
import eu.dime.ps.storage.entities.Tenant;

public class PersonMatchManagerImpl implements PersonMatchManager {

	public static final double THRESHOLD = 0.75;
	public static final int DEFAULT_TECHNIQUE = 3;
	
	@Override
	public List<PersonMatch> getAll() throws InfosphereException {
		Long tenantId = TenantContextHolder.getTenant();
		Tenant tenant = Tenant.find(tenantId);
			
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByThreshold(tenant, DEFAULT_TECHNIQUE, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByThreshold(double threshold) throws InfosphereException {
		Long tenantId = TenantContextHolder.getTenant();
		Tenant tenant = Tenant.find(tenantId);
		
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByThreshold(tenant, DEFAULT_TECHNIQUE, threshold);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByStatus(String status) throws InfosphereException {
		Long tenantId = TenantContextHolder.getTenant();
		Tenant tenant = Tenant.find(tenantId);
		
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndByStatusAndByThreshold(tenant, DEFAULT_TECHNIQUE, status, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches.", e);
		}
	}
	
	@Override
	public List<PersonMatch> getAllByPerson(URI person) throws InfosphereException {
		Long tenantId = TenantContextHolder.getTenant();
		Tenant tenant = Tenant.find(tenantId);
	
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndBySourceAndByThreshold(tenant, DEFAULT_TECHNIQUE, person.asURI().toString(), THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches for person "+person, e);
		}
	}

	@Override
	public List<PersonMatch> getAllByPersonAndByStatus(URI person, String status) throws InfosphereException {
		Long tenantId = TenantContextHolder.getTenant();
		Tenant tenant = Tenant.find(tenantId);
	
		try {
			return PersonMatch.findAllByTenantAndByTechniqueAndBySourceAndByStatusAndByThreshold(tenant, DEFAULT_TECHNIQUE, person.asURI().toString(), status, THRESHOLD);
		} catch (IllegalArgumentException e) {
			throw new InfosphereException("Could not find matches for person "+person, e);
		}
	}

}
