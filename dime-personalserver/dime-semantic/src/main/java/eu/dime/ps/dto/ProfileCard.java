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
import ie.deri.smile.vocabulary.NSO;
import ie.deri.smile.vocabulary.PPO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.Variable;

import eu.dime.ps.semantic.model.ppo.PrivacyPreference;

public class ProfileCard extends Resource {

	private static final Map<URI, String> RENAMING_RULES;
	static {
		RENAMING_RULES = new HashMap<URI, String>();
		RENAMING_RULES.put(PPO.appliesToResource, "items");

	}

	public ProfileCard() {
		super();
                this.put("supportsSharing", true);
	}

	public ProfileCard(PrivacyPreference profileCard, String serviceAccountId,URI me) {
		super();
		addToMap(profileCard, RENAMING_RULES,me);
		this.put("editable", true);
                this.put("supportsSharing", true);
		this.put("guid", "pc_" + profileCard.asURI().toString());
		this.put("said",serviceAccountId);
		// adding said 
		
		ArrayList<String> includes = new ArrayList<String>();
                ArrayList<String> excludes = new ArrayList<String>();
		ClosableIterator<Statement> iterator = profileCard.getModel()
				.findStatements(profileCard, PPO.hasAccessSpace, Variable.ANY);
		while (iterator.hasNext()) {
			Node object = iterator.next().getObject();
			if (object instanceof URI) {
				Collection<Node> nodes = ModelUtils.findObjects(
						profileCard.getModel(), object.asURI(), NSO.includes);
				nodes.size();
				for (Node node : nodes) {
					if (node != null) {
						includes.add(node.asURI().toString());											
					}
				}
				nodes = ModelUtils.findObjects(profileCard.getModel(), object.asURI(), NSO.excludes);
	    		for (Node node : nodes){
	    			if (node != null){
		    			excludes.add(node.asURI().toString());
		    		}
	    		}

			}
		}
		iterator.close();
		this.put("nao:includes", includes);
                this.put("nao:excludes", excludes);
		

	}
}
