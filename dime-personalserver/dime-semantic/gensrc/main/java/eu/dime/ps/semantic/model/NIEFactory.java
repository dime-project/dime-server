package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.nie.*;

/**
 * A factory for the Java classes generated automatically for the NIE vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class NIEFactory extends ResourceFactory {

	public DataObject createDataObject() {
		return new DataObject(createModel(), generateUniqueURI(), true);
	}

	public DataObject createDataObject(URI resourceUri) {
		return new DataObject(createModel(), resourceUri, true);
	}

	public DataObject createDataObject(String resourceUriString) {
		return new DataObject(createModel(), new URIImpl(resourceUriString), true);
	}

	public DataSource createDataSource() {
		return new DataSource(createModel(), generateUniqueURI(), true);
	}

	public DataSource createDataSource(URI resourceUri) {
		return new DataSource(createModel(), resourceUri, true);
	}

	public DataSource createDataSource(String resourceUriString) {
		return new DataSource(createModel(), new URIImpl(resourceUriString), true);
	}

	public InformationElement createInformationElement() {
		return new InformationElement(createModel(), generateUniqueURI(), true);
	}

	public InformationElement createInformationElement(URI resourceUri) {
		return new InformationElement(createModel(), resourceUri, true);
	}

	public InformationElement createInformationElement(String resourceUriString) {
		return new InformationElement(createModel(), new URIImpl(resourceUriString), true);
	}

}