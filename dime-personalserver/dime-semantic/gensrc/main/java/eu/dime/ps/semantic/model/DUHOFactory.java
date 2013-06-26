package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.duho.*;

/**
 * A factory for the Java classes generated automatically for the DUHO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class DUHOFactory extends ResourceFactory {

	public ActionDriven createActionDriven() {
		return new ActionDriven(createModel(), generateUniqueURI(), true);
	}

	public ActionDriven createActionDriven(URI resourceUri) {
		return new ActionDriven(createModel(), resourceUri, true);
	}

	public ActionDriven createActionDriven(String resourceUriString) {
		return new ActionDriven(createModel(), new URIImpl(resourceUriString), true);
	}

	public Log createLog() {
		return new Log(createModel(), generateUniqueURI(), true);
	}

	public Log createLog(URI resourceUri) {
		return new Log(createModel(), resourceUri, true);
	}

	public Log createLog(String resourceUriString) {
		return new Log(createModel(), new URIImpl(resourceUriString), true);
	}

	public TimeDriven createTimeDriven() {
		return new TimeDriven(createModel(), generateUniqueURI(), true);
	}

	public TimeDriven createTimeDriven(URI resourceUri) {
		return new TimeDriven(createModel(), resourceUri, true);
	}

	public TimeDriven createTimeDriven(String resourceUriString) {
		return new TimeDriven(createModel(), new URIImpl(resourceUriString), true);
	}

	public UserAction createUserAction() {
		return new UserAction(createModel(), generateUniqueURI(), true);
	}

	public UserAction createUserAction(URI resourceUri) {
		return new UserAction(createModel(), resourceUri, true);
	}

	public UserAction createUserAction(String resourceUriString) {
		return new UserAction(createModel(), new URIImpl(resourceUriString), true);
	}

	public UserDriven createUserDriven() {
		return new UserDriven(createModel(), generateUniqueURI(), true);
	}

	public UserDriven createUserDriven(URI resourceUri) {
		return new UserDriven(createModel(), resourceUri, true);
	}

	public UserDriven createUserDriven(String resourceUriString) {
		return new UserDriven(createModel(), new URIImpl(resourceUriString), true);
	}

}