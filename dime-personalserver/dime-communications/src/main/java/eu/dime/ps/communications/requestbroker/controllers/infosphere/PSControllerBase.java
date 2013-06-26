package eu.dime.ps.communications.requestbroker.controllers.infosphere;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.rdfreactor.schema.rdfs.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.dime.ps.controllers.TenantContextHolder;
import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.controllers.infosphere.manager.ShareableManager;
import eu.dime.ps.gateway.util.JSONLDUtils;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

public abstract class PSControllerBase {

	private static final Logger logger = LoggerFactory.getLogger(PSControllerBase.class);

	protected <T extends Resource> Object getResource(String said, String resourceId,
			ShareableManager<T> manager) {

		ServiceAccount account = ServiceAccount.findByName(said);
		if (account == null || account.getAccountURI() == null)
			throw new RuntimeException("Account "+said+" does not exist or is corrupted!");

		Object response = null;

		T resource = null;
		try {
			// TODO the managers should ensure that only the "allowed" data is returned,
			// but also that the requester is able to access this element
			// for now, we just return the object without checking any resource
			resource = manager.get(resourceId, getRequesterAccount());
			response = JSONLDUtils.serialize(resource);
			logger.debug("Fetching resource "+resourceId+": "+response);
		} catch (InfosphereException e) {
			response = new HashMap<String, Object>();
			((Map<String, Object>) response).put("error", "Cannot retrieve resource: "+e.getLocalizedMessage());
		}

		return response;
	}

	protected <T extends Resource> Object getAllResources(String said,
			ShareableManager<T> manager) {

		// TODO check what resources this said can access!!! and filter by it :)

		ServiceAccount account = ServiceAccount.findByName(said);
		if (account == null || account.getAccountURI() == null)
			throw new RuntimeException("Account "+said+" does not exist or is corrupted!");

		Object response = null;

		Collection<T> resources = null;
		try {
			// TODO the managers should ensure that only the "allowed" data is returned,
			// but also that the requester is able to access this element
			// for now, we just return the object without checking any resource

			resources = manager.getAll(account.getAccountURI(), getRequesterAccount());

			response = JSONLDUtils.serializeCollection(resources.toArray(new Resource[resources.size()]));
		} catch (InfosphereException e) {
			Map<String, Object> error = new HashMap<String, Object>();
			error.put("error", "Cannot retrieve resource: "+e.getLocalizedMessage());
			response = error;
		}

		return response;
	}

	protected final String getRequester() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	protected final String getRequesterAccount() {
		Long tenantId = TenantContextHolder.getTenant();
		String requester = getRequester();

		Tenant tenant = Tenant.find(tenantId);
		if (tenant == null)
			throw new IllegalArgumentException("Tenant '"+TenantContextHolder.getTenant()+"' not found.");

		User user = User.findByTenantAndByUsername(tenant, requester);
		if (user == null || user.getAccountUri() == null)
			throw new IllegalArgumentException("User's account URI not found for requester '"+requester+"'.");

		return user.getAccountUri();
	}

}
