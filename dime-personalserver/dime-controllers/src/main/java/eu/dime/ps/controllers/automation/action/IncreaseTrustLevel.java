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

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.rules.actions.Action;
import ie.deri.smile.rules.actions.IllegalActionException;
import ie.deri.smile.vocabulary.DAO;
import ie.deri.smile.vocabulary.DRMO;
import ie.deri.smile.vocabulary.NAO;
import ie.deri.smile.vocabulary.PIMO;

import java.util.List;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.model.node.impl.DatatypeLiteralImpl;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncreaseTrustLevel implements Action {

	private static final Logger logger = LoggerFactory.getLogger(IncreaseTrustLevel.class);
	
	public static final URI IDENTIFIER = new URIImpl("urn:actions:ITL");
	
	private static final Model definition;
	static {
		definition = RDF2Go.getModelFactory().createModel().open();
		definition.addStatement(IDENTIFIER, RDF.type, DRMO.Action);
	};
	
	@Override
	public URI getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public void setSubject(Resource subject) {
		if (subject != null) {
			definition.addStatement(IDENTIFIER, DRMO.hasSubject, subject);
		}
	}
	
	@Override
	public void setObject(Node object) {
		if (object != null) {
			definition.addStatement(IDENTIFIER, DRMO.hasObject, object);
		}
	}

	@Override
	public Model toRDF() {
		final Model copy = RDF2Go.getModelFactory().createModel().open();
		copy.addAll(definition.iterator());
		return copy;
	}

	@Override
	public void execute(List<Node> results, ModelSet knowledgeBase, String tenant) throws IllegalActionException {

		if (tenant == null) {
			throw new IllegalActionException("The action cannot be executed: `tenant` must contain a valid value.");
		}
		
		Resource userPIM = ModelUtils.findSubject(knowledgeBase, RDF.type, PIMO.PersonalInformationModel);
		if (userPIM == null) {
			throw new IllegalActionException("The action cannot be executed: User's PIM not found in knowledge base.");
		}
		
		// find dao:Account instance in results, and increase trust level of creator pimo:Person 
		for (Node result : results) {
			if (result instanceof URI
					&& knowledgeBase.containsStatements(userPIM.asURI(), result.asURI(), RDF.type, DAO.Account)) {
				Node creator = ModelUtils.findObject(knowledgeBase, result.asURI(), NAO.creator);
				if (creator != null) {
					Node trustLevel = ModelUtils.findObject(knowledgeBase, creator.asURI(), NAO.trustLevel);
					double value = 0, newValue = 0;
					if (trustLevel != null) {
						value = Double.parseDouble(trustLevel.asDatatypeLiteral().getValue());
					}
//					newValue = value > 0 ? Math.min(value + (1 - value) * 0.1, 1) : 0.1;
					// since trust values are not shown in the UI, we increase it to 0.9 to 'jump' to high trust,
					// so this can be demoed.
					newValue = 0.9f;
					
					logger.info("Increasing trust level of person "+creator.asURI()+" from "+value +" to "+newValue);
					
					knowledgeBase.removeStatements(userPIM.asURI(), creator.asURI(), NAO.trustLevel, Variable.ANY);
					knowledgeBase.addStatement(userPIM.asURI(), creator.asURI(), NAO.trustLevel, new DatatypeLiteralImpl(String.valueOf(newValue), XSD._double));
				}
			}
		}
	}
	
}
