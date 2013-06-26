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