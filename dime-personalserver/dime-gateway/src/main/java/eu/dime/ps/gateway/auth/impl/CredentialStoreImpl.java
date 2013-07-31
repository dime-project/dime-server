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

package eu.dime.ps.gateway.auth.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.dime.ps.gateway.auth.CredentialStore;
import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.semantic.rdf.ResourceStore;
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
public class CredentialStoreImpl implements CredentialStore{
			
	private Logger logger = Logger.getLogger(CredentialStore.class);
	
	private static CredentialStore instance;
		
	private EntityFactory entityFactory;
			
	private ConnectionProvider connectionProvider;
	
	@Autowired
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	@Autowired
	public void setEntityFactory(EntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public CredentialStoreImpl(){
		super();
	}
	


	
	
	@Override
	public void storeCredentialsForAccount(
			String local, String remote, 
			String target, String password, Tenant localTenant) throws RepositoryStorageException{

		ServiceAccount account = ServiceAccount.findAllByAccountUri(local, localTenant);
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
	public void storeOAuthCredentials(String providerName, String accountId, String token, String secret, Tenant localTenant){
		ServiceProvider serviceProvider = ServiceProvider.findByName(providerName);
		if (serviceProvider != null){
			ServiceAccount serviceAccount = entityFactory.buildServiceAccount();
			serviceAccount.setAccountURI(accountId);
			serviceAccount.setAccessToken(token);
			serviceAccount.setAccessSecret(secret);
			serviceAccount.setServiceProvider(serviceProvider);
			serviceAccount.setTenant(localTenant);
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
	
	private String encode(String plaintext){
		return plaintext; //TODO:encode
	}
	
	private String decode(String chiffre){
		return chiffre;	//TODO:decode
	}
		
	@Override
	public String getAccessToken(String accountId, Tenant localTenant) throws NoResultException{
		ServiceAccount sa = ServiceAccount.findAllByAccountUri(accountId, localTenant);
		 if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+accountId);
		 }
		return sa.getAccessToken();
	}
	
	@Override
	public String getAccessSecret(String accountId, Tenant localTenant) {
		ServiceAccount sa = ServiceAccount.findAllByAccountUri(accountId, localTenant);
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
	public String getPassword(String sender, String receiver, Tenant localTenant) {
		ServiceAccount sa = ServiceAccount.findAllByAccountUri(sender, localTenant);
		if (sa == null){
			ServiceAccount.findByName(sender);
		}
		AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(sa, receiver);
		if (ac == null){
			 throw new NoResultException("Could not find AccountCredentials for: "+sa.getAccountURI());
		}
		return ac.getSecret();
	}

	@Override
	public String getUsername(String sender, String receiver, Tenant localTenant) {
		ServiceAccount sa = ServiceAccount.findAllByAccountUri(sender, localTenant);
		if (sa == null){
			sa = ServiceAccount.findByName(sender);
		}
		if (sa == null){
			 throw new NoResultException("Could not find Service Account for: "+sender);
		}
		AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(sa, receiver);
		if (ac == null){
			User user = User.findByAccountUri(receiver, localTenant);
			if(user != null){
				return user.getUsername();
			} else {
				throw new NoResultException("Could not find AccountCredentials for: "+receiver);
			}
		}
		return sa.getName();
	}
	
	@Override
	public String getNameSaid(String account, Tenant localTenant) {
        //if connection was established before
		AccountCredentials ac = AccountCredentials.findAllByTargetUri(account, localTenant);
		if (ac != null){
			return ac.getTarget();
		}
		if (account != null) {
			ServiceAccount sa = ServiceAccount.findAllByAccountUri(account, localTenant);
			if (sa != null){
				return sa.getName();
			} else {
				User user = User.findByAccountUri(account, localTenant);
				return user.getUsername();
			}
		}
		throw new NoResultException("Could not find Service Account for: "+account);
	}

	@Override
	public String getProviderName(String account, Tenant localTenant) {
		 ServiceAccount sa = ServiceAccount.findAllByAccountUri(account, localTenant);
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
			String remote, String target, String password, Tenant localTenant)
			throws RepositoryStorageException {
		
		ServiceAccount account = ServiceAccount.findAllByAccountUri(local, localTenant);
		if (account != null){
			AccountCredentials ac = AccountCredentials.findAllBySourceAndByTargetUri(account, remote);
			if (ac == null){
				storeCredentialsForAccount(local, remote, target, password, localTenant);
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
	public String getUriForAccountName(String saidLocal,
			String saidRemote) {
		ServiceAccount sa = ServiceAccount.findByName(saidLocal);
		if (sa == null){
			throw new NoResultException("No (local) ServiceAccount found for said:"+saidLocal);
		}
		AccountCredentials ac = AccountCredentials.findAllByTenantAndBySourceAndByTarget(sa.getTenant(), sa, saidRemote);
		if (ac == null){
			List<User> users = User.findAllByUsername(saidRemote);
			for (User user : users) {
				if (user.getTenant().getId().equals(sa.getTenant().getId())){
					return user.getAccountUri();
				}
			} 
			logger.info("AccountCredentials not found for sa:"+saidRemote);
			return null;
		} else {
			return ac.getTargetUri();
		}
	}

	@Override
	public void tryCreateAccountCredentials(Resource sender, Collection<URI> recipients, Long tenantId, Tenant localTenant) {
		for (URI recipient : recipients) {
			if (recipient.equals(sender)) {
				continue;
			}
			
			AccountCredentials ac = AccountCredentials.findAllByTargetUri(recipient.toString(), localTenant);
			if (ac == null){
				User user = User.findByAccountUri(recipient.toString(), localTenant);
				ServiceAccount sa = ServiceAccount.findAllByAccountUri(sender.toString(), localTenant);
				Tenant tenant = Tenant.find(tenantId);
				
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