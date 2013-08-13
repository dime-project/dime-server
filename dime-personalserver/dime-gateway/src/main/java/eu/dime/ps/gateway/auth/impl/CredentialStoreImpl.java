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

package eu.dime.ps.gateway.auth.impl;

import java.util.Collection;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.entities.AccountCredentials;
import eu.dime.ps.storage.entities.ServiceAccount;
import eu.dime.ps.storage.entities.ServiceProvider;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.entities.User;
import eu.dime.ps.storage.manager.EntityFactory;

/**
 * 
 * @author marcel
 *
 */
public class CredentialStoreImpl implements CredentialStore {
			
	private Logger logger = Logger.getLogger(CredentialStoreImpl.class);
	
	private static CredentialStore instance;
		
	private EntityFactory entityFactory;
			
	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public CredentialStoreImpl() {
		super();
	}
	
	@Override
	public void storeCredentialsForAccount(
			String local, String remote, 
			String target, String password, Tenant tenant) throws RepositoryStorageException{

		ServiceAccount account = ServiceAccount.findAllByTenantAndAccountUri(tenant, local);
		if (account != null){
			AccountCredentials ac = entityFactory.buildAccountCredentials();
			ac.setSecret(password);
			ac.setSource(account);
			ac.setTarget(target);
			ac.setTargetUri(remote);
			ac.setTenant(account.getTenant());
			ac.merge();
			ac.flush();
		} else {
			// something is wrong. account that received a notification does not exist
			logger.error("ServiceAccount not found: "+local.toString());
		}
	}
	
	@Override
	public void storeOAuthCredentials(String providerName, String accountId, String token, String secret, Tenant tenant){
		ServiceProvider serviceProvider = ServiceProvider.findByName(providerName);
		if (serviceProvider != null){
			ServiceAccount serviceAccount = entityFactory.buildServiceAccount();
			serviceAccount.setAccountURI(accountId);
			serviceAccount.setAccessToken(token);
			serviceAccount.setAccessSecret(secret);
			serviceAccount.setServiceProvider(serviceProvider);
			serviceAccount.setTenant(tenant);
			serviceAccount.merge();
			serviceAccount.flush();
			serviceProvider.getServiceAccounts().add(serviceAccount);
			serviceProvider.merge();
			serviceProvider.flush();
		}
	}
	


	@Override
	public void storeServiceProvider(String adapterId, String consumerKey, String consumerSecret){		
		ServiceProvider serviceProvider = ServiceProvider.findByName(adapterId);
		if (serviceProvider == null){
			serviceProvider= entityFactory.buildServiceProvider();
			serviceProvider.setServiceName(adapterId);
		}
		serviceProvider.setConsumerKey(consumerKey);
		serviceProvider.setConsumerSecret(consumerSecret);
		serviceProvider.persist();
		serviceProvider.flush();	
	}
	
