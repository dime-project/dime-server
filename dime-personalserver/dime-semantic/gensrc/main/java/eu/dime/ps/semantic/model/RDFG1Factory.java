package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.rdfg1.*;

/**
 * A factory for the Java classes generated automatically for the RDFG1 vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class RDFG1Factory extends ResourceFactory {

	public Graph createGraph() {
		return new Graph(createModel(), generateUniqueURI(), true);
	}

	public Graph createGraph(URI resourceUri) {
		return new Graph(createModel(), resourceUri, true);
	}

	public Graph createGraph(String resourceUriString) {
		return new Graph(createModel(), new URIImpl(resourceUriString), true);
	}

}