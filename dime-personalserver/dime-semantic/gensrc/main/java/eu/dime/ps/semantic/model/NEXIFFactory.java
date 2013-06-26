package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nexif.*;

/**
 * A factory for the Java classes generated automatically for the NEXIF vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NEXIFFactory extends ResourceFactory {

	public Photo createPhoto() {
		return new Photo(createModel(), generateUniqueURI(), true);
	}

	public Photo createPhoto(URI resourceUri) {
		return new Photo(createModel(), resourceUri, true);
	}

	public Photo createPhoto(String resourceUriString) {
		return new Photo(createModel(), new URIImpl(resourceUriString), true);
	}

}