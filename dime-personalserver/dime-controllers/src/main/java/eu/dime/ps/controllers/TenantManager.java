package eu.dime.ps.controllers;

import java.util.List;

import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;

/**
 * This manager deals with the creation/removal of tenants in a multi-tenant PS.
 * 
 * @author Ismael Rivera
 */
public interface TenantManager {

	/**
	 * Retrieves a list with the identifiers of all existing tenants. 
	 * @return a list with the identifiers of all existing tenants
	 */
	public List<Tenant> getAll();
	
	/**
	 * Retrieves the tenant identifier which holds a specific account.
	 * @param accountId an account identifier 
	 * @return the tenant identifier which holds a specific account
	 */
	public Tenant getByAccountName(String accountId);
	
	/**
	 * Creates a new tenant for a given user.
	 * @param user the owner of the tenant
	 * @return the identifier of the newly created tenant
	 */
	public Tenant create(String tenantName, User user) throws TenantInitializationException;

	/**
	 * Removes a tenant, and destroys all information belonging to the tenant.
	 * @param tenantId the tenant identifier to remove/destroy
	 */
	public void remove(String tenantId);

}
