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
package eu.dime.ps.gateway.auth;

import java.util.Collection;

import javax.persistence.NoResultException;

import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.semantic.connection.ConnectionProvider;
import eu.dime.ps.semantic.exception.RepositoryStorageException;
import eu.dime.ps.storage.entities.Tenant;
import eu.dime.ps.storage.manager.EntityFactory;

public interface CredentialStore {

    void setConnectionProvider(ConnectionProvider connectionProvider);

    void setEntityFactory(EntityFactory entityFactory);

    /**
     * Returns password to authenticate in PS2PS (di.me) communication
     *
     * @param sender URI of sender account
     * @param receiver URI of receiver account
     * @param tenant
     * @return password
     * @throws NoResultException
     */
    public String getPassword(String sender, String receiver, Tenant tenant) throws NoResultException;

    /**
     * Returns username to authenticate in PS2PS (di.me) communication
     *
     * @param sender URI of sender account
     * @param receiver URI of receiver account
     * @param tenant
     * @return username
     * @throws NoResultException
     */
    public String getUsername(String sender, String receiver, Tenant tenant) throws NoResultException;

    /**
     * get username to communicate with external service
     *
     * @param account account identifier for external sa
     * @param tenant 
     * @return username
     * @throws NoResultException
     */
    public String getNameSaid(String account, Tenant tenant) throws NoResultException;

    /**
     * get provider name for service account (e.g. "facebook", "di.me" etc.)
     *
     * @param account account identifier for external sa
     * @return username
     * @throws NoResultException
     */
    public String getProviderName(String account, Tenant tenant) throws NoResultException;

    /**
     * get usertoken to communicate with external OAuth service
     *
     * @param account account identifier for external sa
     * @param tenant
     * @return token
     * @throws NoResultException
     */
    public String getAccessToken(String account, Tenant tenant) throws NoResultException;

    /**
     * get usersecret to communicate with external OAuth service
     *
     * @param account account identifier for external sa
     * @return secret
     */
    public String getAccessSecret(String account, Tenant tenant) throws NoResultException;

    void storeCredentialsForAccount(String localAccount, String remoteAccount,
            String username, String password, Tenant tenant) throws RepositoryStorageException;

    void updateCredentialsForAccount(String localAccount, String remoteAccount,
            String targetSaid, String password, Tenant tenant) throws RepositoryStorageException;

    void storeServiceProvider(String adaperId, String consumerKey,
            String consumerSecret);

    String getConsumerSecret(String providerName) throws NoResultException;

    String getConsumerKey(String providerName) throws NoResultException;

    public void storeOAuthCredentials(String providerName, String accountId, String token, String secret, Tenant tenant);

    public String getUriForName(String saidNameReceiver) throws NoResultException;

    /**
     * Method to receive URI for saidName of a contact
     *
     * @param saidLocal account connected with contact
     * @param saidRemote said of contact
     * @return
     */
    public String getUriForAccountName(String saidLocal, String saidRemote);

    public void tryCreateAccountCredentials(Resource sender, Collection<URI> recipients, Tenant tenant);
}
