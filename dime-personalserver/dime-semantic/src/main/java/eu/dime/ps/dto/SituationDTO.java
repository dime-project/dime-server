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

package eu.dime.ps.dto;

import ie.deri.smile.rdf.util.ModelUtils;
import ie.deri.smile.vocabulary.DCON;
import ie.deri.smile.vocabulary.NAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;
import org.ontoware.rdf2go.vocabulary.RDF;

import eu.dime.ps.semantic.model.dcon.Situation;
import eu.dime.ps.semantic.model.pimo.Person;

public class SituationDTO extends Resource {

	private static final long serialVersionUID = 1L;

	public SituationDTO() {
		super();
	}

	public SituationDTO(Situation situation, Person me) {
		super();
		addToMap(situation, new HashMap<URI, String>(), me.asURI());

		Model sModel = situation.getModel();
		
		// <me> dcon:hasSituation <situationX> indicates a situation is active
		this.put("active", sModel.contains(me, DCON.hasSituation, situation.asURI()));
		
		// include `contextElements` array with context data
		// [{ 
		//  "guid": "<contextElementId>", 
		//  "type": "as specified by DCON property e.g. dcon:currentActivity",  
		//  "imageUrl": "imageUrl or null", 
		//  "name": "label of element e.g. Working", 
		//  "dcon:weight": 0.5, 
		//  "dcon:isRequired": false, 
		//  "dcon:isExcluder": false
		// }, .. ]
		List<Map<String, Object>> elementsArray = new ArrayList<Map<String, Object>>();
		ClosableIterator<Statement> aspects = sModel.findStatements(Variable.ANY, RDF.type, DCON.Aspect);
		while (aspects.hasNext()) {
			org.ontoware.rdf2go.model.node.Resource aspect = aspects.next().getSubject();
			Collection<Node> contextElements = ModelUtils.findObjects(sModel, aspect, DCON.hasContextElement);
			for (Node contextElement : contextElements) {
				Map<String, Object> elementObject = new HashMap<String, Object>();
				elementObject.put("guid", contextElement.asURI().toString());
				
				String type = null;
				ClosableIterator<Statement> statements = sModel.findStatements(aspect, Variable.ANY, contextElement);
				while (type == null && statements.hasNext()) {
					URI predicate = statements.next().getPredicate();
					if (!predicate.equals(DCON.hasContextElement)) {
						type = collapse(predicate.toString());
					}
				}
				elementObject.put("type", type);
				statements.close();
				
				elementObject.put("imageUrl", null);
				
				Node prefLabel = ModelUtils.findObject(sModel, contextElement.asResource(), NAO.prefLabel);
				elementObject.put("name", prefLabel == null ? null : prefLabel.asLiteral().getValue());
				
				Node weight = ModelUtils.findObject(sModel, contextElement.asResource(), DCON.weight);
				elementObject.put("dcon:weight", weight == null ? null : Double.parseDouble(weight.asLiteral().getValue()));
				
				Node isRequired = ModelUtils.findObject(sModel, contextElement.asResource(), DCON.isRequired);
				boolean required = isRequired == null ? false : Boolean.parseBoolean(isRequired.asLiteral().getValue());
				elementObject.put("dcon:isRequired", required);
	
				Node isExcluder = ModelUtils.findObject(sModel, contextElement.asResource(), DCON.isExcluder);
				boolean excluder = isExcluder == null ? false : Boolean.parseBoolean(isExcluder.asLiteral().getValue());
				elementObject.put("dcon:isExcluder", excluder);
				
				elementsArray.add(elementObject);
			}
		}
		aspects.close();
		this.put("contextElements", elementsArray);
	}

	@Override
	public <T extends org.ontoware.rdfreactor.schema.rdfs.Resource> T asResource(URI resourceUri,
			Class<T> returnType, URI me) {

		// `active` is not supported by super.asResource() 
		boolean isActive = this.get("active").equals(true);
		this.remove("active");
		
		T situation = super.asResource(resourceUri, returnType, me);
		if (isActive) {
			situation.getModel().addStatement(me, DCON.hasSituation, situation);
		}

		return situation;
	}

}