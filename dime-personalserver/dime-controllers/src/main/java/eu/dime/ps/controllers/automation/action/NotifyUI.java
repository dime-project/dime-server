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

package eu.dime.ps.controllers.automation.action;

import ie.deri.smile.rules.actions.Action;
import ie.deri.smile.rules.actions.ActionRegistry;
import ie.deri.smile.rules.actions.IllegalActionException;
import ie.deri.smile.vocabulary.DRMO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.PlainLiteral;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.PlainLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.dime.commons.notifications.user.UNMessage;
import eu.dime.commons.notifications.user.UserNotification;
import eu.dime.ps.controllers.notifier.NotifierManager;
import eu.dime.ps.controllers.notifier.exception.NotifierException;
import eu.dime.ps.semantic.util.StringUtils;

public class NotifyUI implements Action {

	private static final Logger logger = LoggerFactory.getLogger(NotifyUI.class);
	
	private static final URI IDENTIFIER = new URIImpl("urn:dime:action:NotifyUI");
	
	private static final PlainLiteral SUBJECT = new PlainLiteralImpl("Be aware that {{personName}} is nearby.");
	private static final URI OBJECT = new URIImpl("dime:interface");
			
	private static final Model definition;
	static {
		definition = RDF2Go.getModelFactory().createModel().open();
		definition.addStatement(IDENTIFIER, RDF.type, DRMO.Action);
		definition.addStatement(IDENTIFIER, DRMO.hasSubject, SUBJECT);
		definition.addStatement(IDENTIFIER, DRMO.hasObject, OBJECT);
	};
	
	// auto-register this action in the registry
	static {
		ActionRegistry.getInstance().register(IDENTIFIER, NotifyUI.class);
	}
	
	private NotifierManager notifierManager;
	
	public void setNotifierManager(NotifierManager notifierManager) {
		this.notifierManager = notifierManager;
	}
	
	@Override
	public URI getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public Model toRDF() {
		Model copy = RDF2Go.getModelFactory().createModel().open();
		copy.addAll(definition.iterator());
		return copy;
	}

	@Override
	public void execute(List<Node> results, ModelSet knowledgeBase, String tenant) throws IllegalActionException {

		// FIXME [Isma] I'm passing the tenant as a temporary solution for the 2nd review; will be refactored afterwards

//		if (executionContext == null)
//			throw new IllegalActionException("The action cannot be executed without a valid ActionExecutionContext.");
//
//		String tenant = executionContext.getTenant();
		if (tenant == null)
			throw new IllegalActionException("The action cannot be executed. ActionExecutionContext must contain a valid tenant identifier.");
		
		final String queryTemplate = StringUtils.strjoinNL(
				"PREFIX pimo:	"+PIMO.NS_PIMO.toSPARQL(),
				"PREFIX nao:	"+NAO.NS_NAO.toSPARQL(),
				"SELECT ?personName WHERE { {{result}} a pimo:Person ; nao:prefLabel ?personName . }");
		
		for (Node result : results) {
			if (result instanceof URI) {
				String query = queryTemplate.replace("{{result}}", result.asURI().toSPARQL());
				ClosableIterator<QueryRow> queryIt = knowledgeBase.sparqlSelect(query).iterator();
				if (queryIt.hasNext()) {
					// formats the message of the notification
					String message = SUBJECT.getValue().replace("{{personName}}", queryIt.next().getLiteralValue("personName"));
					
					// prepare user notification
					UNMessage unMessage = new UNMessage();
					unMessage.setMessage(message);
					UserNotification notification = new UserNotification(Long.parseLong(tenant), unMessage);
					
					try {
						// push the notification to the notification manager
						notifierManager.pushInternalNotification(notification);
					} catch (NotifierException e) {
						logger.error("Error when pushing notification [" + notification + "].", e);
					}
				}
				queryIt.close();
			}
		}
	}
	
}
