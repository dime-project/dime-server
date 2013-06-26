package eu.dime.ps.semantic.model;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import eu.dime.ps.semantic.model.ppo.*;

/**
 * A factory for the Java classes generated automatically for the PPO vocabulary.
 * 
 * @author Ismael Rivera
 * 
 */

public class PPOFactory extends ResourceFactory {

	public AccessSpace createAccessSpace() {
		return new AccessSpace(createModel(), generateUniqueURI(), true);
	}

	public AccessSpace createAccessSpace(URI resourceUri) {
		return new AccessSpace(createModel(), resourceUri, true);
	}

	public AccessSpace createAccessSpace(String resourceUriString) {
		return new AccessSpace(createModel(), new URIImpl(resourceUriString), true);
	}

	public Condition createCondition() {
		return new Condition(createModel(), generateUniqueURI(), true);
	}

	public Condition createCondition(URI resourceUri) {
		return new Condition(createModel(), resourceUri, true);
	}

	public Condition createCondition(String resourceUriString) {
		return new Condition(createModel(), new URIImpl(resourceUriString), true);
	}

	public PrivacyPreference createPrivacyPreference() {
		return new PrivacyPreference(createModel(), generateUniqueURI(), true);
	}

	public PrivacyPreference createPrivacyPreference(URI resourceUri) {
		return new PrivacyPreference(createModel(), resourceUri, true);
	}

	public PrivacyPreference createPrivacyPreference(String resourceUriString) {
		return new PrivacyPreference(createModel(), new URIImpl(resourceUriString), true);
	}

}