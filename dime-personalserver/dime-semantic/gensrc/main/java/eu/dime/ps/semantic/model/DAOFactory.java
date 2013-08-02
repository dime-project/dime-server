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

package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.dao.*;

/**
 * A factory for the Java classes generated automatically for the DAO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */
public class DAOFactory extends ResourceFactory {

	public Account createAccount() {
		return new Account(createModel(), generateUniqueURI(), true);
	}

	public Account createAccount(URI resourceUri) {
		return new Account(createModel(), resourceUri, true);
	}

	public Account createAccount(String resourceUriString) {
		return new Account(createModel(), new URIImpl(resourceUriString), true);
	}

	public Credentials createCredentials() {
		return new Credentials(createModel(), generateUniqueURI(), true);
	}

	public Credentials createCredentials(URI resourceUri) {
		return new Credentials(createModel(), resourceUri, true);
	}

	public Credentials createCredentials(String resourceUriString) {
		return new Credentials(createModel(), new URIImpl(resourceUriString), true);
	}

}