	@Override
	public String getAccessToken(String accountId, Tenant tenant) throws NoResultException{
		ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, accountId);
		 if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+accountId);
		 }
		return sa.getAccessToken();
	}
	
	@Override
	public String getAccessSecret(String accountId, Tenant tenant) {
		ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, accountId);
		 if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+accountId);
		 }
		return sa.getAccessSecret();
	}

	@Override
	public String getConsumerKey(String providerName) {
		ServiceProvider sp =  ServiceProvider.findByName(providerName);
		 if (sp == null){
			 throw new NoResultException("Could not find Service Account for: "+providerName);
		 }
		 return sp.getConsumerKey();
	} 
	
	@Override
	public String getConsumerSecret(String providerName) {
		ServiceProvider sp = ServiceProvider.findByName(providerName);
		 if (sp == null){
			 throw new NoResultException("Could not find Service Account for: "+providerName);
		 }
		 return sp.getConsumerSecret();
	}
	
	public boolean setEncryptionKey(String key) {
		// TODO do
		return false;
	}

	@Override
	public String getPassword(String sender, String receiver, Tenant tenant) {
		ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, sender);
		if (sa == null){
			throw new NoResultException("Could not find ServiceAccount where tenant = "+tenant.getId()+" and accountUri = "+sender);
		}
		AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(sa, receiver);
		if (ac == null){
			throw new NoResultException("Could not find AccountCredentials for: "+sa.getAccountURI());
		}
		return ac.getSecret();
	}

	@Override
	public String getUsername(String sender, String receiver, Tenant tenant) {
		ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, sender);
		if (sa == null){
			sa = ServiceAccount.findByName(sender);
		}
		if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+sender);
		}
		AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(sa, receiver);
		if (ac == null){
			User user = User.findByTenantAndByAccountUri(tenant, receiver);
			if(user != null){
				return user.getUsername();
			} else {
				throw new NoResultException("Could not find AccountCredentials for: "+receiver);
			}
		}
		return sa.getName();
	}
	
	@Override
	public String getNameSaid(String account, Tenant tenant) {
        //if connection was established before
		AccountCredentials ac = AccountCredentials.findAllByTenantAndByTargetUri(tenant, account);
		if (ac != null){
			return ac.getTarget();
		}
		if (account != null) {
			ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, account);
			if (sa != null){
				return sa.getName();
			} else {
				User user = User.findByTenantAndByAccountUri(tenant, account);
				return user.getUsername();
			}
		}
		throw new NoResultException("Could not find Service Account for: "+account);
	}

	@Override
	public String getProviderName(String account, Tenant tenant) {
		 ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, account);
		 if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+account);
		 }
		 return sa.getServiceProvider().getServiceName();
	}

	/**
	 * translates a said name (for external communication) to an internal uri
	 * only works for tenants!! for contacts use getUriForAccountName()
	 */
	@Override
	public String getUriForName(String saidName) {
		String uri = ServiceAccount.findByName(saidName).getAccountURI();
//		if (uri == null){
//			uri = AccountCredentials.findAllByTargetName(saidName).get(0).getTargetUri();
//		}
		return uri;
	}

	@Override
	public void updateCredentialsForAccount(String local,
			String remote, String target, String password, Tenant tenant)
			throws RepositoryStorageException {
		
		ServiceAccount account = ServiceAccount.findAllByTenantAndAccountUri(tenant, local);
		if (account != null){
			AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(account, remote);
			if (ac == null){
				storeCredentialsForAccount(local, remote, target, password, tenant);
				return;
			}
			ac.setSecret(password);
			ac.setTarget(target);
			ac.merge();
			ac.flush();
		} else {
			// something is wrong. account that received a notification does not exist
			logger.error("ServiceAccount not found: "+local.toString());
		}
		
	}
	
	public static CredentialStore getInstance(){
		if (instance == null){
			instance = new CredentialStoreImpl();
		}
		return instance;
	}

	@Override
	public String getUriForAccountName(String saidLocal, String saidRemote, Tenant tenant) {
		// TODO replace with a findByTenantAndByName
		ServiceAccount sourceAccount = ServiceAccount.findByName(saidLocal);
		if (sourceAccount == null) {
			logger.debug("No ServiceAccount instance found for said " + saidLocal + ". Returning null.");
			return null;
		}
		
		AccountCredentials ac = AccountCredentials.findAllByTenantAndBySourceAndByTarget(tenant, sourceAccount, saidRemote);
		if (ac != null) {
			return ac.getTargetUri();
		} else {
			User user = User.findByTenantAndByUsername(tenant, saidRemote);
			return user == null ? null : user.getAccountUri();
		}
	}

	@Override
	public void tryCreateAccountCredentials(Resource sender, Collection<URI> recipients, Tenant tenant) {
		for (URI recipient : recipients) {
			if (recipient.equals(sender)) {
				continue;
			}
			
			AccountCredentials ac = AccountCredentials.findAllByTenantAndByTargetUri(tenant, recipient.toString());
			if (ac == null){
				User user = User.findByTenantAndByAccountUri(tenant, recipient.toString());
				ServiceAccount sa = ServiceAccount.findAllByTenantAndAccountUri(tenant, sender.toString());
				
				ac = entityFactory.buildAccountCredentials();
				ac.setTenant(tenant);
				ac.setTargetUri(recipient.toString());
				ac.setTarget(user.getUsername());
				ac.setSource(sa);
				ac.setSecret("");
				ac.persist();
			}
		}
	}
	
}