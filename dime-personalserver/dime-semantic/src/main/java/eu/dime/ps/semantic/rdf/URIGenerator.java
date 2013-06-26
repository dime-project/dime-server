package eu.dime.ps.semantic.rdf;

import java.util.UUID;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class URIGenerator {
	
	public static URI createNewRandomUniqueURI() {
		return new URIImpl("urn:uuid:" + UUID.randomUUID().toString());
	}
	
	/**
	 * @param uriPrefix must include schema information
	 * @return a new, random unique URI starting with uriPrefix
	 */
	public static URI createNewRandomUniqueURI(String uriPrefix) {
		return new URIImpl(uriPrefix + UUID.randomUUID().toString());
	}
}
