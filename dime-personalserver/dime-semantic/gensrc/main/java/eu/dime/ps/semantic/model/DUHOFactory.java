/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
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