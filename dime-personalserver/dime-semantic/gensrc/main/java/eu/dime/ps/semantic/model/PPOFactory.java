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