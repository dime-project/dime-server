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

import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

import eu.dime.ps.controllers.exception.InfosphereException;
import eu.dime.ps.gateway.exception.ServiceAdapterNotSupportedException;
import eu.dime.ps.gateway.service.ServiceAdapter;
import eu.dime.ps.semantic.model.dao.Account;
import eu.dime.ps.semantic.model.dao.Credentials;
import eu.dime.ps.semantic.model.pimo.Person;

/**
 * Manager for accounts management.
 * 
 * @author Ismael Rivera
 */
public interface AccountManager extends InfoSphereManager<Account> {

	boolean isAccount(String resourceId) throws InfosphereException;

	/**
	 * Retrieves all accounts of a specific person.
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Account> getAllByCreator(Person creator)
			throws InfosphereException;

	Collection<Account> getAllByCreator(Person creator, URI... properties)
			throws InfosphereException;

	/**
	 * Retrieves accounts of a specific type (e.g. twitter, facebook...).
	 * @param accountType
	 * @return
	 * @throws InfosphereException
	 */
	Collection<Account> getAllByType(String accountType)
			throws InfosphereException;

	Collection<Account> getAllByType(String accountType, URI... properties)
			throws InfosphereException;

	Collection<Account> getAllByCreatorAndByType(String creatorId, String accountType)
			throws InfosphereException;

	Collection<Account> getAllByCreatorAndByType(String creatorId, String accountType,
			URI... properties) throws InfosphereException;

	Credentials getCredentials(String accountId) throws InfosphereException;

//	void add(String accountType, String token, String secret) throws InfosphereException;
	
	/**
	 * Creates an account a service adapter, and initiates the crawling process of the account.
	 * 
	 * @param serviceAdapter
	 * @throws InfosphereException
	 * @throws ServiceAdapterNotSupportedException
	 */
	void add(ServiceAdapter serviceAdapter) throws InfosphereException, ServiceAdapterNotSupportedException;
	
	/**
	 * Triggers the crawl of this account, even if it's not the time it was schedule to run.
	 *  
	 * @param accountId
	 */
	void crawl(String accountId);
	
}