package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nso.*;

/**
 * A factory for the Java classes generated automatically for the NSO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */
public class NSOFactory extends ResourceFactory {

	public AccessSpace createAccessSpace() {
		return new AccessSpace(createModel(), generateUniqueURI(), true);
	}

	public AccessSpace createAccessSpace(URI resourceUri) {
		return new AccessSpace(createModel(), resourceUri, true);
	}

	public AccessSpace createAccessSpace(String resourceUriString) {
		return new AccessSpace(createModel(), new URIImpl(resourceUriString), true);
	}

}