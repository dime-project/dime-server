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

package eu.dime.ps.controllers.automation.action;

import ie.deri.smile.rules.actions.Action;
import ie.deri.smile.rules.actions.IllegalActionException;
import ie.deri.smile.vocabulary.DRMO;

import java.util.List;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.user.UNMessage;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;

public class Notify implements Action {

	private static final Logger logger = LoggerFactory.getLogger(Notify.class);
	
	public static final URI IDENTIFIER = new URIImpl("urn:actions:Notify");
	
	private Resource subject;
	private Node object;
			
	private NotifierManager notifierManager;
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	@Override
	public URI getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void setSubject(Resource subject) {
		this.subject = subject;
	}

	@Override
	public void setObject(Node object) {
		this.object = object;
	}

	@Override
	public Model toRDF() {
		final Model rdf = RDF2Go.getModelFactory().createModel().open();
		rdf.addStatement(IDENTIFIER, RDF.type, DRMO.Action);
		rdf.addStatement(IDENTIFIER, DRMO.hasSubject, subject);
		rdf.addStatement(IDENTIFIER, DRMO.hasObject, object);
		return rdf;
	}

	@Override
	public void execute(List<Node> results, ModelSet knowledgeBase, String tenant) throws IllegalActionException {

		if (tenant == null) {
			throw new IllegalActionException("The action cannot be executed: `tenant` must contain a valid value.");
		}
		
		if (object == null
				|| !(object instanceof Literal)) {
			throw new IllegalActionException("The action's object must not be null and should contain the message to notify.");
		}
		
		// prepare user notification
		UNMessage unMessage = new UNMessage();
		unMessage.setMessage(object.asLiteral().getValue());
		UserNotification notification = new UserNotification(Long.parseLong(tenant), unMessage);
		
		try {
			// push the notification to the notification manager
			notifierManager.pushInternalNotification(notification);
		} catch (NotifierException e) {
			logger.error("Error when pushing notification [" + notification + "].", e);
		}
	}
	
}
