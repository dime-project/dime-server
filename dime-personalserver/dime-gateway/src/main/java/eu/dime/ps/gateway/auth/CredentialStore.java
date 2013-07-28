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

package eu.dime.ps.gateway.auth;

import java.util.Collection;

import javax.persistence.NoResultException;

import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.NotFoundException;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.manager.EntityFactory;

public interface CredentialStore {
	
	void setConnectionProvider(ConnectionProvider connectionProvider);

	void setEntityFactory(EntityFactory entityFactory);
	
	/**
	 * Returns password to authenticate in PS2PS (di.me) communication
	 * @param sender URI of sender account
	 * @param receiver URI of receiver account
	 * @return password
	 */
	public String getPassword(String sender, String receiver) throws NoResultException;
	
	/**
	 * Returns username to authenticate in PS2PS (di.me) communication
	 * @param sender URI of sender account
	 * @param receiver URI of receiver account
	 * @return username
	 */
	public String getUsername(String sender, String receiver) throws NoResultException;
	
	/**
	 * get username to communicate with external service
	 * @param account account identifier for external sa
	 * @return username
	 */
	public String getNameSaid(String account) throws NoResultException;
	
	/**
	 * get provider name for service account (e.g. "facebook", "di.me" etc.)
	 * @param account account identifier for external sa
	 * @return username
	 */
	public String getProviderName(String account) throws NoResultException;
	
	/**
	 * get usertoken to communicate with external OAuth service
	 * @param account account identifier for external sa
	 * @return token
	 * @throws RepositoryStorageException 
	 * @throws NotFoundException 
	 */
	public String getAccessToken(String account) throws NoResultException;
	
	/**
	 * get usersecret to communicate with external OAuth service
	 * @param account account identifier for external sa
	 * @return secret
	 */
	public String getAccessSecret(String account) throws NoResultException;

	void storeCredentialsForAccount(String localAccount, String remoteAccount,
			String username, String password) throws RepositoryStorageException;
	
	void updateCredentialsForAccount(String localAccount, String remoteAccount,
			String targetSaid, String password) throws RepositoryStorageException;

	void storeServiceProvider(String adaperId, String consumerKey,
			String consumerSecret);

	String getConsumerSecret(String providerName) throws NoResultException;

	String getConsumerKey(String providerName) throws NoResultException;

	public void storeOAuthCredentials(String providerName, String accountId, String token, String secret);

	public String getUriForName(String saidNameReceiver) throws NoResultException;

	/**
	 * Method to receive URI for saidName of a contact
	 * @param saidLocal account connected with contact
	 * @param saidRemote said of contact
	 * @return
	 */
	public String getUriForAccountName(String saidLocal, String saidRemote);

	public void tryCreateAccountCredentials(Resource sender, Collection<URI> recipients, Long tenant);
	
}
