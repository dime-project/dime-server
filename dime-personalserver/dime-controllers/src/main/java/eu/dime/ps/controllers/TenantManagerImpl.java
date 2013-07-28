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

package eu.dime.ps.controllers;

import java.util.List;

import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.controllers.exception.TenantManagerException;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * Implements {@link TenantManager} using a relational database as underlying
 * storage. It handles the creation/removal of RDF/semantic services, and CMS
 * instances for file storage.
 * 
 * @author Marc Planaguma
 * @author Ismael Rivera
 */
public class TenantManagerImpl implements TenantManager {

	private EntityFactory entityFactory;
	private ConnectionProvider connectionProvider;

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	@Autowired
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	@Override
	public List<Tenant> getAll() {
		return Tenant.findAll();
	}

	@Override
	public Tenant getByAccountName(String accountId) {
		Tenant tenant = Tenant.findByName(accountId);
		if (tenant != null) {
			return tenant;
		} else {
			ServiceAccount account = ServiceAccount.findByName(accountId);
			if (account == null){
				throw new TenantManagerException("Could not find neither a tenant nor a service account with name '"+accountId+"'.");
			}
			return account.getTenant();
		}
	}

	@Override
	public Tenant create(String tenantName, User user)
			throws TenantInitializationException {

		Tenant tenant = entityFactory.buildTenant();
		tenant.setName(tenantName);
		tenant.persist();

		user.setTenant(tenant);
		user.merge();

		try {
			connectionProvider.newConnection(tenant.getId().toString(), user.getUsername());
		} catch (RepositoryException e) {
			throw new TenantInitializationException(
					"Tenant '"+tenantName+"' couldn't be created: " + e.getMessage(), e);
		}

		return tenant;
	}

	@Override
	public void remove(String tenantId) {
		try {
			connectionProvider.remove(tenantId);
			Tenant.find(Long.parseLong(tenantId)).remove();
		} catch (RepositoryException e) {
			throw new RuntimeException("Tenant '"+tenantId+"' couldn't be removed.", e);
		}
	}

}