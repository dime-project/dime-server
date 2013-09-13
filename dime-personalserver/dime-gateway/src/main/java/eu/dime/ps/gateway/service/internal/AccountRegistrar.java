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
package eu.dime.ps.gateway.service.internal;

import java.net.URL;

import eu.dime.ps.storage.entities.ServiceAccount;

/**
 * Authorizative registrar for di.me accounts.
 * 
 * @author Ismael Rivera
 */
public interface AccountRegistrar {
	
	/**
	 * Registers an account identifier in the registrar.
	 * 
	 * @param accountId account identifier to register.
	 * @return true if successfully registered; false otherwise.
	 */
	boolean register(String accountId);
	
	/**
	 * Registers an account in the registrar, which doesn't yet have an identifier.
	 * 
	 * @param account {@link ServiceAccount} object, which must have a valid account URI.
	 * @return the account identifier if successfully registered; false otherwise.
	 */
	String register(ServiceAccount account);

	/**
	 * Resolves an account identifier into a URL which will respond to requests,
	 * where the account identifier is the target. For example: https://example.org/dime-communications.
	 * 
	 * @param accountId account identifier to resolve
	 * @return URL corresponding to the account endpoint
	 * @throws AccountCannotResolveException if the account couldn't be resolved
	 */
	URL resolve(String accountId) throws AccountCannotResolveException;

}
