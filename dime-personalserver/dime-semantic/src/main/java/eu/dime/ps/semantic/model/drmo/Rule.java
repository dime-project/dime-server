package eu.dime.ps.semantic.model.drmo;

import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

public class Rule extends eu.dime.ps.semantic.model.RDFReactorThing {

	public static final URI RDFS_CLASS = new URIImpl("http://www.semanticdesktop.org/ontologies/2012/03/06/drmo#Rule", false);
    
	public Rule(Model model, org.ontoware.rdf2go.model.node.Resource instanceIdentifier, boolean write) {
		super(model, RDFS_CLASS, instanceIdentifier, write);
	}

	public Rule(Model model, String uriString, boolean write) throws ModelRuntimeException {
		super(model, RDFS_CLASS, new URIImpl(uriString, false), write);
	}

}
